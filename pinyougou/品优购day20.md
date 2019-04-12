# 1. SpringTask任务调度框架

## 1.1 解决的问题

需要在某个事件执行某个任务

## 1.2 定时任务的两个元素

1. 执行什么任务（方法）
2. 什么时候执行(cron表达式)

## 1.3 CRON表达式

7位数【秒 分  时  日   月  星期  [年] 】

特殊字符

```
*：表示匹配该域的任意值
?:只能用在 DayofMonth 和 DayofWeek 两个域。表示放弃指定值，另外一方指定
-:表示范围
/：表示起始时间开始触发，然后每隔固定时间触发一次
,:表示列出枚举值值
L:表示最后，只能出现在 DayofWeek 和 DayofMonth 域
W:表示有效工作日(周一到周五),只能出现在 DayofMonth 域
LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五
#:用于确定每个月第几个星期几，只能出现在 DayofMonth 域。
```

注意：

* 年可以省略
* 日和星期不能同时指定,使用?放弃一方.并且日和星期不能同时放弃

## 1.4 CRON示例

```
0 0 10,14,16 * * ? 每天上午 10 点，下午 2 点，4 点
0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
0 0 12 ? * WED 表示每个星期三中午 12 点
0 0 12 * * ? 每天中午 12 点触发
0 15 10 ? * *  每天上午 10:15 触发
0 15 10 * * ?  每天上午 10:15 触发
0 15 10 * * ? *  每天上午 10:15 触发
0 15 10 * * ? 2005  2005 年的每天上午 10:15 触发
0 * 14 * * ?  在每天下午 2 点到下午 2:59 期间的每 1 分钟触发
0 0/5 14 * * ?  在每天下午 2 点到下午 2:55 期间的每 5 分钟触发
0 0/5 14,18 * * ?  在每天下午 2 点到 2:55 期间和下午 6 点到 6:55 期间的每 5 分钟触发
0 0-5 14 * * ?  在每天下午 2 点到下午 2:05 期间的每 1 分钟触发
0 10,44 14 ? 3 WED  每年三月的星期三的下午 2:10 和 2:44 触发
0 15 10 ? * MON-FRI  周一至周五的上午 10:15 触发
0 15 10 15 * ?  每月 15 日上午 10:15 触发
0 15 10 L * ?  每月最后一日的上午 10:15 触发
0 15 10 ? * 6L  每月的最后一个星期五上午 10:15 触发
0 15 10 ? * 6L 2002-2005  2002 年至 2005 年的每月的最后一个星期五上午 10:15 触发
0 15 10 ? * 6#3  每月的第三个星期五上午 10:15 触发
```

