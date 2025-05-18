import datetime
from enum import Enum

from langchain_openai import ChatOpenAI
from langchain.chains import ConversationChain
from sqlalchemy.orm import Session

from app.db.mongo import get_chat_collection, \
    get_chat_history_collection
from app.services.memory.mongo_memory import get_memory
from app.config.settings import get_settings
from app.services.nutrition.nutrition_chat_service import generate_nutrition_response
from app.config.log import logging_check
import logging
logging_check()

settings = get_settings()

llm = ChatOpenAI( model="gpt-4o-mini", temperature=0.7, api_key=settings.openai_api_key )


def chat_with_session(session_id: str, message: str, user_db: Session,
    health_db: Session) -> dict:
    """
    세션 ID에 해당하는 챗봇과 대화를 처리합니다.

    Args:
        session_id: 챗봇 세션 ID
        message: 사용자 메시지
        user_db: 사용자 DB 세션
        health_db: 건강 DB 세션

    Returns:
        챗봇 응답과 타임스탬프를 포함한 딕셔너리
    """
    try:
        memory = get_memory(session_id)
        session_info = get_chat_collection().find_one(
            {"session_id": session_id})
        chat_history_collection = get_chat_history_collection()

        # 사용자 메시지 저장
        _save_user_message(chat_history_collection, message, session_id)

        # 챗봇 응답 생성
        if session_info and session_info.get("chatbot_type") == "workout":
          ai_message = _handle_workout_chat(session_id, session_info, message,
                                            user_db, health_db)
        elif session_info and session_info.get("chatbot_type") == "nutrition":
          ai_message = _handle_nutrition_chat(session_id, session_info, message,
                                              user_db)
        else:
          ai_message = _handle_general_chat(memory, message,
                                            session_id=session_id)

        # AI 응답 저장
        chat_history_collection.insert_one(ai_message)
        return ai_message

    finally:
        user_db.close()
        health_db.close()


def _save_user_message(chat_history_collection, message: str, session_id: str) -> None:
    """사용자 메시지를 저장합니다."""
    user_message = create_message_data(role=MessageRole.USER, content=message, session_id=session_id)
    chat_history_collection.insert_one(user_message)


def _handle_workout_chat(session_id: str, session_info: dict, message: str,
    user_db: Session, health_db: Session) -> dict:
    """운동 챗봇 대화를 처리합니다."""
    from app.services.workout.feedback import generate_workout_feedback
    user_id = session_info.get("user_id")
    feedback = generate_workout_feedback(user_id, session_id, message, user_db,
                                         health_db)
    return create_message_data(role=MessageRole.ASSISTANT, content=feedback, session_id=session_id)


def _handle_nutrition_chat(session_id: str, session_info: dict, message: str,
    user_db: Session) -> dict:
  """식단 챗봇 대화를 처리합니다."""
  user_id = session_info.get("user_id")
  response = generate_nutrition_response(message, session_id, user_db, user_id)
  return create_message_data(role=MessageRole.ASSISTANT, content=response,
                             session_id=session_id)
def _handle_general_chat(memory, message: str, session_id:str) -> dict:
    """일반 챗봇 대화를 처리합니다."""
    chain = ConversationChain(
        llm=llm,
        memory=memory,
        verbose=False
    )
    reply = chain.predict(input=message)
    return create_message_data(role=MessageRole.ASSISTANT, content=reply, session_id=session_id)


def get_chat_history(session_id: str):
    """채팅 히스토리를 가져옵니다."""
    chat_history_collection = get_chat_history_collection()
    # session_id로 모든 채팅 메시지를 검색하고 시간순으로 정렬
    return list(chat_history_collection.find(
        {"session_id": session_id}
    ).sort("timestamp", 1))



class MessageRole(Enum):
    """
    채팅 메시지의 역할을 정의하는 열거형 클래스
    """
    USER = "user"
    ASSISTANT = "assistant"


def create_message_data(role: MessageRole, content: str, session_id: str) -> dict:

    from bson import ObjectId
    import datetime

    # ObjectId 생성
    id_obj = ObjectId()

    return {
        "role": role.value,
        "content": content,
        "timestamp": datetime.datetime.now(),
        "session_id": session_id,
        "_id": str(id_obj)  # ObjectId를 문자열로 변환하여 직렬화 가능하게 만듦
    }