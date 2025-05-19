from app.schemas.nutrition import FoodNutritionResponse
from app.models.nutrition import Food
from fastapi import HTTPException



def get_food_nutrition(food_name: str, db) -> FoodNutritionResponse:
    # DB 조회
    food: Food = (
        db.query(Food)
          .filter(Food.food_name == food_name, Food.is_deleted == False)
          .first()
    )

    if not food:
        raise HTTPException(status_code=404, detail=f"'{food_name}' 영양정보가 없습니다.")

    response = FoodNutritionResponse(
        foodId=food.food_id,
        foodName=food.food_name,
        calorie=food.calorie,
        carbohydrate=food.carbohydrate,
        protein=food.protein,
        fat=food.fat,
        sweet=food.sweet,
        base_amount=food.base_amount,
    )

    return response