# 1. 项目概述

## 1.1 背景

* 在线学习的互联网项目,为用户提供在线的IT职业课程的学习平台.
* B2B2C业务模式.企业和个人都可以入驻平台给用户提供在线教学服务.

## 1.2 功能模块

![](img/day01-1.png)

| 功能模块名称 | 功能说明                                                     |
| :----------: | ------------------------------------------------------------ |
|     门户     | 在首页、活动页、专题页等页面提供课程学习入口。               |
|   学习中心   | 学生登录学习中心在线学习课程。                               |
|   社交系统   | 社交系统为老师和学生交流搭建沟通的平台，包括：问答系统、评论系统、论坛等，学生和老师
通过问答系统提问问题、回答问题，通过评论系统对老师授课进行评论。 |
| 教学管理中心 | 教师登录教学管理中心进行课程管理、资源管理、考试管理等教学活动。 |
| 系统管理中心 | 系统管理员登录系统管理中心进行分类管理、运维管理等功能。     |

# 2. 技术架构

## 2.1 架构图

![](img/day01-2.png)

| 序号 |     名称     | 功能描述                                                     |
| :--: | :----------: | ------------------------------------------------------------ |
|  1   |    用户层    | 用户层描述了本系统所支持的用户类型包括：pc用户、app用户、h5用户。pc用户通过
浏览器访问系统、app用户通过android、ios手机访问系统，H5用户通过h5页面访问系
统。 |
|  2   |     CDN      | CDN全称Content Delivery Network，即内容分发网络，本系统所有静态资源全部通过
CDN加速来提高访问速度。系统静态资源包括：html页面、js文件、css文件、image图
片、pdf和ppt及doc教学文档、video视频等。 |
|  3   |   负载均衡   | 系统的CDN层、UI层、服务层及数据层均设置了负载均衡服务，上图仅在UI层前边标注
了负载均衡。 每一层的负载均衡会根据系统的需求来确定负载均衡器的类型，系统支持
4层负载均衡+7层负载均衡结合的方式，4层负载均衡是指在网络传输层进行流程转发，
根据IP和端口进行转发，7层负载均衡完成HTTP协议负载均衡及反向代理的功能，根据
url进行请求转发。 |
|  4   |     UI层     | UI层描述了系统向pc用户、app用户、h5用户提供的产品界面。根据系统功能模块特点
确定了UI层包括如下产品界面类型： <br>1. 面向pc用户的门户系统、学习中心系统、教学管理系统、系统管理中心。 <br>2. 面向h5用户的门户系统、学习中心系统。<br>3. 面向app用户的门户系统、学习中心系统未在上图标注，在app项目中有详细说明。 |
|  5   |   微服务层   | 微服务层将系统服务分类三类：前端服务、后端服务及系统服务。 <br>**前端服务：**主要为学习用户提供学习服务。 <br>**后端服务：**主要为管理用户提供教学管理服务。 <br>**系统服务：**公共服务，为系统的所有微服务提供公共服务功能。 服务网关：提供服务路由、负载均
衡、认证授权等服务。 |
|  6   |    数据层    | 数据层描述了系统的数据存储的内容类型.<br>**持久化数据:**使用MySQL和MongoDB保存，其中MongoDB中主要保存系统日志信息。 <br>**消息队列：**存储系统服务间通信的消息，本身提供消息存取服务，与微服务层的系统服务连接。<br>**索引库：**存储课程信息的索引信息，本身提供索引维护及搜索的服务，与微服务层的系统服务连接。 <br>**缓存：**作为系统的缓存服务，存储课程信息、分类信息、用户信息等，与微服务层的所有服务连接。
**文件存储：**提供系统静态资源文件的分布式存储服务，文件存储服务器作为CDN服务器的数据来源，CDN上的静态资源将最终在文件存储服务器上保存多份。<br>**流媒体服务：**作为流媒体服务器，存储所有的流媒体文件。 |
|  7   | 外部系统接口 | 1. **微信、QQ、微博登录接口:**本系统和微信、QQ、微博系统对接，用户输入微信、QQ、微博的账号和密码即可登录本系统。 <br>**2. 微信、QQ、微博分享接口**，本系统和微信、QQ、微博系统对接，可直接将本系统的课程资源信息分享到微信、QQ、微博。
**3. 支付宝、微信、网银支付接口，**本系统提供支付宝、微信、网银三种支付接口。
**4. 短信接口，**本系统与第三方平台对接短信发送接口。<br>**5. 邮件接口，**本系统需要连接第三方的smpt邮件服务器对外发送电子邮件。<br>**6. 微信公众号，**本系统与微信公众号平台接口，用户通过微信公众号访问H5页面。<br>7. **点播、直播，**前期视频点播与直播采用第三方服务方式，本系统与第三方点、直播服务对接，对外提供视频点播与直播服务。<br>**8. OSS存储** ，前期静态资源文件的存储采用第三方服务方式，本系统与第三方提供的OSS存储服务对接，将系统的静态资源文件存储到第三方提供的OSS存储服务器上。
**9. CDN，**本系统与第三方CDN服务对接，使用CDN加速服务来提高本系统的访问速度。 |
|  8   |    DevOps    | DevOps（英文Development和Operations的组合）是一组过程、方法与系统的统称，
用于促进开发（应用程序/软件工程）、技术运营和质量保障（QA）部门之间的沟通、
协作与整合。本项目供了许多开发、运营、维护支撑的技术，包括： <br>1. Eureka服务治理中心：提供服务治理服务，包括：服务注册、服务获取等。 <br>2. Spring Cloud Config服务配置管理中心：提供服务配置管理服务，包括：配置文件更新、配置文件下发等。
3. Hystrix Dashboard服务熔断监控：监控熔断的请求响应时间、成功率等 。 <br>4. Zipkin服务追踪监控：监控服务调用链路健康情况。 <br>5. Jenkins持续集成服务：提供系统持续集成服务。 <br>6. Git/GitLab代码管理服务:提供git代码管理服务。 <br>7. ELK日志分析服务:提供elk日志分析服务，包括系统运行日志分析、告警服务。 <br>8. Docker容器化部署服务：将本系统所有服务采用容器化部署方式。 <br>9. Maven项目管理工具：提供管理项目所有的Java包依赖、项目工程打包服务。<br>**DevOps通过自动化的工具协作和沟通来完成软件的生命周期管理，从而更快、更频繁地交付更稳定的软件** |

