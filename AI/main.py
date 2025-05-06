from fastapi import FastAPI
# from app.body.controller.body_controller import router as body_router
from app.body.controller.chat_controller import router as chat_router

app = FastAPI()
# app.include_router(body_router, prefix="/body", tags=["body"])
app.include_router(chat_router, prefix="/chat", tags=["Chat"])

if __name__ == "__main__":
    import uvicorn
    # 8000번 포트에서 main 모듈의 app 객체를 실행
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)