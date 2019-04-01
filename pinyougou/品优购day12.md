# 1. FreeMarker介绍

## 1.1 静态化页面的特点

1. 访问量比较大
2. 页面的数据基本不会改变

## 1.2 页面静态化处理原理

模板+数据=静态页面

![](./pic/freemarker.png)

## 1.3 静态化技术好处

1. 减少了数据库的访问的压力
2. 有利于SEO优化
3. 可以很好的实现网站的动静分离

# 2. 入门案例

## 2.1 步骤分析

1. 创建模板文件
2. 使用数据填充模板，生成静态页面

## 2.2 编码实现

### 2.2.1 模板文件

```html
<html>
    <head>
        <meta charset="utf-8">
        <title>Freemarker 入门小 DEMO </title>
    </head>
    <body>
        <#--我只是一个注释，我不会有任何输出 -->
        ${name},你好。${message}
        <hr>
        <#assign linkman="周先生">
        联系人：${linkman}
        <hr>
        <#assign info={"mobile":"13301231212",'address':'北京市昌平区王府街'} >
        电话：${info.mobile} 地址：${info.address}
        <hr>
        <#include "head.ftl">
        <hr>
        <#if success=true>
            你已通过实名认证
        <#else>
            你未通过实名认证
        </#if>
        <hr>
        <#list goodsList as goods>
            ${goods_index+1} 商品名称： ${goods.name} 价格：${goods.price}<br>
        </#list>
        共 ${goodsList?size} 条记录
        <hr>
        <#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
        <#assign data=text?eval />
        开户行：${data.bank} 账号：${data.account}
        <hr>
        当前日期：${today?date} <br>
        当前时间：${today?time} <br>
        当前日期+时间：${today?datetime} <br>
        日期格式化： ${today?string("yyyy 年 MM 月")}
        <hr>
        累计积分：${point?c}
        <hr>
        <#if aaa??>
            aaa 变量存在
        <#else>
            aaa 变量不存在
        </#if> 
        <hr>
        ${aaa!'-'}
    </body>
</html>
```

### 2.2.2 填充数据

```java
public static void main(String[] args) throws IOException, TemplateException {
    //1.创建配置类
    Configuration configuration = new Configuration(Configuration.getVersion());
    //2.设置模板所在的目录
    configuration.setDirectoryForTemplateLoading(new 
            File("C:\\work\\workspace\\idea\\pyg\\freemarkerDemo\\src\\main\\resources"));
    //3.设置字符集
    configuration.setDefaultEncoding("utf-8");
    //4.加载模板
    Template template = configuration.getTemplate("test.ftl");
    //5.创建数据模型
    Map map = new HashMap();
    map.put("name", "张三");
    map.put("message", "欢迎来到神奇的品优购世界！");

    map.put("success", true);

    List goodsList=new ArrayList();
    Map goods1=new HashMap();

    goods1.put("name", "苹果");
    goods1.put("price", 5.8);
    Map goods2=new HashMap();
    goods2.put("name", "香蕉");
    goods2.put("price", 2.5);
    Map goods3=new HashMap();
    goods3.put("name", "橘子");
    goods3.put("price", 3.2);
    goodsList.add(goods1);
    goodsList.add(goods2);
    goodsList.add(goods3);
    map.put("goodsList", goodsList);

    map.put("today", new Date());

    map.put("point", 102920122);

    //6.创建 Writer 对象
    Writer out = new FileWriter(new File("C:\\Users\\fudingcheng\\Desktop\\freemarker\\test.html"));
    //7.输出
    template.process(map, out);
    //8.关闭 Writer 对象
    out.close();
}
```

## 2.3 指令

```html
<#assign name="value">
<#include "xxx.ftl">
<#if value=XXX>
<#list list as item>
```

## 2.4 内建函数 

```shell
# 时间
日期${today?date}
时间${today?time}
日期时间${today?datetime}
格式化时间${today?string("yyyy 年 MM 月")}
# 数字格式化	
${point?c}
# 非空判断
aaa??
${aaa!'-'}
```

