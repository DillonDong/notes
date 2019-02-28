# 1. 页面发布

## 1.1 技术方案

![](img/page1.png)

**技术方案:**

1. 平台包括多个站点，页面归属不同的站点。

2. 发布一个页面应将该页面发布到所属站点的服务器上。

3. 每个站点服务部署cms client程序，并与交换机绑定，绑定时指定站点Id为routingKey。

   指定站点id为routingKey就可以实现cms client只能接收到所属站点的页面发布消息。

4. 页面发布程序向MQ发布消息时指定页面所属站点Id为routingKey，将该页面发布到它所在服务器上的cms client。

**路由模式:**

​	发布一个页面，需发布到该页面所属的每个站点服务器，其它站点服务器不发布。

**举例:**

​	发布一个门户的页面，需要发布到每个门户服务器上，而用户中心服务器则不需要发布。

​	所以本项目采用routing模式，用站点id作为routingKey，这样就可以匹配页面只发布到所属的站点服务器上。
**页面发布流程:**

![](img/gridfs3.png)

1. 前端请求cms执行页面发布。
2. cms执行静态化程序生成html文件。
3. cms将html文件存储到GridFS中。
4. cms向MQ发送页面发布消息
5. MQ将页面发布消息通知给Cms Client
6. Cms Client从GridFS中下载html文件
7. Cms Client将html保存到所在服务器指定目录

## 1.2 页面发布消费方

### 1.2.1 需求分析

**功能分析**

创建Cms Client工程作为页面发布消费方，将Cms Client部署在多个服务器上，它负责接收到页面发布 的消息后从GridFS中下载文件在本地保存。

**需求:**

1. 将cms Client部署在服务器，配置队列名称和站点ID。

2. cms Client连接RabbitMQ并监听各自的“页面发布队列”

3. cms Client接收页面发布队列的消息

4. 根据消息中的页面id从mongodb数据库下载页面到本地

   调用 dao查询页面信息，获取到页面的物理路径，调用dao查询站点信息，得到站点的物理路径

   页面物理路径=站点物理路径+页面物理路径+页面名称。

   从GridFS查询静态文件内容，将静态文件内容保存到页面物理路径下。

### 1.2.2 CMS_Client工程

#### pom依赖

```xml
<parent>
    <artifactId>xc-framework-parent</artifactId>
    <groupId>com.xuecheng</groupId>
    <version>1.0-SNAPSHOT</version>
    <relativePath>../xc-framework-parent/pom.xml</relativePath>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>xc-service-manage-cms-client</artifactId>

<dependencies>
    <dependency>
        <groupId>com.xuecheng</groupId>
        <artifactId>xc-framework-model</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-io</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
</dependencies>
```

#### 配置文件

```yaml
server:
  port: 31000
spring:
  application:
    name: xc‐service‐manage‐cms‐client
  data:
    mongodb:
      uri:  mongodb://root:123@localhost:27017
      database: xc_cms
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    virtualHost: /
xuecheng:
  mq:
  #cms客户端监控的队列名称（不同的客户端监控的队列不能重复）
    queue: queue_cms_postpage_01
    routingKey: 5a751fab6abb5044e0d19ea1 #此routingKey为门户站点ID
```

说明：在配置文件中配置队列的名称，每个 cms client在部署时注意队列名称不要重复

#### 启动类

```java
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的所有类
@ComponentScan(basePackages={"com.xuecheng.manage_cms_client"})
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
```

### 1.2.3 RabbitmqConfig 配置类

消息队列设置如下：

1. 创建“ex_cms_postpage”交换机
2. 每个Cms Client创建一个队列与交换机绑定
3. 每个Cms Client程序配置队列名称和routingKey，将站点ID作为routingKey。

```java
@Configuration
public class RabbitmqConfig {

    //队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    //队列的名称
    @Value("${xuecheng.mq.queue}")
    public  String queue_cms_postpage_name;
    //routingKey 即站点Id
    @Value("${xuecheng.mq.routingKey}")
    public  String routingKey;
    /**
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE() {
        Queue queue = new Queue(queue_cms_postpage_name);
        return queue;
    }

    /**
     * 绑定队列到交换机
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue, @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
```

### 1.2.4 消息格式

消息内容采用json格式存储数据，如下：

页面id：发布页面的id

