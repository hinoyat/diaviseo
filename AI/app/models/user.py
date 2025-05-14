from sqlalchemy import Column, Integer, String, Enum as SqlEnum, Numeric
from app.db.mysql import Base
import enum

class GoalEnum(str, enum.Enum):
    WEIGHT_LOSS = "WEIGHT_LOSS"
    WEIGHT_GAIN = "WEIGHT_GAIN"
    WEIGHT_MAINTENANCE = "WEIGHT_MAINTENANCE"

class GenderEnum(str, enum.Enum):
    M = "M"
    F = "F"

class User(Base):
    __tablename__ = "user_tb"

    user_id = Column(Integer, primary_key=True, index=True)
    name = Column(String(50), nullable=False)
    nickname = Column(String(50), nullable=False)
    goal = Column(SqlEnum(GoalEnum), nullable=False)
    height = Column(Numeric(6, 2), nullable=False)
    weight = Column(Numeric(6, 2), nullable=False)
    gender = Column(SqlEnum(GenderEnum), nullable=False)