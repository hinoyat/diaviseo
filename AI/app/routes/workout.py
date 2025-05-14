from fastapi import APIRouter, Depends, Request
from fastapi.params import Header
from pydantic import BaseModel

from app.schemas.workout import WorkoutRequest, WorkoutResponse
from app.services.workout.feedback import generate_workout_feedback

router = APIRouter()

@router.post("/chatbot/workout")
def workout_feedback(request: Request, user_id: str = Header(default="default_user", alias="X-USER-ID")) -> WorkoutResponse:
    feedback = generate_workout_feedback(user_id)
    return WorkoutResponse(feedback=feedback)