'use strict'

import React from 'react'
import ReactDOM from 'react-dom'
import { Link, Router, Route,IndexRoute  } from 'react-router'

import App from './App';
import RealtimePage from './page/RealtimePage';
import EshopStatusPage from './page/EshopStatusPage';
import EshopResponsePage from './page/EshopResponsePage';
import OccResponsePage from './page/OccResponsePage';
import OccStatusPage from './page/OccStatusPage';
import PageViewPage from './page/PageViewPage';

ReactDOM.render((
  <Router>
    <Route path="/" 				component={App}>
	  <Route path="/realtime" 	component={RealtimePage}/>
      <Route path="/eshopstatus" 	component={EshopStatusPage}/>
      <Route path="/occstatus" 	component={OccStatusPage}/>
	  <Route path="/eshopresponse" 	component={EshopResponsePage}/>
	  <Route path="/occresponse" 	component={OccResponsePage}/>
	  <Route path="/pageview" 	component={PageViewPage}/>
      <IndexRoute component={RealtimePage}/>
    </Route>
  </Router>
), document.getElementById('dashboard-container'))
