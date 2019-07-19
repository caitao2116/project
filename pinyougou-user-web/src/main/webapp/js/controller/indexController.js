app.controller('indexController',function($scope,loginService){
	
	$scope.showName=function(){
		
		loginService.showName().success(
			function(response){
//				var s = JSON.stringify(response.loginName);
//				alert(s);
				$scope.loginName=response.loginName;
			}
		);
	}
});