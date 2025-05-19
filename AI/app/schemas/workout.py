
from pydantic import BaseModel, Field
class WorkoutRequest(BaseModel):
    gender: str
    height: float
    weight: float
    body_fat: float
    muscle_mass: float
    remaining_calorie: float
    goal: str
    weight_trend: str
    muscle_trend: str


class WorkoutResponse(BaseModel):
    feedback: str


class FutureWeightInput(BaseModel):
    height: float = Field(..., example=170.0)
    body_fat: float = Field(..., example=22.5)
    muscle_mass: float = Field(..., example=30.0)
    exercise_calories: int = Field(..., example=300)
    current_weight: float = Field(..., example=75.0)