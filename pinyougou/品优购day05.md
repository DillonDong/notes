[TOC]

# 1. 商品分类管理

| Field     | Type                | Comment                          |
| --------- | ------------------- | -------------------------------- |
| id        | bigint(20) NOT NULL | 类目ID                           |
| parent_id | bigint(20) NULL     | 父类目ID=0时，代表的是一级的类目 |
| name      | varchar(50) NULL    | 类目名称                         |
| type_id   | bigint(11) NULL     | 类型id                           |

### 1.1 查询商品分类

 ```sql
--一级分类
SELECT * FROM tb_item_cat WHERE parent_id=0;
--二级分类
SELECT * FROM tb_item_cat WHERE parent_id=161;
--三级分类
SELECT * FROM tb_item_cat WHERE parent_id=162;
 ```

* item_cat.html

```html
<script type="text/javascript" src="../js/base.js"></script>
<script type="text/javascript" src="../js/service/itemCatService.js"></script>
<script type="text/javascript" src="../js/controller/baseController.js"></script>
<script type="text/javascript" src="../js/controller/itemCatController.js"></script>

<body ng-app="pinyougou" ng-controller="itemCatController" ng-init="findByParentId(0)">
    ...
    <tr ng-repeat="entity in list">
        <td><input  type="checkbox" ></td>			                              
        <td>{{entity.id}}</td>
        <td>{{entity.name}}</td>									    
        <td>
            {{entity.typeId}}    
        </td>									      
        <td class="text-center">
            <button ng-click="findByParentId(entity.id)">查询下级</button>
        </td>
    </tr>
</body>
```

* itemCatController.js

```javascript
$scope.findByParentId=function(parentId){
	itemCatService.findByParentId(parentId).success(
		function(response){
			$scope.list=response;				
		}
	);		
}
```

* itemCatService.js

```javascript
this.findByParentId=function(parentId){
	return $http.get('../itemCat/findByParentId.do?parentId='+parentId);
}
```

* ItemCatController.java

```java
@RequestMapping("/findByParentId")
public List<TbItemCat> findByParentId(Long parentId){
	return itemCatService.findByParentId(parentId);
}
```

* ItemCatServiceImpl.java

```java
public List<TbItemCat> findByParentId(Long parentId) {
	TbItemCatExample example=new TbItemCatExample();
	Criteria criteria = example.createCriteria();
	criteria.andParentIdEqualTo(parentId);
	return itemCatMapper.selectByExample(example);
}
```

### 1.2 面包屑

面包屑的作用记录用户在页面的浏览足迹

* item_cat.html

```html
<!--面包屑导航-->
<ol class="breadcrumb">	                        	
    <li>
		 <a href="#" ng-click="grade=1;selectList({id:0})">顶级分类列表</a> 
	</li>
	<li>
		 <a href="#" ng-click="grade=2;selectList(entity_1)">{{entity_1.name}}</a>
	</li>
	<li>
		 <a href="#" ng-click="grade=3;selectList(entity_2)" >{{entity_2.name}}</a>
	</li>
</ol>
<!--查询下级按钮-->
<span ng-if="grade!=3">
	<button type="button" ng-click="setGrade(grade+1);selectList(entity)">查询下级</button>
</span>
```

* itemCatController.js

```javascript
//当前级别
$scope.grade=1;
//设置级别
$scope.setGrade=function(value){
	$scope.grade=value;
}

//查询下级:加载数据并更新面包屑
$scope.selectList=function(p_entity){
	//第一级
	if($scope.grade==1){
		$scope.entity_1=null;
		$scope.entity_2=null;
	}
    //第二季
	if($scope.grade==2){
		
		$scope.entity_1=p_entity;
		$scope.entity_2=null;
	}
    //第三级
	if($scope.grade==3){
		$scope.entity_2=p_entity;
	}
	
	$scope.findByParentId(p_entity.id);
	
}

//根据上级分类ID查询列表
$scope.findByParentId=function(parentId){
    itemCatService.findByParentId(parentId).success(
        function(response){
            $scope.list=response;
        }
    );
}
```

