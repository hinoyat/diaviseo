from datetime import datetime
from typing import Any, Dict, Optional


def create_response(
    data: Any = None,
    message: str = "성공",
    status: str = "OK"
) -> Dict[str, Any]:
    """
    표준화된 API 응답 형식을 생성합니다.

    Args:
        data: 응답 데이터
        message: 응답 메시지
        status: 응답 상태

    Returns:
        표준화된 응답 딕셔너리
    """
    return {
        "timestamp": datetime.now().isoformat(),
        "status": status,
        "message": message,
        "data": data
    }