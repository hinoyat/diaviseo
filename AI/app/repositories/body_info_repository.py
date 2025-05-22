from sqlalchemy.orm import Session
from app.models.workout import BodyInfo
import datetime
from app.repositories.nutrition_repository import get_today_calories_intake
from app.repositories.exercise_repository import get_today_calories_burned, \
  get_someday_calories_burned
from app.services.bodyinfo.bodyinfo_service import get_tdee
from app.models.user import User, GenderEnum, GoalEnum


def get_user_body_info(health_db: Session, user_db: Session, user_id: int):

  body_info = (
    health_db.query(BodyInfo)
    .filter(BodyInfo.user_id == user_id)
    .order_by(BodyInfo.measurement_date.desc())
    .first()
  )
  if not body_info:
    return None
  exercise_calories = calculate_additional_calories_to_burn(health_db, user_db,
                                                          user_id)
  body_info.exercise_calories = exercise_calories
  return body_info

def get_user_info_for_feedback_from_date(health_db: Session, user_db: Session, user_id: int, date=datetime.date) -> dict:
  if date is None:
    date = datetime.datetime.now().date()
  elif isinstance(date, datetime.datetime):
    date = date.date()

  # 요청일 기준 최신 체성분 정보 불러옴.
  body = (
    health_db.query(BodyInfo)
    .filter(BodyInfo.user_id == user_id)
    .filter(BodyInfo.measurement_date <= date)
    .order_by(BodyInfo.measurement_date.desc())
    .first()
  )

  user = user_db.query(User).filter(User.user_id == user_id).first()

  if not body or not user:
    raise ValueError(f"사용자 ID {user_id}에 대한 데이터를 찾을 수 없습니다.")

  # 추세(특정 날짜 기준 최근 7일)
  week_ago = date - datetime.timedelta(days=7)
  recent_records = (
    health_db.query(BodyInfo)
    .filter(BodyInfo.user_id == user_id,
            BodyInfo.measurement_date >= week_ago,
            BodyInfo.measurement_date <= date)
    .order_by(BodyInfo.measurement_date.asc())
    .all()
  )

  remaining_calorie = calculate_additional_calories_to_burn_someday(health_db, user_db,
                                                          user_id, date=date)

  # 추세 계산 (첫 기록과 마지막 기록의 차이)
  weight_trend, muscle_trend = 0, 0
  if len(recent_records) >= 2:
    weight_trend = recent_records[-1].weight - recent_records[0].weight
    muscle_trend = recent_records[-1].muscle_mass - recent_records[
      0].muscle_mass

  return {
    "gender": user.gender.value,
    "height": float(body.height or user.height),
    "weight": float(body.weight),
    "body_fat": float(body.body_fat or 0),
    "muscle_mass": float(body.muscle_mass or 0),
    "remaining_calorie": remaining_calorie,
    "goal": user.goal,
    "weight_trend": weight_trend,
    "muscle_trend": muscle_trend
  }

def get_user_info_for_feedback(health_db: Session, user_db: Session, user_id: int) -> dict:
  body = (
    health_db.query(BodyInfo)
    .filter(BodyInfo.user_id == user_id)
    .order_by(BodyInfo.measurement_date.desc())
    .first()
  )

  user = user_db.query(User).filter(User.user_id == user_id).first()

  if not body or not user:
    raise ValueError(f"사용자 ID {user_id}에 대한 데이터를 찾을 수 없습니다.")

  # 추세(최근 7일)
  today = datetime.datetime.now().date()
  week_ago = today - datetime.timedelta(days=7)
  recent_records = (
    health_db.query(BodyInfo)
    .filter(BodyInfo.user_id == user_id,
            BodyInfo.measurement_date >= week_ago)
    .order_by(BodyInfo.measurement_date.asc())
    .all()
  )

  remaining_calorie = calculate_additional_calories_to_burn(health_db, user_db,
                                                            user_id)

  # 추세 계산 (첫 기록과 마지막 기록의 차이)
  weight_trend, muscle_trend = 0, 0
  if len(recent_records) >= 2:
    weight_trend = recent_records[-1].weight - recent_records[0].weight
    muscle_trend = recent_records[-1].muscle_mass - recent_records[
      0].muscle_mass

  return {
    "gender": user.gender.value,
    "height": float(body.height or user.height),
    "weight": float(body.weight),
    "body_fat": float(body.body_fat or 0),
    "muscle_mass": float(body.muscle_mass or 0),
    "remaining_calorie": remaining_calorie,
    "goal": user.goal,
    "weight_trend": weight_trend,
    "muscle_trend": muscle_trend
  }