```json
{
    "pageId":""
}
```

### 1.2.5 PageDao

1. 创建CmsPageRepository 查询页面信息

```java
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
｝
```

2. 创建CmsSiteRepository查询站点信息，主要获取站点物理路径

```java
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
```

### 1.2.6 PageService

![](img/page2.png)

```java
@Service
public class PageService {

    private static  final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    //保存html页面到服务器物理路径
    public void savePageToServerPath(String pageId){
        //根据pageId查询cmsPage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        //得到html的文件id，从cmsPage中获取htmlFileId内容
        String htmlFileId = cmsPage.getHtmlFileId();

        //从gridFS中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if(inputStream == null){
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}",htmlFileId);
            return ;
        }
        //得到站点id
        String siteId = cmsPage.getSiteId();
        //得到站点的信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //得到站点的物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面的物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //将html文件保存到服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //根据文件id从GridFS中查询文件内容
    public InputStream getFileById(String fileId){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据页面id查询页面信息
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    //根据站点id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
```

### 1.2.7 ConsumerPostPage

监听队列消息,调用服务方发布页面

```java
@Component
public class ConsumerPostPage {

    private static  final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);
    @Autowired
    PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId = (String) map.get("pageId");
        //校验页面是否合法
        CmsPage cmsPage = pageService.findCmsPageById(pageId);
        if(cmsPage == null){
            LOGGER.error("receive postpage msg,cmsPage is null,pageId:{}",pageId);
            return ;
        }
        //调用service方法将页面从GridFs中下载到服务器
        pageService.savePageToServerPath(pageId);

    }
}
```

## 1.3 页面发布生产方

### 1.3.1 需求分析

管理员通过 cms系统发布“页面发布”的消费，cms系统作为页面发布的生产方。

需求如下:

1. 管理员进入管理界面点击“页面发布”，前端请求cms页面发布接口。

2. cms页面发布接口执行页面静态化，并将静态化页面存储至GridFS中。

3. 静态化成功后，向消息队列发送页面发布的消息。

   3.1 获取页面的信息及页面所属站点ID。

   3.2 设置消息内容为页面ID。（采用json格式，方便日后扩展）

   3.3 发送消息给ex_cms_postpage交换机，并将站点ID作为routingKey。

### 1.3.2 RabbitMQ配置

#### pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring‐boot‐starter‐amqp</artifactId>
</dependency>
```

#### application.yml

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    virtualHost: /
```

#### RabbitMQConfig配置

```java
@Configuration
public class RabbitmqConfig {

    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    /**
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
```

### 1.3.3 Api配置

在api工程定义页面发布接口：

```java
//页面发布
@ApiOperation("页面发布")
public ResponseResult post(String pageId);
```

### 1.3.4 PageService

![](img/page3.png)

```java
//页面发布
public ResponseResult post(String pageId){
    //执行页面静态化
    String pageHtml = this.getPageHtml(pageId);
    //将页面静态化文件存储到GridFs中
    CmsPage cmsPage = saveHtml(pageId, pageHtml);
    //向MQ发消息
    sendPostPage(pageId);
    return new ResponseResult(CommonCode.SUCCESS);
}

//页面静态化方法
public String getPageHtml(String pageId){

    //获取数据模型
    Map model = getModelByPageId(pageId);
    if(model == null){
        //数据模型获取不到
        ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
    }

    //获取页面的模板信息
    String template = getTemplateByPageId(pageId);
    if(StringUtils.isEmpty(template)){
        ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
    }

    //执行静态化
    String html = generateHtml(template, model);
    return html;

}

//保存html到GridFS
private CmsPage saveHtml(String pageId,String htmlContent){
    //先得到页面信息
    CmsPage cmsPage = this.getById(pageId);
    if(cmsPage == null){
        ExceptionCast.cast(CommonCode.INVALID_PARAM);
    }
    ObjectId objectId = null;
    try {
        //将htmlContent内容转成输入流
        InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
        //将html文件内容保存到GridFS
        objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
    } catch (IOException e) {
        e.printStackTrace();
    }

    //将html文件id更新到cmsPage中
    cmsPage.setHtmlFileId(objectId.toHexString());
    cmsPageRepository.save(cmsPage);
    return cmsPage;
}

 //向mq 发送消息
 private void sendPostPage(String pageId){
     //得到页面信息
     CmsPage cmsPage = this.getById(pageId);
     if(cmsPage == null){
         ExceptionCast.cast(CommonCode.INVALID_PARAM);
     }
     //创建消息对象
     Map<String,String> msg = new HashMap<>();
     msg.put("pageId",pageId);
     //转成json串
     String jsonString = JSON.toJSONString(msg);
     //发送给mq
     //站点id
     String siteId = cmsPage.getSiteId();
     rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
 }
```

