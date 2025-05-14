from sqlalchemy.orm import Session
from sqlalchemy import func
from datetime import date


def get_bmr(user_db: Session, user_id: int) -> float:
  """
  사용자의 기초대사량(Basal Metabolic Rate)을 계산합니다.

  Args:
      user_db: 사용자 데이터베이스 세션
      user_id: 사용자 ID

  Returns:
      float: 계산된 BMR 값 (일일 칼로리)
  """
  from app.models.user import User
  from app.models.workout import BodyInfo

  user = user_db.query(User).filter(User.user_id == user_id).first()
  if not user:
    raise ValueError(f"사용자 ID {user_id}에 대한 정보를 찾을 수 없습니다.")

  # 가장 최근의 체중 정보 가져오기
  health_db = user_db  # 동일한 세션 사용 또는 필요시 새 세션 생성
  body_info = health_db.query(BodyInfo).filter(
      BodyInfo.user_id == user_id
  ).order_by(BodyInfo.measurement_date.desc()).first()

  # 기본 정보 설정
  age = (date.today().year - user.birthday) if user.birthday else 30
  gender = user.gender or "M"
  weight = float(body_info.weight)
  height = float(body_info.height)

  # Harris-Benedict 공식을 사용한 BMR 계산
  if gender == "M":
    bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
  else:  # FEMALE
    bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)

  return bmr