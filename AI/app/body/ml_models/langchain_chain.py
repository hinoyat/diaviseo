from langchain_community.chat_message_histories import MongoDBChatMessageHistory
from langchain.chains import ConversationChain
from langchain_openai import ChatOpenAI
from app.body.config.settings import get_settings
from langchain.prompts import PromptTemplate


def build_chat_chain(user_id: str) -> ConversationChain:
    """ 사용자 ID를 기반으로 대화형 AI 체인을 생성하는 함수
    Args:
        user_id (str): 대화 기록을 구분하기 위한 고유 사용자 ID

    Returns:
        ConversationChain: 사용자와 대화할 수 있는 대화형 AI 체인
    """
    settings = get_settings()
    # MongoDB를 사용하여 사용자별 대화 기록을 저장할 메모리 객체 생성
    message_history = MongoDBChatMessageHistory(
        connection_string=settings.mongo_uri,
        database_name=settings.mongo_db_name,
        collection_name=settings.mongo_db_collection_name,
        session_id=user_id,
    )

    # 메시지 기록을 사용하여 ConversationBufferMemory 생성
    from langchain.memory import ConversationBufferMemory
    memory = ConversationBufferMemory(chat_memory=message_history,
                                      return_messages=True)

    # OpenAI GPT-4o 모델 초기화
    llm = ChatOpenAI(
        model="gpt-4o-mini",
        temperature=0.1,
        api_key=settings.openai_api_key
    )
    # 언어 모델과 메모리를 사용하여 대화 체인 생성
    chain = ConversationChain(
        llm=llm,
        memory=memory,
        verbose=True
    )

    return chain


async def get_exercise_advice(to_burn: float, user_info: dict, favorite_exercises: list[str] = None) -> str:
  settings = get_settings()

  llm = ChatOpenAI(
      model="gpt-4o-mini",
      temperature=0,
      api_key=settings.openai_api_key
  )

  if favorite_exercises is None:
    favorite_exercises = ["수영", "조깅", "자전거", "필라테스", "요가"]

  prompt = PromptTemplate(
      input_variables=["to_burn", "age", "gender", "current_weight", "height",
                       "favorite_exercises"],
      template=(
        "당신은 개인의 신체 정보를 완벽히 반영하는 **사용자 맞춤형 퍼스널 트레이너**입니다.\n"
        "사용자 정보: {age}세 {gender}, 현재 체중 {current_weight:.1f} kg, 키 {height:.1f} cm\n"
        "즐겨하는 운동: {favorite_exercises}\n"
        "오늘 추가로 소모해야 할 칼로리: {to_burn:.0f} kcal\n\n"
        "#### 작성 지침\n"
        "0. 요청한 시간의 대한민국 계절, 사용자의 나이, 신체 조건을 고려하여 적합한 운동을 추천합니다.\n"
        "1. 반드시 사용자의 즐겨하는 운동 목록에서만 2가지 서로 다른 운동 옵션을 제시합니다.\n"
        "2. 각 옵션은 **운동명, 시간, 강도, 예상 소모 칼로리**를 포함합니다.\n"
        "3. 각 옵션에는 다음 요소를 포함합니다:\n"
        "   - **자율성** → 사용자가 선택하거나 주도하는 느낌의 표현\n"
        "   - **유능감** → 사용자의 능력을 인정하는 긍정 형용사)\n"
        "   - **관계성** → 함께하는 뉘앙스를 녹입니다.\n"
        "4. 체중이 높은 경우 무릎 부담이 적은 운동을 우선 제안합니다.\n"
        "5. 가능한 선호하는 운동 목록에서 옵션을 제공합니다.\n"
        "6. 절대 추가 설명·이유·팁을 붙이지 마세요.\n\n"
        "##### 출력 예시\n"
        "오늘 선택 가능한 운동 옵션:\n"
        "1. 30분 동안 격렬하게 중강도 실내 사이클링 (약 400kcal 소모)\n"
        "2. 45분간 활기차게 걷는 조깅 (약 350kcal 소모)\n"
      )
  )
  chain = prompt | llm
  out = await chain.ainvoke({
    "to_burn": to_burn,
    "age": user_info["age"],
    "gender": user_info["gender"],
    "current_weight": user_info["current_weight"],
    "height": user_info.get("height"),
    "favorite_exercises": ", ".join(favorite_exercises)
  })
  return out.content