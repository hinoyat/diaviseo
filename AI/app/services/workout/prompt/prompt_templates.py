from langchain.prompts import PromptTemplate

workout_feedback_prompt = PromptTemplate(
    input_variables=[
        "gender",
        "height",
        "weight",
        "body_fat",
        "muscle_mass",
        "remaining_calorie",
        "goal",
        "weight_trend",
        "muscle_trend"
    ],
    template="""
당신은 피트니스 전문가입니다.
다음은 한 사용자의 최근 신체 데이터 및 운동 기록입니다:

사용자 정보:
- 성별: {gender}
- 키: {height} cm
- 몸무게: {weight} kg
- 체지방률: {body_fat}%
- 근육량: {muscle_mass} kg
- 잔여 소모 칼로리: {remaining_calorie} kcal
- 운동 목표: {goal}

변화 추이:
- 최근 7일간 체중 변화: {weight_trend}
- 최근 7일간 근육량 변화: {muscle_trend}

작성 조건:
- 냉철한 전문가처럼 말하되, 실행을 유도하는 톤으로 작성해주세요.
- 피드백은 반드시 **3줄**로 구성:
  1. 현재 상태에 대한 냉철한 평가 (객관적인 통찰)
  2. 오늘 실천해야 할 운동의 조건 제안
  3. 자기 점검용 질문 한 가지 제시

지금 위의 정보를 바탕으로 운동 피드백 3줄을 작성해주세요.
"""
)

weight_trend_feedback_prompt = PromptTemplate(
    input_variables=[
        "goal",
        "weight",
        "days",
        "weight_trend",
        "muscle_trend",
        "projected_change",
        "future_weight",
        "status"
    ],
    template="""
사용자 정보:
- 목표: {goal}
- 현재 체중: {weight}kg
- 최근 {days}일간 체중 변화: {weight_trend}kg
- 근육량 변화: {muscle_trend}kg
- 체중 예측 : {projected_change}kg

예측 정보:
- {days}일간의 칼로리 섭취/소모 추세 기반 예상 체중 변화: {projected_change}kg
- 현재 체중을 기준으로 앞으로 {days}일 후 예상 체중: {future_weight}kg
- 상태: {status}

위 정보를 바탕으로 사용자의 체중 변화 추이에 대한 분석과 앞으로의 조언을 제공해주세요.
조언은 간결하면서도 실용적이어야 합니다. 체중 예측 정보(kg, 기간)를 포함하여 200자 이내로 작성해주세요.
"""
)