# 3. 商品详情页显示

## 3.1 配置文件

* Spring配置文件

```xml
<bean id="freemarkerConfig"
	class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
    <!--模板的位置-->
	<property name="templateLoaderPath" value="/WEB-INF/ftl/" />
    <!--默认字符集-->
	<property name="defaultEncoding" value="UTF-8" />
</bean>
```

* 属性文件

```properties
pagedir=C:\\Users\\fudingcheng\\Desktop\\html\\
```

## 3.2 基本数据生成

* 后台数据填充

```java
@Autowired
private FreeMarkerConfigurer freeMarkerConfigurer;

@Value("${pagedir}")
private String pagedir;

@Autowired
private TbGoodsMapper goodsMapper;

@Autowired
private TbGoodsDescMapper goodsDescMapper;

public boolean genItemHtml(Long goodsId) {
	
	Configuration configuration = freeMarkerConfigurer.getConfiguration();
	
	try {
		Template template = configuration.getTemplate("item.ftl");
		//创建数据模型
		Map dataModel=new HashMap<>();
		//1.商品主表数据
		TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
		dataModel.put("goods", goods);
		//2.商品扩展表数据
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
		dataModel.put("goodsDesc", goodsDesc);
      	
		Writer out=new FileWriter(pagedir+goodsId+".html");
		template.process(dataModel, out);//输出
		out.close();
		return true;	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
}
```

* 前台模板

```html
${goods.caption}
${goods.price}
${goodsDesc.introduction}
${goodsDesc.packageList}	
${goodsDesc.saleService}
```

## 3.3 显示图片列表

```html
<!--图片列表集合-->
<#assign imageList=goodsDesc.itemImages?eval>

<!--遍历图片-->
<#--大图-->
<#if (imageList?size>0)></#i>)>
	<img jqimg="${imageList[0].url}" src="${imageList[0].url}" width="400px" height="400px" />
</#if>
<!--小图列表-->
<#list imageList as item>
	<li><img src="${item.url}" bimg="${item.url}" onmousemove="preview(this)" /></li>
</#list>    
```

## 3.4 扩展属性列表

```html
<!--扩展属性-->
<#assign customAttributeList=goodsDesc.customAttributeItems?eval>
    
<#list customAttributeList as item>
   	<#if item.value??>
	    <li>${item.text}：${item.value}</li>
	</#if>
</#list>
```

## 3.5 规格列表

```xml
<#assign specificationList=goodsDesc.specificationItems?eval>

<#list specificationList as specification>
    <dl>
        <dt>
            <div class="fl title">
                <!--规格名称-->
                <i>${specification.attributeName}</i>
            </div>
        </dt> 
                <!--规格属性-->
        <#list specification.attributeValue as item> 
            <dd><a href="javascript:;">${item}</a></dd>
        </#list>
    </dl>
</#list>
```

## 3.6 商品分类面包屑

* 后台

```java
String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
dataModel.put("itemCat1", itemCat1);
dataModel.put("itemCat2", itemCat2);
dataModel.put("itemCat3", itemCat3);
```

* 模板

```html
<ul class="sui-breadcrumb">
    <li><a href="#">${itemCat1}</a></li>
    <li><a href="#">${itemCat2}</a></li>
    <li><a href="#">${itemCat3}</a></li>
</ul>
```

# 4. 页面逻辑处理

## 4.1 商品数量增减

* 模板

```html
<div class="controls">
    <input autocomplete="off" type="text" value="{{num}}" minnum="1" class="itxt" />
    <a href="javascript:void(0)"  ng-click="addNum(1)">+</a>
    <a href="javascript:void(0)"  ng-click="addNum(-1)">-</a>
</div>
```

* JS

```javascript
//数量加减
$scope.addNum=function(x){
	$scope.num+=x;
	if($scope.num<1){
		$scope.num=1;
	}		
}
```

## 4.2 规格选择高亮

* 模板

