import random
import app.core.models as models

# 학습시킨 데이터 기반 코드 짜기
# 1) 템플릿
templates = {
    "no_data": [
        "🥗 아직 식단 정보가 없습니다. 식단을 등록해주세요.",
    ],
    "calorie_over": [
        "오늘 섭취 칼로리({total_cal:.0f}kcal)가 목표 ({tdee:.0f}kcal)를 초과했습니다.",
        "칼로리가 목표({tdee:.0f}kcal)보다 높아요! 오늘 섭취량은 {total_cal:.0f}kcal 입니다.",
        "칼로리 과다: {total_cal:.0f}kcal / {tdee:.0f}kcal. 조금만 더 조절해보세요."
    ],
    "calorie_under": [
        "오늘 섭취 칼로리({total_cal:.0f}kcal)는 목표 ({tdee:.0f}kcal) 이내입니다.",
        "목표 칼로리({tdee:.0f}kcal) 내에서 {total_cal:.0f}kcal 잘 드셨어요!",
        "칼로리 관리 성공! 섭취 {total_cal:.0f}kcal, 목표 {tdee:.0f}kcal."
    ],
    "macro_carb": [
        "🍚 탄수화물 비율이 좀 높아요. 단백질 식품을 추가해 보세요.",
        "🥖 탄수화물 섭취가 과다합니다. 통곡물·채소로 대체해보시길.",
        "🍞 탄수화물 점검: 다음 끼니엔 단백질·지방을 보충해보세요."
    ],
    "macro_prot": [
        "🍗 단백질이 충분해요. 다음 끼니엔 탄수화물을 살짝 추가해보세요.",
        "🥩 단백질 섭취가 높습니다. 곡물·채소로 균형을 맞춰보세요.",
        "🍖 단백질 과다: 채소·곡류로 탄수화물을 보충해보세요."
    ],
    "macro_fat": [
        "🥑 지방 비율이 높습니다. 채소와 저지방 단백질을 추천드려요.",
        "🧈 지방 섭취가 조금 많아요. 기름기 적은 요리로 대체해보세요.",
        "🥓 지방 과다: 단백질과 채소로 균형을 맞춰보세요."
    ],
    "sugar_high": [
        "🍬 당류 섭취가 권장량을 초과했어요. 가당음료는 줄이시면 좋아요.",
        "⚠️ 과도한 당류! 디저트 대신 과일로 단맛을 보충해보세요.",
    ],
    "chol_high": [
        "🥚 콜레스테롤이 높습니다. 기름진 음식 대신 불포화지방을 선택하세요.",
        "⚠️ 콜레스테롤 과다! 달걀 노른자 대신 흰자 위주로 드셔보세요.",
    ]
}

