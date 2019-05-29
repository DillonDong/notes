### 1.Mybatis多表查询

#### 1.1 一对一查询 

##### 1.1.1 一对一查询的模型

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户

一对一查询的需求：查询一个订单，与此同时查询出该订单所属的用户

![](img\图片1.png)

##### 1.1.2一对一查询的语句

对应的sql语句

```sql
select *  from orders o,user u where o.uid=u.id;
```

查询的结果如下：

![](img\图片2.png)

##### 1.1.3 创建Order和User实体

```java
public class Order {

    private int id;
    private Date ordertime;
    private double total;

    //代表当前订单从属于哪一个客户
    private User user;
}

public class User {
    
    private int id;
    private String username;
    private String password;
    private Date birthday;

}
```

##### 1.1.4 创建OrderMapper接口

```java
public interface OrderMapper {
    List<Order> findAll();
}
```

##### 1.1.5 配置OrderMapper.xml

```xml
<mapper namespace="com.itheima.mapper.OrderMapper">
    <resultMap id="orderMap" type="com.itheima.domain.Order">
        <result column="uid" property="user.id"></result>
        <result column="username" property="user.username"></result>
        <result column="password" property="user.password"></result>
        <result column="birthday" property="user.birthday"></result>
    </resultMap>
    <select id="findAll" resultMap="orderMap">
        select * from orders o,user u where o.uid=u.id
    </select>
</mapper>
```

其中<resultMap>还可以配置如下：

```xml
<resultMap id="orderMap" type="com.itheima.domain.Order">
    <result property="id" column="id"></result>
    <result property="ordertime" column="ordertime"></result>
    <result property="total" column="total"></result>
    <association property="user" javaType="com.itheima.domain.User">
        <result column="uid" property="id"></result>
        <result column="username" property="username"></result>
        <result column="password" property="password"></result>
        <result column="birthday" property="birthday"></result>
    </association>
</resultMap>
```

##### 1.1.6 测试结果

```java
OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
List<Order> all = mapper.findAll();
for(Order order : all){
    System.out.println(order);
}
```

![](img\图片3.png)

#### 1.2 一对多查询

##### 1.2.1 一对多查询的模型

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户

一对多查询的需求：查询一个用户，与此同时查询出该用户具有的订单

  ![](img\图片4.png)

##### 1.2.2 一对多查询的语句

对应的sql语句：

```sql
select *,o.id oid from user u left join orders o on u.id=o.uid;
```



查询的结果如下：

![](img\图片5.png)

##### 1.2.3 修改User实体

```java
public class Order {

    private int id;
    private Date ordertime;
    private double total;

    //代表当前订单从属于哪一个客户
    private User user;
}

public class User {
    
    private int id;
    private String username;
    private String password;
    private Date birthday;
    //代表当前用户具备哪些订单
    private List<Order> orderList;
}

```

##### 1.2.4 创建UserMapper接口

```java
public interface UserMapper {
    List<User> findAll();
}

```

##### 1.2.5 配置UserMapper.xml

```xml
<mapper namespace="com.itheima.mapper.UserMapper">
    <resultMap id="userMap" type="com.itheima.domain.User">
        <result column="id" property="id"></result>
        <result column="username" property="username"></result>
        <result column="password" property="password"></result>
        <result column="birthday" property="birthday"></result>
        <collection property="orderList" ofType="com.itheima.domain.Order">
            <result column="oid" property="id"></result>
            <result column="ordertime" property="ordertime"></result>
            <result column="total" property="total"></result>
        </collection>
    </resultMap>
    <select id="findAll" resultMap="userMap">
        select *,o.id oid from user u left join orders o on u.id=o.uid
    </select>
</mapper>
```

##### 1.2.6 测试结果

```java
UserMapper mapper = sqlSession.getMapper(UserMapper.class);
List<User> all = mapper.findAll();
for(User user : all){
    System.out.println(user.getUsername());
    List<Order> orderList = user.getOrderList();
    for(Order order : orderList){
        System.out.println(order);
    }
    System.out.println("----------------------------------");
}
```

![](img\图片6.png)

#### 1.3 多对多查询

##### 1.3.1 多对多查询的模型

用户表和角色表的关系为，一个用户有多个角色，一个角色被多个用户使用

多对多查询的需求：查询用户同时查询出该用户的所有角色

![](img\图片7.png)

##### 1.3.2 多对多查询的语句

对应的sql语句：

```sql
select u.*,r.*,r.id rid from user u 
left join user_role ur on u.id=ur.user_id
inner join role r on ur.role_id=r.id;
```



查询的结果如下：

![](img\图片8.png)

##### 1.3.3 创建Role实体，修改User实体

