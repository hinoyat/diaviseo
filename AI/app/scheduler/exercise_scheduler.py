from apscheduler.schedulers.background import BackgroundScheduler
from fastapi import Depends
import asyncio
import datetime
import logging
# from app.routes.workout import notify_all_users
#
#
# def start_scheduler():
#     scheduler = BackgroundScheduler()
#
#     def call_notify_all_users():
#         try:
#             from app.db.mysql import get_session
#
#             # 데이터베이스 세션 관리를 위한 context manager 사용
#             with next(get_session("user")) as user_db, next(
#                 get_session("health")) as health_db:
#                 asyncio.run(
#                     notify_all_users(user_db=user_db, health_db=health_db))
#
#             logging.info(
#                 f"[Scheduled notify_all_users()] {datetime.datetime.now()}")
#         except Exception as e:
#             logging.error(f"[Error in scheduled task] {e}", exc_info=True)
#
#
#     scheduler.add_job(call_notify_all_users, 'cron', hour=19, minute=0)
#     scheduler.start()
#
#     return scheduler