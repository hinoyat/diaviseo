from pymongo import MongoClient
from app.config.settings import get_settings

settings = get_settings()

client = MongoClient(settings.mongo_uri)
mongo_db = client[settings.mongo_db_name]
chat_collection = mongo_db[settings.mongo_db_collection_name]

def get_mongo_collection():
    return chat_collection
