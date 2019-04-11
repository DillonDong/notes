# 1. 秒杀系统思路

## 1.1 描述

在有限的时间内很多人抢购低价商品

## 1.2 问题分析

1. 对原业务的影响
2. 网络带宽的限制
3. 应用服务器并发访问压力大
4. 数据库的压力大

## 1.3 解决方案

1. 和原业务进行分离
2. 和运营商租借带宽
3. 应用服务器搭建集群
4. 使用缓存减少数据库的压力

# 2. 秒杀商品列表

* HTML

```html
<body ng-app="pinyougou" ng-controller="seckillGoodsController" ng-init="findList()">
	<img src="{{pojo.smallPic}}">
    <span>{{pojo.title}}</span>
    <b class='sec-price'>￥{{pojo.costPrice}}</b>
    <b class='ever-price'>￥{{pojo.price}}</b>
    <div>已售{{((pojo.num-pojo.stockCount)/pojo.num*100).toFixed(0) }}%</div>
    <div>剩余<b class='owned'>{{pojo.stockCount}}</b>件</div>
    <a href='seckill-item.html#?id={{pojo.id}}' >立即抢购</a>
</body>
```

* JS

```javascript
$scope.findList=function(){
	seckillGoodsService.findList().success(
		function(response){
			$scope.list=response;
		}
	);
}
```

* 控制层

```java
@RequestMapping("/findList")
public List<TbSeckillGoods> findList(){
	return seckillGoodsService.findList()
}
```

* 业务层

```java
public List<TbSeckillGoods> findList() {
	List<TbSeckillGoods> seckillGoodsList =	redisTemplate.boundHashOps("seckillGoods").values();
    
	if(seckillGoodsList==null || seckillGoodsList.size()==0){
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");// 审核通过的商品
		criteria.andStockCountGreaterThan(0);//库存数大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());//开始日期小于等于当前日期
		criteria.andEndTimeGreaterThanOrEqualTo(new Date());//截止日期大于等于当前日期
		seckillGoodsList = seckillGoodsMapper.selectByExample(example);
		//将列表数据装入缓存 
		for(TbSeckillGoods seckillGoods:seckillGoodsList){
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
		}	
		System.out.println("从数据库中读取数据装入缓存");
	}else{
		System.out.println("从缓存中读取数据");
		
	}
	return seckillGoodsList;
}
```

# 3. 秒杀商品详情

根据商品ID从缓存中读取商品信息

* HTML

```html
<body ng-app="pinyougou" ng-controller="seckillGoodsController" ng-init="findOne()">
    ...
</body>
```

* 前端

```javascript
//查询商品
$scope.findOne=function(){
	//接收参数ID
	var id= $location.search()['id'];
	seckillGoodsService.findOne(id).success(
		function(response){
			$scope.entity=response;
			//倒计时开始
			//获取从结束时间到当前日期的秒数
			allsecond=  Math.floor( 
                (new Date($scope.entity.endTime).getTime()- new Date().getTime())/1000; 	 
			time= $interval(function(){
					allsecond=allsecond-1;
					$scope.timeString= convertTimeString(allsecond);
                    if(allsecond<=0){
                        $interval.cancel(time);
                    }
			},1000 );
			
		}		
	);		
}

//读秒效果
convertTimeString=function(allsecond){
	var days= Math.floor( allsecond/(60*60*24));//天数
	var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小时数
	var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
	var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
	var timeString="";
	if(days>0){
		timeString=days+"天 ";
	}
	return timeString+hours+":"+minutes+":"+seconds;
}
```

* 后台

```java
@RequestMapping("/findOneFromRedis")
public TbSeckillGoods findOneFromRedis(Long id){
  return seckillGoodsService.findOneFromRedis(id);		
}
```

* 业务层

```java
@Override
public TbSeckillGoods findOneFromRedis(Long id) {
  return  (TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);

}
```

# 4. 秒杀下单

* HTML

```html
<a ng-click="submitOrder()">立即抢购</a>
```

* JS

```javascript
$scope.submitOrder=function(){
	seckillGoodsService.submitOrder( $scope.entity.id ).success(
		function(response){
			if(response.success){//如果下单成功
				alert("抢购成功，请在5分钟之内完成支付");
				location.href="pay.html";//跳转到支付页面				
			}else{
				alert(response.message);
			}				
		}
	);
}
```

* 控制层

```java
@RequestMapping("/submitOrder")
public Result submitOrder(Long seckillId){
	
	//提取当前用户
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	if("anonymousUser".equals(username)){
		return new Result(false, "当前用户未登录");
	}
			
	try {
		seckillOrderService.submitOrder(seckillId, username);
		return new Result(true, "提交订单成功");
		
	}catch (RuntimeException e) {
		e.printStackTrace();
		return new Result(false, e.getMessage());
	} catch (Exception e) {
		e.printStackTrace();
		return new Result(false, "提交订单失败");
	}
	
}
```
* 业务层

