var path = require('path');
var express = require('express');
var app = express();
var staticPath = path.join(__dirname, '/');
app.use(express.static(staticPath));

function onLoadFn(){
  gapi.client.setApiKey('AIzaSyCUCNl4JfN_WLaGY3fFXQDvG08a8vPFyx4');
  gapi.client.load('plus', 'v1', function(){});
}
app.listen(3000, function() {
  console.log('Listening at http://localhost:3000')
});
