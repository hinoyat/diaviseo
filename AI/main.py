from contextlib import asynccontextmanager
from fastapi import FastAPI
from py_eureka_client.eureka_client import EurekaClient
from app.routes.workout import router as workout_router

from app.config.settings import get_settings

# 설정 가져오기
settings = get_settings()

# EurekaClient 초기화
eureka_client = EurekaClient(
    eureka_server=settings.eureka_server,
    app_name=settings.eureka_app_name,
    instance_port=settings.eureka_instance_port,
    instance_host=settings.eureka_instance_host,
    health_check_url=settings.eureka_health_check_url,
    renewal_interval_in_secs=settings.eureka_renewal_interval_in_secs,
    duration_in_secs=settings.eureka_duration_in_secs
)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 애플리케이션 시작 시 Eureka에 등록
    eureka_client.start()
    yield
    # 애플리케이션 종료 시 Eureka에서 해제
    eureka_client.stop()

# FastAPI 앱 생성 (한 번만)
app = FastAPI(lifespan=lifespan)

# 라우터 추가 (한 번만)
app.include_router(workout_router, prefix="/api/chatbot", tags=["Chat"])

# 헬스체크 엔드포인트
@app.get("/health")
def health():
    return {"status": "UP"}

# 기타 라우팅
@app.get("/")
def read_root():
    return {"message": "Hello from FastAPI!"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)