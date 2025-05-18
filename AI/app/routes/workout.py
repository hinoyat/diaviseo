import datetime

from fastapi import APIRouter, Depends, Request
from fastapi.params import Header
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db.mysql import get_session

router = APIRouter()


@router.get("/calories/remaining")
def get_remaining_calories(
    user_id: int = Header(default=None, alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 남은 칼로리를 반환합니다.

    Args:
        user_id: 사용자 ID
        user_db: 사용자 DB 세션
        health_db: 건강 DB 세션

    Returns:
        남은 칼로리 정보 (양수: 추가 섭취 가능, 음수: 초과 섭취)
    """
    from app.services.workout.workout_service import \
        calculate_remaining_calories

    remaining = calculate_remaining_calories(health_db, user_db, user_id)
    return {"remaining_calories": remaining}


@router.get("/weight/trend")
def weight_trend_feedback(
    date: datetime.datetime = None,
    days: int = 7,
    user_id: int = Header(default=None, alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    from app.services.workout.feedback import generate_trend_weight_feedback

    feedback = generate_trend_weight_feedback(user_id, user_db, health_db, days, date)
    return {"feedback": feedback}