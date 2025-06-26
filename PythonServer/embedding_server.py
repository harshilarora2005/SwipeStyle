from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from transformers.pipelines import pipeline

app = FastAPI()

try:
    embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
    classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")
except Exception as e:
    print(f"Error loading models: {e}")
    embedding_model = None
    classifier = None

class TextInput(BaseModel):
    alt_text: str

@app.post("/embed")
def embed_text(input: TextInput):
    if embedding_model is None:
        raise HTTPException(status_code=503, detail="Embedding model not available")
    
    try:
        embedding = embedding_model.encode(input.alt_text).tolist()
        return {"embedding": embedding}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
