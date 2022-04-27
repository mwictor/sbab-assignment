import React from 'react';
import Header from './components/Header';
import BusLines from './components/BusLines';
import { useState, useEffect } from 'react'

function App() {
  const[busLines, setBusLines] = useState([])

  useEffect(() => {
    const initialGetData = async () => {
      const dataFromServer = await getData()
      
      if(!dataFromServer.length) {
        setBusLines(dataFromServer.busLines)
      }
    }
    initialGetData()
  }, [])

  const getDataManually = async () => {
    const dataFromServer = await getData()
    if(!dataFromServer.length) {
      setBusLines(dataFromServer.busLines)
    }
  }

  const getData = async () => {
    const res = await fetch('http://localhost:8080/lines/topTen')
    const data = await res.json()
    console.log(data)

    return data
  }

  return (
    <div className="container">
      <Header onGet={getDataManually} />
      {busLines.length > 0 ? (
        <BusLines 
        busLines={busLines}
        />
        ) : ('Currently no data in backend, refresh in a while to test again')}
    </div>
    
  );
}

export default App;