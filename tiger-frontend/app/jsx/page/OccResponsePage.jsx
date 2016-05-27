'use strict'

import React from 'react'

var divStyle = {
  width: '100%',
  WebkitTransition: 'all', // note the capital 'W' here
  msTransition: 'all' // 'ms' is the only lowercase vendor prefix
};

var OccResponsePage = React.createClass({
  render: function () {
	return (
	<div>
		<section className="content-header">
		  <h1>
			ChartJS
			<small>Preview sample</small>
		  </h1>
		  <ol className="breadcrumb">
			<li><a href="#"><i className="fa fa-dashboard"></i> Home</a></li>
			<li><a href="#">Charts</a></li>
			<li className="active">ChartJS</li>
		  </ol>
		</section>


		<section className="content">
		
		  <div className="box box-default">
			<div className="box-header with-border">
			  <h3 className="box-title">Select2</h3>

			  <div className="box-tools pull-right">
				<button type="button" className="btn btn-box-tool" data-widget="collapse"><i className="fa fa-minus"></i></button>
				<button type="button" className="btn btn-box-tool" data-widget="remove"><i className="fa fa-remove"></i></button>
			  </div>
			</div>

			<div className="box-body">
			  <div className="row">
				<div className="col-md-6">
				  <div className="form-group">
					<label>Domain</label>
					<select id="domain_select" className="form-control select2" style={{width: '100%'}} value="All">
						<option>All</option>
					</select>
				  </div>

				  <div className="form-group">
					<label>Date range:</label>

					<div className="input-group">
					  <div className="input-group-addon">
						<i className="fa fa-calendar"></i>
					  </div>
					  <input type="text" className="form-control pull-right" id="datarange"/>
					</div>
				  </div>

				</div>

				<div className="col-md-6">
				  <div className="form-group">
					<label>ContentPath</label>
					<select id="path_select" className="form-control select2" data-placeholder="Select a Content Path" style={{width: '100%'}}>
					</select>
				  </div>
				  <div className="form-group">
					<label>Aggregate</label>
					<select id="aggregate_select" className="form-control select2" value="Day" style={{width: '100%'}}>
						<option>Day</option>
						<option>Raw</option>
					</select>
				  </div>

				</div>

			  </div>

			</div>

			<div className="box-footer">
			  <div className="row">
				<div className="col-md-6">

				  <div id="landscape" className="form-group" style={{marginBottom: '0px'}}>
					<label>
					  <input id="select_us" type="radio" name="r1" className="minimal" defaultChecked/>
					  US
					</label>
					<label>
					  <input id="select_cn" type="radio" name="r1" className="minimal"/>
					  CN
					</label>
					<label>
					  <input id="select_eu" type="radio" name="r1" className="minimal" disabled/>
					  EU
					</label>
				  </div>
				</div>
				  
				<div className="col-md-6">
				  <button id="search" type="button" className="btn btn-danger btn-flat pull-right" >Go</button>
				</div>
			  </div>
			</div>
		  </div>
		
		  <div className="row">
			<div className="col-md-12">

			  <div className="box box-info">
				<div className="box-header with-border">
				  <h3 className="box-title">Eshop response time(ms)</h3>

				  <div className="box-tools pull-right">
					<button type="button" className="btn btn-box-tool" data-widget="collapse"><i className="fa fa-minus"></i>
					</button>
					<button type="button" className="btn btn-box-tool" data-widget="remove"><i className="fa fa-times"></i></button>
				  </div>
				</div>
				<div className="box-body">
				
				  <div className="chart">
					<canvas id="alleshop" style={{height:'400px'}}></canvas>
				  </div>
				</div>

			  </div>

			</div>
		  </div>

		</section>
	</div>
	)
  }
});

export default OccResponsePage