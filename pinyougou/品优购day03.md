[TOC]

# 1. JavaScript的分层设计

* 服务层:负责和后台进行交互

```javascript
app.service('brandService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../brand/findAll.do');		
	}
    
    ...
});
```

* 控制层:负责控制页面数据显示

```javascript
app.controller('brandController' ,function($scope,$controller,brandService){	//注入服务层
	
    $controller('baseController',{$scope:$scope});//继承
    
	$scope.findAll=function(){
        //调用服务层,请求服务器获得数据
		brandService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
    ...
});	
```

* baseController:抽取controller层公共的业务逻辑

```javascript
app.controller('baseController' ,function($scope){	
	    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
    
    //刷新数据
    $scope.reloadList=function(){
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
	...	
});	
```

* 页面加载JS文件顺序

```html
<script type="text/javascript" src="../js/base_pagination.js"></script>
<script type="text/javascript" src="../js/service/brandService.js"></script>
<script type="text/javascript" src="../js/controller/baseController.js"></script>
<script type="text/javascript" src="../js/controller/brandController.js"></script>
```

# 2. 代码生成器的使用

* 注意
  1. 代码生成器不能放在中文目录下执行
  2. 指定生成的代码所在的包名(3级)

# 3. 规格管理

**规格表**

| Field     | Type                |Comment|
| --------- | ------------------- |-------|
| id        | bigint(20) NOT NULL |ID|
| spec_name | varchar(255) NULL   |规格名称|

**规格选项表**

| Field       | Type                |Comment|
| ----------- | ------------------- |-------|
| id          | bigint(20) NOT NULL |ID|
| option_name | varchar(200) NULL   |规格选项名称|
| spec_id     | bigint(30) NULL     |所属规格|
| orders      | int(11) NULL        |排序|

### 3.1 规格列表

```html
<script type="text/javascript" src="../plugins/angularjs/angular.min.js"></script>

<!-- 分页组件 -->
<script src="../plugins/angularjs/pagination.js"></script>
<link rel="stylesheet" href="../plugins/angularjs/pagination.css">

<script type="text/javascript" src="../js/base_pagination.js"></script>
<script type="text/javascript" src="../js/service/specificationService.js"></script>
<script type="text/javascript" src="../js/controller/baseController.js"></script>
<script type="text/javascript" src="../js/controller/specificationController.js"></script>


<body  ng-app="pinyougou" ng-controller="specificationController">
    <table>
        <!--标题-->
        <tr>
        	...
        </tr>
        <!--内容-->
        <tr ng-repeat="entity in list">
        	...
        </tr>
    </table>
    <!--分页工具栏-->
    <tm-pagination conf="paginationConf"></tm-pagination>
</body>
```

### 3.2 新增规格

**新增行**

* HTML

```html
<!--弹框时,初始化规格选项数组-->
<button type="button"  
        title="新建" 
        data-toggle="modal" data-target="#editModal" ng-click="entity={specificationOptionList:[]}">		新建
</button>

<!--点击新增按钮,给数组中添加JSON对象-->
<button type="button"  title="新建" ng-click="addTableRow()">新增规格选项</button>

--------------------------------------------------------------------------------
<tr ng-repeat="pojo in entity.specificationOptionList">
	<td>
		<input   placeholder="规格选项" ng-model="pojo.optionName"> 
	</td>
	<td>
		<input   placeholder="排序" ng-model="pojo.orders"> 
	</td>
</tr>
```

* Javascript

```javascript
$scope.addTableRow=function(){
	$scope.entity.specificationOptionList.push({});			
}
```

**删除行**

* HTML

```html
<tr ng-repeat="pojo in entity.specificationOptionList">
	...	
    
    <button type="button" title="删除" ng-click="deleTableRow($index)" >删除</button>
</tr>


```

* Javascript

```javascript
$scope.deleTableRow=function(index){
	$scope.entity.specificationOptionList.splice(index,1);
}
```

**保存规格**

同时需要给2张表(规格和规格选项表中保存数据)

* 页面提交的数据格式

```javascript
{"specification":{},"specificationOptionList":[{},{}]}
```

* 组合实体类

```java
public class Specification implements Serializable{
	private TbSpecification specification;	
	private List<TbSpecificationOption> specificationOptionList;
}
```

* HTML:绑定数据

