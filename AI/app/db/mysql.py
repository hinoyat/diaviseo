from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from app.config.settings import get_settings

settings = get_settings()

# 각 데이터베이스별 URL
DATABASE_URLS = {
    "user": f"mysql+pymysql://{settings.db_user}:{settings.db_password}@{settings.db_host}:{settings.db_port}/user_db",
    "health": f"mysql+pymysql://{settings.db_user}:{settings.db_password}@{settings.db_host}:{settings.db_port}/health_db",
}

# 각 DB에 대해 엔진/세션 생성
engines = {
    key: create_engine(url, echo=True)
    for key, url in DATABASE_URLS.items()
}

sessions = {
    key: sessionmaker(autocommit=False, autoflush=False, bind=engine)
    for key, engine in engines.items()
}

# 공용 Base
Base = declarative_base()

# FastAPI 의존성 주입용 세션 함수
def get_session(db_key: str):
    def _get_db():
        db = sessions[db_key]()
        try:
            yield db
        finally:
            db.close()
    return _get_db