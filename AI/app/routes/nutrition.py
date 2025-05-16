from datetime import date

from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.services.nutrition.nutrition_chat_service import generate_nutrition_response
from app.schemas.nutrition import ChatRequest
from app.services.nutrition.feedback_service import generate_nutrition_feedback
router = APIRouter()

@router.post("/nutrition_chat")
def start_nutrition_chat(user_input: ChatRequest, user_db:Session = Depends(get_session("user")), user_id: int = Header(default="default_user", alias="X-USER-ID") ):
    response = generate_nutrition_response(user_input=user_input.user_input, user_db=user_db, user_id=user_id )
    return {"answer": response}

@router.post("/nutrition_feedback")
def nutrition_feedback(datetime: date, user_db:Session = Depends(get_session("user")), health_db:Session = Depends(get_session("health")), user_id: int = Header(default="default_user", alias="X-USER-ID")):
    '''
    1. 프롬프트 만들기
        a. advice 호출
        b. DB 확인
        c. DB 내용 토대로 계산
    2. 프롬프트 입력하여 조언생성

    '''

    response = generate_nutrition_feedback(user_db,health_db,user_id, datetime)
    return {"answer": response}
