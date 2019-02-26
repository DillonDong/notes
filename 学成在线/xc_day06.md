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

# 3. 课程计划

