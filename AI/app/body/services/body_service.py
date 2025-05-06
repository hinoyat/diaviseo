from fastapi import HTTPException
from datetime import datetime

from app.body.ml_models import body_chain
from app.body.models.entity import Body
from app.body.config.body_database import get_db
from app.body.models.response import ChatMessage

def get_body_by_user_id(user_id: int):
    db = next(get_db())
    bodies = db.query(Body).filter(Body.user_id == user_id, Body.is_deleted == False).order_by(Body.created_at.desc()).all()
    if not bodies:
        raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다")
    return bodies


def process_chat(user_id: int, message: str):
    bodies = get_body_by_user_id(user_id)
    response_content = body_chain.run_chat(bodies, message)

    return ChatMessage(
        role="assistant",
        content=response_content,
        timestamp=datetime.now()
    )