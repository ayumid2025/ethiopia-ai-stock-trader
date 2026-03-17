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
import asyncio
import httpx
import random

BACKEND_URL = "http://localhost:8080/api/trading/signal"

async def generate_signal():
    # Simulate market data and decision
    price = 100 + random.uniform(-5, 5)
    sma = 100
    if price < sma:
        # Buy signal
        signal = {
            "symbol": "AAPL",
            "price": round(price, 2),
            "quantity": 10
        }
        async with httpx.AsyncClient() as client:
            await client.post(BACKEND_URL, json=signal)
        print(f"Buy signal sent: {signal}")

async def main():
    while True:
        await generate_signal()
        await asyncio.sleep(60)  # check every minute

if __name__ == "__main__":
    asyncio.run(main())
