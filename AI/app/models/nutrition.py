from sqlalchemy import Column, Integer, Boolean, Date, ForeignKey, String, \
  Float, Enum, DateTime
from sqlalchemy.orm import relationship
import datetime
from app.db.mysql import Base

class Meal(Base):
  __tablename__ = "meal_tb"
  meal_id = Column(Integer, primary_key=True, index=True)
  user_id = Column(Integer, nullable=False)
  meal_date = Column(Date, nullable=False)
  is_meal = Column(Boolean, nullable=False)
  created_at = Column(Date, default=datetime.datetime.now, nullable=False)
  updated_at = Column(Date, default=datetime.datetime.now, onupdate=datetime.datetime.now, nullable=False)
  is_deleted = Column(Boolean, default=False, nullable=False)
  meal_times = relationship("MealTime", back_populates="meal")


class MealTime(Base):
  __tablename__ = "meal_time_tb"
  meal_time_id = Column(Integer, primary_key=True, index=True)
  meal_id = Column(Integer, ForeignKey("meal_tb.meal_id"), nullable=False)
  eating_time = Column(DateTime, nullable=False)
  meal_type = Column(
    Enum('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', name='meal_type_enum'),
    nullable=False)
  created_at = Column(Date, default=datetime.datetime.now, nullable=False)
  updated_at = Column(Date, default=datetime.datetime.now, onupdate=datetime.datetime.now, nullable=False)
  is_deleted = Column(Boolean, default=False, nullable=False)
  meal = relationship("Meal", back_populates="meal_times")
  meal_foods = relationship("MealFood", back_populates="meal_time")


class MealFood(Base):
  __tablename__ = "meal_food_tb"
  meal_food_id = Column(Integer, primary_key=True, index=True)
  meal_time_id = Column(Integer, ForeignKey("meal_time_tb.meal_time_id"),
                        nullable=False)
  food_id = Column(Integer, ForeignKey("food_information_tb.food_id"), nullable=False)
  quantity = Column(Integer, nullable=False)
  food = relationship("Food", back_populates="meal_foods")
  meal_time = relationship("MealTime", back_populates="meal_foods")


class Food(Base):
  __tablename__ = "food_information_tb"
  food_id = Column(Integer, primary_key=True, index=True)
  food_name = Column(String(50), nullable=False)
  calorie = Column(Integer, nullable=False)
  carbohydrate = Column(Float(6), nullable=False)
  protein = Column(Float(6), nullable=False)
  fat = Column(Float(6), nullable=False)
  sweet = Column(Float(6), nullable=False)
  sodium = Column(Float(6), nullable=False)
  saturated_fat = Column(Float(6), nullable=False)
  trans_fat = Column(Float(6), nullable=False)
  cholesterol = Column(Float(6), nullable=False)
  base_amount = Column(String(20), nullable=False)
  created_at = Column(DateTime, default=datetime.datetime.now, nullable=False)
  updated_at = Column(DateTime, default=datetime.datetime.now,
                      onupdate=datetime.datetime.now, nullable=False)
  deleted_at = Column(DateTime, nullable=True)
  is_deleted = Column(Boolean, default=False, nullable=False)
  meal_foods = relationship("MealFood", back_populates="food")

