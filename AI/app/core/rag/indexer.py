import pickle
import logging
import requests, tempfile
import os
from dotenv import load_dotenv
from pathlib import Path
from langchain_community.document_loaders import PyMuPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS
from app.config.log import logging_check

logging_check()
load_dotenv()

'''
1단계 : 문서 로드(Load Documents)
2단계 : 문서 분할(split Documents)
3단계 : 임베딩(embedding)
4단계 : DB 생성 및 저장
'''


def build_index():
    # 1) 프로젝트 루트/data
    # 현재 파일 위치에서 시작
    p = Path(__file__).resolve()
    # 'AI'라는 이름의 디렉터리가 나올 때까지 parent로 올라감
    while p.name != "AI":
        p = p.parent
    project_root = p


    data_dir = project_root / "data"
    data_dir.mkdir(parents=True, exist_ok=True)
    logging.info(f"Data directory: {data_dir}")

    # 2) 이미 색인 돼 있으면 스킵
    pkl_path  = data_dir / "nutrition_split_documents.pkl"
    faiss_path = data_dir / "nutrition_faiss_index"
    if pkl_path.exists() and faiss_path.exists():
        logging.info("❗️ 인덱스 이미 존재—재생성하지 않습니다.")
        return


    pdf_url = os.getenv("PDF_URL")
    # 1) PDF 다운로드해서 임시 파일로 저장
    resp = requests.get(pdf_url)
    resp.raise_for_status()
    with tempfile.NamedTemporaryFile(suffix=".pdf", delete=False) as tf:
        tf.write(resp.content)
        pdf_path = tf.name

    # 2) PyMuPDFLoader로 페이지별 Document 로드
    loader = PyMuPDFLoader(pdf_path)
    pages  = loader.load()

    logging.info(f"페이지(Document) 수: {len(pages)}")

    # 3) 청크 분할
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=50)
    split_documents = text_splitter.split_documents(pages)
    with open(pkl_path, "wb") as f:
        pickle.dump(split_documents, f)    # type: ignore
    logging.info("split_documents.pkl 생성 완료")

    # 4) 임베딩
    embeddings = HuggingFaceEmbeddings(
        model_name="dragonkue/snowflake-arctic-embed-l-v2.0-ko"
    )

    # 5) FAISS
    vectorstore = FAISS.from_documents(split_documents, embeddings)

    # 6) 로컬에 저장
    vectorstore.save_local(str(faiss_path))
    logging.info("nutrition_faiss_index.idx 생성 완료 : {faiss_path}")