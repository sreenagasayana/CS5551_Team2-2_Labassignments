var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var paypal = require('paypal-rest-sdk');
var mongoose = require('mongoose');
var UserRoute = require('./routes/userRouter');
// Mongo DB Url
var url = 'mongodb://gharimanasa:Harimanasa7@ds035643.mlab.com:35643/lab5';

mongoose.connect(url,function(err,db){
    console.log('connected');
});
var app = express();

// configure paypal with the credentials you got when you created your paypal app
paypal.configure({
    'mode': 'sandbox', //sandbox or live
    // providing client Id
    'client_id': 'ATMjhHS250aSqyXywH7j1E2eOvoSTYlJXIsoII6qQ89DWStQN68y700sZ2ZpWlk6OZGHT1m78529CHge',
    // providing client secret
    'client_secret': 'EGG_AdS_L-kuA8FVdy1eMVfCrmkzJqFRh85QR8Fe7nmeV2kvbEPz48TN_mHbVSnj2_9ww-_wQi0ehJmr'
});



var staticPath = path.join(__dirname, '/');
app.use(express.static(staticPath));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'routes')));
app.use(express.static(path.join(__dirname, 'node_modules')));

app.use('/UserRoute',UserRoute);

// start payment process
app.get('/buy' , function( req , res ) {
    // create payment object
    var payment = {
        "intent": "authorize",
        "payer": {
            "payment_method": "paypal"
        },
        "redirect_urls": {
            "return_url": "http://127.0.0.1:3000/success",
            "cancel_url": "http://127.0.0.1:3000/err"
        },
        "transactions": [{
            "amount": {
                "total": 25.00,
                "currency": "USD"
            },
            "description": " a book on mean stack "
        }]
    }


    // call the create Pay method
    createPay( payment )
    .then( function( transaction ) {
    var id = transaction.id;
var links = transaction.links;
var counter = links.length;
while( counter -- ) {
    if ( links[counter].method == 'REDIRECT') {
        // redirect to paypal where user approves the transaction
        return res.redirect( links[counter].href )
    }
}
})
.catch( function( err ) {
    console.log( err );
res.redirect('/err');
});
});


// success page
app.get('/success' , function(req ,res ) {
    console.log(req.query);
res.redirect('/sucess.html');
})

// error page
app.get('/err' , function(req , res) {
    console.log(req.query);
res.redirect('/err.html');
})


// helper functions
var createPay = function( payment ) {
    return new Promise( function( resolve , reject ) {
        paypal.payment.create( payment , function( err , payment ) {
        if ( err ) {
            reject(err);
        }
        else {
            resolve(payment);
        }
    });
});
}

function onLoadFn(){
    gapi.client.setApiKey('AIzaSyCUCNl4JfN_WLaGY3fFXQDvG08a8vPFyx4');
    gapi.client.load('plus', 'v1', function(){});
}



app.listen(3000, function() {
    console.log('Listening at http://localhost:3000')
});