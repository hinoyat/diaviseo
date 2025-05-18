from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains.retrieval import create_retrieval_chain
from langchain_openai import ChatOpenAI
from langchain_core.prompts import PromptTemplate
from pathlib import Path
from app.config.settings import get_settings
settings = get_settings()

from app.core.t5.t5_wrapping import get_t5_langchain_llm


qa = None
retriever = None

'''
5단계 indexer에서 생성한 문서 가져오기
6단계 Retreiver

예시 질문 
- 자세히 에너지 섭취와 소비의 균형을 맞추려면 어떤 요소들을 고려해야 하나요?
- 자세히 건강한 아침 식단 구성 방법을 3가지 제안해줘.
- 자세히 동물성 단백질과 식물성 단백질의 차이점은 무엇인가요
- 자세히 하루 세 끼 식단 예시를 짜줘.
- 자세히 과잉 섭취 시 주의해야 할 영양소는 무엇이고, 그 이유는 무엇인가요?
- 자세히 탄수화물을 건강하게 섭취하려면 어떤 식품을 우선적으로 선택해야 하나요?
- 자세히 정제 탄수화물(흰 빵, 흰 쌀) 대신 대체할 수 있는 식단 예시는?
- 자세히 과도한 탄수화물 섭취가 건강에 미치는 부작용은 무엇인가요?
- 자세히 동물성 단백질 식품 중 포화지방·콜레스테롤을 줄일 수 있는 선택지는?
- 자세히 근육량 증가를 위해 섭취하면 좋은 고단백 식품 Top 5는?
'''
def init_rag():
    global qa, retriever

    embeddings = HuggingFaceEmbeddings(
        model_name="dragonkue/snowflake-arctic-embed-l-v2.0-ko"
    )

    # 'AI' 기준 절대 경로
    p = Path(__file__).resolve()
    while p.name != "AI":
        p = p.parent
    project_root = p

    data_dir = project_root / "data" / "nutrition_faiss_index"


    db = FAISS.load_local(
        str(data_dir),
        embeddings,
        allow_dangerous_deserialization=True)
    retriever = db.as_retriever(search_kwargs={"k": 4})

    llm = ChatOpenAI(
        model="gpt-4o",  # 또는 gpt-3.5-turbo
        temperature=0.3,
        api_key=settings.openai_api_key
    )

    prompt = PromptTemplate.from_template("""
    당신은 식단 전문가입니다. 아래 문서를 참고하여 사용자 질문에 정확하고 간결하게 답해주세요.

    문서 내용:
    {context}

    질문:
    {input}

    답변:
    """)

    doc_chain = create_stuff_documents_chain(llm=llm, prompt=prompt)

    qa = create_retrieval_chain(
        retriever=retriever,
        combine_docs_chain=doc_chain
    )