def get_bmr(user_db: Session, health_db: Session, user_id: int) -> float:
  """
  사용자의 기초대사량(BMR)을 계산합니다.

  Args:
      user_db: 사용자 정보 데이터베이스 세션
      health_db: 건강 정보 데이터베이스 세션
      user_id: 사용자 ID

  Returns:
      float: 계산된 BMR (kcal/day)
  """
  from app.models.user import User
  from app.models.workout import BodyInfo

  # 사용자 기본 정보 가져오기
  user = user_db.query(User).filter(User.user_id == user_id).first()

  # 최신 신체 정보 가져오기
  body_info = health_db.query(BodyInfo) \
    .filter(BodyInfo.user_id == user_id) \
    .order_by(BodyInfo.measurement_date.desc()) \
    .first()

  if not user or not body_info:
    raise ValueError(f"사용자 ID {user_id}에 대한 정보를 찾을 수 없습니다.")

  # 계산에 필요한 정보 추출
  weight = float(body_info.weight)
  height = float(body_info.height or user.height)
  current_date = datetime.datetime.now().date()
  age = current_date.year - user.birthday.year - (
        (current_date.month, current_date.day) < (
    user.birthday.month, user.birthday.day))
  gender = user.gender

  # 해리스-베네딕트 공식을 사용한 BMR 계산
  if gender == GenderEnum.M:
    bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
  else:  # 여성
    bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)

  return round(bmr, 2)


def predict_weight_change_from_base_date(health_db: Session, user_db: Session,
    user_id: int,
    days: int, base_date=datetime.datetime):
  """
  사용자의 체중 변화를 예측합니다.

  Args:
      health_db: 건강 정보 데이터베이스 세션
      user_db: 사용자 정보 데이터베이스 세션
      user_id: 사용자 ID
      days: 분석할 기간(일)
      base_date: 기준 날짜 (기본값: 오늘)

  Returns:
      dict: 예측된 체중 변화 정보
  """
  bmr = get_bmr(user_db, health_db, user_id)
  tdee = get_tdee(bmr, activity_level='light')

  if base_date is None:
    base_date = datetime.date.today()
  elif isinstance(base_date, datetime.datetime):
    base_date = base_date.date()

  total_calorie_balance = 0.0
  for i in range(days):
    day = base_date - datetime.timedelta(days=i)

    # 섭취한 칼로리
    intake = float(
        get_today_calories_intake(health_db, user_id, meal_date=day))

    # 운동으로 소모한 칼로리
    burned = float(
      get_someday_calories_burned(health_db, user_id, exercise_date=day))

    # 총 소모 칼로리 = 기초대사량 + 활동 칼로리(TDEE-BMR) + 운동 칼로리
    total_expenditure = tdee + burned

    # 칼로리 수지(양수: 잉여 칼로리, 음수: 부족 칼로리)
    daily_balance = intake - total_expenditure
    total_calorie_balance += daily_balance

  # 7700 칼로리가 약 1kg의 체지방과 동일
  weight_change_kg = round(total_calorie_balance / 7700, 2)

  return {
    "projected_change": weight_change_kg,
    "days_tracked": days,
    "status": "증량 예상" if weight_change_kg > 0 else "감량 예상"
  }

def predict_weight_change(health_db: Session, user_db: Session, user_id: int, days:int):
  bmr = get_bmr(user_db, health_db, user_id)
  target = get_tdee(bmr, activity_level='light')

  total_gap = 0.0
  for i in range(days):
    day = datetime.date.today() - datetime.timedelta(days=i)

    intake = float(get_today_calories_intake(health_db, user_id, meal_date=day))
    burned = float(get_today_calories_burned(health_db, user_id))

    net = intake - bmr - burned
    gap = target - net
    total_gap += gap

  weight_change_kg = round(total_gap / 7700, 2)

  return {
    "projected_change": weight_change_kg,
    "days_tracked": days,
    "status": "감량 예상" if weight_change_kg < 0 else "증량 예상"
  }


