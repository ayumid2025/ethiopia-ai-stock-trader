import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const WS_URL = process.env.REACT_APP_WS_URL || 'http://localhost:8080/ws';

export const useWebSocket = (topic: string) => {
  const [messages, setMessages] = useState<any>(null);

  useEffect(() => {
    const socket = new SockJS(WS_URL);
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
      stompClient.subscribe(topic, (message) => {
        setMessages(JSON.parse(message.body));
      });
    });

    return () => {
      stompClient.disconnect();
    };
  }, [topic]);

  return messages;
};
