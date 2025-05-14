import os
from dotenv import load_dotenv

from app.body.config.settings import get_settings


def build_qa():
  from langchain_openai import ChatOpenAI
  from langchain.prompts import PromptTemplate
  from langchain.schema.runnable import RunnablePassthrough
  settings = get_settings()
  # 1단계: 사용자 상태 요약 프롬프트
  profile_prompt = PromptTemplate(
      input_variables=["age", "gender", "weight", "skeletal_muscle_mass",
                       "body_fat_mass", "calories_intake"],
      template="""
      사용자는 {age}세 {gender}입니다. 현재 체중은 {weight}kg이며, 골격근량은 {skeletal_muscle_mass}kg, 체지방량은 {body_fat_mass}kg입니다.
      하루 섭취 칼로리는 {calories_intake}kcal입니다.

      체성분을 바탕으로 현재 상태를 간단히 설명해주세요.
      """
  )

  calorie_prompt = PromptTemplate(
      input_variables=["calories_intake", "calories_burned"],
      template="""
      사용자는 오늘 {calories_intake}kcal를 섭취했고, {calories_burned}kcal를 운동으로 소모했습니다.

      체중 감량을 목표로 한다면, 하루 총 칼로리 수지(섭취 - 소모)를 약 -300kcal 수준으로 유지하는 것이 좋습니다.

      현재 수지를 분석하고, 추가로 얼마만큼의 칼로리를 운동으로 더 소모해야 하는지 알려주세요.
      """
  )

  # 2단계: 오늘의 운동 코칭 메시지 생성 프롬프트
  coaching_prompt = PromptTemplate(
      input_variables=["user_summary", "calorie_feedback"],
      template="""
      사용자의 상태는 다음과 같습니다:
      {user_summary}

      칼로리 분석 결과:
      {calorie_feedback}

      체중 감량을 위해 오늘 어떤 운동을 추가하면 좋을지 한 문장으로 구체적으로 제안해주세요.
      어조는 따뜻하고 명확하며 행동 유도적이어야 합니다.
      """
  )

  # LLM 설정
  llm = ChatOpenAI(
      model="gpt-4o-mini",
      temperature=0.3,
      api_key=settings.openai_api_key
  )

  # 각각의 LLMChain 생성
  profile_chain = profile_prompt | llm
  calorie_chain = calorie_prompt | llm
  coaching_chain = coaching_prompt | llm


  # 체인 연결
  chain = (
      {"user_summary": profile_chain,
       "calorie_feedback": lambda x: calorie_chain.invoke({
         "calories_intake": x["calories_intake"],
         "calories_burned": x.get("calories_burned", 0)
       })}
      | RunnablePassthrough.assign(
      coaching_message=lambda x: coaching_chain.invoke({
        "user_summary": x["user_summary"],
        "calorie_feedback": x["calorie_feedback"]
      }))
      | (lambda x: {"summary": x["user_summary"],
                    "calorie_feedback": x["calorie_feedback"],
                    "coaching": x["coaching_message"]})
  )

  return chain

qa = build_qa()


def run_chat(bodies, message):
  # 가장 최근 body 정보를 사용 (첫 번째 항목)
  if bodies and len(bodies) > 0:
    latest_body = bodies[0]

    test_data = {
      "age": 30,
      "gender": "남성",
      "weight": latest_body.weight,
      "skeletal_muscle_mass": latest_body.skeletal_muscle,
      "body_fat_mass": latest_body.body_fat,
      "calories_intake": 2000,
      "calories_burned": 500,
      "message": message
    }

    return qa.invoke(test_data)
  else:
    return {"error": "사용자의 체성분 데이터가 없습니다."}