```java
public class User {
    private int id;
    private String username;
    private String password;
    private Date birthday;
    //代表当前用户具备哪些订单
    private List<Order> orderList;
    //代表当前用户具备哪些角色
    private List<Role> roleList;
}

public class Role {

    private int id;
    private String rolename;

}

```

##### 1.3.4  添加UserMapper接口方法

```java
List<User> findAllUserAndRole();
```

##### 1.3.5 配置UserMapper.xml

```xml
<resultMap id="userRoleMap" type="com.itheima.domain.User">
    <result column="id" property="id"></result>
    <result column="username" property="username"></result>
    <result column="password" property="password"></result>
    <result column="birthday" property="birthday"></result>
    <collection property="roleList" ofType="com.itheima.domain.Role">
        <result column="rid" property="id"></result>
        <result column="rolename" property="rolename"></result>
    </collection>
</resultMap>
<select id="findAllUserAndRole" resultMap="userRoleMap">
    select u.*,r.*,r.id rid from user u 
    left join user_role ur on u.id=ur.user_id
    inner join role r on ur.role_id=r.id
</select>
```

##### 1.3.6 测试结果

```java
UserMapper mapper = sqlSession.getMapper(UserMapper.class);
List<User> all = mapper.findAllUserAndRole();
for(User user : all){
    System.out.println(user.getUsername());
    List<Role> roleList = user.getRoleList();
    for(Role role : roleList){
        System.out.println(role);
    }
    System.out.println("----------------------------------");
}
```

![](img\图片9.png)

#### 1.4 知识小结

MyBatis多表配置方式：

**一对一配置：使用<resultMap>+<association>做配置**

**一对多配置：使用<resultMap>+<collection>做配置**

**多对多配置：使用<resultMap>+<collection>做配置**



### 2.Mybatis的注解开发

#### 2.1 MyBatis的常用注解 

这几年来注解开发越来越流行，Mybatis也可以使用注解开发方式，这样我们就可以减少编写Mapper

映射文件了。我们先围绕一些基本的CRUD来学习，再学习复杂映射多表操作。

@Insert：实现新增

@Update：实现更新

@Delete：实现删除

@Select：实现查询

@Result：实现结果集封装

@Results：可以与@Result 一起使用，封装多个结果集

@One：实现一对一结果集封装

@Many：实现一对多结果集封装

#### 2.2 MyBatis的增删改查 

我们完成简单的user表的增删改查的操作

```java
private UserMapper userMapper;

@Before
public void before() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new 
                 SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession(true);
    userMapper = sqlSession.getMapper(UserMapper.class);
}

@Test
public void testAdd() {
    User user = new User();
    user.setUsername("测试数据");
    user.setPassword("123");
    user.setBirthday(new Date());
    userMapper.add(user);
}
@Test
public void testUpdate() throws IOException {
    User user = new User();
    user.setId(16);
    user.setUsername("测试数据修改");
    user.setPassword("abc");
    user.setBirthday(new Date());
    userMapper.update(user);
}

@Test
public void testDelete() throws IOException {
    userMapper.delete(16);
}
@Test
public void testFindById() throws IOException {
    User user = userMapper.findById(1);
    System.out.println(user);
}
@Test
public void testFindAll() throws IOException {
    List<User> all = userMapper.findAll();
    for(User user : all){
        System.out.println(user);
    }
}

```

修改MyBatis的核心配置文件，我们使用了注解替代的映射文件，所以我们只需要加载使用了注解的Mapper接口即可

```xml
<mappers>
    <!--扫描使用注解的类-->
    <mapper class="com.itheima.mapper.UserMapper"></mapper>
</mappers>
```

或者指定扫描包含映射关系的接口所在的包也可以

```xml
<mappers>
    <!--扫描使用注解的类所在的包-->
    <package name="com.itheima.mapper"></package>
</mappers>
```

#### 2.3 MyBatis的注解实现复杂映射开发

实现复杂关系映射之前我们可以在映射文件中通过配置<resultMap>来实现，使用注解开发后，我们可以使用@Results注解，@Result注解，@One注解，@Many注解组合完成复杂关系的配置

![](img\图片10.png)

![](img\图片11.png)

#### 2.4 一对一查询

##### 2.4.1 一对一查询的模型

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户

一对一查询的需求：查询一个订单，与此同时查询出该订单所属的用户

![](img\图片12.png)

##### 2.4.2 一对一查询的语句

对应的sql语句：

    select * from orders;
    
    select * from user where id=查询出订单的uid;

查询的结果如下：

![](img\图片13.png)

##### 2.4.3 创建Order和User实体

```java
public class Order {

    private int id;
    private Date ordertime;
    private double total;

    //代表当前订单从属于哪一个客户
    private User user;
}

public class User {
    
    private int id;
    private String username;
    private String password;
    private Date birthday;

}
```

