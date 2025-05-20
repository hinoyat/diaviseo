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
    mongo_db_chat_collection_name: str
    mongo_db_chat_history_collection_name: str
    mongo_db_feedback_collection_name: str
    # Eureka
    eureka_server: str
    eureka_app_name: str
    eureka_instance_port: int
    eureka_instance_host: str
    eureka_health_check_url: str
    eureka_renewal_interval_in_secs: int
    eureka_duration_in_secs: int

    # minio
    pdf_url: str
    index_pkl: str
    index_faiss: str

    model_service_url: str
    predict_model_path: str
    train_model_path: str

    # RabbitMQ 설정
    rabbitmq_host: str
    rabbitmq_port: int
    rabbitmq_username: str
    rabbitmq_password: str
    rabbitmq_exchange: str = "alert_exchange"
    rabbitmq_queue: str = "notification-queue"
    rabbitmq_routing_key: str = "alert.push.notification"

    # 설정 파일 위치 (.env)
    model_config = {
        "env_file": ".env",
        "env_file_encoding": "utf-8"
    }

@lru_cache
def get_settings():
    return Settings()
