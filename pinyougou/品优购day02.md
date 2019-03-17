

# 1.AngularJS介绍

###  1.1 四大特征

	MVC
	数据双向绑定
	依赖注入
	模块化

![](pic/angular特征.png)

### 1.2 指令

```javascript
ng-app:定义angular的模块
ng-controller:定义angular的控制器
ng-model:绑定变量
ng-click:绑定单击事件
ng-repeat:遍历  XX in list
ng-init:初始化
```

### 1.3 内置对象

```javascript
$scope:在控制器中获得ng-model绑定的变量
$http: 发起Ajax请求; $http.get()/$http.post()
$event:事件对象;获得事件源;$event.target=DOM对象
$index:在ng-repeat中获得元素的索引
```

# 2.品牌管理

### 2.1 列表显示

* HTML

```html
<body ng-app="pinyougou" ng-controller="brandController" ng-init="findAll()">
    <tr ng-repeat="entity in list">
		 <td><input  type="checkbox"></td>			                              
		 <td>{{entity.id}}</td>
		 <td>{{entity.name}}</td>									     
		 <td>{{entity.firstChar}}</td>		                                 
	</tr>
</body>
```

* Javascript

```javascript
$scope.findAll=function(){
	$http.get('../brand/findAll.do').success(
		function(response){
			$scope.list=response;
		}		
	);				
}
```

### 2.2 分页查询

#### 数据传递

- 前台------>后台

  当前页码

  每页显示的记录数

- 后台------>前台

  数据集合

  总记录数

#### 代码实现

* 分页实体类

```java
public class PageResult implements Serializable{
	private long total;//总记录数
	private List rows;//当前页记录
}
```
* 页面

```html
<!--导入分页资源文件-->
<script src="../plugins/angularjs/pagination.js"></script>
<link rel="stylesheet" href="../plugins/angularjs/pagination.css">

<!--在table标签下添加导入分页资源文件-->    
<tm-pagination conf="paginationConf"></tm-pagination>
```

* Javascript

```javascript
var app=angular.module('pinyougou',['pagination']);

//初始化paginationConf值
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
$scope.reloadList=function(){
	$scope.findPage( $scope.paginationConf.currentPage ,  $scope.paginationConf.itemsPerPage );
}

$scope.findPage=function(page,size){
	$http.get('../brand/findPage.do?page='+page +'&size='+size).success(
		function(response){
			$scope.list=response.rows;//显示当前页数据 	
			$scope.paginationConf.totalItems=response.total;//更新总记录数 
		}		
	);				
}
```
* Web层

```java
@RequestMapping("/findPage")
public PageResult findPage(int page,int size){
	return brandService.findPage(page, size);
}
```
* 服务层

```java
public PageResult findPage(int pageNum, int pageSize) {
	PageHelper.startPage(pageNum, pageSize);//分页
	Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
	return new PageResult(page.getTotal(), page.getResult());
}
```
### 2.3 增加品牌

* 页面数据绑定

```html
<input  class="form-control" placeholder="品牌名称" ng-model="entity.name">
<input  class="form-control" placeholder="首字母" ng-model="entity.firstChar">
<button class="btn btn-success" ng-click="save()">保存</button>
```

* Javascript

```javascript
//新增
$scope.save=function(){			
	$http.post('../brand/add.do',$scope.entity).success(
		function(response){
			if(response.success){
				$scope.reloadList();	//刷新页面数据
			}else{
				alert(response.message);
			}				
		}		
	);
}
```

* Web层

```java
@RequestMapping("/add")
public Result add(@RequestBody TbBrand brand){
	try {
		brandService.add(brand);
		return new Result(true, "增加成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "增加失败");
	}		
}
```

* 服务层

```java
@Override
public void add(TbBrand brand) {
	brandMapper.insert(brand);
}
```

![](pic/前后台数据交互.png)

### 2.4 修改品牌

##### 数据的回显

* 页面

```html
<tr ng-repeat="entity in list">    
    ...
	<button type="button" ng-click="findOne(entity.id)" >修改</button></td>
</tr>
```

* Javascript

