from pydantic import BaseModel

class ChatRequest(BaseModel):
    user_input: str

class FoodRequest(BaseModel):
    foodName: str

class FoodNutritionResponse(BaseModel):
    foodId: int
    foodName: str
    calorie: int
    carbohydrate: float
    protein: float
    fat: float
    sweet: float
    base_amount: str

    class Config:
        orm_mode = True

