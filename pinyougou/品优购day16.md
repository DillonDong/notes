# 1. 购物车实现思路

## 1.1 思路分析

* 购物车中数据的特征:用户想要购买的商品


* 如何存储购物车的数据(私人数据)

  未登录:存入Cookie

  登录后:存入Redis[用户的信息-购物车]

## 1.2 实体分析

品优购是B2B2C模式,每个商家的商品对应存储到一个购物车

### 1.2.1 购物车

```java
public class Cart implements Serializable{
	private String sellerId;//商家ID
	private String sellerName;//商家名称
	private List<TbOrderItem> orderItemList;//购物车明细集合
}
```

### 1.2.2 购物项 

```java
public class TbOrderItem implements Serializable{
    private Long id;

    private Long itemId;

    private Long goodsId;

    private Long orderId;

    private String title;

    private BigDecimal price;

    private Integer num;

    private BigDecimal totalFee;

    private String picPath;

    private String sellerId;
}
```

# 2. Cookie存储购物车

* 添加购物车路径

http://localhost:9107/cart/addGoodsToCartList.do?itemId=1369290&num=10

*  CartController

```java
//添加购物车
@RequestMapping("/addGoodsToCartList")
public Result addGoodsToCartList(Long itemId,Integer num){
	try {
		//提取购物车
		List<Cart> cartList = findCartList();
		//调用服务方法操作购物车
		cartList = cartService.addGoodsToCartList(cartList, itemId, num);
		String cartListString = JSON.toJSONString(cartList);
		util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600*24, "UTF-8");
		return new Result(true, "存入购物车成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "存入购物车失败");
	}
}
```

```java
//查询购物车
@RequestMapping("/findCartList")
public List<Cart> findCartList(){	
	String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");

  
	if(cartListString==null || cartListString.equals("")){
		cartListString="[]";
	}
	List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
	return cartList_cookie;		
}
```

* CartServiceImpl

```java
@Override
public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
	
	//1.根据skuID查询商品明细SKU的对象
	TbItem item = itemMapper.selectByPrimaryKey(itemId);
	if(item==null){
		throw new RuntimeException("商品不存在");
	}
	if(!item.getStatus().equals("1")){
		throw new RuntimeException("商品状态不合法");
	}		
	//2.根据SKU对象得到商家ID
	String sellerId = item.getSellerId();//商家ID
	
	//3.根据商家ID在购物车列表中查询购物车对象
	Cart cart = searchCartBySellerId(cartList,sellerId);
	
	if(cart==null){//4.如果购物车列表中不存在该商家的购物车
		
		//4.1 创建一个新的购物车对象
		cart=new Cart();
		cart.setSellerId(sellerId);//商家ID
		cart.setSellerName(item.getSeller());//商家名称

		List<TbOrderItem> orderItemList=new ArrayList();//创建购物车明细列表
		TbOrderItem orderItem = createOrderItem(item,num);			
		orderItemList.add(orderItem);			
		cart.setOrderItemList(orderItemList);
		
		//4.2将新的购物车对象添加到购物车列表中
		cartList.add(cart);
		
	}else{//5.如果购物车列表中存在该商家的购物车
		// 判断该商品是否在该购物车的明细列表中存在
		TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
		if(orderItem==null){
			//5.1  如果不存在  ，创建新的购物车明细对象，并添加到该购物车的明细列表中
			orderItem=createOrderItem(item,num);
			cart.getOrderItemList().add(orderItem);				
			
		}else{
			//5.2 如果存在，在原有的数量上添加数量 ,并且更新金额	
			orderItem.setNum(orderItem.getNum()+num);//更改数量
			//金额
			orderItem.setTotalFee( new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()) );
			//当明细的数量小于等于0，移除此明细
			if(orderItem.getNum()<=0){
				cart.getOrderItemList().remove(orderItem);					
			}
			//当购物车的明细数量为0，在购物车列表中移除此购物车
			if(cart.getOrderItemList().size()==0){
				cartList.remove(cart);
			}				
		}
	}
	
	return cartList;
}
```

```java
//根据商家ID在购物车集合中查询对应的购物车对象
private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
	for(Cart cart:cartList){
		if(cart.getSellerId().equals(sellerId)){
			return cart;
		}
	}
	return null;		
}
```

```java
//根据skuID在购物车明细列表中查询购物车明细对象
public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
	for(TbOrderItem orderItem:orderItemList){
		if(orderItem.getItemId().longValue()==itemId.longValue()){
			return orderItem;
		}			
	}
	return null;
}
```

```java
//创建购物车明细对象
private TbOrderItem createOrderItem(TbItem item,Integer num){
	//创建新的购物车明细对象
	TbOrderItem orderItem=new TbOrderItem();
	orderItem.setGoodsId(item.getGoodsId());
	orderItem.setItemId(item.getId());
	orderItem.setNum(num);
	orderItem.setPicPath(item.getImage());
	orderItem.setPrice(item.getPrice());
	orderItem.setSellerId(item.getSellerId());
	orderItem.setTitle(item.getTitle());
	orderItem.setTotalFee(  new BigDecimal(item.getPrice().doubleValue()*num) );
	return orderItem;
}
```

# 3. 显示购物车列表

