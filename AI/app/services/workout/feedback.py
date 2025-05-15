from langchain_openai import ChatOpenAI
from requests import Session

from app.repositories.body_info_repository import get_user_info_for_feedback
from app.services.workout.prompt.prompt_templates import workout_feedback_prompt
from app.config.settings import get_settings

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