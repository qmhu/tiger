var http = require("http");  //1
var proxy = require('express-http-proxy');
var url = require("url");  //2
var express = require('express');
var consolidate = require('consolidate');
var handlebars = require('handlebars');
var bodyParser = require('body-parser');

var app = express();

app.set('views', 'app'); //Set the folder-name from where you serve the html page. ]
app.set('view engine', 'html');
app.engine('html', consolidate.handlebars);
app.use(express.static('./app')); //Set the folder from where you serve all static files like images, css, javascripts, libraries etc
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/', function(req, res){ 
	res.render('index.html');		
});

app.use('/api', proxy('localhost:8080', {
  forwardPath: function(req, res) {
    return url.parse(req.url).path;
  }
}));

app.listen(3000, function () {
  console.log('Eshop Admin listening on port 3000!');
});