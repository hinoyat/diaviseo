from pydantic_settings import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    openai_api_key: str
    mongo_uri: str
    db_name: str
    db_user: str
    db_password: str
    db_host: str
    db_port: str
    mongo_db_name: str

    model_config = {"env_file": ".env", "env_file_encoding": "utf-8"}

@lru_cache
def get_settings():
    return Settings()
