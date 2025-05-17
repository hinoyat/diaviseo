from sqlalchemy.orm import Session
from app.services.memory.mongo_memory import get_memory
from app.core.t5_wrapping import get_t5_langchain_llm
from langchain.prompts import PromptTemplate
from langchain.chains.conversation.base import ConversationChain

def generate_nutrition_response(user_input:str, session_id:str, user_db:Session, user_id: int):
    print("✅ generate_nutrition_response 호출됨")
    memory = get_memory(session_id)
    llm = get_t5_langchain_llm()
    response = llm.invoke(input=user_input)

    return response

# def is_uncertain(user_input:str)
#
#     return response