### 1.3.5 CmsPageController

编写Controller实现api接口，接收页面请求，调用service执行页面发布。

```java
@Override
@PostMapping("/postPage/{pageId}")
public ResponseResult post(@PathVariable("pageId") String pageId) {
    return pageService.postPage(pageId);
}
```

## 1.4 页面发布前端

用户操作流程：

1. 用户进入cms页面列表。
2. 点击“发布”请求服务端接口，发布页面。
3. 提示“发布成功”，或发布失败。

### 1.4.1 API方法

```java
/*发布页面*/
export const page_postPage= id => {
  return http.requestPost(apiUrl+'/cms/page/postPage/'+id)
}
```

### 1.4.2 页面

1. 修改page_list.vue，添加发布按钮

```html
<el‐table‐column label="发布" width="80">
  <template slot‐scope="scope">
    <el‐button
      size="small" type="primary" plain @click="postPage(scope.row.pageId)">发布
    </el‐button>
  </template>
</el‐table‐column>
```

2. 添加页面发布事件：

```javascript
postPage (id) {
  this.$confirm('确认发布该页面吗?', '提示', {
  }).then(() => {
    cmsApi.page_postPage(id).then((res) => {
      if(res.success){
        console.log('发布页面id='+id);
        this.$message.success('发布成功，请稍后查看结果');
      }else{
        this.$message.error('发布失败');
      }
    });
  }).catch(() => {
  });
}
```

### 1.4.3 思考

1. 如果发布到服务器的页面内容不正确怎么办？

   将原来的页面进行重新名备份,如果发现新页面内容有误,将新页面删除,然后再将就页面的名称恢复即可

2. 一个页面需要发布很多服务器，点击“发布”后如何知道详细的发布结果？

    使用RPC工作模式,让消费方处理完消息后,给发送方通知消费的结果

3. 一个页面发布到多个服务器，其中有一个服务器发布失败时怎么办？

   使用RPC工作模式,当消息的发送方如果没有收到消息的消费方成功的通知,那么就再次的发送消息让消费方重新处理

# 2. 课程管理

在线教育平台的课程信息相当于电商平台的商品。课程管理是后台管理功能中最重要的模块。本项目为教学机构提供课程管理功能，教学机构可以添加属于自己的课程，供学生在线学习。

## 2.1 需求分析

包含功能需求:

1. 分类管理
2. 新增课程
3. 修改课程
4. 预览课程
5. 发布课程

用户操作流程:

1. 进入我的课程

![](img/course1.png)

2. 点击“新增课程”，进入添加课程界面

![](img/course2.png)

3. 输入课程基本信息，点击提交

4. 课程基本信息提交成功，自动进入“管理课程”界面，点击“管理课程”也可以进入“管理课程”界面

![](img/course3.png)

5. 编辑图片

![](img/course4.png)

6. 编辑课程营销信息

营销信息主要是设置课程的收费方式及价格。

![](img/course5.png)

7. 编辑课程计划

![](img/course6.png)

![](img/course7.png)

## 2.2 教学计划

本模块对课程信息管理功能的教学方法采用实战教学方法，旨在通过实战提高接口编写的能力，具体教学方法如下：

1. 前后端工程导入

   教学管理前端工程采用与系统管理工程相同的技术，直接导入后在此基础上开发。

   课程管理服务端工程采用Spring Boot技术构建，技术层技术使用Spring data Jpa（与Spring data Mongodb类似）、Mybatis，直接导入后在此基础上开发。

2. 课程计划功能

   课程计划功能采用全程教学。

3. 我的课程、新增课程、修改课程、课程营销

   我的课程、新增课程、修改课程、课程营销四个功能采用实战方式，课堂上会讲解每个功能的需求及技术点，讲解完成学生开始实战，由导师进行技术指导。

4. 参考文档

   实战结束提供每个功能的开发文档，学生参考文档并修正功能缺陷。

