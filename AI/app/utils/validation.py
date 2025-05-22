import re

def is_valid(text: str) -> bool:
    # 1) 비어 있거나 너무 짧으면 False
    if not text or len(text) < 10:
        return False

    # 2) 중복 문장 검사
    sentences = re.split(r'[.?!]\s*', text.strip())
    if len(sentences) >= 2 and sentences[0] == sentences[1]:
        return False

    # 3) 금칙어 검사
    forbidden = ["에러", "모르겠어요", "죄송"]
    if any(f in text for f in forbidden):
        return False

    # 4) 지나치게 길면 False
    if len(text) > 2000:
        return False

    return True
