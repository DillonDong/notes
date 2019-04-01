# 1. 价格区间筛选

## 1.1 思路分析

1. 查询在某个价格区间的商品，前台给后台传递【价格下限-价格上限】参数
2. 后台获得价格区间的参数，过滤查询商品价格大于下限，小于上线的集合。

## 1.2 代码实现

* html页面

```html
<!--面包屑-->
<ul>
    ...
   	<li  
        ng-if="searchMap.price!=''" 
        ng-click="removeSearchItem('price')">
        	价格：{{searchMap.price}}
    </li>
</ul>
<!--过滤面板-->
<ul>
    <li>
		<a href="#" ng-click="addSearchItem('price','0-500')">0-500元</a>
    </li>
    <li>
        <a href="#" ng-click="addSearchItem('price','500-1000')">500-1000元</a>
    </li>
    <li>
        <a href="#" ng-click="addSearchItem('price','1000-1500')">1000-1500元</a>
    </li>
    <li>
        <a href="#" ng-click="addSearchItem('price','1500-2000')">1500-2000元</a>
    </li>
    <li>
        <a href="#" ng-click="addSearchItem('price','2000-3000')">2000-3000元 </a>
    </li>
    <li>
        <a href="#" ng-click="addSearchItem('price','3000-*')">3000元以上</a>
    </li>
</ul>
```

* searchController.js

```json
$scope.searchMap={
    'keywords':'',	//关键字
    'category':'',	//分类
    'brand':'',		//品牌
    'spec':{},		//规格
    'price':''		//价格
};

//构建搜索条件对象
$scope.addSearchItem=function(key,value){
	
	if(key=='category' || key=='brand' || key=='price'){//如果用户点击的是分类或品牌
		$scope.searchMap[key]=value;
		
	}else{//用户点击的是规格
		$scope.searchMap.spec[key]=value;		
	}
	$scope.search();//查询
}

//撤销搜索项
$scope.removeSearchItem=function(key){
    if(key=='category' || key=='brand' || key=='price' ){//如果用户点击的是分类或品牌
        $scope.searchMap[key]="";
    }else{//用户点击的是规格
        delete $scope.searchMap.spec[key];
    }
    $scope.search();//查询
}
```

* 后台进行查询

```java
//查询商品列表
private Map searchList(Map searchMap){
	...
	//按价格过滤
	if(!StringUtils.isEmpty(searchMap.get("price"))){
		String[] price = ((String) searchMap.get("price")).split("-");
		if(!price[0].equals("0")){ //如果最低价格不等于0
			FilterQuery filterQuery=new SimpleFilterQuery();
			Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);	
		}
		if(!price[1].equals("*")){ //如果最高价格不等于
			FilterQuery filterQuery=new SimpleFilterQuery();
			Criteria filterCriteria=new Criteria("item_price").lessThanEqual(price[1]);
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);	
		}			
	}
	...
}
```

# 2. 搜索结果分页

## 2.1 思路分析

前台给后台传递数据：

```
1. 当前页码:pageNo
2. 每页记录数:pageSize
```

后台给前台返回：

```
1. 总记录数:total
2. 总页数:totalPages
3. 数据集合:rows
```

实现步骤：

```
1. 显示分页的工具栏
2. 点击分页工具栏查询数据
3. 前后省略点
```

## 2.2 代码实现

* HTML

```html
<body ng-app="pinyougou" ng-controller="searchController"> 
    <!--搜索框-->
    <input type="text" id="autocomplete" ng-model="searchMap.keywords"  />
    <button ng-click="searchMap.pageNo=1;search()" type="button">搜索</button>
    ...
    <!--分页栏-->
    <ul>
        <!--上一页-->
        <li class="prev {{isTopPage()?'disabled':''}}">
            <a href="#" ng-click="queryByPage(searchMap.pageNo-1)">«</a>
        </li>

        <!--前省略点-->
        <li ng-if="firstDot==true"><span>...</span></li>

        <!--页码-->
        <li ng-repeat="page in pageLabel">
            <a href="#" ng-click="queryByPage(page)">{{page}}</a>
        </li>

        <!--后省略点-->
        <li ng-if="lastDot==true"><span>...</span></li>

        <!--下一页-->
        <li class="next {{isEndPage()?'disabled':''}}">
            <a href="#" ng-click="queryByPage(searchMap.pageNo+1)">»</a>
        </li>
	</ul>
    <!--总页数-->
    <span>共{{resultMap.totalPages}}页&nbsp;</span>
    <!--快速跳转页码-->
    到第<input type="text" class="page-num" ng-model="searchMap.pageNo">页
    <button class="page-confirm" ng-click="queryByPage(searchMap.pageNo)" >确定</button>
</body>
```

