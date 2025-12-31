import React from 'react';
import './App.css';
import DocumentUpload from './components/DocumentUpload';

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <h1>Intelligent Document Processing</h1>
            </header>
            <main>
                <DocumentUpload />
            </main>
        </div>
    );
}

export default App;
