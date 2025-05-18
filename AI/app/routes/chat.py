import datetime
from typing import Optional, Any

from fastapi import APIRouter, Header, Depends, Path, Query
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.schemas.chat import ChatRequest, FeedbackType
from app.services.chat.chat_service import chat_with_session, get_chat_history
from app.services.feedback.feedback_service import get_feedback
router = APIRouter()

@router.post("/chat/{session_id}")
def chat(session_id: str, req: ChatRequest, user_id: int = Header(default="default_user", alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))):
    return chat_with_session(session_id, req.message, user_db, health_db)

@router.get("/messages/{session_id}")
def messages(session_id: str):
    return get_chat_history(session_id)


@router.get("/feedback/{feedback_type}")
def feedback(
    user_id: int = Header(..., alias="X-USER-ID"),
    feedback_type: FeedbackType = Path(..., description="피드백 유형 (workout, weight_trend, nutrition)"),
    date: Optional[datetime.date] = Query(None, description="피드백 날짜 (YYYY-MM-DD)")
) -> str | None:
    """
    사용자의 특정 유형 피드백을 조회합니다.
    """
    feedback_date = date or datetime.date.today()
    _feedback = get_feedback(user_id, feedback_date, feedback_type)

    if feedback:
        return _feedback
    return ""