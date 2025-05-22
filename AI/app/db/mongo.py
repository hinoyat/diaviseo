from langchain_core.messages import HumanMessage, AIMessage
from pymongo import MongoClient
from app.config.settings import get_settings
from langchain_community.chat_message_histories import MongoDBChatMessageHistory
from langchain_core.messages import BaseMessage
settings = get_settings()


class TimestampedMongoHistory(MongoDBChatMessageHistory):
    def __init__(self, *args, chatbot_type: str = None, **kwargs):
        super().__init__(*args, **kwargs)
        self.chatbot_type = chatbot_type

    def add_user_message(self, content: str) -> None:
        # 생성 시각을 ISO 문자열로 추가
        import datetime
        timestamp = datetime.datetime.now
        msg = HumanMessage(
            content=content,
            additional_kwargs={"created_at": timestamp}
        )
        super().add_message(msg)

    def add_ai_message(self, content: str) -> None:
        import datetime
        timestamp = datetime.datetime.now
        msg = AIMessage(
            content=content,
            additional_kwargs={"created_at": timestamp}
        )
        super().add_message(msg)

client = MongoClient(settings.mongo_uri, authSource=settings.mongo_db_name)
mongo_db = client[settings.mongo_db_name]
chat_collection = mongo_db[settings.mongo_db_chat_collection_name]
chat_history_collection = mongo_db[settings.mongo_db_chat_history_collection_name]
feedback_collection = mongo_db[settings.mongo_db_feedback_collection_name]

def get_chat_collection():
    return chat_collection

def get_chat_history_collection():
    return chat_history_collection

def get_feedback_collection():
    return feedback_collection