![](img/Devops.png)

## 2.2 技术栈

![](img/技术栈.png)

| 名称       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| 微服务     | Spring Boot+Spring Cloud                                     |
| 微服务治理 | Eureka、Zuul、Hystrix、Spring Cloud Config                   |
| 持久层     | MySQL、MongoDB、Redis、ElasticSearch                         |
| 数据访问层 | Spring Data JPA 、Mybatis、Spring Data Mongodb               |
| 业务层     | Spring IOC、Aop事务控制、Spring Task任务调度、Feign、Ribbon、Spring AMQP、Spring Data Redis等。 |
| 控制层     | Spring MVC、FastJSON、RestTemplate、Spring Security Oauth2+JWT |

## 2.3 开发步骤

项目是基于前后端分离的架构进行开发,多人协作并行开发

1. 需求分析:梳理用户的需求，分析业务流程
2. 接口定义:根据需求分析定义接口
3. 服务端和前端并行开发:依据接口进行服务端接口开发,前端开发用户操作界面，并请求服务端接口完成业务处理。
4. 前后端集成测试:最终前端调用服务端接口完成业务。

# 3. CMS需求分析

## 3.1 CMS

### 3.1.1 什么是CMS

CMS （Content Management System）即内容管理系统，不同的项目对CMS的定位不同，比如：一个在线教育网
站，有些公司认为CMS系统是对所有的课程资源进行管理，而在早期网站刚开始盛行时很多公司的业务是网站制
作，当时对CMS的定位是创建网站，即对网站的页面、图片等静态资源进行管理。

### 3.1.2 CMS类型

