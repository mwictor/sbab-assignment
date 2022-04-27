const BusLine = ({ busLine }) => {
  return (
    <div>
        <h3>{busLine.busLine}</h3> 
        <ol>
          {busLine.listOfStopPoints.map((busStop, index) => (
            <li>{busStop}</li>
          ))}
        </ol>
    </div>
  )
}

export default BusLine