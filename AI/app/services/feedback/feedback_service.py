import datetime
from typing import Any

from app.repositories.feedback_repository import find_feedback
from app.schemas.chat import FeedbackType


def get_feedback(user_id: int, feedback_date: datetime.date,
    feedback_type: FeedbackType) -> Any | None:
  """
  사용자의 피드백 텍스트를 조회합니다.

  Args:
      user_id: 사용자 ID
      feedback_date: 피드백 날짜
      feedback_type: 피드백 유형

  Returns:
      피드백 텍스트 또는 None (피드백이 없는 경우)
  """
  feedback = find_feedback(user_id, feedback_date, feedback_type)
  if feedback:
    return feedback.get('feedback_text')
  return None
