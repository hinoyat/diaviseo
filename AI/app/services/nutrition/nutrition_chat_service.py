from langchain.chains.conversation.base import ConversationChain
from sqlalchemy.orm import Session
from app.services.memory.mongo_memory import get_memory
from app.core.llm_langchain import get_t5_langchain_llm
import logging
from app.config.log import logging_check
logging_check()

def generate_nutrition_response(user_input:str, session_id:str, user_db:Session, user_id: int):
    memory = get_memory(session_id)
    llm = get_t5_langchain_llm()
    logging.info(f"LLM Type: {type(llm).__name__}")
    chain = ConversationChain(
        llm = llm,
        memory = memory
    )
    response = chain.predict(input=user_input)
    logging.info(f"Generated response: {response[:30]}...")
    return response

# def is_uncertain(user_input:str)
#
#     return response