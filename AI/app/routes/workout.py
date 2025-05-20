import datetime

import pika
from fastapi import APIRouter, Depends, Request
from fastapi.params import Header
from sqlalchemy.orm import Session

from app.db.mysql import get_session
from app.schemas.notification import NotificationMessageDto, NotificationType, \
    NotificationChannel
import json

router = APIRouter()


@router.get("/calories/remaining")
def get_remaining_calories(
    user_id: int = Header(default=None, alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    """
    사용자의 남은 칼로리를 반환합니다.

    Args:
        user_id: 사용자 ID
        user_db: 사용자 DB 세션
        health_db: 건강 DB 세션

    Returns:
        남은 칼로리 정보 (양수: 추가 섭취 가능, 음수: 초과 섭취)
    """
    from app.services.workout.workout_service import \
        calculate_remaining_calories

    remaining = calculate_remaining_calories(health_db, user_db, user_id)
    return {"remaining_calories": remaining}


@router.get("/weight/trend")
def weight_trend_feedback(
    date: datetime.datetime = None,
    days: int = 7,
    user_id: int = Header(default=None, alias="X-USER-ID"),
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    from app.services.workout.feedback import generate_trend_weight_feedback

    feedback = generate_trend_weight_feedback(user_id, user_db, health_db, days, date)
    return {"feedback": feedback}

@router.post("/notify")
async def notify(payload: NotificationMessageDto):
    try:

        from app.config.settings import get_settings
        settings = get_settings()

        credentials = pika.PlainCredentials(
            username=settings.rabbitmq_username,
            password=settings.rabbitmq_password
        )

        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=settings.rabbitmq_host,
                port=settings.rabbitmq_port,
                credentials=credentials
            )
        )
        channel = connection.channel()

        exchange_name = settings.rabbitmq_exchange
        queue_name = settings.rabbitmq_queue
        routing_key = settings.rabbitmq_routing_key

        channel.exchange_declare(exchange=exchange_name, exchange_type='topic',
                                 durable=True)
        channel.queue_declare(queue=queue_name, durable=True)
        channel.queue_bind(exchange=exchange_name, queue=queue_name,
                           routing_key='alert.push.#')

        def datetime_serializer(obj):
            if isinstance(obj, datetime.datetime):
                return obj.isoformat()  # 2023-06-15T14:30:00 형식으로 변환
            return str(obj)

        message_body = json.dumps(payload.model_dump(),
                                  default=datetime_serializer)

        channel.basic_publish(
            exchange=exchange_name,
            routing_key=routing_key,
            body=message_body.encode("utf-8"),
            properties=pika.BasicProperties(
                delivery_mode=2,
                content_type='application/json'
            )
        )

        connection.close()
        return {"status": "queued", "data": payload.model_dump()}

    except Exception as e:
        from fastapi import HTTPException
        raise HTTPException(status_code=500, detail=f"Queue publish failed: {str(e)}")


@router.post("/notify-all-users")
async def notify_all_users(
    user_db: Session = Depends(get_session('user')),
    health_db: Session = Depends(get_session('health'))
):
    try:
        # 모든 사용자 목록 가져오기
        from app.repositories.user_repository import get_all_users
        users = get_all_users(user_db)
        print(users)
        # 피드백 결과와 전송 결과를 저장할 리스트
        results = []

        # RabbitMQ 연결 설정
        from app.config.settings import get_settings
        settings = get_settings()

        credentials = pika.PlainCredentials(
            username=settings.rabbitmq_username,
            password=settings.rabbitmq_password
        )

        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=settings.rabbitmq_host,
                port=settings.rabbitmq_port,
                credentials=credentials
            )
        )
        channel = connection.channel()

        exchange_name = settings.rabbitmq_exchange
        queue_name = settings.rabbitmq_queue
        routing_key = settings.rabbitmq_routing_key

        channel.exchange_declare(exchange=exchange_name,
                                 exchange_type='topic',
                                 durable=True)
        channel.queue_declare(queue=queue_name, durable=True)
        channel.queue_bind(exchange=exchange_name, queue=queue_name,
                           routing_key='alert.push.#')

        from app.services.workout.feedback import \
            generate_trend_weight_feedback

        # 각 사용자의 운동 피드백 생성 및 알림 전송
        for user in users:
            user_id = user.user_id

            # 운동 피드백 생성
            feedback = generate_trend_weight_feedback(user_id, user_db,
                                                      health_db, days=7, date=datetime.datetime.now())

            # 알림 메시지 생성
            notification = NotificationMessageDto(
                userId=user_id,
                notificationType=NotificationType.EXERCISE,
                message=feedback,
                sentAt=datetime.datetime.now(),
                channel=NotificationChannel.PUSH
            )

            # RabbitMQ로 메시지 전송
            def datetime_serializer(obj):
                if isinstance(obj, datetime.datetime):
                    return obj.isoformat()
                return str(obj)

            message_body = json.dumps(notification.model_dump(),
                                      default=datetime_serializer)

            channel.basic_publish(
                exchange=exchange_name,
                routing_key=routing_key,
                body=message_body.encode("utf-8"),
                properties=pika.BasicProperties(
                    delivery_mode=2,
                    content_type='application/json'
                )
            )

            results.append({
                "user_id": user_id,
                "feedback": feedback,
                "status": "queued"
            })

        connection.close()
        return {"status": "success", "results": results}

    except Exception as e:
        from fastapi import HTTPException
        raise HTTPException(status_code=500,
                            detail=f"Notification process failed: {str(e)}")