from pydantic import BaseModel
from datetime import datetime

from typing import List, Optional
from sqlalchemy import Column, Integer, String, DateTime, Boolean
from app.body.config.body_database import Base

class Body(Base):
    __tablename__ = "body_tb"

    body_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, index=True)
    weight = Column(String(255))
    body_fat = Column(String(255))
    skeletal_muscle = Column(String(255))
    input_type = Column(String(50))
    is_deleted = Column(Boolean, default=False)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)
    deleted_at = Column(DateTime)




class BodyComposition(BaseModel):
  age: int
  gender: str  # "남성" / "여성"
  weight: float
  skeletal_muscle: float
  body_fat: float
  calories_intake: int
  calories_burned: int
  goal: str  # "감량", "유지", "증량"

  @classmethod
  def from_multiple_tables(cls, user_data, body_data, nutrition_data,
      goal_data):
    """
        여러 테이블에서 데이터를 합쳐 BodyComposition 객체를 생성합니다.

        Args:
            user_data: 사용자 정보 테이블 데이터 (나이, 성별 등)
            body_data: 체성분 데이터 (체중, 골격근량, 체지방 등)
            nutrition_data: 영양 섭취 데이터 (칼로리 섭취량 등)
            goal_data: 목표 정보 데이터 (목표 유형 등)

        Returns:
            BodyComposition 객체
        """
    return cls(
        age=user_data["age"],
        gender=user_data["gender"],
        weight=body_data["weight"],
        skeletal_muscle=body_data["skeletal_muscle"],
        body_fat=body_data["body_fat"],
        calories_intake=nutrition_data["calories_intake"],
        calories_burned=nutrition_data["calories_burned"],
        goal=goal_data["goal_type"]
    )


class WeightRecord(BaseModel):
  date: datetime
  weight:float

class ExerciseRecord(BaseModel):
  date: datetime
  exerciseName: str
  exerciseTime: int
  calories_burned: Optional[float] = None

class UserHealthData(BaseModel):
  user_id: int
  goal_weight: Optional[float] = None
  weight_history: List[WeightRecord] = []
  exercise_history: List[ExerciseRecord] = []
