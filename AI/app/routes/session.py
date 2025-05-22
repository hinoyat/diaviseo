from fastapi import APIRouter
from fastapi.params import Header

from app.schemas.session import StartSessionRequest, EndSessionRequest
from app.services.chat import chat_session

router = APIRouter()

@router.post("/start-session")
def start_session(req: StartSessionRequest, user_id: int = Header(default="default_user", alias="X-USER-ID")):
    try:
        from app.services.chat.chat_session import create_session
        result = create_session(user_id, req.chatbot_type)
        return result
    except Exception as e:
        from fastapi import HTTPException
        raise HTTPException(status_code=500, detail=f"세션 생성 실패: {str(e)}")

@router.delete("/end-session")
def end_session(req: EndSessionRequest):
    return chat_session.end_session(req.session_id)

@router.get("/chats")
def list_user_sessions(user_id: int = Header(default=None, alias="X-USER-ID")):
    if user_id is None:
        from fastapi import HTTPException
        raise HTTPException(status_code=400, detail="X-USER-ID 헤더가 필요합니다")
    return chat_session.get_sessions_by_user(user_id)