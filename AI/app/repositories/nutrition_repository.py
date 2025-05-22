import datetime
import logging

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

from datetime import date
from typing import Dict

from fastapi import Depends
from sqlalchemy import func, case
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.models.nutrition import Meal, MealTime, MealFood, Food  # 실제 경로에 맞게 조정

def generate_nutrition_info(
    user_id: int,
    target_date: date,
    db: Session = Depends(get_session("health")),
) -> Dict[str, float]:
    """
    주어진 user_id 와 날짜에 대해 끼니별 칼로리·탄수화물·단백질·지방·콜레스테롤·당류 합계를 계산하여
    dict 로 반환합니다.
    """
    MT = MealTime
    MF = MealFood
    F  = Food

    base_q = (
        db.query(
            # 칼로리
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.calorie* MF.quantity), else_=0)),
                0
            ).label("breakfast_kcal"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.calorie* MF.quantity), else_=0)),
                0
            ).label("lunch_kcal"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.calorie* MF.quantity), else_=0)),
                0
            ).label("dinner_kcal"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.calorie* MF.quantity), else_=0)),
                0
            ).label("snack_kcal"),

            # 탄수화물
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.carbohydrate* MF.quantity), else_=0)),
                0
            ).label("breakfast_carb"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.carbohydrate* MF.quantity), else_=0)),
                0
            ).label("lunch_carb"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.carbohydrate* MF.quantity), else_=0)),
                0
            ).label("dinner_carb"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.carbohydrate* MF.quantity), else_=0)),
                0
            ).label("snack_carb"),

            # 단백질
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.protein* MF.quantity), else_=0)),
                0
            ).label("breakfast_protein"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.protein* MF.quantity), else_=0)),
                0
            ).label("lunch_protein"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.protein* MF.quantity), else_=0)),
                0
            ).label("dinner_protein"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.protein* MF.quantity), else_=0)),
                0
            ).label("snack_protein"),

            # 지방
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.fat* MF.quantity), else_=0)),
                0
            ).label("breakfast_fat"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.fat* MF.quantity), else_=0)),
                0
            ).label("lunch_fat"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.fat* MF.quantity), else_=0)),
                0
            ).label("dinner_fat"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.fat* MF.quantity), else_=0)),
                0
            ).label("snack_fat"),

            # 콜레스테롤
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.cholesterol* MF.quantity), else_=0)),
                0
            ).label("breakfast_cholesterol"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.cholesterol* MF.quantity), else_=0)),
                0
            ).label("lunch_cholesterol"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.cholesterol* MF.quantity), else_=0)),
                0
            ).label("dinner_cholesterol"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.cholesterol* MF.quantity), else_=0)),
                0
            ).label("snack_cholesterol"),

            # 당류
            func.coalesce(
                func.sum(case((MT.meal_type == "BREAKFAST", F.sweet* MF.quantity), else_=0)),
                0
            ).label("breakfast_sugar"),
            func.coalesce(
                func.sum(case((MT.meal_type == "LUNCH", F.sweet* MF.quantity), else_=0)),
                0
            ).label("lunch_sugar"),
            func.coalesce(
                func.sum(case((MT.meal_type == "DINNER", F.sweet* MF.quantity), else_=0)),
                0
            ).label("dinner_sugar"),
            func.coalesce(
                func.sum(case((MT.meal_type == "SNACK", F.sweet* MF.quantity), else_=0)),
                0
            ).label("snack_sugar"),
        )
        .select_from(Meal)
        .join(MealTime, MealTime.meal_id == Meal.meal_id)
        .join(MealFood, MealFood.meal_time_id == MealTime.meal_time_id)
        .join(Food, Food.food_id == MealFood.food_id)
        .filter(
            Meal.user_id == user_id,
            Meal.meal_date == target_date,
            Meal.is_deleted == False,
            MealTime.is_deleted == False)
    )

    # ① 실제 SQLAlchemy가 쓰는 SQL을 찍어보고
    logging.debug(str(base_q.statement.compile(compile_kwargs={"literal_binds": True})))

    row = base_q.one()
    print("row:", row)  # namedtuple 형태로 모든 컬럼이 보임
    print("breakfast_kcal=", row.breakfast_kcal,
          " lunch_kcal=", row.lunch_kcal,  # 식사별 합계
          " dinner_kcal=", row.dinner_kcal,
          " snack_kcal=", row.snack_kcal)

    # 하루 전체 합계 계산
    total_calorie     = row.breakfast_kcal     + row.lunch_kcal     + row.dinner_kcal     + row.snack_kcal
    total_carbohydrate = row.breakfast_carb    + row.lunch_carb     + row.dinner_carb     + row.snack_carb
    total_protein     = row.breakfast_protein  + row.lunch_protein  + row.dinner_protein  + row.snack_protein
    total_fat         = row.breakfast_fat      + row.lunch_fat      + row.dinner_fat      + row.snack_fat
    total_cholesterol = row.breakfast_cholesterol + row.lunch_cholesterol + row.dinner_cholesterol + row.snack_cholesterol
    total_sugar       = row.breakfast_sugar    + row.lunch_sugar    + row.dinner_sugar    + row.snack_sugar

    # logging.info("총칼로리:",total_calorie)
    # logging.info("총 탄수화물:",total_carbohydrate)
    # logging.info("총 단백질:",total_protein)
    # logging.info("총 지방:",total_fat)
    # logging.info("청 콜레스테롤:",total_cholesterol)
    # logging.info("총 당:",total_sugar)
    return {
        "calorie": float(total_calorie),
        "carbohydrate": float(total_carbohydrate),
        "protein": float(total_protein),
        "fat": float(total_fat),
        "cholesterol": float(total_cholesterol),
        "sugar": float(total_sugar),
    }
