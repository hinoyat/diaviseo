from sqlalchemy.orm import Session

import app.core.models as models

def generate_nutrition_response(user_input:str, user_db:Session, user_id: int):
    response=models.diet_model.generate(user_input)
    return response

# def is_uncertain(user_input:str)
#
#     return response