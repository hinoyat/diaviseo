from pydantic import BaseModel

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