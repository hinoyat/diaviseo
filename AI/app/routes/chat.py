import datetime
from typing import Optional, Any

from fastapi import APIRouter, Header, Depends, Path, Query
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.repositories.body_info_repository import \
    predict_weight_change_from_base_date
from app.schemas.chat import ChatRequest, FeedbackType
from app.services.chat.chat_service import chat_with_session, get_chat_history
from app.services.feedback.feedback_service import get_feedback
from app.utils.weight_predictor import create_user_60d_dataset

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
    days_ahead: int = Query(30, description="예측할 미래 일수"),
    base_date: Optional[datetime.date] = Query(None,
                                               description="예측 기준 날짜 (기본값: 오늘)"),
    user_id: int = Header(..., alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 향후 몸무게 변화를 예측합니다.
    """


    # 기준일이 없으면 오늘 날짜 사용
    if base_date is None:
        base_date = datetime.date.today()

    try:
        # 체중 변화 예측
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
    except Exception as e:
        return {"error": str(e), "message": "몸무게 예측에 실패했습니다."}


# 체중 예측 모델 훈련 데이터
@router.get("/training-data")
async def get_user_training_data(
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 훈련 데이터를 가져옵니다.
    """
    from app.repositories.body_info_repository import fetch_user_training_data

    try:
        # 훈련 데이터 가져오기
        data = fetch_user_training_data(1, health_db)
        data = create_user_60d_dataset(data,60)
        if data is None:
            return {"message": "학습에 필요한 충분한 데이터가 없습니다. (최소 10개 필요)"}

        return {"data": data.to_dict(orient="records")}
    except Exception as e:
        return {"error": str(e)}