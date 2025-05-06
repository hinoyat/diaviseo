from pydantic import BaseModel
from datetime import datetime

class ChatMessage(BaseModel):
    role: str  # "user" or "assistant"
    content: str
    timestamp: datetime

class ChatHistoryResponse(BaseModel):
    history: list[ChatMessage]