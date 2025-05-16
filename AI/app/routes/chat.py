from fastapi import APIRouter, Header, Depends
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.schemas.chat import ChatRequest
from app.services.chat.chat_service import chat_with_session, get_chat_history

router = APIRouter()

@router.post("/chat/{session_id}")
def chat(session_id: str, req: ChatRequest, user_id: int = Header(default="default_user", alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))):
    return chat_with_session(session_id, req.message, user_db, health_db)

@router.get("/messages/{session_id}")
def messages(session_id: str):
    return get_chat_history(session_id)