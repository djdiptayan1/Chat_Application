import React, { useState, useEffect } from "react";
import "./App.css";

const SERVER_ADDRESS = "ws:10.5.252.134"; // Change to your server address

function App() {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState("");
  const [username, setUsername] = useState("");

  const socket = new WebSocket(SERVER_ADDRESS);

  useEffect(() => {
    socket.onopen = () => {
      console.log("Connected to the server");
      setUsername(prompt("Enter your name:"));
    };

    socket.onmessage = (event) => {
      const newMessage = event.data;
      setMessages([...messages, newMessage]);
    };

    socket.onclose = () => {
      console.log("Connection closed");
    };
  }, [messages]);

  const sendMessage = () => {
    if (inputMessage.trim() !== "") {
      socket.send(`${username}: ${inputMessage}`);
      setInputMessage("");
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Chat App</h1>
        <div className="chat-box">
          <div className="message-list">
            {messages.map((message, index) => (
              <div key={index} className="message">
                {message}
              </div>
            ))}
          </div>
          <div className="input-area">
            <input
              type="text"
              placeholder="Type your message"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
            />
            <button onClick={sendMessage}>Send</button>
          </div>
        </div>
      </header>
    </div>
  );
}

export default App;
