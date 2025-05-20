from datetime import datetime
from enum import Enum

from pydantic import BaseModel


class NotificationChannel(str, Enum):
    PUSH = "PUSH"
    INAPP = "INAPP"
    BOTH = "BOTH"

class NotificationType(str, Enum):
    DIET = "DIET"
    EXERCISE = "EXERCISE"
    WEIGHT = "WEIGHT"
    INACTIVE = "INACTIVE"

class NotificationMessageDto(BaseModel):
  userId: int
  notificationType: NotificationType
  message: str
  sentAt: datetime
  channel: NotificationChannel