## 2.3 环境搭建

### 2.3.1 搭建数据库环境

1. 创建数据库

   课程管理使用MySQL数据库，创建课程管理数据库：xc_course。导入xc_course.sql脚本

   ![](img/course9.png)

2. 数据表介绍

![](img/course8.png)

### 2.3.2 导入课程管理服务工程

#### 2.3.2.1 持久层技术介绍

课程管理服务使用MySQL数据库存储课程信息，持久层技术如下：

1. spring data jpa：用于表的基本CRUD。

2. mybatis：用于复杂的查询操作。

3. druid：使用阿里巴巴提供的spring boot 整合druid包druid-spring-boot-starter管理连接池。

   druid-spring-boot-starter地址：https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

#### 2.3.2.2 导入工程

导入资料下的 “xc-service-manage-course.zip”。

### 2.3.3 导入课程管理前端工程

课程管理属于教学管理子系统的功能，使用用户为教学机构的管理人员和老师，为保证系统的可维护性，单独创建一个教学管理前端工程。 教学管理前端工程与系统管理前端的工程结构一样，也采用vue.js框架来实现。

从课程资料目录拷贝xc-ui-pc-teach.zip到工程，使用webstorm打开，启动工程，效果图如下：

![](img/course10.png)

# 3. 课程计划

## 3.1 需求分析

**什么是课程计划？**

课程计划定义了课程的章节内容，学生通过课程计划进行在线学习，下图中右侧显示的就是课程计划。

课程计划包括两级，第一级是课程的大章节,第二级是大章节下属的小章节，每个小章节通常是一段视频，学生点击小章节在线学习。

**教学管理人员对课程计划如何管理？**

功能包括：添加课程计划、删除课程计划、修改课程计划等。

![](img/course11.png)

## 3.2 课程计划查询

### 3.2.1 需求分析

课程计划查询是将某个课程的课程计划内容完整的显示出来，如下图所示：

![](img/course12.png)

左侧显示的就是课程计划，课程计划是一个树型结构，方便扩展课程计划的级别。

在上边页面中，点击“添加课程计划”即可对课程计划进行添加操作。

点击修改可对某个章节内容进行修改。

点击删除可删除某个章节。

### 3.2.2 界面原型

#### 3.2.2.1 Tree组件介绍

本功能使用element-ui 的tree组件来完成

