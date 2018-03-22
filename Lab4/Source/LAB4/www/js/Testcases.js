 describe("Validatelogin", function() {
   var scope;

   beforeEach(angular.mock.module("Validlogin"));
   beforeEach(angular.mock.inject(function($rootScope, $controller) {
     scope = $rootScope.$new();
     $controller('Validatelogin', {$scope: scope});
   }));

   it("check if username and password is empty", function() {

     scope.Valid('','');
     expect(scope.temp).toEqual("username and password cannot be empty");
   });


   it("check if username is empty", function() {

     scope.Valid('','sharath');
     expect(scope.temp).toEqual("username cannot be empty");
   });

   it("check if  password is empty", function() {

     scope.Valid('sharath','');
     expect(scope.temp).toEqual("password cannot be empty");
   });

});



