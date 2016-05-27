'use strict'

import React from 'react'
import { Link, Router, Route,RouteHandler } from 'react-router'

var NavigationMenu = React.createClass({
  render: function () {
	return (
		<aside className="main-sidebar">
			{/* sidebar: style can be found in sidebar.less */}
			<section className="sidebar" >
				{/* sidebar menu: : style can be found in sidebar.less */}
				<ul className="sidebar-menu">
					<li className="header">MAIN NAVIGATION</li>
					<li>
					  <Link to="/realtime" activeClassName="active" activeStyle={{ background: '#1e282c', borderLeftColor:'#3c8dbc'}}>
						<i className="fa fa-bar-chart-o"></i> <span>RealTime(new)</span>
					  </Link>
					</li>
					<li>
					  <Link to="/eshopstatus" activeClassName="active" activeStyle={{ background: '#1e282c', borderLeftColor:'#3c8dbc'}}>
						<i className="fa fa-dashboard"></i> <span>Eshop Access Status</span>
					  </Link>
					</li>
					<li>
					  <Link to="/occstatus" activeClassName="active" activeStyle={{ background: '#1e282c', borderLeftColor:'#3c8dbc'}} >
						<i className="fa fa-th"></i> <span>Eshop Service Access Status</span>
					  </Link>
					</li>
					<li>
					  <Link to="/eshopresponse" activeClassName="active" activeStyle={{ background: '#1e282c', borderLeftColor:'#3c8dbc'}}>
						<i className="fa fa-table"></i> <span>Eshop Response Time</span>
					  </Link>
					</li>
					<li>
					  <Link to="/pageview" activeClassName="active" activeStyle={{ background: '#1e282c', borderLeftColor:'#3c8dbc'}}>
						<i className="fa fa-edit"></i> <span>Eshop Page View</span>
					  </Link>
					</li>
				</ul>
			</section>
			{/* /.sidebar */}
		</aside>
	)
  }
});

export default NavigationMenu