* 针对后台数据内容的管理
* 针对前端页面的管理、针对样式风格的管理等

学成在线作为一个大型的在线教育平台，对CMS系统的定位是对各各网站（子站点）页面的管理，主要管理由于运营
需要而经常变动的页面，从而实现根据运营需要快速进行页面开发、上线的需求。

## 3.2 门户网站搭建

### 3.2.1 网站描述

门户，是一个网站的入口，一般网站都有一个对外的门户.
首先搭建学成网的静态门户工程，根据门户的页面结构来分析页面的管理方案。

![](img/门户.png)

### 3.2.2 搭建步骤

1. 安装WebStorm,破解网站:[https://www.jianshu.com/p/0bf76557153a](https://www.jianshu.com/p/0bf76557153a)

   WebStorm的作用是进行前端页面的开发

2. 安装Nginx

   提供前端网站的访问

3. 在webStorm中导入门户工程

4. 在nginx中配置虚拟主机

   ```shell
   server{
   	listen 80;
   	server_name www.xuecheng.com;
   	ssi on;
   	ssi_silent_errors on;
   	location /{
   		alias C:/work/develop/nginx-1.14.0/html/xc-ui-pc-static-portal/;
   		index index.html;
   	}
   }
   ```

5. 修改windows的hosts文件

   ```shell
   127.0.0.1	www.xuecheng.com
   ```

6. 访问[http://www.xuecheng.com/](http://www.xuecheng.com/)

## 3.3 SSI技术

### 3.3.1 什么是SSI

Server Side Include，通常称为服务器端嵌入，是一种类似于ASP/JSP的基于服务器的网页制作技术。大多数（尤其是基于Unix平台）的WEB服务器如Netscape Enterprise Server等均支持SSI命令。

### 3.3.2 为什么用SSI

一个静态化的页面中,通常将页面拆分成一个一个的小页面，通过cms去管理这些小页面，当要更改部分页面内容时,只需要更改具体某个小页面即可。

### 3.3.3 SSI参数配置

* ssi on： 开启ssi支持
* ssi_silent_errors on：默认为off，设置为on则在处理SSI文件出错时不输出错误信息 
* ssi_types：默认为 ssi_types text/html,如果需要支持shtml（服务器执行脚本，类似于jsp）则需要设置为ssi_types text/shtml

### 3.3.4 SSI语法

```html
<!--#include virtual="/include/header.html"-->
```

## 3.4 CMS页面管理流程

1. 创建站点

   一个网站有很多子站点，比如：学成在线有主门户、学习中心、问答系统等子站点。具体的哪个页面是归属于具体的站点，所以要管理页面，先要管理页面所属的站点。

2. 创建模板

   页面如何创建呢？比如电商网站的商品详情页面，每个页面的内容布局、板式是相同的，不同的只是内容，这个页
   面的布局、板式就是页面模板，模板+数据就组成一个完整的页面，最终要创建一个页面文件需要先定义此页面的
   模板，最终拿到页面的数据再结合模板就拼装成一个完整的页面。

3. 创建页面

   创建页面是指填写页面的基本信息，如：页面的名称、页面的url地址等。

4. 页面浏览

   页面预览是页面发布前的一项工作，页面预览使用静态化技术根据页面模板和数据生成页面内容，并通过浏览器预
   览页面。页面发布前进行页面预览的目是为了保证页面发布后的正确性。

5. 页面发布

   使用计算机技术将页面发送到页面所在站点的服务器，页面发布成功就可以通过浏览器来访问了。

#4. CMS服务端

## 4.1 开发工具

IDEA+JDK1.8

## 4.2 导入基础工程

### 4.2.1 工程结构

![](img/工程结构.png)

1. parent工程：父工程，提供依赖管理。
2. common工程：通用工程，提供各层封装
3. model工程：模型工程，提供统一的模型类管理
4. utils工程：工具类工程，提供本项目所使用的工具类
5. Api工程：接口工程，统一管理本项目的服务接口。

### 4.2.2 导入工程

用IDEA打开基础工程目录即可

### 4.2.3 安装工程

1. 在父工程中添加待安装子工程依赖

   ```xml
   <modules>
       <module>../xc-framework-common</module>
       <module>../xc-framework-model</module>
       <module>../xc-framework-utils</module>
       <module>../xc-service-api</module>
   </modules>
   ```

2. 安装父工程

   ```shell
   mvn install
   ```

# 5.  MongoDB

MongoDB优点:

1. Mongodb是非关系型数据库，存储Json格式数据 ,数据格式灵活
2. 相比课程管理等核心数据CMS数据不重要，且没有事务管理要求。

## 5.1 安装MongoDB

1. 基本步骤安装文档
2. 将Mongo注册到本地服务
3. 启动关闭/命令

```shell
# 启动mongo
net start MongoDB
# 关闭mongo
net stop MongoDB
```

4. 测试

   访问:[http://127.0.0.1:27017/](http://127.0.0.1:27017/)

5. 安装mongodb图形化客户端studio 3T

   破解链接:[https://www.jianshu.com/p/cc97f31509ea](https://www.jianshu.com/p/cc97f31509ea)

## 5.2  入门

### 5.2.1 基础概念

| SQL 术语/概念 | MongoDB术语/概念 | 解释/说明                                 |
| ------------- | ---------------- | ----------------------------------------- |
| database      | database         | 数据库                                    |
| table         | collection       | 数据库表/集合                             |
| row           | document         | 数据记录行/文档                           |
| column        | field            | 数据字段/域                               |
| index         | index            | 索引                                      |
| table         | joins            | 表连接（MongoDB不支持）                   |
| primary key   | primary key      | 主键,MongoDB自动在每个集合中添加_id的主键 |

![](img/mongo1.png)

1. 一个mongodb实例可以创建多个数据库
2. 一个数据库可以创建多个集合
3. 一个集合可以包括多个文档。

### 5.2.2 连接方式

* 命令格式

```shell
mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?
options]]
```

> mongodb://  固定前缀
> username： 账号，可不填
> password： 密码，可不填
> host：          主机名或ip地址，只有host主机名为必填项。
> port：          端口，可不填，默认27017
> /database:  连接某一个数据库
> ?options:     连接参数，key/value对

```shell
## 举例
mongodb://localhost 连接本地数据库27017端口 
mongodb://root:itcast@localhost 使用用户名root密码为itcast连接本地数据库27017端口 
mongodb://localhost,localhost:27018,localhost:27019，连接三台主从服务器，端口为27017、27018、27019
```

* mongo.exe

  ![](img/mongo2.png)

* studio3T

* java程序

  1. 导入依赖

     ```xml
     <dependency>
         <groupId>org.mongodb</groupId>
         <artifactId>mongo‐java‐driver</artifactId>
         <version>3.4.3</version>
     </dependency>
     ```

  2. 测试程序

     ```java
     @Test
     public void testConnection(){
         //1. 创建mongodb 客户端
         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
         //或者采用连接字符串
         //MongoClientURI connectionString 
         						= new MongoClientURI("mongodb://root:root@localhost:27017");     
     	//MongoClient mongoClient = new MongoClient(connectionString);    
         //2.连接数据库
         MongoDatabase database = mongoClient.getDatabase("test");
         //3.连接collection
         MongoCollection<Document> collection = database.getCollection("student");
         //4.查询第一个文档
         Document myDoc = collection.find().first();
         //5.得到文件内容 json串
         String json = myDoc.toJson();
         System.out.println(json);
     }
     ```

### 5.2.3 数据库

1. 查询数据库

   ```shell
   # 显示所有数据库
   show dbs
   # 显示当前数据库
   show db
   ```

2. 创建数据库

   ```shell
   # use 数据库名
   use test02
   ```

3. 删除数据库（慎用！！！）

   ```shell
   db.dropDatabase()
   
   # 例子:删除test2数据库
   # 1.先切换数据库
   use test02
   # 2.再执行删除
   db.dropDatabase()
   ```

### 5.2.4 集合

集合相当于关系数据库中的表，一个数据库可以创建多个集合，一个集合是将相同类型的文档管理起来。

1. 创建集合

   ```shell
   # name: 新创建的集合名称
   # options: 创建参数
   db.createCollection(name, options)
   ```

2. 删除集合

   ```shell
   db.collection.drop()
   
   #例子:删除student集合
   db.student.drop() 
   ```

### 5.2.5 文档

1. 插入文档

   ```shell
   {
       "_id" : ObjectId("5b2cc4bfa6a44812707739b5"),
       "name" : "黑马程序员"
   }
   ```

   ```shell
   db.COLLECTION_NAME.insert(document)
   
   # 例子:插入一条文档数据
   db.student.insert({"name":"黑马程序员","age":10})
   ```

   > 注意：同一个集合中的文档的key可以不相同！但是建议设置为相同的。

2. 更新文档

   ```shell
   # query:查询条件，相当于sql语句的where
   # update：更新文档内容
   # options：选项
   db.collection.update(
      <query>,
      <update>,
      <options>
   )
   
   # 例子1:将符合条件 "name":"北京黑马程序"的第一个文档替换为{"name":"北京黑马程序员","age":10}。
   db.student.update({"name":"黑马程序员"},{"name":"北京黑马程序员","age":10})
   # $set修改器:$set修改器指定要更新的key，key不存在则创建，存在则更新
   
   # 例子2:将符合条件 "name":"北京黑马程序"的所有文档更新name和age的值。
   db.student.update({"name":"黑马程序员"},{$set:{"name":"北京黑马程序员","age":10}},{multi:true})
   # multi：false表示更新第一个匹配的文档，true表示更新所有匹配的文档。
   ```

3. 删除文档

   ```shell
   # query：删除条件，相当于sql语句中的where
   db.student.remove(<query>)
   
   # 例子1:删除所有文档
   db.student.remove({})
   
   # 例子2: 删除"name":"黑马程序员"的文档
   db.student.remove({"name":"黑马程序员"})
   ```

4. 查询文档

   ```shell
   # query：查询条件，可不填
   # projection：投影查询key，可不填
   db.collection.find(query, projection)
   
   # 查询全部
   db.student.find()
   # 查询name等为"黑马程序员"的文档。
   db.student.find({"name":"黑马程序员"})
   # 投影查询:只显示name和age两个key，_id主键不显示。
   db.student.find({"name":"黑马程序员"},{name:1,age:1,_id:0})
   ```

## 5.3 用户管理

### 5.3.1 创建用户

```shell
db.createUser(
{ user: "<name>",
  pwd: "<cleartext password>",
  customData: { <any information> },
  roles: [
    { role: "<role>", db: "<database>" } | "<role>",
    ...
  ]}
)

# 举例:
use admin	# 切换到admin用户
db.createUser(
     {
       user:"root",
       pwd:"root",
       roles:[{role:"root",db:"admin"}]
     }
)
```

内置角色如下：
1. 数据库用户角色：read、readWrite;
2. 数据库管理角色：dbAdmin、dbOwner、userAdmin；
3. 集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
4. 备份恢复角色：backup、restore；
5. 所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
6. 超级用户角色：root

### 5.3.2 查询用户

```shell
# 查询所有用户
show users
```

### 5.3.3 删除用户

```sh
db.dropUser("用户名")

# 举例:删除root1用户
db.dropUser("root1")
```

### 5.3.4 修改用户

```shell
db.updateUser(
  "<username>",
  {
    	customData : { <any information> },
    	roles : [
    	          { role: "<role>", db: "<database>" } | "<role>",
    	          ...
    	        ],
    	pwd: "<cleartext password>"
    	},
    	writeConcern: { <write concern> 
   }
)

# 举例:修改root用户的角色为readWriteAnyDatabase
use admin
db.updateUser("root",{roles:[{role:"readWriteAnyDatabase",db:"admin"}]})
```

### 5.3.5 修改密码

```shell
db.changeUserPassword("username","newPasswd")

# 举例:修改root用户的密码为123
use admin
db.changeUserPassword("root","123")
```

## 5.4 导入CMS数据库

1. 创建xc_cms数据库

   ![](img/studio1.png)

2. 导入 cms数据库

   ![](img/studio2.png)

![](img/studio3.png)

![](img/studio4.png)

![](img/studio2.png)

# 6. 定义查询接口

定义接口是为了规范前后端的交互规则,接口定义后前端和后端就可以依据接口去开发功能了.

## 6.1 模型定义

### 6.1.1 需求分析

定义页面查询接口，本接口供前端请求查询页面列表，支持分页及自定义条件查询方式。

具体需求如下：

1. 分页查询CmsPage 集合下的数据
2. 根据站点Id、模板Id、页面别名查询页面信息
3. 接口基于Http Get请求，响应Json数据

### 6.1.2 模型类介绍

* 站点

```java
@Data
@ToString
@Document(collection = "cms_site")
public class CmsSite {
    //站点ID
    @Id
    private String siteId;
    //站点名称
    private String siteName;
    //站点名称
    private String siteDomain;
    //站点端口
    private String sitePort;
    //站点访问地址
    private String siteWebPath;
    //创建时间
    private Date siteCreateTime;

}
```

* 页面模板

```java
@Data
@ToString
@Document(collection = "cms_template")
public class CmsTemplate {
    //站点ID
    private String siteId;
    //模版ID
    @Id
    private String templateId;
    //模版名称
    private String templateName;
    //模版参数
    private String templateParameter;
    //模版文件Id
    private String templateFileId;
}
```

* 页面信息

```java
@Data
@ToString
@Document(collection = "cms_page")
public class CmsPage {
    /**
     * 页面名称、别名、访问地址、类型（静态/动态）、页面模版、状态
     */
    //站点ID
    private String siteId;
    //页面ID
    @Id
    private String pageId;
    //页面名称
    private String pageName;
    //别名
    private String pageAliase;
    //访问地址
    private String pageWebPath;
    //参数
    private String pageParameter;
    //物理路径
    private String pagePhysicalPath;
    //类型（静态/动态）
    private String pageType;
    //页面模版
    private String pageTemplate;
    //页面静态化内容
    private String pageHtml;
    //状态
    private String pageStatus;
    //创建时间
    private Date pageCreateTime;
    //模版id
    private String templateId;
    //参数列表
    private List<CmsPageParam> pageParams;
    //模版文件Id
    private String templateFileId;
    //静态文件Id
    private String htmlFileId;
    //数据Url
    private String dataUrl;
    
}
```

属性说明：

1. **定义一个页面需要指定页面所属站点**

   一个站点包括多个页面，比如：学成在线的门户站点（网站）包括了多个页面。

2. **定义一个页面需要指定页面使用的模板**

   多个页面可以使用相同的模板，比如：商品信息模板，每个商品就是一个页面，所有商品使用同一个商品信息模板

> @Data、@ToString：属于Lombok提供的注解
>
> @Document：是Spring Data mongodb提供的注解，最终CMS的开发会使用Mongodb数据库。

### 6.1.3 Lombok

Lombok是一个实用的java工具，使用它可以消除java代码的臃肿，Lombok提供一系列的注解，使用这些注解可
以不用定义getter/setter、equals、构造方法等，它会在编译时在字节码文件自动生成这些通用的方法，简化开发
人员的工作。

* 使用方法

1. 在项目中添加Lombok的依赖

   作用：项目在编译时根据Lombok注解生成通用方法。

   ```xml
   <dependency>
       <groupId>org.projectlombok</groupId>
       <artifactId>lombok</artifactId>
   </dependency>
   ```

2. 在IDEA开发工具中添加Lombok插件

   ![](img/lombok.png)

* 举例说明

  * 源码

  ```java
  @Data
  @ToString
  @Document(collection = "cms_site")
  public class CmsSite {
  
      //站点ID
      @Id
      private String siteId;
      //站点名称
      private String siteName;
      //站点名称
      private String siteDomain;
      //站点端口
      private String sitePort;
      //站点访问地址
      private String siteWebPath;
      //创建时间
      private Date siteCreateTime;
  
  }
  ```

  * lombok插件效果

  ![](img/lombok1.png)

## 6.2 接口定义

### 6.2.1 定义请求模型

* 定义请求模型```QueryPageRequest```，此模型作为查询条件类型,为后期扩展需求，请求类型统一继承RequestData类型。

  ![](img/request1.png)

```java
@Data
public class QueryPageRequest extends RequestData {
    //接收页面查询的查询条件
    //站点id
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //页面ID
    private String pageId;
    //页面名称
    private String pageName;
    //别名
    private String pageAliase;
    //模版id
    private String templateId;
    //....
}

```

```java
@Data
@ToString
public class RequestData {
    ...
}
```

* 定义响应模型```QueryResponseResult```,每次响应均要返回【操作是否成功、操作code码、提示信息】，所以将这三个属性定义在父类```ResponseResult```中

  ![](img/response1.png)

```java
@Data
@ToString
public class QueryResponseResult extends ResponseResult {

    QueryResult queryResult;

    public QueryResponseResult(ResultCode resultCode,QueryResult queryResult){
        super(resultCode);
       this.queryResult = queryResult;
    }

}

```

```java
@Data
@ToString
@NoArgsConstructor
public class ResponseResult implements Response {

    //操作是否成功
    boolean success = SUCCESS;

    //操作代码
    int code = SUCCESS_CODE;

    //提示信息
    String message;

    public ResponseResult(ResultCode resultCode){
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    public static ResponseResult SUCCESS(){
        return new ResponseResult(CommonCode.SUCCESS);
    }
    public static ResponseResult FAIL(){
        return new ResponseResult(CommonCode.FAIL);
    }

}
```

```java
public interface Response {
    public static final boolean SUCCESS = true;
    public static final int SUCCESS_CODE = 10000;
}
```

### 6.2.2 定义接口

在 Api接口工程专门定义接口，在Api工程单独定义接口的原因如下：

1. 接口集中管理
2. Api工程的接口将作为各微服务远程调用使用。

```java
public interface CmsPageControllerApi {
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) ;
}
```

# 6. 服务端开发

## 6.1 创建CMS服务工程

![](img/api1.png)

* pom文件

```xml
<parent>
        <artifactId>xc-framework-parent</artifactId>
    <groupId>com.xuecheng</groupId>
    <version>1.0-SNAPSHOT</version>
    <relativePath>../xc-framework-parent/pom.xml</relativePath>
</parent>

<dependencies>
    <dependency>
        <groupId>com.xuecheng</groupId>
        <artifactId>xc-service-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.xuecheng</groupId>
        <artifactId>xc-framework-model</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.xuecheng</groupId>
        <artifactId>xc-framework-utils</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.xuecheng</groupId>
        <artifactId>xc-framework-common</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-freemarker</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
</dependencies>
```

* 包接口

```properties
com.xuecheng.manage_cms.config：配置类目录，数据库配置、MQ配置等
com.xuecheng.manage_cms.dao：dao接口目录
com.xuecheng.manage_cms.service：service类目录
com.xuecheng.manage_cms.web.controller：controller类目录
```

* yml配置文件

```yaml
server:
  port: 31001
spring:
  application:
    name: xc‐service‐manage‐cms
  data:
    mongodb:
      uri:  mongodb://root:123@localhost:27017
      database: xc_cm
```

* 起步类

```java
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.manage_cms"})//扫描本项目下的所有类
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
```

## 6.2 测试Controller

在xc-service-manage-cms工程中编写Controller

```java
@RestController
public class CmsPageController implements CmsPageControllerApi {
   
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int
size,QueryPageRequest queryPageRequest) {
        //暂时采用测试数据，测试接口是否可以正常运行
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2);
        //静态数据列表
        List list = new ArrayList();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage)
        queryResult.setList(list);
        QueryResponseResult queryResponseResult = new
QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
```

访问:[http://localhost:31001/cms/page/list/1/10](http://localhost:31001/cms/page/list/1/10)

# 7. 接口测试