##### 2.4.4 创建OrderMapper接口

```java
public interface OrderMapper {
    List<Order> findAll();
}
```

##### 2.4.5 使用注解配置Mapper

```java
public interface OrderMapper {
    @Select("select * from orders")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "ordertime",column = "ordertime"),
            @Result(property = "total",column = "total"),
            @Result(property = "user",column = "uid",
                    javaType = User.class,
                    one = @One(select = "com.itheima.mapper.UserMapper.findById"))
    })
    List<Order> findAll();
}
```

```java
public interface UserMapper {

    @Select("select * from user where id=#{id}")
    User findById(int id);
    
}
```

##### 2.4.6 测试结果

```java
@Test
public void testSelectOrderAndUser() {
    List<Order> all = orderMapper.findAll();
    for(Order order : all){
        System.out.println(order);
    }
}
```

![](img\图片14.png)

#### 2.5 一对多查询

##### 2.5.1 一对多查询的模型

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户

一对多查询的需求：查询一个用户，与此同时查询出该用户具有的订单

![](img\图片15.png)

##### 2.5.2 一对多查询的语句

对应的sql语句：

    select * from user;
    
    select * from orders where uid=查询出用户的id;

查询的结果如下：

![](img\图片16.png)

##### 2.5.3 修改User实体

```java
public class Order {

    private int id;
    private Date ordertime;
    private double total;

    //代表当前订单从属于哪一个客户
    private User user;
}

public class User {
    
    private int id;
    private String username;
    private String password;
    private Date birthday;
    //代表当前用户具备哪些订单
    private List<Order> orderList;
}
```

##### 2.5.4 创建UserMapper接口

```java
List<User> findAllUserAndOrder();
```

##### 2.5.5 使用注解配置Mapper

```java
public interface UserMapper {
    @Select("select * from user")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "birthday",column = "birthday"),
            @Result(property = "orderList",column = "id",
                    javaType = List.class,
                    many = @Many(select = "com.itheima.mapper.OrderMapper.findByUid"))
    })
    List<User> findAllUserAndOrder();
}

public interface OrderMapper {
    @Select("select * from orders where uid=#{uid}")
    List<Order> findByUid(int uid);

}
```

##### 2.5.6 测试结果

```java
List<User> all = userMapper.findAllUserAndOrder();
for(User user : all){
    System.out.println(user.getUsername());
    List<Order> orderList = user.getOrderList();
    for(Order order : orderList){
        System.out.println(order);
    }
    System.out.println("-----------------------------");
}
```

![](img\图片17.png)

#### 2.6 多对多查询

##### 2.6.1 多对多查询的模型

用户表和角色表的关系为，一个用户有多个角色，一个角色被多个用户使用

多对多查询的需求：查询用户同时查询出该用户的所有角色

![](img\图片18.png)

##### 2.6.2 多对多查询的语句

对应的sql语句：

    select * from user;
    
    select * from role r,user_role ur where r.id=ur.role_id and ur.user_id=用户的id

查询的结果如下：

![](img\图片19.png)

##### 2.6.3 创建Role实体，修改User实体

```java
public class User {
    private int id;
    private String username;
    private String password;
    private Date birthday;
    //代表当前用户具备哪些订单
    private List<Order> orderList;
    //代表当前用户具备哪些角色
    private List<Role> roleList;
}

public class Role {

    private int id;
    private String rolename;

}
```

##### 2.6.4 添加UserMapper接口方法

```java
List<User> findAllUserAndRole();
```

##### 2.6.5 使用注解配置Mapper

```java
public interface UserMapper {
    @Select("select * from user")
    @Results({
        @Result(id = true,property = "id",column = "id"),
        @Result(property = "username",column = "username"),
        @Result(property = "password",column = "password"),
        @Result(property = "birthday",column = "birthday"),
        @Result(property = "roleList",column = "id",
                javaType = List.class,
                many = @Many(select = "com.itheima.mapper.RoleMapper.findByUid"))
})
List<User> findAllUserAndRole();}



public interface RoleMapper {
    @Select("select * from role r,user_role ur where r.id=ur.role_id and ur.user_id=#{uid}")
    List<Role> findByUid(int uid);
}

```

##### 2.6.6 测试结果

```java
UserMapper mapper = sqlSession.getMapper(UserMapper.class);
List<User> all = mapper.findAllUserAndRole();
for(User user : all){
    System.out.println(user.getUsername());
    List<Role> roleList = user.getRoleList();
    for(Role role : roleList){
        System.out.println(role);
    }
    System.out.println("----------------------------------");
}
```

![](img\图片20.png)













  



  























