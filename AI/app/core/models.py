from app.core.diet_predictor import DietModel

diet_model = None

# 모델 올리기
def init_model():
    global diet_model

    diet_model = DietModel()
