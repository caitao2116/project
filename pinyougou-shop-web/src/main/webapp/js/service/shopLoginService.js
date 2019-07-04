app.service("shopLoginService",function($http){
	
	this.loginName=function(){
		return $http.get("../shopLogin/name.do")
	}
});