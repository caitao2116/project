app.controller("shopIndexController",function($scope,shopLoginService){
	
	$scope.showLoginName=function(){
		shopLoginService.loginName().success(
				function(response){
					$scope.loginName=response.loginName;
					
				}
		);
	}
});