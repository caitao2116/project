app.controller('baseController',function($scope){

	//分页控件配置
	$scope.paginationConf = {
			currentPage: 1,
			totalItems: 10,
			itemsPerPage: 10,
			perPageOptions: [10, 20, 30, 40, 50],
			onChange: function(){
				$scope.reloadList();
			}
	};
	
	
	//刷新列表
	$scope.reloadList = function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	
	//创建一个数组，保存用户勾选中复选框的id
	$scope.selectIds = [];
	$scope.getIds=function($event,id){
		if($event.target.checked){
			$scope.selectIds.push(id);
		}else{
			var index=$scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);
		}
	}
	
	//JSON格式转化
	$scope.jsonToString=function(jsonString,key){
		var value = "";
	 	var json = JSON.parse(jsonString);
	 	for(var i=0;i<json.length;i++){
	 		if(i>0){
	 			value += ",";
	 		}
	 		value += json[i][key];
	 	}
	 	return value;
	}
	
	//根据key查询集合中对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		for(var i = 0;i<list.length;i++){
			if(list[i][key] == keyValue){
				return list[i];
			}
		}
		return null;
	}
});