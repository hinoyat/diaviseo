import datetime

from langchain.chains.conversation.base import ConversationChain
from langchain_openai import ChatOpenAI
from requests import Session

from app.config.settings import get_settings
from app.repositories.body_info_repository import get_user_info_for_feedback, \
  calculate_additional_calories_to_burn, predict_weight_change_from_base_date, \
  calculate_additional_calories_to_burn_someday, \
  get_user_info_for_feedback_from_date
from app.repositories.feedback_repository import insert_feedback, FeedbackType
from app.services.memory.mongo_memory import get_memory
from app.services.workout.prompt.prompt_templates import \
  workout_feedback_prompt, weight_trend_feedback_prompt

settings = get_settings()


# GPT 운동 피드백 생성 함수 (간소화된 버전)
def generate_workout_feedback(user_id: int, session_id:str, message:str,user_db: Session,
    health_db: Session) -> str:
  # LLM 초기화
  llm = ChatOpenAI(
      model="gpt-4o-mini",
      temperature=0.1,
      api_key=settings.openai_api_key
  )
  chain = ConversationChain(
      llm = llm,
  )

  # 사용자 정보 가져오기
  try:
    user_info = get_user_info_for_feedback(health_db, user_db, user_id)
  except Exception as e:
    # 사용자 정보 조회 실패 시 일반 챗봇 응답
    return chain.predict(
    input=f"다음 질문에 대해 운동 코치로서 일반적인 조언을 200자 이내로 간략하게 제공해주세요: {message}") + " 체성분 정보를 등록하면 사용자에게 맞춤형 조언을 받을 수 있어요!"
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
      muscle_trend=user_info.get("muscle_trend"),
      message=message
  )

  # LLM 호출하여 피드백 생성
  reply = chain.predict(input=formatted_prompt)

  # 결과 반환
  return reply

# 체중 추이 피드백 생성 함수
def generate_trend_weight_feedback(user_id: int, user_db: Session,
    health_db: Session, days, date:datetime.datetime) -> str:

  from app.repositories.body_info_repository import predict_weight_change, \
    get_user_info_for_feedback

  # 사용자 정보 및 체중 변화 예측 정보 가져오기
  user_info = get_user_info_for_feedback_from_date(health_db, user_db, user_id, date=date)
  prediction = predict_weight_change_from_base_date(health_db, user_db, user_id, days, base_date=date)
  remaining_calorie = calculate_additional_calories_to_burn_someday(health_db, user_db, user_id, date=date)
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

  insert_feedback(user_id, response.content, FeedbackType.WEIGHT_TREND, date)
  # 결과 반환
  return response.content