```html
<!--规格选项-->
<dd>
    <a class="{{isSelected('${specification.attributeName}','${item}')?'selected':''}}"
       ng-click="selectSpecification('${specification.attributeName}','${item}')">
    	${item}
    </a>
</dd>
```

* JS

```javascript
$scope.specificationItems={};//存储用户选择的规格

//用户选择规格
$scope.selectSpecification=function(key,value){
	$scope.specificationItems[key]=value;		
}

//判断某规格是否被选中
$scope.isSelected=function(key,value){
	if($scope.specificationItems[key]==value){
		return true;
	}else{
		return false;
	}	
}
```

# 5. 读取SKU

## 5.1 生成SKU列表

* 后台

```java
TbItemExample example=new TbItemExample();
Criteria criteria = example.createCriteria();
criteria.andGoodsIdEqualTo(goodsId);//SPU ID
criteria.andStatusEqualTo("1");//状态有效			
example.setOrderByClause("is_default desc");//按是否默认字段进行降序排序，目的是返回的结果第一条为默认SKU
List<TbItem> itemList = itemMapper.selectByExample(example);
dataModel.put("itemList", itemList);
```

* 模板

```javascript
//规格列表生成
var skuList=[
  <#list itemList as item>
   {
     id:${item.id?c},
     title:'${item.title}',
     price:${item.price?c},
     spec:${item.spec}
   } ,
  </#list>   
];
-------------------------生成数据--------------------------------------
var skuList=[
    {
     id:1369280,
     title:'精品半身裙（秋款打折） 移动3G 16G',
     price:0.01,
     spec:{"网络":"移动3G","机身内存":"16G"}
   } ,
   {
     id:1369281,
     title:'精品半身裙（秋款打折） 移动3G 32G',
     price:0.02,
     spec:{"网络":"移动3G","机身内存":"32G"}
   } ,
   {
     id:1369282,
     title:'精品半身裙（秋款打折） 移动4G 16G',
     price:0.03,
     spec:{"网络":"移动4G","机身内存":"16G"}
   } ,
];
------------------------生成数据-----------------------------------
```

## 5.2 选择SKU

* 模板

```html
<body ng-app="pinyougou"ng-controller="itemController"ng-init="num=1;loadSku()">
	<!--SKU名称-->
    <div class="sku-name"><h4>{{sku.title}}</h4></div>
    <!--SKU价格-->
    <div class="fl price"><i>¥</i><em>{{sku.price}}</em><span>降价通知</span></div>
    <!--规格选项-->
    <dd>
        <a class="{{isSelected('${specification.attributeName}','${item}')?'selected':''}}"
           ng-click="selectSpecification('${specification.attributeName}','${item}')">
            ${item}
        </a>
    </dd>
</body>
```

* JS

```javascript
//定义SKU变量
$scope.sku={};

//默认加载第一个SKU
$scope.loadSku=function(){
	$scope.sku=skuList[0];
	$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
}

//用户选择规格
$scope.selectSpecification=function(key,value){
	$scope.specificationItems[key]=value;	
    searchSku();	//查询规格
}

//根据选中规格更新SKU
searchSku=function(){
	for(var i=0;i<skuList.length;i++){
		 if(matchObject(skuList[i].spec ,$scope.specificationItems)){
			 $scope.sku=skuList[i];
			 return;
		 }			
	}
	$scope.sku={id:0,title:'-----',price:0};
}

//判断两个JSON对象是否相等
matchObject=function(map1,map2){
	
	for(var k in map1){
		if(map1[k]!=map2[k]){
			return false;
		}			
	}

	for(var k in map2){
		if(map2[k]!=map1[k]){
			return false;
		}			
	}		
	return true;
}
```

## 5.3 添加购物车

* 页面

```html
<li><a href="#" target="_blank" ng-click="addToCart()">加入购物车</a></li>
```

* JS

```javascript
//添加商品到购物车
$scope.addToCart=function(){
	alert('skuid:'+$scope.sku.id);
}
```

# 6. 系统对接



