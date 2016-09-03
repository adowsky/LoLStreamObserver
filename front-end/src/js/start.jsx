"use strict";

import App from './application/App.jsx';
import React           from 'react';
import ReactDOM        from 'react-dom';

ReactDOM.render((
    <App host={document.location.host}/>
), document.querySelector('#application'));