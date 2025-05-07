from datetime import datetime

from langchain_community.chat_message_histories import MongoDBChatMessageHistory

from app.body.config.settings import get_settings
from app.body.ml_models.langchain_chain import build_chat_chain
from app.body.models.entity import BodyComposition

# TODO: MYSQL에서 가져와야 돼 !!!!
def get_user_profile(user_id: str) -> BodyComposition:
    return BodyComposition(
        age=30,
        gender="남성",
        weight=75.0,
        skeletal_muscle=32.0,
        body_fat=18.0,
        calories_intake=2200,
        calories_burned=500,
        goal="감량"
    )

async def handle_chat(user_id: str, message: str):
    user_profile = get_user_profile(user_id)

    # LangChain 체인 호출
    prompt = f"""
    당신은 **자기결정성이론(SDT)에 기반한 AI 퍼스널 피트니스 코치**입니다.

    [사용자 프로필]
    - 나이: {user_profile.age}세
    - 성별: {user_profile.gender}
    - 체중: {user_profile.weight} kg
    - 골격근량: {user_profile.skeletal_muscle} kg
    - 체지방량: {user_profile.body_fat} kg
    - 하루 섭취 kcal: {user_profile.calories_intake}
    - 하루 소비 kcal: {user_profile.calories_burned}
    - 목표: {user_profile.goal}

    [응답 지침]  
    1. **사용자 질문 유형**에 따라 다음 두 가지 포맷 중 하나를 택해 120 단어 이내로 답한다.  
       **① 운동 추천** — 1 문장으로 *운동명·시간·강도* 제시. 문장 안에 자연스럽게
       • “언제·어디서·무엇을·얼마나” 형식의 **구현 의도**를 넣어라.  
       • 관절 부담을 고려해 안전도가 높은 운동부터 추천하고, 운동 후 **즉시 긍정 보상**을 한 가지 묘사한다.  
       **② 일반 피트니스 Q&A** — 영양·회복·부상 예방 등 어떤 질문이든 **사용자 정보를 반영**해 2–3 문장으로 핵심만 설명한다. 답변에는  
    2. 과도한 전문용어·다중 옵션·장황한 배경 설명은 피한다(인지 부하 최소화).
    3. **칭찬은 최대 두 음절 감탄사**(예: “좋아요!”, “굿!”, “멋져요!”)로만 한 번 사용하고,  
   “당신의 유능함”처럼 장황한 추상 표현은 쓰지 않는다.  
    4. 건강‧안전 관련 근거가 불확실할 때는 “의료 전문가 상담”을 안내한다.  

    [예시]  
    “오늘 저녁 7시, 20 분간 스텝퍼를 밟아 300 kcal를 태워보는 건 어때요?!”
    아래 질문에 답해주세요:
    {message}
    """

    chain = build_chat_chain(user_id)
    response = chain.invoke(prompt)

    return {"response": response["response"]}


async def get_langchain_chat_history(user_id: str):
    settings = get_settings()
    message_history = MongoDBChatMessageHistory(
        connection_string=settings.mongo_uri,
        database_name=settings.mongo_db_name,
        collection_name=settings.mongo_db_collection_name,
        session_id=user_id,
    )

    # LangChain 형식의 메시지를 가져옴
    messages = message_history.messages

    # 응답 형식으로 변환
    response_history = []
    for msg in messages:
        response_history.append({
            "role": msg.type,  # 'human' 또는 'ai'
            "content": msg.content,
            "timestamp": msg.additional_kwargs.get("timestamp") if hasattr(msg,
                                                                           "additional_kwargs") else None
        })

    return {"history": response_history}