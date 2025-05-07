from datetime import datetime, timedelta

from datetime import date
from pydantic import BaseModel

from app.body.ml_models.langchain_chain import get_exercise_advice
from app.body.models.response import CalorieResponse


class HealthDataDummy(BaseModel):
  current_weight: float
  goal_weight: float
  today_burned: float
  gender: str
  age: int
  muscle_mass: float
  fat_mass: float
  height: float
  bmr: float
  record_date: date
  target_date: date
  total_intake_calories: float

async def fetch_dummy_health(user_id: str) -> tuple[HealthDataDummy, int]:
  """
  DB 대신 더미 HealthData를 돌려주는 함수.
  days_of_data는 임시로 10일치 있다고 가정합니다.
  """
  dummy = HealthDataDummy(
      current_weight=70.0,  # kg
      goal_weight=65.0,  # kg
      today_burned=500.0,  # kcal
      gender="M",
      age=27,  # years
      muscle_mass=30.0,  # kg
      fat_mass=15.0,  # kg
      height=175.0,  # cm
      bmr=1600.0,  # kcal (예비값)
      record_date=date.today(),
      target_date = date.today() + timedelta(days=30),
      total_intake_calories=2000.0  # 하루 섭취 칼로리 (kcal)
  )
  days_of_data = 10
  return dummy, days_of_data



# 3.2) BMR 계산 (Mifflin-St Jeor 공식)
def calculate_bmr(weight: float, height: float, age: int, gender: str) -> float:
    if gender == "M":
        return 10 * weight + 6.25 * height - 5 * age + 5
    return 10 * weight + 6.25 * height - 5 * age - 161

# 3.3) 일일 추가 소모 칼로리 계산
def compute_to_burn(latest: HealthDataDummy, days_of_data: int) -> float:
    # 2주(14일) 미만 데이터면 BMR 재계산
    bmr = (
        calculate_bmr(
            latest.current_weight,
            latest.height,
            latest.age,
            latest.gender
        )
        if days_of_data < 14
        else latest.bmr
    )

    # 목표일까지 남은 일수 (최소 1일)
    remaining_days = max(
        (latest.target_date - datetime.now().date()).days,
        1
    )
    # 감량해야 할 총 칼로리
    weight_diff      = latest.current_weight - latest.goal_weight
    total_extra_cal  = weight_diff * 7700
    daily_extra_cal  = total_extra_cal / remaining_days

    # 오늘까지 소모된 칼로리 대비 추가 소모 필요량
    # 추가로 소모해야 할 칼로리 계산
    # 하루 총 섭취량 - (기초대사량 + 이미 소모한 칼로리) + 체중 감량을 위한 추가 칼로리
    to_burn = latest.total_intake_calories - (bmr + latest.today_burned) + daily_extra_cal
    return round(to_burn, 2)


async def predict_calories(user_id: str) -> CalorieResponse:
  try:
    latest, days = await fetch_dummy_health(user_id)
    to_burn = compute_to_burn(latest, days)

    # LangChain 호출을 위해 간략한 사용자 정보 dict 생성
    user_info = {
      "age": latest.age,
      "gender": latest.gender,
      "current_weight": latest.current_weight,
      "goal_weight": latest.goal_weight,
      "height": latest.height
    }
    favorite_exercises = ["헬스", "필라테스", "요가", "크로스핏", "러닝", "클라이밍", "홈트레이닝",
                          "수영", "복싱", "사이클링"]
    advice = await get_exercise_advice(to_burn, user_info, favorite_exercises)

    return CalorieResponse(
        success=True,
        message=advice,
        to_burn_calories=to_burn
    )
  except Exception as e:
    return CalorieResponse(
        success=False,
        message=f"칼로리 계산 중 오류가 발생했습니다: {str(e)}",
        to_burn_calories=0
    )