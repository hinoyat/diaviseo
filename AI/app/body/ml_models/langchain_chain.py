from langchain_community.chat_message_histories import MongoDBChatMessageHistory
from langchain.chains import ConversationChain
from langchain_openai import ChatOpenAI
from app.body.config.settings import get_settings




def build_chat_chain(user_id: str) -> ConversationChain:
    """ 사용자 ID를 기반으로 대화형 AI 체인을 생성하는 함수
    Args:
        user_id (str): 대화 기록을 구분하기 위한 고유 사용자 ID

    Returns:
        ConversationChain: 사용자와 대화할 수 있는 대화형 AI 체인
    """
    settings = get_settings()
    # MongoDB를 사용하여 사용자별 대화 기록을 저장할 메모리 객체 생성
    message_history = MongoDBChatMessageHistory(
        connection_string=settings.mongo_uri,
        database_name=settings.mongo_db_name,
        collection_name="chat_logs",
        session_id=user_id,
    )

    # 메시지 기록을 사용하여 ConversationBufferMemory 생성
    from langchain.memory import ConversationBufferMemory
    memory = ConversationBufferMemory(chat_memory=message_history,
                                      return_messages=True)

    # OpenAI GPT-4o 모델 초기화
    # temperature=0.7은 응답의 창의성 수준 설정 (높을수록 더 창의적)
    llm = ChatOpenAI(
        model="gpt-4o-mini",
        temperature=0.3,
        api_key=settings.openai_api_key
    )
    # 언어 모델과 메모리를 사용하여 대화 체인 생성
    # verbose=True로 설정하여 체인의 작업을 상세하게 출력
    chain = ConversationChain(
        llm=llm,
        memory=memory,
        verbose=True
    )

    return chain