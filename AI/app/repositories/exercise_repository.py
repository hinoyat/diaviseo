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