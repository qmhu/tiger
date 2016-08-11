'use strict'

import React from 'react'

var RealtimePage = React.createClass({
	getInitialState: function() {
		return {
			
		}
	},
	componentDidMount: function(){
		
		$('#datarange').daterangepicker({startDate: '+1d', timePicker: true, timePickerIncrement: 30, format: 'MM/DD/YYYY HH:mm A'});
		
		function addParameter(href, name, value) {  
			var delimiter;  
			if (href.indexOf("?") == -1)  
				delimiter = "?";  
			else  
				delimiter = "&";  
			return href + delimiter + name + "=" + value;  
		};  
		
		$('#search').click(function(){
			var domain = $('#domain').val();
			var landscape = $('#landscape').val();
			var interval = $('#interval').val();
			
			var start_date = $('#datarange').data('daterangepicker').startDate;
			var end_date = $('#datarange').data('daterangepicker').endDate;
			
			var chartUrl = "/api/report/realtime";
			
			
			if (landscape) {
				chartUrl = addParameter(chartUrl,"landscape",landscape);
			}
			
			
			if (domain != ""){
				chartUrl = addParameter(chartUrl,"domain",domain);
			}
			
			if (interval != ""){
				chartUrl = addParameter(chartUrl,"interval",interval);
			}
			
			if (start_date) {
				chartUrl = addParameter(chartUrl,"startDate",start_date);
			}
			
			if (end_date) {
				chartUrl = addParameter(chartUrl,"endDate",end_date);
			}
			

			$.ajax({
				url: chartUrl
			}).then(function(response) {
				var labels = [];
				var data = [];
				$(response['responseTimes']).each(function(index){
					var dateTmp = new Date(response['responseTimes'][index]['date']);
					labels.push((dateTmp.getMonth() + 1) + '/' + dateTmp.getDate() + " " + dateTmp.getHours() + ":" + dateTmp.getMinutes());
					
					data.push(response['responseTimes'][index]['time']);
				});
				
				var chartData = {
					  labels: labels,
					  datasets: [
						{
						  label: "Electronics",
						  fillColor: "#5cb85c",
						  strokeColor: "#5cb85c",
						  pointColor: "#5cb85c",
						  pointStrokeColor: "#c1c7d1",
						  pointHighlightFill: "#fff",
						  pointHighlightStroke: "rgba(220,220,220,1)",
						  data: data
						}
					  ]
				};
				
				if (window.chart){
					window.chart.destroy();
				}
				
				
				
				var myDiv= document.getElementById("alleshop");
				myDiv.innerHTML = "";
				
				//Create the line chart
				var chartCanvas = $("#alleshop").get(0).getContext("2d");
				// This will get the first returned node in the jQuery collection.
				var chart = new Chart(chartCanvas);
				window.chart = chart.Line(chartData, chartOptions);
			   
			});
			
			
			
		});
		
		
		
		
		/* ChartJS
		 * -------
		 * Here we will create a few charts using ChartJS
		 */

	  
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
		  datasetFill: true,
		  //String - A legend template
		  legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].lineColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>",
		  //Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
		  maintainAspectRatio: true,
		  //Boolean - whether to make the chart responsive to window resizing
		  responsive: true
		};

	},
	
	componentDidUpdate: function(){

	},

  render: function () {
	return (
	<div>
		<section className="content-header">
		  <h1>
			Realtime Eshop Dashboard
		  </h1>
		</section>


		<section className="content">
		
		  <div className="box box-default">
			<div className="box-header with-border">
			  <h3 className="box-title">Query condition</h3>

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
					<input type="text" className="form-control pull-right" id="domain"/>
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
					<label>Landscape</label>
					<input type="text" className="form-control pull-right" id="landscape"/>
				  </div>
				  <div className="form-group">
					<label>Interval</label>
					<input type="text" className="form-control pull-right" placeholder="input time interval like '1h'" id="interval" defaultValue="1h" />
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
				  <h3 className="box-title">95 Percentile response time(ms)</h3>

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

export default RealtimePage