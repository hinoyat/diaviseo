from fastapi import APIRouter, Depends, Request
from fastapi.params import Header
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.schemas.workout import WorkoutResponse
from app.services.workout.feedback import generate_workout_feedback

router = APIRouter()

@router.post("/workout")
def workout_feedback(
    request: Request,
    user_id: int = Header(default="default_user", alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
) -> WorkoutResponse:
    feedback = generate_workout_feedback(user_id, user_db, health_db)
    return WorkoutResponse(feedback=feedback)


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