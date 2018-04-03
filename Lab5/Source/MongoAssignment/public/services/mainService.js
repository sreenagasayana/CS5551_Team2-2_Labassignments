angular.module('myApp')
.factory('mainService',function($http){
  return{
    registerUser : function(formData){
      return $http.post('/UserRoute/addNewUser',formData);
    },
    checkCredentials: function(loginForm){
      //console.log("$$$$$$$$$");
      console.log(loginForm);
      return $http.post('/UserRoute/checkUserCredential',loginForm);
    },
    getRecipeData: function(rName){
          return $http.get('/UserRoute/getAllRecipes/'+rName);
      },
    addRecipeData: function(data){
      return $http.post('/UserRoute/addRecipe',data);
    },
    deleteRecipeData: function(rName){
      return $http.delete('/UserRoute/deleteRecipe/'+rName);
    }
  }
})