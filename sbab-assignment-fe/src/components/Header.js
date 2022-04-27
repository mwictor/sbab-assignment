import Button from "./Button";
const Header = ({onGet}) => {
  return (
    <header className='header'>
        <h1>Top ten buslines with the most bus stops</h1>
        <Button onGet={onGet}/>
    </header>
  )
}

export default Header;