# 2. 电商概念

### 2.1 SPU

SPU = Standard Product Unit  （标准产品单位）

### 2.2 SKU

SKU=stock keeping unit（库存量单位）

| 名词                  | 类型                                       |
| --------------------- | ------------------------------------------ |
| 手机                  | 分类                                       |
| Iphone X              | SPU      （土豪金、黑色 \|  移动、联通  ） |
| 联通版+土豪金Iphone X | SKU                                        |
| 移动版+黑色Iphone X   | SKU                                        |
| 联通版+黑色Iphone X   | SKU                                        |
| 移动版+土豪金Iphone X | SKU                                        |

### 2.3 表结构

**商品表(SPU)**

| Field            | Type                | Comment      |
| ---------------- | ------------------- | ------------ |
| id               | bigint(20) NOT NULL | 主键         |
| seller_id        | varchar(20) NULL    | 商家ID       |
| goods_name       | varchar(100) NULL   | SPU名        |
| default_item_id  | bigint(20) NULL     | 默认SKU      |
| audit_status     | varchar(2) NULL     | 状态         |
| is_marketable    | varchar(1) NULL     | 是否上架     |
| brand_id         | bigint(10) NULL     | 品牌         |
| caption          | varchar(100) NULL   | 副标题       |
| category1_id     | bigint(20) NULL     | 一级类目     |
| category2_id     | bigint(10) NULL     | 二级类目     |
| category3_id     | bigint(10) NULL     | 三级类目     |
| small_pic        | varchar(150) NULL   | 小图         |
| price            | decimal(10,2) NULL  | 商城价       |
| type_template_id | bigint(20) NULL     | 分类模板ID   |
| is_enable_spec   | varchar(1) NULL     | 是否启用规格 |
| is_delete        | varchar(1) NULL     | 是否删除     |

**商品详情表**

| Field                  | Type                | Comment                              |
| ---------------------- | ------------------- | ------------------------------------ |
| goods_id               | bigint(20) NOT NULL | SPU_ID                               |
| introduction           | varchar(3000) NULL  | 描述                                 |
| specification_items    | varchar(3000) NULL  | 规格结果集，所有规格，包含isSelected |
| custom_attribute_items | varchar(3000) NULL  | 自定义属性（参数结果）               |
| item_images            | varchar(3000) NULL  |                                      |
| package_list           | varchar(3000) NULL  | 包装列表                             |
| sale_service           | varchar(3000) NULL  | 售后服务                             |

**SKU表**

| Field          | Type                   | Comment                          |
| -------------- | ---------------------- | -------------------------------- |
| id             | bigint(20) NOT NULL    | 商品id，同时也是商品编号         |
| title          | varchar(100) NOT NULL  | 商品标题                         |
| sell_point     | varchar(500) NULL      | 商品卖点                         |
| price          | decimal(20,2) NOT NULL | 商品价格，单位为：元             |
| stock_count    | int(10) NULL           | 库存数量                         |
| num            | int(10) NOT NULL       | 库存数量                         |
| barcode        | varchar(30) NULL       | 商品条形码                       |
| image          | varchar(2000) NULL     | 商品图片                         |
| categoryId     | bigint(10) NOT NULL    | 所属类目，叶子类目               |
| status         | varchar(1) NOT NULL    | 商品状态，1-正常，2-下架，3-删除 |
| create_time    | datetime NOT NULL      | 创建时间                         |
| update_time    | datetime NOT NULL      | 更新时间                         |
| item_sn        | varchar(30) NULL       | 条形码                           |
| cost_price     | decimal(10,2) NULL     | 成本价                           |
| market_price   | decimal(10,2) NULL     | 市场价                           |
| is_default     | varchar(1) NULL        | 默认SKU                          |
| goods_id       | bigint(20) NULL        | 所属SPU                          |
| seller_id      | varchar(30) NULL       | 商家ID                           |
| cart_thumbnail | varchar(150) NULL      | 缩略图                           |
| category       | varchar(200) NULL      | 分类                             |
| brand          | varchar(100) NULL      | 品牌                             |
| spec           | varchar(200) NULL      | 规格                             |
| seller         | varchar(200) NULL      | 所属商家                         |

