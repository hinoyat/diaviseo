import pandas as pd



def create_user_60d_dataset(df: pd.DataFrame, gap_days=60):
  df = df.sort_values("measurement_date").reset_index(drop=True)
  samples = []

  for i in range(len(df) - gap_days):
    t = df.iloc[i]
    t_future = df.iloc[i + gap_days]

    samples.append({
      "height": t.height,
      "body_fat": t.body_fat,
      "muscle_mass": t.muscle_mass,
      "exercise_calories": t.exercise_calories,
      "current_weight": t.weight,
      "weight_60d_later": t_future.weight
    })

  return pd.DataFrame(samples)