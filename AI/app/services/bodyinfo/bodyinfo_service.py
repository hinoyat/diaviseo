def get_tdee(bmr: float, activity_level: str) -> float:
  """
  기초대사량(BMR)과 활동 수준을 기반으로 총 일일 에너지 소비량(TDEE)을 계산합니다.

  Args:
      bmr: 기초대사량 (kcal/day)
      activity_level: 활동 수준 ('sedentary', 'light', 'moderate', 'active', 'very_active')

  Returns:
      float: 계산된 TDEE (kcal/day)
  """
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


def get_daily_target_by_goal(tdee: float, goal: str) -> float:
  """
  목표(체중 감량, 증가, 유지)에 따른 일일 권장 칼로리를 계산합니다.

  Args:
      tdee: 총 일일 에너지 소비량 (kcal/day)
      goal: 사용자의 목표 (WEIGHT_LOSS, WEIGHT_GAIN, WEIGHT_MAINTENANCE)

  Returns:
      float: 목표에 따른 일일 권장 칼로리
  """
  from app.models.user import GoalEnum

  if goal == GoalEnum.WEIGHT_LOSS:
    return round(tdee * 0.8, 2)  # 체중 감량 시 20% 칼로리 감소
  elif goal == GoalEnum.WEIGHT_GAIN:
    return round(tdee * 1.15, 2)  # 체중 증가 시 15% 칼로리 증가
  else:  # GoalEnum.WEIGHT_MAINTENANCE
    return round(tdee, 2)  # 체중 유지 시 TDEE 유지