def calculate_additional_calories_to_burn(health_db: Session, user_db: Session,
    user_id: int) -> float:
  """
  사용자의 남은 칼로리를 계산합니다.

  Args:
      health_db: 건강 정보 데이터베이스 세션
      user_db: 사용자 정보 데이터베이스 세션
      user_id: 사용자 ID

  Returns:
      float: 남은 칼로리양
  """
  from app.repositories.user_repository import get_user_info
  from app.services.bodyinfo.bodyinfo_service import get_daily_target_by_goal

  bmr = get_bmr(user_db, health_db, user_id)

  # TDEE 계산 (기초대사량에 활동 수준 곱함)
  activity_level = 'light'
  tdee = get_tdee(bmr, activity_level)

  # 사용자 목표에 맞는 일일 목표 칼로리
  user = get_user_info(user_db, user_id)
  target = get_daily_target_by_goal(tdee, user.goal.value)

  # 오늘 섭취한 칼로리
  intake = float(get_today_calories_intake(health_db, user_id,
                                           meal_date=datetime.datetime.now().date()))

  # 오늘 운동으로 소모한 칼로리
  burned = float(get_today_calories_burned(health_db, user_id))

  # 목표 칼로리 + 소모한 칼로리 - 섭취한 칼로리 = 남은 칼로리
  remaining = intake - target - burned
  return round(remaining, 2)


def calculate_additional_calories_to_burn_someday(health_db: Session, user_db: Session,
    user_id: int, date=None) -> float:
  """
  사용자의 남은 칼로리를 계산합니다.

  Args:
      health_db: 건강 정보 데이터베이스 세션
      user_db: 사용자 정보 데이터베이스 세션
      user_id: 사용자 ID
      date: 계산할 날짜 (기본값: 오늘)

  Returns:
      float: 남은 칼로리양
  """
  from app.repositories.user_repository import get_user_info
  from app.services.bodyinfo.bodyinfo_service import get_daily_target_by_goal

  if date is None:
    date = datetime.datetime.now().date()
  elif isinstance(date, datetime.datetime):
    date = date.date()

  bmr = get_bmr(user_db, health_db, user_id)

  # TDEE 계산 (기초대사량에 활동 수준 곱함)
  activity_level = 'light'
  tdee = get_tdee(bmr, activity_level)

  # 사용자 목표에 맞는 일일 목표 칼로리
  user = get_user_info(user_db, user_id)
  target = get_daily_target_by_goal(tdee, user.goal.value)

  # 지정된 날짜에 섭취한 칼로리
  intake = float(get_today_calories_intake(health_db, user_id, meal_date=date))

  # 지정된 날짜에 운동으로 소모한 칼로리
  burned = float(get_someday_calories_burned(health_db, user_id, exercise_date=date))

  # 목표 칼로리 + 소모한 칼로리 - 섭취한 칼로리 = 남은 칼로리
  remaining = intake - target - burned
  return round(remaining, 2)


def fetch_user_training_data(user_id: int, db: Session):
  """
  사용자의 60일간 체성분 정보와 운동 칼로리 데이터를 가져옵니다.

  Args:
      user_id: 사용자 ID
      db: 데이터베이스 세션

  Returns:
      pd.DataFrame: 사용자의 체성분 정보와 운동 칼로리 데이터
  """
  from sqlalchemy import select
  import pandas as pd
  from datetime import datetime, timedelta

  # 오늘 날짜로부터 60일 전 날짜 계산
  end_date = datetime.now().date()
  start_date = end_date - timedelta(days=60)

  # 60일간의 체성분 정보 불러오기
  query = select(BodyInfo).where(
      BodyInfo.user_id == user_id,
      BodyInfo.measurement_date >= start_date,
      BodyInfo.measurement_date <= end_date
  ).order_by(BodyInfo.measurement_date.asc())

  records = db.execute(query).scalars().all()

  if len(records) < 10:  # 최소 데이터 요구사항
    return None  # 학습에 필요한 데이터가 부족함

  # 데이터 변환
  data = []
  for r in records:
    # 각 날짜별 소모 칼로리 계산
    exercise_calories = float(get_someday_calories_burned(db, user_id,
                                                          exercise_date=r.measurement_date))

    data.append({
      "measurement_date": r.measurement_date,
      "height": r.height,
      "body_fat": r.body_fat,
      "muscle_mass": r.muscle_mass,
      "weight": r.weight,
      "exercise_calories": exercise_calories  # 계산된 소모 칼로리 사용
    })

  return pd.DataFrame(data)