## 3.1 列表页面

* HTML

```html
<body ng-app="pinyougou" ng-controller="cartController" ng-init="findCartList()">
```

* JS

```javascript
$scope.findCartList=function(){
	cartService.findCartList().success(
		function(response){
			$scope.cartList=response;
			$scope.totalValue= cartService.sum($scope.cartList);
		}
	);
}
```

```javascript
this.sum=function(cartList){
	var totalValue={totalNum:0,totalMoney:0 };
		
	for(var i=0;i<cartList.length ;i++){
		var cart=cartList[i];//购物车对象
		for(var j=0;j<cart.orderItemList.length;j++){
			var orderItem=  cart.orderItemList[j];//购物车明细
			totalValue.totalNum+=orderItem.num;//累加数量
			totalValue.totalMoney+=orderItem.totalFee;//累加金额				
		}			
	}
	return totalValue;
}
```

## 3.2 数量加减

* HTML

```html
<a href="javascript:void(0)" class="increment mins" ng-click="addGoodsToCartList(item.itemId,-1)">-</a>
<a href="javascript:void(0)" class="increment plus" ng-click="addGoodsToCartList(item.itemId,1)">+</a>
```

* JS

```javascript
$scope.addGoodsToCartList=function(itemId,num){
	cartService.addGoodsToCartList(itemId,num).success(
		function(response){
			if(response.success){//如果成功
				$scope.findCartList();//刷新列表
			}else{
				alert(response.message);
			}				
		}		
	);		
}
```

# 4. Redis存储购物车

## 4.1 获得用户信息

* 代码

```java
SecurityContextHolder.getContext().getAuthentication().getName();
```

* 问题

  空指针

* 原因

  未登录用户无法获得身份信息

* 解决

  未登录的身份统一是匿名身份

```xml
<intercept-url pattern="/cart/*.do" access="IS_AUTHENTICATED_ANONYMOUSLY"></intercept-url> 
<intercept-url pattern="/**" access="ROLE_USER"/>  
```

## 4.2 购物车存取

* CartController

```java
//添加购物车
@RequestMapping("/addGoodsToCartList")
public Result addGoodsToCartList(Long itemId,Integer num){
	//当前登录人账号
	String name = SecurityContextHolder.getContext().getAuthentication().getName();
	System.out.println("当前登录人："+name);

	try {
		//提取购物车
		List<Cart> cartList = findCartList();
		//调用服务方法操作购物车
		cartList = cartService.addGoodsToCartList(cartList, itemId, num);
		
		if(name.equals("anonymousUser")){//如果未登录
			//将新的购物车存入cookie
			String cartListString = JSON.toJSONString(cartList);
			util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600*24, "UTF-8");
			System.out.println("向cookie存储购物车");		
			
		}else{//如果登录				
			cartService.saveCartListToRedis(name, cartList);			
		}

		return new Result(true, "存入购物车成功");
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "存入购物车失败");
	}
}

//查询购物车列表
@RequestMapping("/findCartList")
public List<Cart> findCartList(){
	//当前登录人账号
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	System.out.println("当前登录人："+username);

	if(username.equals("anonymousUser")){//如果未登录
		//从cookie中提取购物车
		System.out.println("从cookie中提取购物车");
		String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		return cartList_cookie;
		
	}else{//如果已登录
		//获取redis购物车
		List<Cart> cartList_redis = cartService.findCartListFromRedis(username);				
		return cartList_redis;
	}
			
}
```

* CartService

```java
//从redis中提取购物车
public List<Cart> findCartListFromRedis(String username) {
	System.out.println("从redis中提取购物车"+username);
	List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
	if(cartList==null){
		cartList=new ArrayList();
	}		
	return cartList;
}

//给redis中保存购物车
public void saveCartListToRedis(String username, List<Cart> cartList) {
	System.out.println("向redis中存入购物车"+username);
	redisTemplate.boundHashOps("cartList").put(username, cartList);
}
```

## 4.3 合并Cookie购物车

* CartController.java

```java
@RequestMapping("/findCartList")
public List<Cart> findCartList(){
	//当前登录人账号
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	System.out.println("当前登录人："+username);
	
	String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
	if(cartListString==null || cartListString.equals("")){
		cartListString="[]";
	}
	List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
	
	if(username.equals("anonymousUser")){//如果未登录
		//从cookie中提取购物车
		System.out.println("从cookie中提取购物车");			
		return cartList_cookie;
	}else{//如果已登录
        //获取redis购物车
        List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
        if(cartList_cookie.size()>0){//判断当本地购物车中存在数据
            //得到合并后的购物车
            List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
            //将合并后的购物车存入redis 
            cartService.saveCartListToRedis(username, cartList);
            //本地购物车清除
            util.CookieUtil.deleteCookie(request, response, "cartList");
            System.out.println("执行了合并购物车的逻辑");
            return cartList;
        }						
	}
	return cartList_redis;
}
```

* 合并购物车

```java
public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
	// cartList1.addAll(cartList2);  不能简单合并 		
	for(Cart cart:cartList2){
		for( TbOrderItem orderItem :cart.getOrderItemList() ){
			cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
		}
	}
	return cartList1;		
}
```
