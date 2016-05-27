'use strict'

import React from 'react'

var OccStatusPage = React.createClass({

	getInitialState: function() {
		return {
			
		}
	},
	componentDidMount: function(){
		// Get context with jQuery - using jQuery's .get() method.
		var allChartCanvas = $("#allstatus").get(0).getContext("2d");
		// This will get the first returned node in the jQuery collection.
		var allChart = new Chart(allChartCanvas);
		
		$.ajax({
			url: "/api/report/occStatusChart"
		}).then(function(response) {
			var labels = [];
			var data = [];
			var statusArray = [];
			
			var colorArray = ["#3c8dbc", "#dd4b39", "#605ca8", "#00a65a", "#00c0ef", "#f39c12"];
			$(response).each(function(index){
				var dataItem = [];
				dataItem['data'] = [];
				var color = colorArray[index];
				dataItem['fillColor'] = color;
				dataItem['strokeColor'] = color;
				dataItem['pointColor'] = color;
				dataItem['pointStrokeColor'] = color;
				dataItem['lineColor'] = color;
				$(response[index]['accessStatus']).each(function(indexInner){
					if (indexInner == 0){
						dataItem['label'] = response[index]['accessStatus'][0]['statusCode'];
					}
					var dateTmp = new Date(response[index]['accessStatus'][indexInner]['date']);
					var dataStr = (dateTmp.getMonth() + 1) + '/' + dateTmp.getDate() + " " + dateTmp.getHours() + ":" + dateTmp.getMinutes();
					if (index == 0){
						labels.push(dataStr);
					}
					
					dataItem['data'].push(response[index]['accessStatus'][indexInner]['count']);
				});	
				data.push(dataItem);
			});
			
			var chartData = {
				  labels: labels,
				  datasets: data
			};

			var chartOptions = {
			  //Boolean - If we should show the scale at all
			  showScale: true,
			  //Boolean - Whether grid lines are shown across the chart
			  scaleShowGridLines: false,
			  //String - Colour of the grid lines
			  scaleGridLineColor: "rgba(0,0,0,.05)",
			  //Number - Width of the grid lines
			  scaleGridLineWidth: 1,
			  //Boolean - Whether to show horizontal lines (except X axis)
			  scaleShowHorizontalLines: true,
			  //Boolean - Whether to show vertical lines (except Y axis)
			  scaleShowVerticalLines: true,
			  //Boolean - Whether the line is curved between points
			  bezierCurve: true,
			  //Number - Tension of the bezier curve between points
			  bezierCurveTension: 0.3,
			  //Boolean - Whether to show a dot for each point
			  pointDot: false,
			  //Number - Radius of each point dot in pixels
			  pointDotRadius: 4,
			  //Number - Pixel width of point dot stroke
			  pointDotStrokeWidth: 1,
			  //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
			  pointHitDetectionRadius: 20,
			  //Boolean - Whether to show a stroke for datasets
			  datasetStroke: true,
			  //Number - Pixel width of dataset stroke
			  datasetStrokeWidth: 2,
			  //Boolean - Whether to fill the dataset with a color
			  datasetFill: false,
			  //String - A legend template
			  legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].strokeColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>",
			  //Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
			  maintainAspectRatio: true,
			  //Boolean - whether to make the chart responsive to window resizing
			  responsive: true
			};
			
			
			//Create the line chart
			var chartCanvas = $("#allstatus").get(0).getContext("2d");
			// This will get the first returned node in the jQuery collection.
			var chartAll = new Chart(chartCanvas);
			window.chartAll = chartAll.Line(chartData, chartOptions);
		   
		});

		var table499 = $('#499-table').DataTable({
		  "paging": true,
		  "lengthChange": false,
		  "searching": true,
		  "ordering": true,
		  "info": true,
		  "autoWidth": false,
		  "order": [[ 1, "desc" ]]
		});
	  
		$.ajax({
			url: "/api/report/occStatusTop?dayBefore=5&status=499"
		}).then(function(data) {
			$(data['accessStatus']).each(function(index){
				table499.row.add([data['accessStatus'][index]['contentPath'],data['accessStatus'][index]['count']])
			});
			
			table499.draw();
		   
		});
		
		var table500 = $('#500-table').DataTable({
		  "paging": true,
		  "lengthChange": false,
		  "searching": true,
		  "ordering": true,
		  "info": true,
		  "autoWidth": false,
		  "order": [[ 1, "desc" ]]
		});
	  
		$.ajax({
			url: "/api/report/occStatusTop?dayBefore=5&status=500"
		}).then(function(data) {
			$(data['accessStatus']).each(function(index){
				table500.row.add([data['accessStatus'][index]['contentPath'],data['accessStatus'][index]['count']])
			});
			
			table500.draw();
		   
		});
		
		var table400 = $('#400-table').DataTable({
		  "paging": true,
		  "lengthChange": false,
		  "searching": true,
		  "ordering": true,
		  "info": true,
		  "autoWidth": false,
		  "order": [[ 1, "desc" ]]
		});
	  
		$.ajax({
			url: "/api/report/occStatusTop?dayBefore=5&status=400"
		}).then(function(data) {
			$(data['accessStatus']).each(function(index){
				table400.row.add([data['accessStatus'][index]['contentPath'],data['accessStatus'][index]['count']])
			});
			
			table400.draw();
		});
		
		//Initialize Select2 Elements
		$(".select2").select2();
		
		//Date range picker with time picker
		$('#datarange').daterangepicker({startDate: '+1d', timePicker: true, timePickerIncrement: 30, format: 'MM/DD/YYYY h:mm A'});
		
		var tableAccess = $('#detail-table').DataTable({
		  "paging": true,
		  "lengthChange": false,
		  "searching": true,
		  "ordering": true,
		  "info": true,
		  "autoWidth": false,
		  "order": [[ 1, "desc" ]],
		  "columns": [
			{ "width": "10%" },
			{ "width": "10%" },
			{ "width": "20%" },
			{ "width": "20%" },
			{ "width": "20%" },
			{ "width": "10%" },
			{ "width": "5%" },
			{ "width": "10%" },
			{ "width": "10%" },
			{ "width": "20%" },
			{ "width": "10%" },
			{ "width": "10%" }
		  ]
		});
		
		$('#search').click(function(){
			var contentPath = $('#contentPath').val();
			var status = $("#status_select").val();
			
			var start_date = $('#datarange').data('daterangepicker').startDate;
			var end_date = $('#datarange').data('daterangepicker').endDate;
			
			var chartUrl = "/api/report/occaccess";
			
			if (status){
				chartUrl = chartUrl + "?status=" + status;
			}else{
				return;
			}
			
			if (!contentPath){
				return;
			}
			
			if (contentPath != ""){
				chartUrl = chartUrl + "&contentPath=" + contentPath;
			}
			
			if (start_date) {
				chartUrl = chartUrl + "&startDate=" + start_date;
			}
			
			if (end_date) {
				chartUrl = chartUrl + "&endDate=" + end_date;
			}
			
			chartUrl = chartUrl + "&limit=100";
			
			tableAccess.clear();
			
			$.ajax({
				url: chartUrl
			}).then(function(response) {
				$(response).each(function(index){
					var dateTmp = new Date(response[index]['time']);
					var dataStr = '"' + (dateTmp.getMonth() + 1) + '/' + dateTmp.getDate() + " " + dateTmp.getHours() + ":" + dateTmp.getMinutes() + ":" + dateTmp.getSeconds() + '"';
					tableAccess.row.add([dataStr,response[index]['httpHost'],response[index]['contentPath'],response[index]['httpHost'] + response[index]['contentUri'],response[index]['requestUri'],response[index]['requestMethod'],response[index]['responseTime'],response[index]['status'],response[index]['schema'],response[index]['userAgent'],response[index]['device'],response[index]['landscape']]);
				});
				
				tableAccess.draw();
				tableAccess.columns.adjust().draw();
			});	
			
		});
	},

	componentDidUpdate: function(){

	},
  render: function () {
	return (
		<div>
			<section className="content-header">
			  <h1>
				OCC status code dashboard
			  </h1>
			</section>

			<section className="content">
			
			  <div className="row">
				<div className="col-md-12">

				  <div className="box box-info">
					<div className="box-header with-border">
					  <h3 className="box-title">OCC status code historical chart</h3>

					  <div className="box-tools pull-right">
						<button type="button" className="btn btn-box-tool" data-widget="collapse"><i className="fa fa-minus"></i>
						</button>
						<button type="button" className="btn btn-box-tool" data-widget="remove"><i className="fa fa-times"></i></button>
					  </div>
					</div>
					<div className="box-body">
					  <div className="chart">
						<canvas id="allstatus" style={{height:'400px'}}></canvas>
					  </div>
					</div>
					<div className="box-footer">
					  <div className="row">
						<div className="col-md-6">
						  <span className="btn btn-primary">499</span>
						  <span className="btn btn-danger">503</span>
						  <span className="btn" style={{backgroundColor:'#605ca8',color:'#fff'}}>500</span>
						  <span className="btn btn-success">403</span>
						  <span className="btn btn-info">400</span>
						  <span className="btn btn-warning">404</span>
						</div>
					  </div>
					</div>
				  </div>


				</div>
			  </div>
			  
			  <div className="row">
				<div className="col-xs-12">
				  <div className="box">
					<div className="box-header">
					  <h3 className="box-title">Status 499(5 day before to today)</h3>
					</div>

					<div className="box-body">
					  <table id="499-table" className="table table-bordered table-hover">
						<thead>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</thead>
						<tbody>
						</tbody>
						<tfoot>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</tfoot>
					  </table>
					</div>

				  </div>
				</div>
			  </div>
			  
			  <div className="row">

				<div className="col-xs-12">
				  <div className="box">
					<div className="box-header">
					  <h3 className="box-title">Status 500(5 day before to today)</h3>
					</div>
					<div className="box-body">
					  <table id="500-table" className="table table-bordered table-hover">
						<thead>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</thead>
						<tbody>
						</tbody>
						<tfoot>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</tfoot>
					  </table>
					</div>
				  </div>
				</div>
			  </div>
			  
			  <div className="row">
				<div className="col-xs-12">
				  <div className="box">
					<div className="box-header">
					  <h3 className="box-title">Status 400(5 day before to today)</h3>
					</div>
					<div className="box-body">
					  <table id="400-table" className="table table-bordered table-hover">
						<thead>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</thead>
						<tbody>
						</tbody>
						<tfoot>
						<tr>
						  <th>Url</th>
						  <th>Count</th>
						</tr>
						</tfoot>
					  </table>
					</div>
				  </div>
				</div>
			  </div>
			  
			  
			  <div className="box box-default">
				<div className="box-header with-border">
				  <h3 className="box-title">Select a condition to query eshop occ status code</h3>

				  <div className="box-tools pull-right">
					<button type="button" className="btn btn-box-tool" data-widget="collapse"><i className="fa fa-minus"></i></button>
					<button type="button" className="btn btn-box-tool" data-widget="remove"><i className="fa fa-remove"></i></button>
				  </div>
				</div>

				<div className="box-body">
				  <div className="row">
					<div className="col-md-6">
					  <div className="form-group">
						<label>ContentUri</label>
						<input type="text" id="contentPath" className="form-control" placeholder="Enter url like '/sbo/service/EShopService@placeOrder'"/>
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
						<label>Status Code</label>
						<select id="status_select" className="form-control select2" style={{width: '100%'}}>
							<option selected="selected">200</option>
							<option>499</option>
							<option>500</option>
							<option>503</option>
							<option>400</option>
							<option>403</option>
							<option>404</option>
						</select>
					  </div>
					</div>
				  </div>
				</div>

				<div className="box-footer">
				  <div className="row">
					<div className="col-md-12">
					  <button id="search" type="button" className="btn btn-danger btn-flat pull-right" >Go</button>
					</div>
				  </div>
				</div>
			  </div>
			
			  <div className="row">
				<div className="col-md-12">
				  <div className="box box-info">
					<div className="box-header with-border">
					  <h3 className="box-title">Eshop Service Status Detail</h3>

					  <div className="box-tools pull-right">
						<button type="button" className="btn btn-box-tool" data-widget="collapse"><i className="fa fa-minus"></i>
						</button>
						<button type="button" className="btn btn-box-tool" data-widget="remove"><i className="fa fa-times"></i></button>
					  </div>
					</div>
					<div className="box-body">
					
					 <table id="detail-table" className="table table-bordered table-hover">
						<thead>
						<tr>
						  <th>Time</th>
						  <th>HttpHost</th>
						  <th>ContentPath</th>
						  <th>ContentUri</th>
						  <th>RequestUri</th>
						  <th>Method</th>
						  <th>ResponseTime(ms)</th>
						  <th>Status</th>
						  <th>Schema</th>
						  <th>UserAgent</th>
						  <th>Device</th>
						  <th>Landscape</th>
						</tr>
						</thead>
						<tbody>
						</tbody>
						<tfoot>
						<tr>
						  <th>Time</th>
						  <th>HttpHost</th>
						  <th>ContentPath</th>
						  <th>ContentUri</th>
						  <th>RequestUri</th>
						  <th>Method</th>
						  <th>ResponseTime(ms)</th>
						  <th>Status</th>
						  <th>Schema</th>
						  <th>UserAgent</th>
						  <th>Device</th>
						  <th>Landscape</th>
						</tr>
						</tfoot>
					  </table>
					  
					</div>
				  </div>

				</div>
			  </div>

			</section>
		</div>
	)
  }
});

export default OccStatusPage