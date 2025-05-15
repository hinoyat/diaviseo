from langchain.chains import ConversationChain
from langchain_community.chat_models import ChatOpenAI
from app.services.memory.mongo_memory import get_memory
from app.services.workout.prompt.prompt_templates import workout_feedback_prompt
from app.config.settings import get_settings

settings = get_settings()

# GPT 운동 피드백 생성 함수 (메모리 포함)
def generate_workout_feedback(user_id: str) -> str:
    """
    LangChain Memory + GPT로 피드백 생성
    :param user_data: 사용자 입력 dict
    :param session_id: 사용자 고유 세션 ID (ex: 유저 ID, JWT, UUID 등)
    :return: GPT가 생성한 피드백 (3줄)
    """
    # LLM 초기화
    llm = ChatOpenAI(
        model="gpt-4",
        temperature=0.7,
        openai_api_key=settings.openai_api_key
    )

    # MongoDB 기반 메모리 생성
    memory = get_memory(user_id)

    # LangChain ConversationChain 구성
    chain = ConversationChain(
        llm=llm,
        memory=memory,
        prompt=workout_feedback_prompt,
        verbose=True
    )

    result = chain.invoke(user_id)
    return result["response"]