# def generate_advice(data: dict, tdee: float) -> str:
#     """
#     data: {
#       "calorie": float,
#       "carbohydrate": float,
#       "protein": float,
#       "fat": float,
#       "cholesterol": float,
#       "sugar": float,
#     }
#     tdee: 목표 일일 에너지 소비량 (kcal)
#     """
#     total_cal = float(data.get("calorie", 0))
#     carb_g    = float(data.get("carbohydrate", 0))
#     prot_g    = float(data.get("protein", 0))
#     fat_g     = float(data.get("fat", 0))
#     chol_mg   = float(data.get("cholesterol", 0))
#     sugar_g   = float(data.get("sugar", 0))
#
#     msgs = []
#
#     # 1) 칼로리 피드백
#     if total_cal == 0:
#         return random.choice(templates["no_data"])
#     if total_cal > tdee:
#         msgs.append(
#             random.choice(templates["calorie_over"])
#                   .format(total_cal=total_cal, tdee=tdee)
#         )
#     else:
#         msgs.append(
#             random.choice(templates["calorie_under"])
#                   .format(total_cal=total_cal, tdee=tdee)
#         )
#
#     # 2) 탄단지 비율 계산 (칼로리 기준)
#     cal_from_carb = carb_g * 4
#     cal_from_prot = prot_g * 4
#     cal_from_fat  = fat_g  * 9
#     cal_sum_macro = cal_from_carb + cal_from_prot + cal_from_fat or 1  # 0 방지
#     if cal_sum_macro == 0:
#         cal_sum_macro = 1
#
#     carb_pct = cal_from_carb / cal_sum_macro * 100
#     prot_pct = cal_from_prot / cal_sum_macro * 100
#     fat_pct  = cal_from_fat  / cal_sum_macro * 100
#
#     msgs.append(
#         f"📊 탄단지 비율: 탄수화물 {carb_pct:.0f}%, 단백질 {prot_pct:.0f}%, 지방 {fat_pct:.0f}%"
#     )
#
#     # 3) 탄단지 균형 피드백 (권장 50:30:20)
#     tgt = {"carb":50, "prot":30, "fat":20}
#     diffs = {
#         "carb": abs(carb_pct - tgt["carb"]),
#         "prot": abs(prot_pct - tgt["prot"]),
#         "fat":  abs(fat_pct  - tgt["fat"])
#     }
#     if max(diffs.values()) <= 10:
#         msgs.append("🎉 탄단지 비율이 대체로 균형을 이루고 있습니다.")
#     else:
#         worst = max(diffs, key=diffs.get)
#         if worst == "carb":
#             msgs.append(random.choice(templates["macro_carb"]))
#         elif worst == "prot":
#             msgs.append(random.choice(templates["macro_prot"]))
#         else:
#             msgs.append(random.choice(templates["macro_fat"]))
#
#     # 4) 당류 피드백 (권장당류 = TDEE의 10%)
#     rec_sugar = tdee * 0.10
#     sugar_pct = sugar_g / rec_sugar * 100 if rec_sugar else 0
#     msgs.append(f"🍬 당류 섭취: {sugar_g:.0f}g ({sugar_pct:.0f}% of 권장 {rec_sugar:.0f}g)")
#     if sugar_pct > 100:
#         msgs.append(random.choice(templates["sugar_high"]))
#     elif sugar_pct > 80:
#         msgs.append(random.choice(templates["sugar_high"]))
#
#     # 5) 콜레스테롤 피드백 (권장 300mg)
#     msgs.append(f"🥚 콜레스테롤: {chol_mg:.0f}mg")
#     if chol_mg > 300:
#         msgs.append(random.choice(templates["chol_high"]))
#
#     # 최종 조합
#     response = " ".join(msgs)
#     return response


# 모델 호출
def generate_advice(data:dict, tdee: float) -> str:
    # 전체 데이터
    total_cal = data.get("calorie", 0)
    carb_g    = data.get("carbohydrate", 0)
    prot_g    = data.get("protein", 0)
    fat_g     = data.get("fat", 0)
    chol_mg   = data.get("cholesterol", 0)
    sugar_g   = data.get("sugar", 0)

    # 탄수화물 비율
    cal_from_carb = carb_g * 4
    cal_from_prot = prot_g * 4
    cal_from_fat  = fat_g  * 9
    cal_sum_macro = cal_from_carb + cal_from_prot + cal_from_fat or 1  # 0 방지

    carb_pct = round((cal_from_carb / cal_sum_macro),3)
    prot_pct = round((cal_from_prot / cal_sum_macro),3)
    fat_pct  = round((cal_from_fat  / cal_sum_macro),3)

    '''
    권장 칼로리: 2298 kcal, 탄수화물 비율: 0.396, 단백질 비율: 0.373, 지방 비율: 0.23, 콜레스테롤: 296 mg, 당류: 433 g, 탄단지 조언
    '''


    prompt = f"권장칼로리: {total_cal} kcal, 탄수화물 비율: {carb_pct}, 단백질 비율: {prot_pct}, 지방 비율: {fat_pct}, 콜레스테롤: {chol_mg} mg, 당류: {sugar_g} g, 탄단지 조언"
    response = models.diet_model.generate(prompt)
    return response