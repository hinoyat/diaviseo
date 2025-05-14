from sqlalchemy import Column, Integer, Numeric, Date, Enum as SqlEnum
from app.db.mysql import Base
import enum

class InputTypeEnum(str, enum.Enum):
    MANUAL = "MANUAL"
    OCR = "OCR"

class BodyInfo(Base):
    __tablename__ = "body_tb"

    body_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False)  # 외래키 관계만 숫자로 유지

    input_type = Column(SqlEnum(InputTypeEnum), nullable=False)
    height = Column(Numeric(6, 2), nullable=True)
    weight = Column(Numeric(6, 2), nullable=False)
    body_fat = Column(Numeric(6, 2), nullable=True)
    muscle_mass = Column("skeletal_muscle", Numeric(6, 2), nullable=True)
    measurement_date = Column(Date, nullable=False)

class Exercise(Base):
    __tablename__ = "exercise_tb"

    exercise_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False)
    exercise_type_id = Column(Integer, nullable=False)
    exercise_date = Column(Date, nullable=False)
    exercise_time = Column(Integer, nullable=False)
    exercise_calorie = Column(Integer, nullable=False)
    created_at = Column(Date, nullable=False)
    updated_at = Column(Date, nullable=False)
    deleted_at = Column(Date, nullable=True)
    is_deleted = Column(Integer, nullable=False, default=0)