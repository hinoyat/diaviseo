from transformers import pipeline
from langchain.llms import HuggingFacePipeline
import app.core.models as models

def get_t5_langchain_llm():
    tokenizer = models.diet_model.tokenizer
    model = models.diet_model.model

    pipe = pipeline("text2text-generation", model=model, tokenizer=tokenizer, max_length=200)
    return HuggingFacePipeline(pipeline=pipe)