```html
<input placeholder="规格名称" ng-model="entity.specification.specName">

<tr ng-repeat="pojo in entity.specificationOptionList">
	<td>
		<input   placeholder="规格选项" ng-model="pojo.optionName"> 
	</td>
	<td>
		<input   placeholder="排序" ng-model="pojo.orders"> 
	</td>
</tr>

<button ng-click="save()">保存</button>

```

* Javascript

```javascript
$scope.save=function(){				
	var serviceObject;//服务层对象  				
	if($scope.entity.specification.id!=null){//如果有ID
		serviceObject=specificationService.update( $scope.entity ); //修改  
	}else{
		serviceObject=specificationService.add( $scope.entity  );//增加 
	}				
	serviceObject.success(
		function(response){
			if(response.success){
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		}		
	);				
}

//增加 
this.add=function(entity){
	return  $http.post('../specification/add.do',entity );
}
//修改 
this.update=function(entity){
	return  $http.post('../specification/update.do',entity );
}
```

* Web层

```java
@RequestMapping("/add")
public Result add(@RequestBody Specification specification){
	try {
		specificationService.add(specification);
		return new Result(true, "增加成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "增加失败");
	}
}
```

* 服务层

```java
public void add(Specification specification) {
	//获取规格实体,保存规格
	TbSpecification tbspecification = specification.getSpecification();				
	specificationMapper.insert(tbspecification);	
	
	//获取规格选项集合,保存规格选项
	List<TbSpecificationOption> specificationOptionList = 	specification.getSpecificationOptionList();
	
    for( TbSpecificationOption option:specificationOptionList){
		option.setSpecId(tbspecification.getId());//设置规格ID
		specificationOptionMapper.insert(option);//新增规格
	}
}
```

### 3.3 修改规格

**数据回显** 

* HTML 

```html
<button type="button" data-target="#editModal" ng-click="findOne(entity.id)">修改</button> 
```

* Javascript

```javascript
$scope.findOne=function(id){				
	specificationService.findOne(id).success(
		function(response){
			$scope.entity= response;					
		}
	);				
}
```

* Web层

```java
@RequestMapping("/findOne")
public Specification findOne(Long id){
	return specificationService.findOne(id);		
}
```

* 服务层

```java
public Specification findOne(Long id){
	
    //创建组合实体类
	Specification specification=new Specification();
	
    //封装规格数据
	TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
	specification.setSpecification(tbSpecification);
	
    //封装规格选项数据
	TbSpecificationOptionExample example=new TbSpecificationOptionExample();
	com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
	criteria.andSpecIdEqualTo(id);
	List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
	specification.setSpecificationOptionList(specificationOptionList);
    
	return specification;
}
```

**修改数据** 

* Web层

```java
@RequestMapping("/update")
public Result update(@RequestBody Specification specification){
	try {
		specificationService.update(specification);
		return new Result(true, "修改成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "修改失败");
	}
}	
```

* 服务层

```java
@Override
public void update(Specification specification){
	
	//获取规格实体
	TbSpecification tbspecification = specification.getSpecification();				
	specificationMapper.updateByPrimaryKey(tbspecification);	
	
	//删除原来规格对应的规格选项	
	TbSpecificationOptionExample example=new TbSpecificationOptionExample();
	com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
	criteria.andSpecIdEqualTo(tbspecification.getId());
	specificationOptionMapper.deleteByExample(example);
	
	//获取规格选项集合
	List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
    //新增规格选项
	for( TbSpecificationOption option:specificationOptionList){
		option.setSpecId(tbspecification.getId());//设置规格ID
		specificationOptionMapper.insert(option);//新增规格
	}
}
```

### 3.4 删除规格

* 前台

  略

* Web层

  略

* 服务层

```java
public void delete(Long[] ids) {
	for(Long id:ids){
		//删除规格表数据
		specificationMapper.deleteByPrimaryKey(id);
		//删除规格选项表数据		
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		specificationOptionMapper.deleteByExample(example);
	}
}
```

# 4. 模板管理

模板用于关联规格和品牌信息

| Field                  | Type                | Comment    |
| ---------------------- | ------------------- | ---------- |
| id                     | bigint(11) NOT NULL | ID         |
| name                   | varchar(80) NULL    | 模板名称   |
| spec_ids               | varchar(1000) NULL  | 关联规格   |
| brand_ids              | varchar(1000) NULL  | 关联品牌   |
| custom_attribute_items | varchar(2000) NULL  | 自定义属性 |

### 4.1 模板列表

略

### 4.2 新增模板

### 4.3 修改模板

### 4.4 删除模板

### 4.5 优化列表显示