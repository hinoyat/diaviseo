from datetime import datetime

from langchain_openai import ChatOpenAI
from langchain.chains import ConversationChain
from sqlalchemy.orm import Session

from app.db.mongo import TimestampedMongoHistory, get_chat_collection
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
    print("[DEBUG] 셋션!:", session_id)
    print("[DEBUG] chatbot_type:",get_chat_collection().find_one(
            {"session_id": session_id}))

    try:
        memory = get_memory(session_id)
        session_info = get_chat_collection().find_one(
            {"session_id": session_id})

        print("[DEBUG] session_info:", session_info)
        print("[DEBUG] chatbot_type:", session_info.get("chatbot_type") if session_info else None)

        # 운동 챗봇 타입인 경우 운동 피드백 제공
        if session_info and session_info.get("chatbot_type") == "workout":
            logging.info("운동챗봇으로 진입함")
            from app.services.workout.feedback import generate_workout_feedback
            user_id = session_info.get("user_id")
            feedback = generate_workout_feedback(user_id, session_id, user_db, health_db)
            return {
                "response": f"운동 피드백: {feedback}",
                "timestamp": datetime.utcnow().isoformat()
            }

        # 식단 챗봇 타입인 경우
        if session_info and session_info.get("chatbot_type") == "nutrition":
            logging.info("식단챗봇으로 진입함")
            user_id = session_info.get("user_id")
            response = generate_nutrition_response(message, session_id, user_db, user_id)
            return {
                "response": response,
                "timestamp": datetime.utcnow().isoformat()
            }


        # 일반 대화 처리
        chain = ConversationChain(
            llm=llm,
            memory=memory,
            verbose=False
        )
        reply = chain.predict(input=message)
        return {
            "response": reply,
            "timestamp": datetime.utcnow().isoformat()
        }
    finally:
        user_db.close()
        health_db.close()


def get_chat_history(session_id: str):
    """채팅 히스토리를 가져옵니다."""
    memory = get_memory(session_id)

    # LangChain의 Message 객체 목록을 반환합니다
    chat_history = memory.load_memory_variables({}).get("history", "")
    return chat_history