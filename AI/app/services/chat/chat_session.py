
from app.config.settings import get_settings
from app.db.mongo import get_chat_collection, TimestampedMongoHistory, settings
from langchain_community.chat_message_histories import MongoDBChatMessageHistory
from langchain.schema import AIMessage

def generate_session_id(user_id: str, chatbot_type: str) -> str:
    import datetime
    ts = datetime.datetime.now()
    return f"session_{user_id}_{chatbot_type}_{ts}"

def get_welcome_message(chatbot_type: str) -> str:
    if chatbot_type == "workout":
        return "🏋️ 운동 피드백 챗봇에 오신 것을 환영합니다. 어떤 목표를 도와드릴까요?"
    elif chatbot_type == "nutrition":
        return "🥗 식단 피드백 챗봇입니다. 오늘 섭취한 식단을 기록해볼까요?"
    return "안녕하세요! 챗봇과 대화를 시작합니다."

def create_session(user_id: int, chatbot_type: str) -> dict:
    session_id = generate_session_id(str(user_id), chatbot_type)
    welcome = get_welcome_message(chatbot_type)

    # 1. 세션 메타 정보 저장
    settings = get_settings()

    import datetime
    session_data = {
        "session_id": session_id,
        "user_id": user_id,
        "chatbot_type": chatbot_type,
        "started_at": datetime.datetime.now()
    }
    get_chat_collection().insert_one(session_data)

    return {
        "session_id": session_id,
        "welcome_message": welcome
    }


def end_session(session_id: str):
    import datetime
    message_history = TimestampedMongoHistory(
        connection_string=settings.mongo_uri,
        database_name=settings.mongo_db_name,  # 설정에서 DB 이름 사용
        collection_name=settings.mongo_db_chat_collection_name,
        session_id=session_id,
    )
    message_history.clear()
    # 세션 메타 정보 삭제
    get_chat_collection().update_one(
        {"session_id": session_id},
        {"$set": {"is_saved": False, "ended_at": datetime.datetime.now()}}
    )

    return {
        "session_id": session_id,
        "message": "세션이 성공적으로 종료되었습니다"
    }


def get_sessions_by_user(user_id):
    results = get_chat_collection().find({"user_id": user_id}).sort("started_at", -1)

    return [
        {
            "session_id": doc["session_id"],
            "chatbot_type": doc["chatbot_type"],
            "started_at": doc["started_at"].isoformat(),
            "ended_at": doc.get("ended_at", None),
            "is_saved": doc.get("is_saved", None)
        }
        for doc in results
    ]