* searchController.js

```javascript
$scope.searchMap={
    'keywords':'',	//关键字
    'category':'',	//分类
    'brand':'',		//品牌
    'spec':{},		//规格
    'price':'',		//价格
    'pageNo':1,		//页码
    'pageSize':40,	//每页记录数
    'sort':'',		//是否排序
    'sortField':''	//排序字段
};

//搜索方法
$scope.search=function(){
	$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);//转换为数字
	searchService.search($scope.searchMap).success(
		function(response){
			$scope.resultMap=response;	
            //构建分页栏
			buildPageLabel();		
		}
	);		
}

//构建分页工具栏
buildPageLabel=function(){
	//构建分页栏
	$scope.pageLabel=[];	//存放页码

	//开始页码默认值
	var begin=1;
	//截止页码默认值
	var end=$scope.resultMap.totalPages;

	//是否显示前后省略点
	$scope.firstDot=true;
    $scope.lastDot=true;

	//校准begin和end
    if($scope.resultMap.totalPages>5){
		if($scope.searchMap.pageNo<=3){		//前5页
			//校准end
			end=5;
            $scope.firstDot=false;	//不显示前省略点
		}else if($scope.searchMap.pageNo+2>=$scope.resultMap.totalPages){	//后5页
			//校准begin
			begin=$scope.resultMap.totalPages-4;
            $scope.lastDot=false;	//不显示后省略点
		}else{	//中间页码
			begin=$scope.searchMap.pageNo-2;
			end=$scope.searchMap.pageNo+2;
		}
	}else{	//页码不足5页,前后省略点均省略
        $scope.firstDot=false;
        $scope.lastDot=false;
	}

	//构建页码
	for(var i=begin;i<=end;i++){
		$scope.pageLabel.push(i);
	}
}

//分页查询
$scope.queryByPage=function(pageNo){
    if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
        return ;
    }
    $scope.searchMap.pageNo=pageNo;
    $scope.search();//查询
}

//判断当前页是否为第一页
$scope.isTopPage=function(){
	if($scope.searchMap.pageNo==1){
		return true;
	}else{
		return false;
	}		
}

//判断当前页是否为最后一页
$scope.isEndPage=function(){
	if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
		return true;
	}else{
		return false;
	}	
}
```

* 后台SearchServiceImpl

```java
private Map searchList(Map searchMap){
	Map map=new HashMap();
	...
	//分页
	Integer pageNo= (Integer) searchMap.get("pageNo");//获取页码
	if(pageNo==null){
		pageNo=1;
	}
	Integer pageSize= (Integer) searchMap.get("pageSize");//获取页大小
	if(pageSize==null){
		pageSize=20;
	}
	
	query.setOffset( (pageNo-1)*pageSize  );//起始索引
	query.setRows(pageSize);//每页记录数

	//***********  获取高亮结果集  ***********
	//高亮页对象
	HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

	map.put("rows", page.getContent());
	map.put("totalPages", page.getTotalPages());//总页数
	map.put("total", page.getTotalElements());//总记录数
	return map;
}
```

# 3. 多关键字搜索

后台接收到关键字后,将关键字进行分词后再和solr中词条进行匹配

* 注意要对关键字中的空格进行过滤

```java
//空格处理
String keywords= (String)searchMap.get("keywords");
searchMap.put("keywords", keywords.replace(" ", ""));//关键字去掉空格
```

# 4. 多维度排序

order by 【字段名称】 【排序方式】

## 4.1 价格排序和新品排序

* HTML

```html
<ul class="sui-nav">
	<li class="active">
		<a href="#" ng-click="sortSearch('','')">综合</a>
	</li>
	<li>
		<a href="#">销量</a>
	</li>
	<li>
		<a href="#" ng-click="sortSearch('updatetime','DESC')">新品</a>
	</li>
	<li>
		<a href="#">评价</a>
	</li>
	<li>
		<a href="#" ng-click="sortSearch('price','ASC')">价格↑</a>
	</li>
	<li>
		<a href="#" ng-click="sortSearch('price','DESC')">价格↓</a>
	</li>
</ul>
```

