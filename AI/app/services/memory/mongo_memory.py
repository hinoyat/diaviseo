from langchain_community.chat_message_histories import MongoDBChatMessageHistory
from langchain.memory import ConversationBufferMemory
from app.config.settings import get_settings


def get_memory(session_id: str):
    settings = get_settings()

    message_history = MongoDBChatMessageHistory(
        connection_string=settings.mongo_uri,
        session_id=session_id,
        database_name=settings.mongo_db_name,
        collection_name=settings.mongo_db_chat_collection_name
    )
    memory = ConversationBufferMemory(
        memory_key="history",
        return_messages=True,
        chat_memory=message_history
    )
    return memory