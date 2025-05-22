from typing import Optional
from enum import Enum
from datetime import date

from pydantic import BaseModel

class ChatRequest(BaseModel):
    message: str

class FeedbackType(str, Enum):
    workout = "workout"
    weight_trend = "weight_trend"
    nutrition = "nutrition"
    general = "general"

class FeedbackRequest(BaseModel):
    date: Optional[date] = None  # YYYY-MM-DD 형식 자동 파싱