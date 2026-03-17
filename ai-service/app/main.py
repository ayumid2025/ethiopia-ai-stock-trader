from fastapi import FastAPI
import uvicorn

app = FastAPI(title="AI Stock Trader - AI Service")

@app.get("/")
def root():
    return {"message": "AI Service is running"}

@app.get("/health")
def health():
    return {"status": "ok"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
