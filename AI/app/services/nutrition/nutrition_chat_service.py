from sqlalchemy.orm import Session
from app.core.t5.t5_wrapping import get_t5_langchain_llm
from app.core.rag import rag
from app.config.log import logging_check
from app.utils.validation import is_valid

import logging
logging_check()

# 0) 영양 정보 키워드 목록
ALLOWED_TOPICS = [
    "칼로리", "열량", "단백질", "탄수화물", "지방", "성분", "영양",
    "비타민", "무기질", "영양성분", "20대", "30대", "40대", "60대", "65세 이상","50대", "10대", "평균",
    "미네랄", "무기질", "칼슘", "철분", "아연", "마그네슘", "나트륨",
    "콜레스테롤", "당류", "설탕", "식이섬유", "섬유질", "당", "탄단지",
    "포화지방", "불포화지방", "트랜스지방",
    "오메가", "오메가3", "오메가6",
    "성분", "함량", "제공량", "1회 제공량", "1인분", "g당",
    "퍼센트", "%", "권장량", "권장섭취량", "섭취량", "하루섭취량", "총섭취량",
    "기준치", "RDA", "영양정보",
    "식단", "메뉴", "식사", "음식"
]


def generate_nutrition_response(user_input:str, session_id:str, user_db:Session, user_id: int):
    logging.info("✅ generate_nutrition_response 호출됨")

    user_input = user_input.lower()
    # 1) '자세히'가 들어간 질문이면 → RAG
    if "자세히" in user_input:
        if rag.qa is None:
            raise RuntimeError("❌ qa가 초기화되지 않았습니다. init_rag()를 먼저 호출하세요.")

        rag_result = rag.qa.invoke({"input": user_input})
        source_note = "📘 출처: 질병관리청 건강정보 자료"
        return f"{rag_result['answer']} \n\n {source_note}"


    # 2) '자세히' 없을 땐, 허용된 주제(영양 정보 키워드)만 처리
    if not any(topic in user_input for topic in ALLOWED_TOPICS):
        return (
            "저는 일반적인 영양 정보(칼로리·단백질·탄수화물·지방·20대 평균 열량 등)만 제공해 드릴 수 있습니다. "
            "보다 폭넓은 답변을 원하시면 질문에 ‘자세히’라는 키워드를 포함해 주세요."
        )


    # 2) 아니면 일반 T5 응답
    logging.info("✅ 일반 T5 모델 사용")
    llm = get_t5_langchain_llm()

    for _ in range(2):  # 최대 2회 생성 시도
        resp = llm.invoke(input=user_input)
        if is_valid(resp):
            return resp

    return llm.invoke(input=user_input)


def generate_nutrition_chat(user_input:str, session_id:str):
    logging.info("✅ generate_nutrition_chat 호출됨")

    # 1) '자세히'가 들어간 질문이면 → RAG
    if "자세히" in user_input:
        if rag.qa is None:
            raise RuntimeError("❌ qa가 초기화되지 않았습니다. init_rag()를 먼저 호출하세요.")

        rag_result = rag.qa.invoke({"input": user_input})
        source_note = "📘 출처: 질병관리청 건강정보 자료"
        return f"{rag_result['answer']}\n\n{source_note}"


    # 2) '자세히' 없을 땐, 허용된 주제(영양 정보 키워드)만 처리
    if not any(topic in user_input for topic in ALLOWED_TOPICS):
        return (
            "저는 일반적인 영양 정보(칼로리·단백질·탄수화물·지방·20대 평균 열량 등)만 제공해 드릴 수 있습니다. "
            "보다 폭넓은 답변을 원하시면 질문에 ‘자세히’라는 키워드를 포함해 주세요."
        )


    # 2) 아니면 일반 T5 응답
    llm = get_t5_langchain_llm()
    response = llm.invoke(input=user_input)
    logging.info("✅ 일반 T5 모델 사용")
    return response