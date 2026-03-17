import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getPortfolio, getOrders } from '../services/api';

interface PortfolioItem {
  symbol: string;
  quantity: number;
  averageBuyPrice: number;
}

interface Order {
  id: number;
  symbol: string;
  price: number;
  quantity: number;
  type: 'BUY' | 'SELL';
  timestamp: string;
}

const Dashboard: React.FC = () => {
  const [portfolio, setPortfolio] = useState<PortfolioItem[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const { logout } = useAuth();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [portfolioRes, ordersRes] = await Promise.all([
          getPortfolio(),
          getOrders()
        ]);
        setPortfolio(portfolioRes.data);
        setOrders(ordersRes.data);
      } catch (error) {
        console.error('Failed to fetch data', error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <div className="dashboard">
      <h1>Dashboard</h1>
      <button onClick={logout}>Logout</button>

      <section>
        <h2>Portfolio</h2>
        <table>
          <thead>
            <tr>
              <th>Symbol</th>
              <th>Quantity</th>
              <th>Avg Buy Price</th>
              <th>Current Value</th>
            </tr>
          </thead>
          <tbody>
            {portfolio.map(item => (
              <tr key={item.symbol}>
                <td>{item.symbol}</td>
                <td>{item.quantity}</td>
                <td>${item.averageBuyPrice.toFixed(2)}</td>
                <td>${(item.quantity * item.averageBuyPrice).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      <section>
        <h2>Order History</h2>
        <table>
          <thead>
            <tr>
              <th>Symbol</th>
              <th>Type</th>
              <th>Price</th>
              <th>Quantity</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(order => (
              <tr key={order.id}>
                <td>{order.symbol}</td>
                <td>{order.type}</td>
                <td>${order.price.toFixed(2)}</td>
                <td>{order.quantity}</td>
                <td>{new Date(order.timestamp).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
};

export default Dashboard;
