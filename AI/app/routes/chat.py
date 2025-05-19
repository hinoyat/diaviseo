import datetime
from decimal import Decimal
from typing import Optional, Any

from fastapi import APIRouter, Header, Depends, Path, Query
from sqlalchemy.orm import Session

from app.config.settings import get_settings
from app.db.mysql import get_session
from app.repositories.body_info_repository import \
    predict_weight_change_from_base_date, fetch_user_training_data, \
    get_user_body_info
from app.schemas.chat import ChatRequest, FeedbackType
from app.services.chat.chat_service import chat_with_session, get_chat_history
from app.services.feedback.feedback_service import get_feedback
from app.utils.weight_predictor import create_user_60d_dataset
import requests

router = APIRouter()

@router.post("/chat/{session_id}")
def chat(session_id: str, req: ChatRequest, user_id: int = Header(default="default_user", alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))):
    return chat_with_session(session_id, req.message, user_db, health_db)

@router.get("/messages/{session_id}")
def messages(session_id: str):
    return get_chat_history(session_id)


@router.get("/feedback/{feedback_type}")
def feedback(
    user_id: int = Header(..., alias="X-USER-ID"),
    feedback_type: FeedbackType = Path(..., description="피드백 유형 (workout, weight_trend, nutrition)"),
    date: Optional[datetime.date] = Query(None, description="피드백 날짜 (YYYY-MM-DD)")
) -> str | None:
    """
    사용자의 특정 유형 피드백을 조회합니다.
    """
    feedback_date = date or datetime.date.today()
    _feedback = get_feedback(user_id, feedback_date, feedback_type)

    if feedback:
        return _feedback
    return ""



@router.get("/weight/predict")
async def predict_weight_change(
    days_ahead: int = Query(60, description="예측할 미래 일수"),
    base_date: Optional[datetime.date] = Query(None,
                                               description="예측 기준 날짜 (기본값: 오늘)"),
    user_id: int = Header(..., alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 향후 몸무게 변화를 예측합니다.
    """
    settings = get_settings()
    # model_url = "http://localhost:8100/predict-future-weight"
    # 기준일이 없으면 오늘 날짜 사용
    if base_date is None:
        base_date = datetime.date.today()

    user_body_info = get_user_body_info(health_db, user_db, user_id)
    if not user_body_info:
        from fastapi import HTTPException
        raise HTTPException(
            status_code=404,
            detail="체중 예측에 필요한 사용자 데이터가 없습니다."
        )
    latest = user_body_info
    payload = {
        "height": float(latest.height),
        "body_fat": float(latest.body_fat),
        "muscle_mass": float(latest.muscle_mass),
        "exercise_calories": float(latest.exercise_calories),
        "current_weight": float(latest.weight),
    }
    model_url = f"{settings.model_service_url}{settings.predict_model_path}"
    params = {"user_id": user_id}

    # 2) 모델 예측 시도
    try:
        resp = requests.post(
            model_url,
            json=payload,
            params={"user_id": user_id},
            headers={"Content-Type": "application/json"}
        )
        resp.raise_for_status()
        data = resp.json()
        projected = float(data['predicted_weight_60d'])
        return {
            "base_date": base_date,
            "prediction_date": base_date + datetime.timedelta(days=days_ahead),
            "predicted_weight": {
                "projected_change": projected - float(latest.weight),
                "days_tracked": days_ahead,
                "status": "감량 예상" if projected < float(
                    latest.weight) else "증량 예상"
            },
            "days_ahead": days_ahead
        }
    except requests.HTTPError:
        # 모델 예측 실패 시 TDEE 기반 폴백
        predicted_weight = predict_weight_change_from_base_date(
            user_db=user_db,
            health_db=health_db,
            user_id=user_id,
            days=days_ahead,
            base_date=base_date,
        )
        return {
            "base_date": base_date,
            "prediction_date": base_date + datetime.timedelta(days=days_ahead),
            "predicted_weight": predicted_weight,
            "days_ahead": days_ahead
        }


@router.post("/trigger_training")
def trigger_training(
    user_id: int = Header(..., alias="X-USER-ID"),
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 데이터를 모아 모델 학습을 트리거합니다.
    """
    settings = get_settings()
    raw = fetch_user_training_data(user_id, health_db)
    if len(raw) < 60:
        from fastapi import HTTPException
        raise HTTPException(status_code=400, detail="학습용 데이터가 부족합니다. (최소 60개 필요)")

    df = create_user_60d_dataset(raw, gap_days=60)
    df = df.applymap(lambda x: float(x) if isinstance(x, Decimal) else x)
    payload = df.to_dict(orient="records")

    train_url = f"{settings.model_service_url}{settings.train_model_path}/{user_id}"
    resp = requests.post(
        train_url,
        json=payload,
        headers={"Content-Type": "application/json"}
    )
    resp.raise_for_status()
    return resp.json()
