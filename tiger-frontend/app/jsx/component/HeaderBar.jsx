'use strict'

import React from 'react'

var HeaderBar = React.createClass({
	getInitialState: function () {
		return {
			messages: [],
			notifications: [],
			tasks: []
		}
	},
	pushMenu: function () {
		var body = document.body;
		if(body.clientWidth > 768){
			if(body.className.indexOf('sidebar-collapse') === -1){
				body.className += ' sidebar-collapse';
			}else {
				body.className = body.className.replace(' sidebar-collapse', '');
			}
		}else{
			if (body.className.indexOf('sidebar-open') === -1) {
				body.className += ' sidebar-open';
			}else{
				body.className = body.className.replace(' sidebar-open','');
			}
		}
	},
	componentDidMount: function () {
		var messages = [{
			displayName: 'Support Team',
			displayPicture: 'dist/img/user2-160x160.jpg',
			messageSubject: 'Why not buy a new awesome theme?',
			messageTime: '5 mins',
		}, {
			displayName: 'AdminLTE Design Team',
			displayPicture: 'dist/img/user3-128x128.jpg',
			messageSubject: 'Why not buy a new awesome theme?',
			messageTime: '2 hours',
		}, {
			displayName: 'Developers',
			displayPicture: 'dist/img/user4-128x128.jpg',
			messageSubject: 'Why not buy a new awesome theme?',
			messageTime: 'Today',
		}, {
			displayName: 'Sales Department',
			displayPicture: 'dist/img/user3-128x128.jpg',
			messageSubject: 'Why not buy a new awesome theme?',
			messageTime: 'Yesterday',
		}, {
			displayName: 'Reviewers',
			displayPicture: 'dist/img/user4-128x128.jpg',
			messageSubject: 'Why not buy a new awesome theme?',
			messageTime: '2 days',
		}];

		var notifications = [{
			subject: '5 new members joined today',
			className: 'fa fa-users text-aqua'
		}, {
			subject: 'Very long description here that may not fit into the page and may cause design problems',
			className: 'fa fa-warning text-yellow'
		}, {
			subject: '5 new members joined',
			className: 'fa fa-users text-red'
		}, {
			subject: '25 sales made',
			className: 'fa fa-shopping-cart text-green'
		}, {
			subject: 'You changed your username',
			className: 'fa fa-user text-red'
		}];

		var tasks = [{
			subject: 'Design some buttons',
			percentage: 20
		}, {
			subject: 'Create a nice theme',
			percentage: 40
		}, {
			subject: 'Some task I need to do',
			percentage: 60
		}, {
			subject: 'Make beautiful transitions',
			percentage: 80
		}];

		this.setState({
			messages: messages,
			notifications: notifications,
			tasks: tasks
		});
	},
	render: function () {
		var that = this;
		return (
			<header className="main-header">
				{/* Logo */}
				<a href="index2.html" className="logo">
					{/* mini logo for sidebar mini 50x50 pixels */}
					<span className="logo-mini"><b>A</b>LT</span>
					{/* logo for regular state and mobile devices */}
					<span className="logo-lg"><b>Tiger</b>  Admin</span>
				</a>
				{/* Header Navbar: style can be found in header.less */}
				<nav className="navbar navbar-static-top" role="navigation">
					{/* Sidebar toggle button*/}
					<a href="#" className="sidebar-toggle" data-toggle="offcanvas" role="button" onClick={that.pushMenu}>
						<span className="sr-only">Toggle navigation</span>
					</a>
				</nav>
			</header>
		)
	}
});


export default HeaderBar