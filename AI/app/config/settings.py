from pydantic_settings import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    # OpenAI
    openai_api_key: str

    # MySQL
    db_name: str
    db_user: str
    db_password: str
    db_host: str
    db_port: str

    # MongoDB
    mongo_uri: str
    mongo_db_name: str
    mongo_db_collection_name: str

    # Eureka
    eureka_server: str
    eureka_app_name: str
    eureka_instance_port: int
    eureka_instance_host: str
    eureka_health_check_url: str
    eureka_renewal_interval_in_secs: int
    eureka_duration_in_secs: int

    # 설정 파일 위치 (.env)
    model_config = {
        "env_file": ".env",
        "env_file_encoding": "utf-8"
    }

@lru_cache
def get_settings():
    return Settings()
