from typing import List

from fastapi import FastAPI, HTTPException, Path
from pydantic import BaseModel
from autogluon.tabular import TabularPredictor
from pathlib import Path as FilePath
import pandas as pd

app = FastAPI()

# --------------------
# Pydantic schema
# --------------------
class FutureWeightInput(BaseModel):
    height: float
    body_fat: float
    muscle_mass: float
    exercise_calories: float
    current_weight: float


def load_user_or_global_model(user_id: int) -> TabularPredictor:
    import os
    models_dir = os.getenv("MODELS_DIR", "models")
    global_model_path = os.getenv("GLOBAL_MODEL_PATH", "global_60d_model")

    user_model_path = FilePath(f"{models_dir}/user_{user_id}")
    if user_model_path.exists():
        return TabularPredictor.load(str(user_model_path))
    return TabularPredictor.load(f"{models_dir}/{global_model_path}")


def train_autogluon_model(user_id: int, df: pd.DataFrame):
    import os
    models_dir = os.getenv("MODELS_DIR", "models")
    path = f"{models_dir}/user_{user_id}"
    predictor = TabularPredictor(label="weight_60d_later", path=path).fit(df)
    predictor.save()


@app.post("/predict-future-weight")
def predict_weight(
    future_weight: FutureWeightInput,
    user_id: int
):
    try:
        model = load_user_or_global_model(user_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Model loading error: {e}")

    df = pd.DataFrame([future_weight.model_dump()])
    pred = model.predict(df).iloc[0]
    return {"user_id": user_id, "predicted_weight_60d": round(float(pred), 2)}



class TrainingSample(BaseModel):
    height: float
    body_fat: float
    muscle_mass: float
    exercise_calories: float
    current_weight: float
    weight_60d_later: float


@app.post("/train-model/{user_id}")
async def api_train_model(
    user_id: int = Path(..., description="사용자 ID"),
    samples: List[TrainingSample] = ...
):
    try:
        df = pd.DataFrame([s.model_dump() for s in samples])

        import os
        from dotenv import load_dotenv

        # .env 파일 로드
        load_dotenv()

        # 최소 샘플 수 환경 변수에서 가져오기
        min_samples = int(os.getenv("MIN_TRAINING_SAMPLES", "10"))

        if len(df) < min_samples:
            raise HTTPException(
                status_code=400,
                detail=f"모델 학습을 위한 데이터가 부족합니다. (최소 {min_samples}개 필요)"
            )

        train_autogluon_model(user_id, df)

        return {
            "detail": f"User {user_id}의 모델 학습 완료",
            "data_count": len(df)
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"모델 학습 중 오류 발생: {str(e)}"
        )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8100, reload=True)

