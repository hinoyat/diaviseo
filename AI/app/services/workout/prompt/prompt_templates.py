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
        "muscle_trend",
        "message"
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

지금 위의 정보를 바탕으로 사용자의 질문에 대답하세요 
사용자 질문: {message}
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
        "remaining_calorie",
        "future_weight",
        "status"
    ],
    template="""
💬 피드백은 반드시 아래 구조로 작성해주세요:

1️⃣ [객관적 분석] 최근 측정값 기반의 냉철한 체중 예측 정보(kg, 기간), 피드백  
2️⃣ [행동 유도] 오늘 해야 할 운동 조건 또는 루틴 (⚠️ 소모해야 할 칼로리 {remaining_calorie} kcal 이상 포함하여 제시)  
3️⃣ [자기 점검] 사용자 스스로를 돌아보는 질문 (1줄)

예시:
1. 체지방률은 줄었지만 근육량도 함께 감소하고 있습니다.  
2. 오늘은 상체 근력 + 유산소 20분 이상을 병행하세요 (추가 소모 목표: 500 kcal)  
3. 최근 운동을 미룬 이유가 있다면, 무엇이었나요?

사용자 정보:
- 목표: {goal}
- 현재 체중: {weight}kg
- 최근 {days}일간 체중 변화: {weight_trend}kg
- 근육량 변화: {muscle_trend}kg
- 체중 예측 : {projected_change}kg
- 잔여 소모 칼로리: {remaining_calorie} kcal

예측 정보:
- {days}일간의 칼로리 섭취/소모 추세 기반 예상 체중 변화: {projected_change}kg
- 현재 체중을 기준으로 앞으로 {days}일 후 예상 체중: {future_weight}kg
- 상태: {status}

⚠️ 운동 루틴 제안 시 '소모해야 할 칼로리 {remaining_calorie} kcal 이상'을 기준으로 루틴을 구체적으로 제시해주세요.  
예: "전신 서킷 30분 + 유산소 20분으로 약 450kcal 소모 가능"

조언은 간결하면서도 실용적이어야 합니다. 전체 답변은 200자 이내로 작성해주세요.
"""
)