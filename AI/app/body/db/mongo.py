from pymongo import MongoClient
from app.body.config.settings import get_settings

settings = get_settings()
_mongo_client = MongoClient(settings.mongo_uri)

def get_mongo_client() -> MongoClient:
    """
    이미 생성된 _mongo_client를 반환합니다.
    """
    return _mongo_client

def get_chat_collection():
    """
    fitness 데이터베이스의 chat_logs 컬렉션을 반환합니다.
    """
    db = _mongo_client["fitness"]
    return db["chat_logs"]