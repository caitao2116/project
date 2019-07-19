 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	

	//注册
	$scope.reg=function(){
		//判断两个密码是否一致
		if($scope.entity.password != $scope.password){
			alert("输入的密码不一致");
			$scope.entity.password="";
			$scope.password="";
			return;
		}
		
		userService.add($scope.entity,$scope.smscode).success(
			function(response){
				alert(response.message);
			}
		);
	}
	
	
	//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请输入手机号");
			return;
		}
		
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);
			}
		);
	}
    
});	