```javascript
$scope.findOne=function(id){
	$http.get('../brand/findOne.do?id='+id).success(
		function(response){
			$scope.entity=response;
		}		
	);				
}
```

* Web层

```java
@RequestMapping("/findOne")
public TbBrand findOne(Long id){
	return brandService.findOne(id);
}
```

* 服务层

```java
@Override
public TbBrand findOne(Long id) {
	return brandMapper.selectByPrimaryKey(id);
}
```

##### 更新数据

* Javascript

```javascript
$scope.save=function(){
	var methodName='add';//方法名 
	if($scope.entity.id!=null){
		methodName='update';
	}				
	$http.post('../brand/'+methodName +'.do',$scope.entity).success(
		function(response){
			if(response.success){
				$scope.reloadList();//刷新页面数据
			}else{
				alert(response.message);
			}				
		}		
	);
}
```

* Web层

```java
@RequestMapping("/update")
public Result update(@RequestBody TbBrand brand){
	try {
		brandService.update(brand);
		return new Result(true, "修改成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "修改失败");
	}		
}
```

* 服务层

```java
public void update(TbBrand brand) {
	brandMapper.updateByPrimaryKey(brand);
}
```

### 2.5 删除品牌

##### 记录勾选的品牌ID

* 页面

```html
<tr ng-repeat="entity in list">
    <td><input  type="checkbox" ng-click="updateSelection($event, entity.id)"></td>			     
    ...
</tr>
```

* Javascript

```javascript
//用户勾选的ID集合 
$scope.selectIds=[];
//用户勾选复选框 
$scope.updateSelection=function($event,id){
	if($event.target.checked){
		$scope.selectIds.push(id);//push向集合添加元素 					
	}else{
		var index= $scope.selectIds.indexOf(id);//查找值的 位置
		$scope.selectIds.splice(index,1);//参数1：移除的位置 参数2：移除的个数  
	}
}
```

##### 批量删除

* 页面

```html
<button type="button" class="btn btn-default" title="删除" ng-click="dele()">
```

* Javascript

```javascript
$scope.dele=function(){
	if(confirm('确定要删除吗？')){
		$http.get('../brand/delete.do?ids='+$scope.selectIds).success(
				function(response){
					if(response.success){
						$scope.reloadList();//刷新页面数据
					}else{
						alert(response.message);
					}						
				}
		);
	}					
}
```

* Web层

```java
@RequestMapping("/delete")
public Result delete(Long [] ids){
	try {
		brandService.delete(ids);
		return new Result(true, "删除成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "删除失败");
	}	
}
```

* 服务层

```java
@Override
public void delete(Long[] ids) {				
	for(Long id:ids){
		brandMapper.deleteByPrimaryKey(id);
	}		
}
```

### 2.6 条件查询

* 页面绑定数据

```html
品牌名称:<input ng-model="searchEntity.name"> 
品牌首字母:<input ng-model="searchEntity.firstChar">  
<button  class="btn btn-default" ng-click="reloadList()">查询</button>  
```

* Javascript

```javascript
//刷新数据
$scope.reloadList=function(){
	$scope.search( $scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage );
}


$scope.searchEntity={};		//第一次加载页面,初始化searchEntity

//条件查询 
$scope.search=function(page,size){
	$http.post('../brand/search.do?page='+page +'&size='+size, $scope.searchEntity).success(
		function(response){
			$scope.list=response.rows;//显示当前页数据 	
			$scope.paginationConf.totalItems=response.total;//更新总记录数 
		}		
	);
}
```

* Web层

```java
@RequestMapping("/search")
public PageResult search(@RequestBody TbBrand brand,int page,int size){
	return brandService.findPage(brand, page, size);		
}
```

* 服务层

```java
public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
	
    //分页
	PageHelper.startPage(pageNum, pageSize);
	//条件查询对象
	TbBrandExample example=new TbBrandExample();
	Criteria criteria = example.createCriteria();
    //封装查询条件
	if(brand!=null){
		if(brand.getName()!=null && brand.getName().length()>0){
			criteria.andNameLike("%"+brand.getName()+"%");
		}
		if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
			criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
		}			
	}
	Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
	return new PageResult(page.getTotal(), page.getResult());
}
```

