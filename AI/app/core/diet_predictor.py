import logging

from transformers import T5ForConditionalGeneration, T5TokenizerFast
from torch.quantization import quantize_dynamic
import torch
# 모델 호출

class DietModel:

    def __init__(self):

        # 저장한 가중치 불러오기(양자화된 모델 불러오기)
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model = T5ForConditionalGeneration.from_pretrained("Sseolily/pko-t5-large-v2")
        self.tokenizer = T5TokenizerFast.from_pretrained('Sseolily/pko-t5-large-v2')
        # ✅ 양자화 적용
        self.model = quantize_dynamic(
            self.model,
            {torch.nn.Linear},
            dtype=torch.qint8
        )
        self.model.eval()

        logging.info("✅ T5 양자화 모델 로드 완료")

    def generate(self, user_input):
        inputs = self.tokenizer(user_input, return_tensors="pt")
        outputs = self.model.generate(**inputs, max_new_tokens=200,early_stopping=True,no_repeat_ngram_size=2)
        return self.tokenizer.decode(outputs[0], skip_special_tokens=True)