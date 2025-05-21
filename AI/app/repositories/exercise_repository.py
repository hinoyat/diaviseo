from sqlalchemy.orm import Session
import datetime


def get_today_calories_burned(health_db: Session, user_id: int) -> int:
  """
  특정 사용자의 오늘 소모한 칼로리의 총합을 반환합니다.

  Args:
      health_db: 데이터베이스 세션
      user_id: 사용자 ID

  Returns:
      int: 오늘 소모한 칼로리 총합
  """
  from app.models.workout import Exercise
  from sqlalchemy import func

  today = datetime.date.today()

  result = health_db.query(
      func.coalesce(func.sum(Exercise.exercise_calorie * Exercise.exercise_time), 0).label(
        "total_burned")
  ).filter(
      Exercise.user_id == user_id,
      Exercise.exercise_date == today,
      Exercise.is_deleted == 0
  ).one()

  return result.total_burned

import datetime
from sqlalchemy import func
from sqlalchemy.orm import Session
from typing import Union
def get_someday_calories_burned(
    health_db: Session,
    user_id: int,
    exercise_date: Union[datetime.date, datetime.datetime, None] = None
) -> float:
  """
  특정 날짜에 소모한 칼로리(kcal) 총합을 반환합니다.
  - Exercise.exercise_calorie 컬럼: **세션별 kcal** 로 저장돼 있어야 함.
  """

  from app.models.workout import Exercise  # 로컬 import 유지

  # 날짜 정규화
  if exercise_date is None:
    exercise_date = datetime.date.today()
  elif isinstance(exercise_date, datetime.datetime):
    exercise_date = exercise_date.date()

  total_burned = health_db.query(
      func.coalesce(
          func.sum(Exercise.exercise_calorie), 0.0
      ).label("total_burned")
  ).filter(
      Exercise.user_id == user_id,
      Exercise.exercise_date == exercise_date,
      Exercise.is_deleted == 0
  ).scalar()  # 바로 숫자 값 반환

  return float(total_burned)