```java
public void submitOrder(Long seckillId, String userId) {
	
	//1.查询缓存中的商品
	TbSeckillGoods seckillGoods= (TbSeckillGoods)redisTemplate
       							 .boundHashOps("seckillGoods")
        						 .get(seckillId);
	if(seckillGoods==null){
		throw new RuntimeException("商品不存在");			
	}
	if(seckillGoods.getStockCount()<=0){
		throw new RuntimeException("商品已经被抢光");			
	}
	
	//2.减少库存	
	seckillGoods.setStockCount( seckillGoods.getStockCount()-1  );//减库存
	redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);//存入缓存
	if(seckillGoods.getStockCount()==0){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);	//更新数据库
		redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
		System.out.println("商品同步到数据库...");
	}
	
	//3.存储秒杀订单 (不向数据库存 ,只向缓存中存储 )
	TbSeckillOrder seckillOrder=new TbSeckillOrder();
	seckillOrder.setId(idWorker.nextId());
	seckillOrder.setSeckillId(seckillId);
	seckillOrder.setMoney(seckillGoods.getCostPrice());
	seckillOrder.setUserId(userId);
	seckillOrder.setSellerId(seckillGoods.getSellerId());//商家ID
	seckillOrder.setCreateTime(new Date());
	seckillOrder.setStatus("0");//状态
	
	
	redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
	System.out.println("保存订单成功(redis)");
}
```

# 5. 秒杀支付

## 5.1 显示支付二维码

* HTML

```html
<body ng-app="pinyougou" ng-controller="payController" ng-init="createNative()">
    ...
</body>
```

* JS

```javascript
//发起支付请求
$scope.createNative=function(){
	payService.createNative().success(
		function(response){
			
			//显示订单号和金额
			$scope.money= (response.total_fee/100).toFixed(2);
			$scope.out_trade_no=response.out_trade_no;
			
			//生成二维码
			 var qr=new QRious({
				    element:document.getElementById('qrious'),
					size:250,
					value:response.code_url,
					level:'H'
		     });
			 queryPayStatus();//调用查询
		}	
	);	
}
```

* 控制层

```java
@RequestMapping("/createNative")
public Map createNative(){
	//1.获取当前登录用户
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	//2.提取秒杀订单（从缓存 ）
	TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);
	//3.调用微信支付接口	
	if(seckillOrder!=null){
		return weixinPayService.createNative(seckillOrder.getId()+"", (long)(seckillOrder.getMoney().doubleValue()*100)+"");		
	}else{
		return new HashMap<>();
	}		
}
```

## 5.2 查询支付状态

* JS

```javascript
//查询支付状态
queryPayStatus=function(){
	payService.queryPayStatus($scope.out_trade_no).success(
		function(response){
			if(response.success){
				location.href="paysuccess.html#?money="+$scope.money;
			}else{
				if(response.message=='二维码超时'){
					//$scope.createNative();//重新生成二维码
					alert("二维码超时");
				}else{
					location.href="payfail.html";
				}
			}				
		}		
	);		
}
```

* 控制层

```java
@RequestMapping("/queryPayStatus")
public Result queryPayStatus(String out_trade_no){
	
	//1.获取当前登录用户
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
			
	Result result=null;
	int x=0;
	while(true){
		
		Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);//调用查询
		if(map==null){
			result=new Result(false, "支付发生错误");
			break;
		}
		if(map.get("trade_state").equals("SUCCESS")){//支付成功
			result=new Result(true, "支付成功");				
			//保存订单
			seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
			break;
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		x++;
		if(x>=100){	
							
			result=new Result(false, "二维码超时");
			
			// 关闭支付
			Map<String,String> payResult = weixinPayService.closePay(out_trade_no);
			if(payResult!=null &&  "FAIL".equals( payResult.get("return_code"))){
				if("ORDERPAID".equals(payResult.get("err_code"))){
					result=new Result(true, "支付成功");				
					//保存订单
					seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
				}					
			}
			
			//删除订单
			if(result.isSuccess()==false){
				seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(out_trade_no));
			}
			break;				
		}
		
	}
	return result;
}
```

## 5.3 支付成功

* 业务层,将订单数据保存在数据库,删除缓存

```java
@Override
public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
	
	//1.从缓存中提取订单数据
	TbSeckillOrder seckillOrder = searchOrderFromRedisByUserId(userId);
	if(seckillOrder==null){
		throw  new  RuntimeException("不存在订单");
	}
	if(seckillOrder.getId().longValue()!=orderId.longValue()){
		throw  new  RuntimeException("订单号不符");
	}
	
	//2.修改订单实体的属性
	seckillOrder.setPayTime(new Date());//支付日期
	seckillOrder.setStatus("1");//已支付 状态
	seckillOrder.setTransactionId(transactionId);
			
	//3.将订单存入数据库
	seckillOrderMapper.insert(seckillOrder);
	
	//4.清除缓存中的订单 
	redisTemplate.boundHashOps("seckillOrder").delete(userId);	
}
```

## 5.4 支付超时

删除缓存,重新抢购

```java
public void deleteOrderFromRedis(String userId, Long orderId) {
	
	//1.查询出缓存中的订单
	TbSeckillOrder seckillOrder = searchOrderFromRedisByUserId(userId);
	if(seckillOrder!=null){
        //2.删除缓存中的订单 
        redisTemplate.boundHashOps("seckillOrder").delete(userId);

        //3.库存回退
        TbSeckillGoods  seckillGoods = 							(TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
        if(seckillGoods!=null){ //如果不为空
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
        }else{
            seckillGoods=new TbSeckillGoods();
            seckillGoods.setId(seckillOrder.getSeckillId());
            //属性要设置。。。。省略
            seckillGoods.setStockCount(1);//数量为1
            redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
        }
		System.out.println("订单取消："+orderId);
	}
}
```
