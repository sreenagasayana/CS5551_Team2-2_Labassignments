'use strict';
angular.module('myApp')
.controller('appController',function($scope,$location,$http){
  $scope.errorMessage = "";
  $scope.kgraphRecords = [];
  document.getElementsByClassName('navStyle')[0].style.display="none";
  $scope.homePage = function(){
    $location.path("/homepage");
  }
  $scope.aboutPage = function(){
    $location.path("/about");
  }
  $scope.contactPage = function(){
    $location.path("/contact");
  }
  $scope.registerFn = function(){
    $location.path("/register");
  }
  $scope.logoutFn = function(){
    $location.path("/login");
  }

  $scope.onFocusFn = function(){
    $scope.errorMessage = "";
  }

  $scope.registerUser = function(regForm){
    if(regForm.pwd == regForm.cPwd){
      localStorage.setItem('user', JSON.stringify(regForm));
      swal({
        title: "Good job!",
        text: "Registration Successful.",
        icon: "success",
        button: "okay",
      });
      $location.path("/login");
    }else{
      swal({
        title: "Oops!",
        text: "Password are not matched. Please re-enter.",
        icon: "error",
        button: "okay",
      });
    }
  }

  $scope.login = function(loginForm){
    localStorage.setItem('myVal',true);
    console.log(loginForm);
    var user = JSON.parse(localStorage.getItem('user'));
    if(loginForm == undefined){
      $scope.errorMessage = "Inavlid Email ID or Password";
    }else if(loginForm.eMail == user.eMail && loginForm.pwd == user.pwd){
      console.log("Login Successful");
      document.getElementsByClassName('navStyle')[0].style.display="block";
      $location.path("/homepage");
    }else {
      $scope.errorMessage = "Inavlid Email ID or Password";
    }
  }

  $scope.onGoogleLogin = function(){
    var params = {
      'clientid': '743992359852-rcf59v468vts4ffji64ckvljhfr5npur.apps.googleusercontent.com',
      'cookiepolicy':'single_host_origin',
      'callback': function(result){
        if(result['status']['signed_in']){
          $scope.$apply(function(){
            document.getElementsByClassName('navStyle')[0].style.display="block";
            $location.path("/homepage");
          });
        }
      },
      'approvalprompt': 'force',
      'scope': 'https://www.googleapis.com/auth/gmail.readonly'
    };
    gapi.auth.signIn(params);
  }

  $scope.onFacebookLogin = function(){
    FB.login(function(response){
      if(response.authResponse){
        FB.api('/me', 'GET', {fields: 'email, first_name, name, id, picture'}, function(response){
          $scope.$apply(function(){
            document.getElementsByClassName('navStyle')[0].style.display="block";
            $location.path("/homepage");
          });
        });
      }else{

      }
    }, {
      scope: 'email, user_likes',
      return_scopes: true
    });
  }

  $scope.KGSearch = function(name){
    console.log("faf");
    $http({
      url: 'https://kgsearch.googleapis.com/v1/entities:search',
      method: "GET",
      params: {'query': name,
      'limit': 10,
      'indent': true,
      'key' : 'AIzaSyDpDsG0uiBrPsdETv8MixKaz66Roi9Oslc'}
    })
    .then(function(res) {
      console.log(res);
      $scope.kgraphRecords = res.data.itemListElement;
    }
  )
}

$scope.itemName = "";
$scope.ingrData = {};
$scope.viewContent = false;
var app_id = "60807554";
var app_key = "a726841ff35ac99bec287e824c6bc906";
var url ="https://api.edamam.com/api/nutrition-data?app_id="+app_id+"&app_key="+app_key+"&ingr=";
$scope.ingrSearch = function(ingrName){
  if(ingrName == undefined){
    alert('Please enter valid Ingredient Name');
  }
  else{
    /* Nutrition API CAll*/
    $http.get(url+ingrName)
    .then(function(res) {
      $scope.itemName = ingrName;
      $scope.viewContent = true;
      $scope.ingrData = res.data;
      ingrDetails(ingrName,res.data.calories,res.data.totalWeight);
    }
  )
}
}

function ingrDetails(ingredientText,calories,totalWeight){
  var text = ingredientText + " has " + calories + " calories " + " and total weight is " + totalWeight;
  var nutritionDtlsURL = "https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize?username=ee3f194c-eb5e-4daf-bfed-bef4ac390c47&password=TS5t3IbHyE0T&text="+text;

  $scope.playNutritionDetails = function() {
    var audio = new Audio(nutritionDtlsURL);
    audio.play();
  };

}
})
