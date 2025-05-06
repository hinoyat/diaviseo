from fastapi import APIRouter, Header, Request
from app.body.models.request import ChatRequest
from app.body.services.chat_service import handle_chat, get_chat_history

router = APIRouter()

@router.post("")
async def chat_endpoint(request: Request, body: ChatRequest, x_user_id: str = Header(...)):
    """
    사용자 메시지를 받아 LangChain을 통해 응답 생성하고 저장합니다.
    """
    return await handle_chat(user_id=x_user_id, message=body.message)

@router.get("/history")
async def chat_history_endpoint(x_user_id: str = Header(...)):
    """
    사용자별 채팅 로그를 role 구분 구조로 반환합니다.
    """
    return await get_chat_history(user_id=x_user_id)