* JS

```javascript
$scope.searchMap={
    'keywords':'',	//关键字
    'category':'',	//分类
    'brand':'',		//品牌
    'spec':{},		//规格
    'price':'',		//价格
    'pageNo':1,		//页码
    'pageSize':40,	//每页记录数
    'sort':'',		//是否排序
    'sortField':''	//排序字段
};
//排序查询
$scope.sortSearch=function(sortField,sort){
	$scope.searchMap.sortField=sortField;
	$scope.searchMap.sort=sort;
	$scope.search();//查询
}
```

* 后台代码

```java
//查询商品列表
private Map searchList(Map searchMap){
	String sortValue= (String)searchMap.get("sort");//升序ASC 降序DESC
    String sortField=  (String)searchMap.get("sortField");//排序字段

    //排序查询
    if(sortValue!=null && !sortValue.equals("")){
        if(sortValue.equals("ASC")){
            Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
            query.addSort(sort);				
        }
        if(sortValue.equals("DESC")){
            Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
            query.addSort(sort);				
        }
    }
}
```

## 4.2 销量排序

* 思路分析

```
1. 在索引库中添加表示销量的域
2. 以某个固定的周期(月/周)统计销量
3. 通过定时任务在更新索引库
```

## 4.3 评价排序思路

* 思路分析

```
1. 将用户的评价进行量化处理(好评:3分;中评:1分;差评:-3)
2. 在索引库中添加表示评价的域
3. 通过定时任务定期统计每个商品的评价分数更新到索引库
```

# 5. 搜索系统与首页系统对接

## 5.1 思路分析

1. 用户在首页输入关键字,点击搜索按钮
2. 首页跳转到搜索页,并将搜索关键字传递到搜索页
3. 搜索页通过$location对象获得首页传递的搜索关键字
4. 搜索页将获取到搜索关键字传递到后台进行搜索

## 5.2 代码实现

### 5.2.1 首页

* html

```html
<input type="text"  ng-model="keywords" type="text"/>
<button ng-click="search()" type="button">搜索</button>
```

* JS

```javascript
//搜索  （传递参数）
$scope.search=function(){
	location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
}
```

### 5.2.2 搜索页

* html

```html
<body ng-app="pinyougou" ng-controller="searchController" ng-init="loadkeywords()">
    ...
</body>
```

* JS

```javascript
//加载关键字
$scope.loadkeywords=function(){
    $scope.searchMap.keywords= $location.search()['keywords'];
    $scope.search();//查询
}
```

# 6. 索引库更新和删除

## 6.1 更新

更新时机:在运营商后台审核通过商品的时候后才更新solr索引库

* 运营商后台GoodsController.java

```java
@RequestMapping("/updateStatus")
public Result updateStatus(Long[] ids,String status){	
	try {
		goodsService.updateStatus(ids, status);
		if("1".equals(status)){//如果是审核通过 
			//得到需要导入的SKU列表
			List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids,status);
			//导入到solr
			itemSearchService.importList(itemList);				
		}
		return new Result(true, "修改状态成功"); 
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "修改状态失败");
	}
}
```

* 运营商服务-sellergoods-service

  从数据库中查询审核通过的商品集合

```java
public List<TbItem>	findItemListByGoodsIdListAndStatus(Long[]goodsIds,String status){
	TbItemExample example=new TbItemExample();
	com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
	criteria.andStatusEqualTo(status);//状态
	criteria.andGoodsIdIn( Arrays.asList(goodsIds));//指定条件：SPUID集合
	return itemMapper.selectByExample(example);
}
```

* 搜索服务-search-service

  导入审核通过的商品集合到Solr中

```java
public void importList(List list) {
	solrTemplate.saveBeans(list);
	solrTemplate.commit();
}
```

## 6.2 删除

* 运行商后台GoodsController.java

```java
/**
 * 批量删除
 */
@RequestMapping("/delete")
public Result delete(Long [] ids){
	try {
		goodsService.delete(ids);
		
		//从索引库中删除
		itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
					
		return new Result(true, "删除成功"); 
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "删除失败");
	}
}
```

* 搜索服务-search-service

```java
public void deleteByGoodsIds(List goodsIds) {
			
	Query query=new SimpleQuery("*:*");		
	Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
	query.addCriteria(criteria);		
	solrTemplate.delete(query);
	solrTemplate.commit();
}
```