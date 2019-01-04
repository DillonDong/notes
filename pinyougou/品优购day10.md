[TOC]

# 1. 搜索结果高亮显示

### 1.1 思路分析

页面的高亮显示,本质是对搜索的关键字进行了样式处理

### 1.2 实现步骤

1. 后台指定在哪些域中进行高亮显示
2. 后台确定高亮域中包裹关键字的HTML标签(开始表现和结束标签)和样式

### 1.3 代码实现

* 后台

```java
//查询列表
private Map searchList(Map searchMap){
	Map map=new HashMap();
	//高亮选项初始化
	HighlightQuery query=new SimpleHighlightQuery();		
	HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
	highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
	highlightOptions.setSimplePostfix("</em>");		
	query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项
	
	//关键字查询
	Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
	query.addCriteria(criteria);

	//***********  获取高亮结果集  ***********
	//高亮页对象
	HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
	//高亮入口集合(每条记录的高亮入口)
	List<HighlightEntry<TbItem>> entryList = page.getHighlighted();		
	for(HighlightEntry<TbItem> entry:entryList  ){
		//获取高亮列表(高亮域的个数)
		List<Highlight> highlightList = entry.getHighlights();		
		if(highlightList.size()>0 &&  highlightList.get(0).getSnipplets().size()>0 ){
			TbItem item = entry.getEntity();
			item.setTitle(highlightList.get(0).getSnipplets().get(0));			
		}			
	}
	map.put("rows", page.getContent());
	return map;
	
}
```

* 页面

```html
<body ng-app="pinyougou" ng-controller="searchController">
    <input type="text" id="autocomplete" ng-model="searchMap.keywords"  />
    <button ng-click="search()" type="button">搜索</button>
    ....
    <ul>
		<li  ng-repeat="item in resultMap.rows">	
			<a href="item.html" target="_blank"><img src="{{item.image}}" /></a>
			<i>{{item.price}}</i>
            <!--{{item.title}}-->
            <div class="attr" ng-bind-html="item.title | trustHtml"></div>
		</li>
	</ul>
</body>
```

* baseController.js

```javascript
// 定义模块:
var app = angular.module("pinyougou",[]);
// 定义过滤器
app.filter('trustHtml',['$sce',function($sce){
	return function(data){//传入参数时被过滤的内容
		return $sce.trustAsHtml(data);//返回的是过滤后的内容（信任html的转换）
	}	
} ]);
```

# 2. 搜索页面过滤面板的实现思路分析

* 思路分析

过滤面板的数据来自于搜索的商品,目的是对搜索结果进行过滤

* 过滤面板的显示
  1. 针对搜索条件,对商品数据进行按照分类进行分组查询
  2. 将分组的结果(分类信息)封装到结果集
  3. 默认根据第一个分类信息,查询其对应的模板
  4. 根据模板查询其对应的品牌信息,封装到结果集
  5. 根据模板查询其对应的规格信息,封装到结果集
* 过滤查询
  1. 用户点击过滤面板的分类、品牌和规格等过滤条件时，将条件封装到searchEntity中
  2. 后台根据过滤条件筛选结果
  3. 前台隐藏用户已点击的条件

# 3. 显示过滤面板中分类信息

# 4. 缓存品牌和规格信息

# 5. 显示过滤面板中品牌和规格信息

# 6. 过滤查询