from sqlalchemy.orm import Session
from app.services.memory.mongo_memory import get_memory
from app.core.t5.t5_wrapping import get_t5_langchain_llm
from app.core.rag import rag
from app.config.log import logging_check

import logging
logging_check()

def generate_nutrition_response(user_input:str, session_id:str, user_db:Session, user_id: int):
    logging.info("✅ generate_nutrition_response 호출됨")
    memory = get_memory(session_id)

    # 1) '자세히'가 들어간 질문이면 → RAG
    if "자세히" in user_input:
        if rag.qa is None:
            raise RuntimeError("❌ qa가 초기화되지 않았습니다. init_rag()를 먼저 호출하세요.")

        rag_result = rag.qa.invoke({"input": user_input})
        source_note = "📘 출처: 질병관리청 건강정보 자료"
        return f"{rag_result['answer']} {source_note}"

    # 2) 아니면 일반 T5 응답
    logging.info("✅ 일반 T5 모델 사용")
    llm = get_t5_langchain_llm()
    return llm.invoke(input=user_input)
