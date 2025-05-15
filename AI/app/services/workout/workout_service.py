from typing import Any

from sqlalchemy.orm import Session

from app.repositories.body_info_repository import \
  calculate_additional_calories_to_burn
from app.repositories.user_repository import get_user_info
from app.repositories.exercise_repository import get_today_calories_burned
from app.repositories.nutrition_repository import get_today_calories_intake
from app.services.bodyinfo.bodyinfo_service import get_tdee, \
  get_daily_target_by_goal


def calculate_weight_trend(health_db: Session, user_db: Session, user_id: int, days: int) -> \
dict[str, int | str | Any]:
  from app.repositories.user_repository import get_bmr
  user = get_user_info(user_db, user_id)
  bmr = get_bmr(user_db, user_id)
  activity_level = 'light'
  tdee = get_tdee(bmr, activity_level)
  target = get_daily_target_by_goal(tdee, user.goal.value)

  total_gap = 0.0
  for i in range(days):
    from datetime import date
    from datetime import timedelta
    day = date.today()- timedelta(days=i)

    calories_intake = get_today_calories_intake(health_db, user_id, day)
    calories_burned = get_today_calories_burned(health_db, user_id)
    net = calories_intake - calories_burned - bmr
    gap = target - net
    total_gap += gap

  weight_change_kg = round(total_gap / days, 2)
  return {
    "projected_change": weight_change_kg,
    "days_tracked": days,
    "status": "감량 예상" if weight_change_kg < 0 else "증량 예상"
  }


def calculate_remaining_calories(health_db: Session, user_db: Session,
    user_id: int) -> float:
  """
  사용자의 남은 칼로리를 계산합니다.

  Args:
      health_db: 건강 정보 데이터베이스 세션
      user_db: 사용자 정보 데이터베이스 세션
      user_id: 사용자 ID

  Returns:
      float: 남은 칼로리량 (양수: 추가 섭취 가능, 음수: 초과 섭취)
  """
  return calculate_additional_calories_to_burn(health_db, user_db, user_id)