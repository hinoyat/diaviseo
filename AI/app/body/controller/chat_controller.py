from fastapi import APIRouter, Header, Request
from app.body.models.request import ChatRequest
from app.body.services.chat_service import handle_chat, get_langchain_chat_history
from app.body.services.calories_service import compute_to_burn, predict_calories
from app.body.models.response import CalorieResponse
from fastapi import HTTPException

from app.common.responses import create_response

router = APIRouter()

@router.post("/exercise")
async def chat_endpoint(request: Request, body: ChatRequest, x_user_id: str = Header(...)):
    """
    사용자 메시지를 받아 LangChain을 통해 응답 생성하고 저장합니다.
    """
    result = await handle_chat(user_id=x_user_id, message=body.message)
    return create_response(data=result)

@router.get("/exercise/history")
async def chat_history_endpoint(x_user_id: str = Header(...)):
    """
    사용자별 채팅 로그를 role 구분 구조로 반환합니다.
    """
    history = await get_langchain_chat_history(user_id=x_user_id)
    return create_response(data=history)
@router.get("/predict-calories", response_model=CalorieResponse)
async def predict_daily_calories(x_user_id: str = Header(...)):
    try:
        result = await predict_calories(user_id=x_user_id)
        return create_response(data=result)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception:
        raise HTTPException(status_code=500, detail="서버 내부 오류가 발생했습니다.")