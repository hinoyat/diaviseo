from sqlalchemy.orm import Session
from sqlalchemy import func
from datetime import date

def get_today_calories_intake(db: Session, user_id: int, meal_date: date):

  from app.models.nutrition import Meal, MealTime, MealFood, Food

  result = db.query(
      func.coalesce(func.sum(Food.calorie * MealFood.quantity), 0).label(
        "total_calorie")
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

  return result.total_calorie

