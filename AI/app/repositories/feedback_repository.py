import datetime
from enum import Enum

from app.db.mongo import get_feedback_collection


class FeedbackType(Enum):
  """피드백 유형을 정의하는 열거형 클래스"""
  WORKOUT = "workout"
  WEIGHT_TREND = "weight_trend"
  NUTRITION = "nutrition"
  GENERAL = "general"

def insert_feedback(user_id: int, feedback: str, feedback_type: FeedbackType,
    feedback_date: datetime.datetime):
  """피드백을 삽입하거나 이미 존재하는 경우 업데이트합니다."""
  from datetime import datetime, time

  feedback_collection = get_feedback_collection()

  start = datetime.combine(feedback_date.date(), time.min)
  end = datetime.combine(feedback_date.date(), time.max)

  filter_query = {
    "user_id": user_id,
    "feedback_date": {"$gte": start, "$lte": end},
    "feedback_type": feedback_type.value
  }

  doc = {
    "user_id": user_id,
    "feedback_text": feedback,
    "feedback_date": feedback_date,
    "feedback_type": feedback_type.value
  }

  feedback_collection.update_one(
      filter_query,
      {"$set": doc},
      upsert=True
  )

def find_feedback(user_id:int, feedback_date: datetime.date, feedback_type: FeedbackType):
  from datetime import datetime, time, timedelta
  # 피드백이 없으면 빈 문자열 반환.
  feedback_collection = get_feedback_collection()
  # 날짜의 시작과 끝을 계산
  start = datetime.combine(feedback_date, time.min)  # 00:00:00
  end = datetime.combine(feedback_date, time.max)  # 23:59:59.999999
  docs = feedback_collection.find_one(
      {"user_id": user_id, "feedback_date": {"$gte": start, "$lte": end},
       "feedback_type": feedback_type.value},
      sort=[("feedback_date", -1)]  # -1은 내림차순 정렬을 의미
  )
  return docs


