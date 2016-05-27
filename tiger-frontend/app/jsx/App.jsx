'use strict'

import React, {Component} from 'react'
import ReactDOM from 'react-dom'
import { Link, Router, Route } from 'react-router'
import NavigationMenu from './component/NavigationMenu';
import HeaderBar from './component/HeaderBar';

var App = React.createClass({
	getInitialState: function() {
		return {
			statTileOptions: []
		}
	},
	componentDidMount: function(){

	},

	componentDidUpdate: function(){

	},

	render: function(){
		return (
			<div className="wrapper">
				<HeaderBar />
				
				<NavigationMenu />
				
				<div className="content-wrapper">
					{this.props.children}
				</div>

				<footer className="main-footer">
					<div className="pull-right hidden-xs">
						<b>Version</b> 0.1.0
					</div>
					<strong>This project is a dashboard for eshop online status.</strong>
				</footer>

				{/*<ControlsMenu />*/}
			</div>
		)
	}
})

export default App