import asyncio
import httpx
import os
import pandas as pd
import ta  # technical analysis library

API_KEY = os.getenv("AI_API_KEY", "your-secret-key")
BACKEND_URL = os.getenv("BACKEND_URL", "http://localhost:8080/api/ai/signal")
ALPHA_VANTAGE_KEY = os.getenv("ALPHA_VANTAGE_KEY", "demo")

async def fetch_price(symbol: str) -> float:
    async with httpx.AsyncClient() as client:
        url = f"https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey={ALPHA_VANTAGE_KEY}"
        resp = await client.get(url)
        data = resp.json()
        return float(data["Global Quote"]["05. price"])

async def fetch_historical(symbol: str) -> pd.DataFrame:
    async with httpx.AsyncClient() as client:
        url = f"https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol={symbol}&apikey={ALPHA_VANTAGE_KEY}&outputsize=compact"
        resp = await client.get(url)
        data = resp.json()
        df = pd.DataFrame.from_dict(data["Time Series (Daily)"], orient="index")
        df = df.astype(float)
        df.columns = ["open", "high", "low", "close", "volume"]
        df = df.sort_index()
        return df

async def generate_signal(symbol: str):
    # Fetch historical data
    df = await fetch_historical(symbol)
    # Compute indicators
    df['sma_20'] = ta.trend.sma_indicator(df['close'], window=20)
    df['rsi'] = ta.momentum.rsi(df['close'], window=14)
    last_close = df['close'].iloc[-1]
    last_sma = df['sma_20'].iloc[-1]
    last_rsi = df['rsi'].iloc[-1]

    # Decision logic
    action = None
    if last_close < last_sma and last_rsi < 30:  # oversold
        action = "BUY"
    elif last_close > last_sma and last_rsi > 70:  # overbought
        action = "SELL"

    if action:
        signal = {
            "symbol": symbol,
            "price": round(last_close, 2),
            "quantity": 10,  # fixed for demo
            "action": action
        }
        async with httpx.AsyncClient() as client:
            headers = {"X-API-Key": API_KEY}
            await client.post(BACKEND_URL, json=signal, headers=headers)
        print(f"Signal sent: {signal}")

async def main():
    symbols = ["AAPL", "GOOGL", "TSLA"]
    while True:
        for sym in symbols:
            try:
                await generate_signal(sym)
            except Exception as e:
                print(f"Error processing {sym}: {e}")
        await asyncio.sleep(60)  # check every minute

if __name__ == "__main__":
    asyncio.run(main())
