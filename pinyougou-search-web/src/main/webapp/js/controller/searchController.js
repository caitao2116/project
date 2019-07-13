app.controller('searchController',function($scope,$location,searchService){
	
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		
		searchService.search($scope.searchMap).success(
			function(response){
				
				$scope.resultMap=response;
				buildPageLabel();
			}
		);
	}
	
	//初始化搜索项
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':30,'sort':'','sortField':''};//搜索对象
	
	//增加搜索项
	$scope.addSearchItem=function(key,value){
		if(key == 'category' || key == 'brand' || key == 'price'){
			$scope.searchMap[key] = value;
		}else{
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
	}
	
	//移除搜索项
	$scope.removeSearchItem=function(key){
		if(key == 'category' || key == 'brand' || key == 'price'){
			$scope.searchMap[key] = '';
		}else{
			delete $scope.searchMap.spec[key] ;
		}
		$scope.search();
	}
	
	//构建分页标签
	buildPageLabel=function(){
		$scope.pageLabel=[];
		var firstPage=1;
		var lastPage=$scope.resultMap.totalPages;
		$scope.firstDot=true;
		$scope.lastDot=true;
		if($scope.resultMap.totalPages>5){//如果总页数大于5，只显示5页
			if($scope.searchMap.pageNo<=3){//当前页小于等于3 ，显示前5页
				lastPage = 5;
				$scope.firstDot=false;
			}else if($scope.searchMap.pageNo >= ($scope.resultMap.totalPages-2)){
				//当前页大于倒数第三页，显示后5页
				firstPage = $scope.resultMap.totalPages-4;
				$scope.lastDot=false;
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		
		for(var i = firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		if(pageNo<=0 || pageNo>$scope.resultMap.totalPages){
			return;
		}
		
		$scope.searchMap.pageNo = pageNo;
		$scope.search();
	}
	
	//判断是否是第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo == 1){
			return true;
		}else{
			return false;
		}
	}
	
	//判断是否为当前页
	$scope.isCurrentPage=function(p){
		if(p==$scope.searchMap.pageNo){
			return true;
		}else{
			return false;
		}
	}
	
	
	//判断是否是最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo == $scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	//排序查询
	$scope.sortSearch=function(sort,sortField){
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	
	//判断关键词中是否包含品牌信息
	$scope.keywordsIsBrand=function(){
		for(var i = 0 ;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >=0){
				return true;
			}
		}
		return false;
	}
	
	//接收首页的关键词
	$scope.loadKeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}
});