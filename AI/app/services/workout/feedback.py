from langchain_openai import ChatOpenAI
from requests import Session

from app.config.settings import get_settings
from app.repositories.body_info_repository import get_user_info_for_feedback, \
  calculate_additional_calories_to_burn
from app.services.workout.prompt.prompt_templates import \
  workout_feedback_prompt, weight_trend_feedback_prompt

settings = get_settings()


# GPT 운동 피드백 생성 함수 (간소화된 버전)
def generate_workout_feedback(user_id: int, user_db: Session,
    health_db: Session) -> str:
  # LLM 초기화
  llm = ChatOpenAI(
      model="gpt-4o-mini",
      temperature=0.1,
      api_key=settings.openai_api_key
  )

  # 사용자 정보 가져오기
  user_info = get_user_info_for_feedback(health_db, user_db, user_id)

  # 프롬프트 준비
  formatted_prompt = workout_feedback_prompt.template.format(
      gender=user_info.get("gender"),
      height=user_info.get("height"),
      weight=user_info.get("weight"),
      body_fat=user_info.get("body_fat"),
      muscle_mass=user_info.get("muscle_mass"),
      remaining_calorie=user_info.get("remaining_calorie"),
      goal=user_info.get("goal"),
      weight_trend=user_info.get("weight_trend"),
      muscle_trend=user_info.get("muscle_trend")
  )

  # LLM 호출하여 피드백 생성
  response = llm.invoke(formatted_prompt)

  # 결과 반환
  return response.content

# 체중 추이 피드백 생성 함수
def generate_trend_weight_feedback(user_id: int, user_db: Session,
    health_db: Session, days: int = 7) -> str:
  """
  사용자의 체중 변화 추이를 분석하고 피드백을 생성합니다.

  Args:
      user_id: 사용자 ID
      user_db: 사용자 정보 데이터베이스 세션
      health_db: 건강 정보 데이터베이스 세션
      days: 분석할 기간(일)

  Returns:
      str: 체중 변화 추이에 대한 피드백
  """
  from app.repositories.body_info_repository import predict_weight_change, \
    get_user_info_for_feedback

  # 사용자 정보 및 체중 변화 예측 정보 가져오기
  user_info = get_user_info_for_feedback(health_db, user_db, user_id)
  prediction = predict_weight_change(health_db, user_db, user_id, days)
  remaining_calorie = calculate_additional_calories_to_burn(health_db, user_db, user_id)
  # LLM 초기화
  llm = ChatOpenAI(
      model="gpt-4o-mini",
      temperature=0.1,
      api_key=settings.openai_api_key
  )

  # 프롬프트 준비
  formatted_prompt = weight_trend_feedback_prompt.format(
      goal=user_info.get('goal'),
      weight=user_info.get('weight'),
      days=days,
      weight_trend=user_info.get('weight_trend'),
      remaining_calorie=remaining_calorie,
      muscle_trend=user_info.get('muscle_trend'),
      projected_change=prediction['projected_change'],
      future_weight=user_info.get('weight') + prediction['projected_change'],
      status=prediction['status']
  )

  # LLM 호출하여 피드백 생성
  response = llm.invoke(formatted_prompt)

  # 결과 반환
  return response.content