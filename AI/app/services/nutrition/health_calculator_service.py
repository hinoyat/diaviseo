from requests import Session


def get_tdee(user_db: Session, health_db:Session, user_id: int, activity_level: str) -> float:
  """
  기초대사량(BMR)과 활동 수준을 기반으로 총 일일 에너지 소비량(TDEE)을 계산합니다.

  Args:
      bmr: 기초대사량 (kcal/day)
      activity_level: 활동 수준 ('sedentary', 'light', 'moderate', 'active', 'very_active')

  Returns:
      float: 계산된 TDEE (kcal/day)
  """
  from app.repositories.body_info_repository import get_bmr
  bmr = get_bmr(user_db=user_db, health_db=health_db, user_id=user_id)

  activity_multipliers = {
    'sedentary': 1.2,  # 거의 운동하지 않음
    'light': 1.375,  # 가벼운 운동(주 1-3일)
    'moderate': 1.55,  # 보통 수준 운동(주 3-5일)
    'active': 1.725,  # 활발한 운동(주 6-7일)
    'very_active': 1.9  # 매우 활발한 운동(하루 2회 이상)
  }

  # 기본값 설정 (알 수 없는 활동 수준의 경우)
  multiplier = activity_multipliers.get(activity_level.lower(), 1.2)

  return round(bmr * multiplier, 2)

