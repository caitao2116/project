 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,goodsService ,uploadService ,itemCatService ,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){	
		
		var id = $location.search()['id'];
		if(id == null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//富文本读取商品介绍内容
				editor.html($scope.entity.goodsDesc.introduction);
				//图片列表的显示
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//扩展属性的显示
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格显示
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//SKU列表
				for(var i = 0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	//回显勾选项
	$scope.checkAttributeValue=function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(items,'attributeName',specName);
		if(object == null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName) >= 0){
				return true;
			}else{
				return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){	
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					 alert("保存成功");
					 window.location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
//	//添加
//	$scope.add=function(){	
//		$scope.entity.goodsDesc.introduction=editor.html();
//		goodsService.add( $scope.entity).success(
//			function(response){
//				if(response.success){
//					 alert("添加成功");
//					 $scope.entity={};
//					 editor.html("");
//					 window.location.reload();
//				}else{
//					alert(response.message);
//				}
//			}		
//		);				
//	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//文件上传
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
				function(response){
					if(response.success){
						$scope.image_entity.url=response.message;//设置文件地址
					}else{
						alert(response.message);
					}
				}
		).error(function(){
			alert("文件上传错误");
		});
	}
	
	//初始化entity
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};
	
	//添加文件列表
	$scope.add_image_entity=function(){
		
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
	//删除图片文件
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	//读取一级下拉列表
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(
				function(response){
					$scope.itemCat1List=response;
					
				}
		);
	};
	
	
	//读取二级分类列表
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		if(newValue != undefined){
			$scope.itemCat3List=[];
			$scope.entity.goods.typeTemplateId=null;
			
			itemCatService.findByParentId(newValue).success(
					function(response){
						$scope.itemCat2List=response;
					}
			);
		}
	});
	
	//读取三级分类列表
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		if(newValue != undefined){
			itemCatService.findByParentId(newValue).success(
					function(response){
						$scope.itemCat3List=response;
					}
			);
		}
	});
	
	//读取模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		if(newValue != undefined){
			itemCatService.findOne(newValue).success(
					function(response){
						$scope.entity.goods.typeTemplateId=response.typeId;
					}
			);
		}	
	});
	
	//读取品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		if(newValue != undefined){
			typeTemplateService.findOne(newValue).success(
					function(response){
						$scope.typeTemplate=response;
						//获取品牌列表数据
						$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
						if($location.search()['id'] == null){
							//获取扩展属性
							$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
						}
						
					}
			);
			
			typeTemplateService.findSpecList(newValue).success(
			
					function(response){
						$scope.specList=response;
					}
			);
		}
	});
	
	
	$scope.updateSpecAttribute=function($event,name,value){
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(object != null){
			//判断时选中还是取消选中
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{//取消选中操作
				object.attributeValue.splice(object.attributeValue.indexOf(value ),1);
				//如果这个选择项没有一个选中的了，则删除这个选择项
				if(object.attributeValue.length == 0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
			
		}else{
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
		}
	}
	
	$scope.createItemList=function(){
		//初始化一个空的集合
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
		var items = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	addColumn=function(list,columnName,columnValues){
		var newList = [];//新的集合
		for(var i = 0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<columnValues.length;j++){
				//深克隆
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
    
	
	$scope.goodsStatus=['未审核','审核通过','审核未通过','关闭'];
	
	//商品分类集合
	$scope.itemCatList=[];
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
				function(response){
					for(var i=0;i<response.length;i++){
						$scope.itemCatList[response[i].id]=response[i].name;
					}
				}
		);
	}
});	
