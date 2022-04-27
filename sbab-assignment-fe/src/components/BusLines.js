import BusLine from "./BusLine"

const BusLines = ({ busLines }) => {
    return (
    <>
      {busLines.map((busLine) => (
      <BusLine key={busLine.busLine} busLine={busLine}/>
      ))}
    </>
  )
}

export default BusLines
