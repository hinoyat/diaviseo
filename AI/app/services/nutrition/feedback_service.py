from requests import Session
from datetime import date
import logging
from app.repositories.nutrition_repository import generate_nutrition_info
from app.services.nutrition.health_calculator_service import get_tdee
from app.services.nutrition.generate_advice_service import generate_advice

def generate_nutrition_feedback(user_db:Session, health_db:Session, userId:int, datetime:date):

    # 1. DB 에서 meal_tb에 오늘날짜 Db가 있어? 없으면
    data  = generate_nutrition_info(userId,datetime, health_db)
    logging.info("데이터",data)


    if data["calorie"] == 0:
        return "🥗아직 영양 정보가 없습니다. 식단을 등록해주세요"
    tdee = get_tdee(user_db, health_db, userId, 'light')
    logging.info("tdee",tdee)

    # 2. 있으면 DB 확인 -> calculation 호출
    #       -> 계산 값을 받아서 조언 답변 생성 -> 챗봇에게 전달 -> 응답

    response = generate_advice(data, tdee)

    return response