### 2.4 组合实体类

```java
public class Goods implements Serializable{
	private TbGoods goods;			//商品SPU基本信息
	private TbGoodsDesc goodsDesc;	//商品SPU扩展信息
	private List<TbItem> itemList;  //SKU列表
}
```

### 2.5 entity结构

```
{
   goods:{},
   goodsDesc:{}
   itemList:{}
}
```

# 3. 商品基本信息录入

* goods_edit.html

```html
<script type="text/javascript" src="../js/base.js"></script>
<script type="text/javascript" src="../js/service/goodsService.js"></script>
<script type="text/javascript" src="../js/service/uploadService.js"></script>
<script type="text/javascript" src="../js/controller/baseController.js"></script>
<script type="text/javascript" src="../js/controller/goodsController.js"></script>

<body ng-app="pinyougou" ng-controller="goodsController">
	<input type="text" ng-model="entity.goods.goodsName"   placeholder="商品名称">
	<input type="text" ng-model="entity.goods.caption" placeholder="副标题">
	<input type="text" ng-model="entity.goods.price" placeholder="价格">
    <textarea rows="4" ng-model="entity.goodsDesc.packageList" placeholder="包装列表">
	<textarea rows="4" ng-model="entity.goodsDesc.saleService"    placeholder="售后服务">
	...
	<button  ng-click="add()">保存</button>
</body>
```

* goodsController.js

```html
$scope.add=function(){				
	goodsService.add( $scope.entity  ).success(
		function(response){
			if(response.success){
				alert("新增成功");
				$scope.entity={};
			}else{
				alert(response.message);
			}
		}		
	);				
}
```

* GoodsController.java

```java
@RequestMapping("/add")
public Result add(@RequestBody Goods goods){
	//获取商家ID
	String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
	goods.getGoods().setSellerId(sellerId);//设置商家ID
	
	try {
		goodsService.add(goods);
		return new Result(true, "增加成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "增加失败");
	}
}
```

* GoodsServiceImpl.java

```java
@Override
public void add(Goods goods) {
	
	goods.getGoods().setAuditStatus("0");//状态：未审核
	goodsMapper.insert(goods.getGoods());//插入商品基本信息
	
	goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//将商品基本表的ID给商品扩展表
	goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展表数据
	
}
```

# 3. 富文本编辑器

编辑丰富的文本信息，达到所写即所得的效果。

### 3.1 如何使用

* 页面

```html
<link rel="stylesheet" href="../plugins/kindeditor/themes/default/default.css" />
<script charset="utf-8" src="../plugins/kindeditor/kindeditor-min.js"></script>
<script charset="utf-8" src="../plugins/kindeditor/lang/zh_CN.js"></script>

<body>
    <textarea name="content"></textarea>
</body>
```

* Javascript:初始化富文本编辑器

```javascript
var editor;
KindEditor.ready(function(K) {
	editor = K.create('textarea[name="content"]', {
		allowFileManager : true
	});
});
```

### 3.2 保存商品详情

```javascript
$scope.add=function(){		
    //获得富文本编辑器信息
	$scope.entity.goodsDesc.introduction=editor.html();
	goodsService.add( $scope.entity  ).success(
		function(response){
			if(response.success){
				alert("新增成功");
				$scope.entity={};
				editor.html("");//清空富文本编辑器
			}else{
				alert(response.message);
			}
		}		
	);				
}
```

# 4. 分布式文件服务器FastDFS

使用分布式方式解决海量文件存储的问题

* Tracker server：调度服务器；负责负载均衡和任务调度，管理所有的存储服务器。
* Storage server：存储服务器；负责文件的存储。

# 5. 商品图片录入

# 6. AngularJS文件上传

