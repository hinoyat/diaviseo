# database.py
from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import sessionmaker
from app.body.config.settings import get_settings

settings = get_settings()

DB_USER = settings.db_user
DB_PASSWORD = settings.db_password
DB_HOST = settings.db_host
DB_PORT = settings.db_port
DB_NAME = settings.db_name

DATABASE_URL = f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
# 엔진 생성
engine = create_engine(DATABASE_URL)

# 세션 생성
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 모델 선언을 위한 Base 클래스
Base = declarative_base()

# DB 세션 의존성 함수
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()