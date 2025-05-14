from sqlalchemy.orm import Session
from sqlalchemy import func
from app.body.models.entity import Meal, MealTime, MealFood, Food
from datetime import date


def get_daily_nutrition_totals(db: Session, user_id: int, meal_date: date):
  result = db.query(
      func.coalesce(func.sum(Food.calorie * MealFood.quantity), 0).label(
        "total_calorie"),
      func.coalesce(func.sum(Food.carbohydrate * MealFood.quantity), 0).label(
        "total_carb"),
      func.coalesce(func.sum(Food.protein * MealFood.quantity), 0).label(
        "total_protein"),
      func.coalesce(func.sum(Food.fat * MealFood.quantity), 0).label(
        "total_fat"),
  ).select_from(Meal) \
    .join(MealTime, MealTime.meal_id == Meal.meal_id) \
    .join(MealFood, MealFood.meal_time_id == MealTime.meal_time_id) \
    .join(Food, Food.food_id == MealFood.food_id) \
    .filter(
      Meal.user_id == user_id,
      Meal.meal_date == meal_date,
      Meal.is_deleted == False,
      MealTime.is_deleted == False
  ).one()

  return {
      "calorie": result.total_calorie,
      "carbohydrate": result.total_carb,
      "protein": result.total_protein,
      "fat": result.total_fat
  }