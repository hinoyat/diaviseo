from datetime import datetime
from app.body.ml_models.langchain_chain import build_chat_chain
from app.body.db.mongo import get_chat_collection
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
    당신은 전문 피트니스 코치입니다.

    사용자 정보:
    - 나이: {user_profile.age}세
    - 성별: {user_profile.gender}
    - 체중: {user_profile.weight}kg
    - 골격근량: {user_profile.skeletal_muscle}kg
    - 체지방량: {user_profile.body_fat}kg
    - 하루 섭취 칼로리: {user_profile.calories_intake}kcal
    - 하루 소모 칼로리: {user_profile.calories_burned}kcal
    - 목표: {user_profile.goal}

    아래 질문에 답해주세요:
    {message}
    """

    chain = build_chat_chain(user_id)
    response = chain.run(prompt)

    # MongoDB에 대화 저장
    chat_collection = get_chat_collection()
    chat_collection.insert_one({
        "user_id": user_id,
        "timestamp": datetime.utcnow(),
        "message": message,
        "response": response
    })

    return {"response": response}

async def get_chat_history(user_id: str):
    chat_collection = get_chat_collection()
    history = list(chat_collection.find(
        {"user_id": user_id}
    ).sort("timestamp", 1))

    response_history = []
    for h in history:
        response_history.append({
            "role": "user",
            "content": h["message"],
            "timestamp": h["timestamp"]
        })
        response_history.append({
            "role": "assistant",
            "content": h["response"],
            "timestamp": h["timestamp"]
        })

    return {"history": response_history}