from pydantic import BaseModel

class StartSessionRequest(BaseModel):
    chatbot_type: str

class EndSessionRequest(BaseModel):
    session_id: str