使用案例:[http://element-cn.eleme.io/#/zh-CN/component/tree](http://element-cn.eleme.io/#/zh-CN/component/tree)

![](img/Tree1.png)

在course_plan.vue文件中添加tree组件的代码

1. 组件标签

```html
<el-tree
  :data="data"
  show-checkbox
  node-key="id"
  :default-expanded-keys="[2, 3]"
  :default-checked-keys="[5]"
  :props="defaultProps">
</el-tree>
```

2. 数据对象

```javascript
<script>
  export default {
    data() {
      return {
        data: [{
          id: 1,
          label: '一级 1',
          children: [{
            id: 4,
            label: '二级 1-1',
            children: [{
              id: 9,
              label: '三级 1-1-1'
            }, {
              id: 10,
              label: '三级 1-1-2'
            }]
          }]
        }, {
          id: 2,
          label: '一级 2',
          children: [{
            id: 5,
            label: '二级 2-1'
          }, {
            id: 6,
            label: '二级 2-2'
          }]
        }, {
          id: 3,
          label: '一级 3',
          children: [{
            id: 7,
            label: '二级 3-1'
          }, {
            id: 8,
            label: '二级 3-2'
          }]
        }],
        defaultProps: {
          children: 'children',
          label: 'label'
        }
      };
    }
  };
</script>
```

#### 3.2.2.2 webStorm配置JSX

本组件用到了JSX语法，如下所示：

![](img/jsx.png)

JSX 是Javascript和XML结合的一种格式，它是React的核心组成部分，JSX和XML语法类似，可以定义属性以及子元素。唯一特殊的是可以用大括号来加入JavaScript表达式。遇到 HTML 标签（以 < 开头），就用 HTML 规则解析；遇到代码块（以 { 开头），就用 JavaScript 规则解析。

参考文档:[https://react.docschina.org/docs/introducing-jsx.html](https://react.docschina.org/docs/introducing-jsx.html)

下面是一个例子：

![](img/jsx2.png)

WebStorm配置JSX语法支持:

1. Javascript version 选择 React JSX （如果没有就选择JSX Harmony）

![](img/jsx3.png)

2. HTML 类型文件中增加vue

![](img/jsx4.png)

如果已经在 vue template 中已存在.vue 则把它改为.vue2(因为要在Html中添加.vue)

![](img/jsx5.png)

### 3.2.3 API接口

#### 3.2.3.1 数据模型

##### 表结构

![](img/course13.png)

##### 模型类

课程计划为树型结构，由树根（课程）和树枝（章节）组成，为了保证系统的可扩展性，在系统设计时将课程计划设置为树型结构。

```java
@Data
@ToString
@Entity
@Table(name="teachplan")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Teachplan implements Serializable {
    private static final long serialVersionUID = -916357110051689485L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String pname;
    private String parentid;
    private String grade;
    private String ptype;
    private String description;
    private String courseid;
    private String status;
    private Integer orderby;
    private Double timelength;
    private String trylearn;

}
```

#### 3.2.3.2 自定义模型类

前端页面需要树型结构的数据来展示Tree组件，如下：

```json
[{
    id: 1,
    label: '一级 1',
    children: [{
      id: 4,
      label: '二级 1‐1'
     }]
}]
```

自定义课程计划结点类如下：		

```java
@Data
@ToString
public class TeachplanNode extends Teachplan {
    List<TeachplanNode> children;
}
```

#### 3.2.3.3 接口定义

根据课程id查询课程的计划接口如下，在api工程创建course包，创建CourseControllerApi接口类并定义接口方法如下：

```java
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);
}
```

### 3.2.3 课程管理服务

#### 3.2.3.1 Sql

课程计划是树型结构，采用表的自连接方式进行查询，sql语句如下：

```java
SELECT
  a.id one_id,
  a.pname one_pname,
  b.id two_id,
  b.pname two_pname,
  c.id three_id,
  c.pname three_pname
FROM
  teachplan a
  LEFT JOIN teachplan b
    ON b.parentid = a.id
  LEFT JOIN teachplan c
    ON b.id = c.parentid
WHERE a.parentid = '0'
  AND a.courseid = '402885816243d2dd016243f24c030002'
ORDER BY a.orderby,
  b.orderby,
  c.orderby
```

#### 3.2.3.2 Dao

1. mapper接口

```java
@Mapper
public interface TeachplanMapper {
    public TeachplanNode selectList(String courseId);
}
```

2. mapper映射文件

```xml
<mapper namespace="com.xuecheng.manage_course.dao.TeachplanMapper">

    <resultMap id="teachplanMap" type="com.xuecheng.framework.domain.course.ext.TeachplanNode">
        <id column="one_id" property="id"></id>
        <result column="one_pname" property="pname"></result>
        <collection property="children" 
                    ofType="com.xuecheng.framework.domain.course.ext.TeachplanNode">
            <id column="two_id" property="id"></id>
            <result column="two_pname" property="pname"></result>
            <collection property="children" 
                        ofType="com.xuecheng.framework.domain.course.ext.TeachplanNode">
                <id column="three_id" property="id"></id>
                <result column="three_pname" property="pname"></result>
            </collection>
        </collection>
    </resultMap>

    <select id="selectList" parameterType="java.lang.String"
            resultMap="teachplanMap">
  SELECT
  a.id one_id,
  a.pname one_pname,
  b.id two_id,
  b.pname two_pname,
  c.id three_id,
  c.pname three_pname
FROM
  teachplan a
  LEFT JOIN teachplan b
    ON b.parentid = a.id
  LEFT JOIN teachplan c
    ON c.parentid = b.id
WHERE a.parentid = '0'
<if test="_parameter !=null and _parameter!=''">
    AND a.courseid = #{courseId}
</if>

ORDER BY a.orderby,
  b.orderby,
  c.orderby
    </select>
</mapper>
```

说明：针对输入参数为简单类型 #{}中可以是任意类型，判断参数是否为空要用 _parameter（它属于mybatis的内置参数）

#### 3.4.3.3 Service
创建CourseService类，定义查询课程计划方法。

```java
@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;
    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId){
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }
}
```

#### 3.4.3.4 Controller

```java
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;
    //查询课程计划
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(String courseId) {
        return courseService.findTeachplanList(courseId);
    }
}
```

#### 3.4.3.5 测试

使用postman或swagger-ui测试查询接口。

Get 请求：http://localhost:31200/course/teachplan/list/402885816243d2dd016243f24c030002

### 3.2.4 前端方法

#### 3.2.4.1 Api方法

定义课程计划查询的api方法：

```javascript
/*查询课程计划*/
export const findTeachplanList = courseid => {
  return http.requestQuickGet(apiUrl+'/course/teachplan/list/'+courseid)
}
```

#### 3.2.4.3 Api调用

1. 在mounted钩子方法 中查询 课程计划

   定义查询课程计划的方法，赋值给数据对象teachplanList

```javascript
findTeachplan(){
  courseApi.findTeachplanList(this.courseid).then((res) => {
    this.teachplanList = [];//清空树
    if(res.children){
      this.teachplanList = res.children;
    }
});
```

2. 在mounted钩子中查询课程计划

```javascript
mounted(){ 
  //课程id
  this.courseid = this.$route.params.courseid;
  //课程计划
  this.findTeachplan();
}
```

3. 修改树结点的标签属性

   课程计划信息中pname为结点的名称，需要修改树结点的标签属性方可正常显示课程计划名称，如下：

```javascript
defaultProps: { 
  children: 'children',
  label: 'pname'
}
```

#### 3.2.4.3 测试

![](img/course14.png)

## 3.3 添加课程计划

### 3.3.1 需求分析

操作流程:

1. 进入课程计划页面，点击“添加课程计划”
2. 打开添加课程计划页面，输入课程计划信息
3. 点击提交

![](img/course15.png)

上级节点说明:

1. 不选择上级结点表示当前课程计划为该课程的一级结点。
2. 当添加该课程在课程计划中还没有节点时要自动添加课程的根结点。

#### 页面原型说明

添加课程计划采用弹出窗口组件Dialog。

1. 视图部分

   在course_plan.vue页面添加添加课程计划的弹出窗口代码：

```javascript
<el-dialog title="添加课程计划" :visible.sync="teachplayFormVisible" >

<el-form ref="teachplanForm"  :model="teachplanActive" label-width="140px" style="width:600px;" :rules="teachplanRules" >
    <el-form-item label="上级结点" >
      <el-select v-model="teachplanActive.parentid" placeholder="不填表示根结点">
        <el-option
          v-for="item in teachplanList"
          :key="item.id"
          :label="item.pname"
          :value="item.id">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="章节/课时名称" prop="pname">
      <el-input v-model="teachplanActive.pname" auto-complete="off"></el-input>
    </el-form-item>
    <el-form-item label="课程类型" >
      <el-radio-group v-model="teachplanActive.ptype">
        <el-radio class="radio" label='1'>视频</el-radio>
        <el-radio class="radio" label='2'>文档</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="学习时长（分钟）  请输入数字" >
      <el-input type="number" v-model="teachplanActive.timelength" auto-complete="off" ></el-input>
    </el-form-item>
    <el-form-item label="排序字段" >
      <el-input v-model="teachplanActive.orderby" auto-complete="off" ></el-input>
    </el-form-item>
    <el-form-item label="章节/课时介绍" prop="description">
      <el-input type="textarea" v-model="teachplanActive.description" ></el-input>
    </el-form-item>

    <el-form-item label="状态" prop="status">
      <el-radio-group v-model="teachplanActive.status" >
        <el-radio class="radio" label="0" >未发布</el-radio>
        <el-radio class="radio" label='1'>已发布</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item  >
      <el-button type="primary" v-on:click="addTeachplan">提交</el-button>
      <el-button type="primary" v-on:click="resetForm">重置</el-button>
    </el-form-item>

   </el-form>
 </el-dialog>
```

2. 数据模型

   在数据模型中添加如下变量,进行输入校验：

```javascript
teachplanRules: {
  pname: [
    {required: true, message: '请输入课程计划名称', trigger: 'blur'}
  ],
  status: [
    {required: true, message: '请选择状态', trigger: 'blur'}
  ]
},
```

3. 添加按钮

   通过变量 teachplayFormVisible控制弹出窗口是否显示。

```javascript
<el‐button type="primary" @click="teachplayFormVisible = true"> 添加课程计划</el‐button>
```

4. 定义表单提交方法和重置方法

```javascript
// 提交课程计划
addTeachplan(){
    alert("提交课程计划")
},
//重置表单
resetForm(){
  this.teachplanActive = {}
}
```

### 3.3.2 API接口

```java
@ApiOperation("添加课程计划")
public ResponseResult addTeachplan(Teachplan teachplan);
```

### 3.3.3 课程管理服务

#### 3.3.3.1 Dao

```java
public interface TeachplanRepository extends JpaRepository<Teachplan, String> { 
   //定义方法根据课程id和父结点id查询出结点列表，可以使用此方法实现查询根结点
   public List<Teachplan> findByCourseidAndParentid(String courseId,String parentId);
}
```

#### 3.3.3.2 Service

```java
@Transactional
public ResponseResult addTeachplan(Teachplan teachplan) {

    if(teachplan == null ||
            StringUtils.isEmpty(teachplan.getPname()) ||
            StringUtils.isEmpty(teachplan.getCourseid())){
        ExceptionCast.cast(CommonCode.INVALID_PARAM);
    }
    //课程id
    String courseid = teachplan.getCourseid();
    //父结点的id
    String parentid = teachplan.getParentid();
    if(StringUtils.isEmpty(parentid)){
        //获取课程的根结点
        parentid = getTeachplanRoot(courseid);
    }
    //查询根结点信息
    Optional<Teachplan> optional = teachplanRepository.findById(parentid);
    Teachplan teachplan1 = optional.get();
    //父结点的级别
    String parent_grade = teachplan1.getGrade();
    //创建一个新结点准备添加
    Teachplan teachplanNew = new Teachplan();
    //将teachplan的属性拷贝到teachplanNew中
    BeanUtils.copyProperties(teachplan,teachplanNew);
    //要设置必要的属性
    teachplanNew.setParentid(parentid);
    if(parent_grade.equals("1")){
        teachplanNew.setGrade("2");
    }else{
        teachplanNew.setGrade("3");
    }
    teachplanNew.setStatus("0");//未发布
    teachplanRepository.save(teachplanNew);
    return new ResponseResult(CommonCode.SUCCESS);
}
```

```java
//获取课程的根结点
public String getTeachplanRoot(String courseId){
    Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
    if(!optional.isPresent()){
        return null;
    }
    CourseBase courseBase = optional.get();
    //调用dao查询teachplan表得到该课程的根结点（一级结点）
    List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
    if(teachplanList == null || teachplanList.size()<=0){
        //新添加一个课程的根结点
        Teachplan teachplan = new Teachplan();
        teachplan.setCourseid(courseId);
        teachplan.setParentid("0");
        teachplan.setGrade("1");//一级结点
        teachplan.setStatus("0");
        teachplan.setPname(courseBase.getName());
        teachplanRepository.save(teachplan);
        return teachplan.getId();
    }
    //返回根结点的id
    return teachplanList.get(0).getId();
}
```

### 3.3.4 前端开发

```java
//添加课程计划
@Override
@PostMapping("/teachplan/add")
public ResponseResult addTeachplan(@RequestBody  Teachplan teachplan) {
    return courseService.addTeachplan(teachplan);
}
```

#### 3.3.3.4 测试

复杂一些的业务逻辑建议写完服务端代码就进行单元测试。

使用swagger-ui或postman测试上边的课程计划添加接口。

### 3.3.5 前端

#### 3.3.5.1 Api前端

1. 定义api方法

```javascript
/* 添加课程计划*/
export const addTeachplan = teachplah => {
  return http.requestPost(apiUrl+'/course/teachplan/add',teachplah)
}
```

2. 调用 api

```javascript
addTeachplan(){
  this.$refs.teachplayForm.validate((valid) => {
    if (valid) {
      //添加课程计划时带上课程id
      this.teachplanActive.courseid = this.courseid;
      courseApi.addTeachplan(this.teachplanActive).then((res) => {
        if(res.success){
          this.$message.success('提交成功');
          //清空表单
          this.teachplanActive = {}
          //刷新整个树
          this.findTeachplan();
       }else{
          this.$message.error('提交失败');
        }
      });
    }
  })
}
```

### 3.3.5 测试

测试流程:

1. 新建一个课程

2. 向新建课程中添加课程计划

   添加一级结点

   添加二级结点