import React from 'react'

const Button = ({onGet}) => {
  return (
    <button className='btn' onClick={onGet}>Refresh data</button>
  )
}

export default Button