[CRON网址](http://cron.qqe2.com/)

## 1.4 开发步骤

* 开启task注解

```xml
<context:component-scan base-package="com.pinyougou.task"/>
<task:annotation-driven/>
```

* 创建方法并指定cron表达式时间

```java
@Scheduled(cron="0 * * * * ?")
public void doSth(){
 	... 
}
```

## 1.5 秒杀商品定期更新（增量更新）

```java
@Scheduled(cron="0 * * * * ?")
public void refreshSeckillGoods(){
	System.out.println("执行了秒杀商品增量更新 任务调度"+new Date());
	
	//查询缓存中的秒杀商品ID集合
	List goodsIdList =  new ArrayList( redisTemplate.boundHashOps("seckillGoods").keys());
	System.out.println(goodsIdList);
	
	TbSeckillGoodsExample example=new TbSeckillGoodsExample();
	Criteria criteria = example.createCriteria();
	criteria.andStatusEqualTo("1");// 审核通过的商品
	criteria.andStockCountGreaterThan(0);//库存数大于0
	criteria.andStartTimeLessThanOrEqualTo(new Date());//开始日期小于等于当前日期
	criteria.andEndTimeGreaterThanOrEqualTo(new Date());//截止日期大于等于当前日期
	
	if(goodsIdList.size()>0){
		criteria.andIdNotIn(goodsIdList);//排除缓存中已经存在的商品ID集合
	}
			
	List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
	//将列表数据装入缓存 
	for(TbSeckillGoods seckillGoods:seckillGoodsList){
		redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
		System.out.println("增量更新秒杀商品ID:"+seckillGoods.getId());
	}	
	System.out.println(".....end....");
}
```

## 1.6 移除过期商品

```java
@Scheduled(cron="* * * * * ?")
public void removeSeckillGoods(){
	//查询出缓存中的数据，扫描每条记录，判断时间，如果当前时间超过了截止时间，移除此记录
	List<TbSeckillGoods> seckillGoodsList= redisTemplate.boundHashOps("seckillGoods").values();
	System.out.println("执行了清除秒杀商品的任务"+new Date());
	for(TbSeckillGoods seckillGoods :seckillGoodsList){
		if(seckillGoods.getEndTime().getTime() < new Date().getTime() ){
			//同步到数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);				
			//清除缓存
			redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
			System.out.println("秒杀商品"+seckillGoods.getId()+"已过期");
							
		}			
	}		
	System.out.println("执行了清除秒杀商品的任务...end");
}
```

# 2. MavenProfile

## 2.1 解决的问题

项目的开发环境和生产环境存在差异。

通过MavenProfile可以快速实现不同环境参数的切换。

## 2.2 解决思路

1. 在项目中配置所有的环境信息
2. 使用maven的profile命令参数指定运行的环境

## 2.3 切换Tomcat启动端口

* 配置环境的变量

```xml
<!--默认环境-->
<properties>
    <port>9105</port>
</properties>

<profiles>
    <!--生产环境-->
    <profile>
        <id>pro</id>
        <properties>
            <port>9205</port>
        </properties>
    </profile>
     <!--开发环境-->
    <profile>
        <id>dev</id>
        <properties>
            <port>9105</port>
        </properties>
    </profile>
</profiles>
```

* 使用变量

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <version>2.2</version>
            <configuration>
                <!-- 指定端口 -->
                <port>${port}</port>
                <!-- 请求路径 -->
                <path>/</path>
            </configuration>
        </plugin>
    </plugins>
</build>
```

* 执行maven命令指定profile值

```shell
mvn tomcat7:run -P pro
mvn tomcat7:run -P dev
```

## 2.4 切换数据库的配置

* 准备数据库的环境配置

```shell
# db_dev.properties
env.jdbc.driver=com.mysql.jdbc.Driver
env.jdbc.url=jdbc:mysql://localhost:3306/pinyougoudb?characterEncoding=utf-8
env.jdbc.username=root
env.jdbc.password=root
```

``` shell
# db_pro.properties
env.jdbc.driver=com.mysql.jdbc.Driver
env.jdbc.url=jdbc:mysql://localhost:3306/pinyougoudb_pro?characterEncoding=utf-8
env.jdbc.username=root
env.jdbc.password=root
```

* 配置db.properties

```shell
jdbc.driver=${env.jdbc.driver}
jdbc.url=${env.jdbc.url}
jdbc.username=${env.jdbc.username}
jdbc.password=${env.jdbc.password}
```

* 配置maven环境变量

```xml
<!--默认环境-->
<properties>
	 <env>dev</env>
</properties>

<profiles>
    <!--开发环境-->
	<profile>
		<id>dev</id>
		<properties>
	  	 	<env>dev</env>
		</properties>
	</profile>
    <!--生产环境-->
	<profile>
		<id>pro</id>
		<properties>
	  	 	<env>pro</env>
		</properties>
	</profile> 
</profiles>
```

* 配置maven过滤器根据参数选择指定的环境文件

```xml
<build>
  <!--根据参数查找文件-->
	<filters>
		<filter>src/main/resources/filters/db_${env}.properties</filter>
	</filters>
  <!--过滤/查找文件的位置-->
	<resources>
		<resource>
			<directory>src/main/resources</directory>
			<filtering>true</filtering>
		</resource>
	</resources>
</build>
```

## 2.5 切换zookeeper配置

* 准备环境配置

```properties
# dubbox_dev.properties
env.address=192.168.25.135:2181
```

```properties
# dubbox_pro.properties
env.address=192.168.25.136:2181
```

* 配置dubbox.properties

```properties
address=${env.address}
```

* 配置Maven环境变量

```xml
<!--默认环境-->
<properties>
	  <env>dev</env>
</properties>

<profiles>
    <!--开发环境-->
	<profile>
		<id>dev</id>
        <properties>
           <env>dev</env>
        </properties>
	</profile>
    <!--生产环境-->
	<profile>
		<id>pro</id>
        <properties>
           <env>pro</env>
        </properties>
	</profile>
</profiles>
```

* 配置maven过滤器根据参数选择指定的环境文件

```xml
<build>
	<filters>
		<filter>src/main/resources/filters/dubbox_${env}.properties</filter>
	</filters>
    <resources>
      <resource>
          <directory>src/main/resources</directory>
          <filtering>true</filtering>
      </resource>    
    </resources>  
</build>
```

# 3. Mongodb

## 3.1 概述

面向文档的no-sql数据库

## 3.2 数据格式

BSON格式;BSON=Binary+JSON

```json
{
    title:"MongoDB",
    last_editor:"192.168.1.122",
    last_modified:new Date("27/06/2011"),
    body:"MongoDB introduction",
    categories:["Database","NoSQL","BSON"],
    revieved:false
}
```

## 3.3 类比关系型数据库

| MongoDb         | 关系型数据库         |
| --------------- | -------------- |
| 数据库(databases)  | 数据库(databases) |
| 集合(collections) | 表(table)       |
| 文档(document)    | 行(row)         |

## 3.4 安装/启动

* 启动

```shell
# 端口默认为27017可以缺省
mongod --port XXX --dbpath XXX
```

* 浏览器测试

```http
http://127.0.0.1:27017/
```

* 客户端连接

```shell
# 在本机默认端口启动ip和Port可以缺省
mongo ip:port
```

* 安装mongodb图形化客户端studio 3T

  破解链接:[https://www.jianshu.com/p/cc97f31509ea](https://www.jianshu.com/p/cc97f31509ea)

## 3.5 数据操作

* 创建/选择数据库

```shell
use itcastdb
```

* 增

  db.集合名称.save(变量)

```shell
#1.创建数据
r={name:'zs',age:20}
#2.在集合中保存数据
db.student.save(r)
###############################
db.student.save({name:"沙和尚",sex:"男",age:25,address:"流沙河路11号"});
```

* 删

  db.集合名称.remove( 条件 )

```shell
# 如果条件为{}代表删除所有
db.student.remove({name:'zs',age:20});
```

* 改

  db.集合名称.update( 条件,{$set:{age:30}})

```shell
#$set代表只修改指定字段,其他字段保留
db.student.update({name:"zs1"},{$set:{age:30}})
```

* 查

  db.集合名称.find();

```shell
# 查询所有
db.student.find()
# 条件查询
db.student.find({name:"zs"})
# 查询唯一数据
db.student.findOne({_id:ObjectId("5c46fac976489082b76ad6d3")})
```

