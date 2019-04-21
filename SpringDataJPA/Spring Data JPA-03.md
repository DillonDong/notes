# 1. Specification动态条件查询

```JpaSpecificationExecutor```提供了一种以更加面向对象的动态构建查询条件的方式.

* 动态条件查询
* 面向对象的查询方式

## 1.1 步骤

```
1. Dao接口继承JpaSpecificationExecutor
2. 创建Specification对象封装查询条件
3. 调用JpaSpecificationExecutor接口方法,传递Specification对象进行条件查询
```

## 1.2 常见查询

### 1.2.1 创建Dao接口

```java
/**
 * 客户Dao
 */
public interface CustomerDao extends JpaRepository<Customer,Long> ,JpaSpecificationExecutor<Customer>{

}
```

### 1.2.2 单条件

```java
/**
 * 单条件查询:查询名称是'黑马程序员1'的数据
 */
@Test
public void testSpec() {
    /**
     * 创建Specification对象,封装查询条件
     *      root对象:获得条件查询的属性;
     *      cb对象:构建查询条件;
     */

  Specification<Customer> spec = new Specification<Customer>() {
     public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
     		//1.获得条件查询属性
     		Path<Object> custName = root.get("custName");
     		//2.构造查询条件;   
         	//同功能JPQL: from Customer where  custName = "黑马程序员1"
     		Predicate predicate = cb.equal(custName, "黑马程序员1");
            return predicate;
      }
  };
  Customer customer = customerDao.findOne(spec);
  System.out.println(customer);
}
```

### 1.2.3 多条件

```java
/**
 * 多条件查询:根据客户名（传智播客）和客户所属行业查询（it教育）
 */
@Test
public void testSpec1() {

    /**
     * 创建Specification对象,封装查询条件
     *      root对象:获得条件查询的属性;
     *      cb对象:构建查询条件;
     */
  Specification<Customer> spec = new Specification<Customer>() {
     public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        //属性1:客户名
        Path<Object> custName = root.get("custName");
        //属性2:所属行业
        Path<Object> custIndustry = root.get("custIndustry");
        //构造查询条件
        //1.构造客户名的精准匹配查询
        Predicate p1 = cb.equal(custName, "传智播客");
        //2.构造所属行业的精准匹配查询
        Predicate p2 = cb.equal(custIndustry, "it教育");
        //3.将多个查询条件进行组合
        //同功能JPQL语句:from Customer where custName = "传智播客" and custIndustry="it教育"
        Predicate and = cb.and(p1, p2);
        return and;
     }
  };
  Customer customer = customerDao.findOne(spec);
  System.out.println(customer);
}
```

### 1.2.4 带条件查询并排序

```java
/**
 * 模糊查询:查询客户名称以 ’黑马程序员‘ 开头的客户列表,并按照ID排序
 */
@Test
public void testSpec3() {
  //构造查询条件
  Specification<Customer> spec = new Specification<Customer>() {
     public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            //查询属性：客户名
            Path<Object> custName = root.get("custName");
            //查询方式：模糊匹配,除过equals的其他查询方式必须指定比较的属性类型
            Predicate like = cb.like(custName.as(String.class), "黑马程序员%");
            return like;
        }
    };

    //创建排序对象
    Sort sort = new Sort(Sort.Direction.DESC,"custId");
    //执行查询
    List<Customer> list = customerDao.findAll(spec, sort);
    for (Customer customer : list) {
        System.out.println(customer);
    }
}
```

### 1.2.5 带条件分页查询

```java
/**
 * 带条件分页查询:查询客户名称以 ’黑马程序员‘ 开头的客户列表,显示0-5条数据
 */
@Test
public void testSpec4() {
    //条件条件对象
    Specification spec = new Specification() {
        public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
            //查询属性：客户名
            Path<Object> custName = root.get("custName");
            //查询方式：模糊匹配,除过equals的其他查询方式必须指定比较的属性类型
            Predicate like = cb.like(custName.as(String.class), "黑马程序员%");
            return like;
        }
    };
    //创建分页对象,参数1:从哪里开始查;参数2:查询多少条
    Pageable pageable = new PageRequest(0,5);
    //分页查询
    Page<Customer> page = customerDao.findAll(spec, pageable);
    System.out.println("数据集合:"+page.getContent());
    System.out.println("总条数:"+page.getTotalElements());
    System.out.println("总页数:"+page.getTotalPages());
}
```

### 1.2.6 动态查询

```java
public void test(){
        Customer customer = new Customer();
        customer.setCustIndustry("it教育");
        customer.setCustName("传智播客");

        Specification<Customer> spec = new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> list = new ArrayList<Predicate>();

                //判断cusName是否有值
                if(customer.getCustName()!=null && !"".equals(customer.getCustName())){
                    Path<Object> cusName = root.get("custName");
                    Predicate p1 = cb.like(cusName.as(String.class), customer.getCustName());
                    list.add(p1);
                }
                
                //判断custIndustry是否有值
                if(customer.getCustIndustry()!=null && !"".equals(customer.getCustIndustry())){
                    Path<Object> custIndustry = root.get("custIndustry");
                    Predicate p2 = cb.equal(custIndustry.as(String.class), customer.getCustIndustry());
                    list.add(p2);
                }

                Predicate[] predicates = list.toArray(new Predicate[0]);

                return cb.and(predicates);
            }
        };
        List<Customer> list = customerDao.findAll(spec);
        System.out.println(list);
}
```

## 1.3 cb查询方式总结

| 方法名称                      | Sql对应关系              |
| ------------------------- | -------------------- |
| equle                     | filed = value        |
| gt（greaterThan ）          | filed > value        |
| lt（lessThan ）             | filed < value        |
| ge（greaterThanOrEqualTo ） | filed >= value       |
| le（ lessThanOrEqualTo）    | filed <= value       |
| notEqule                  | filed != value       |
| like                      | filed like value     |
| notLike                   | filed not like value |

# 2. JPA的多表操作

## 2.1 多表之间的关系

**举例:**

```
一对一:居民和身份证
一对多:公司和员工
多对多:用户和角色
```

**数据库实现:**

```
一对一:在任意一张表中添加外键(唯一的),关联对方主键
一对多:在多方表中添加外键,关联一方的主键
多对多:创建中间表,分别关联两张表的主键
```

**JPA实体的实现:**

```
一对一:在任意一方添加对方的对象属性
一对多:在一方添加多方的集合属性,在多方中添加一方的对象属性
多对多:在任意一方添加对方的集合属性
```

**ORM配置多表关系步骤:**

```
1. 添加关系注解:@OneToMany @ManyToOne @ManyToMany
2. 确定由哪方维护外键关系(另外一方放弃关系维护)
3. 在维护关系一方配置外键信息
```

## 2.2 一对多配置

一方

```java
/**
 * 客户实体类
 */
@Entity
@Table(name="cst_customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long custId;
    private String custAddress;
    private String custIndustry;
    private String custLevel;
    private String custName;
    private String custPhone;
    private String custSource;
    
   /**
    * 一对多注解
    * 一方放弃维护外键关系
    */
    @OneToMany(mappedBy = "customer")
    private Set<LinkMan> linkMans = new HashSet<LinkMan>();
}
```

多方

```java
@Entity
@Table(name = "cst_linkman")
public class LinkMan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lkmId; //联系人编号(主键)
    private String lkmName;//联系人姓名
    private String lkmGender;//联系人性别
    private String lkmPhone;//联系人办公电话
    private String lkmMobile;//联系人手机
    private String lkmEmail;//联系人邮箱
    private String lkmPosition;//联系人职位
    private String lkmMemo;//联系人备注
    
   /**
    * 多对一注解
    * 多方维护外键关系,配置外键名称
    */
    @ManyToOne
    @JoinColumn(name = "lkm_cust_id")
    private Customer customer;    
}
```

### 2.2.1 一般保存

```java
@Test
@Transactional //配置事务
@Rollback(false) //不自动回滚
public void testAdd() {
    //创建一个客户
    Customer customer = new Customer();
    customer.setCustName("百度");
	//创建一个联系人
    LinkMan linkMan = new LinkMan();
    linkMan.setLkmName("小李");

	//维护外键关系
    linkMan.setCustomer(customer);
    //保存客户
    customerDao.save(customer);
    //保存联系人
    linkManDao.save(linkMan);
}
```

### 2.2.2 级联保存

```java
/**
 * 级联添加：保存一个客户的同时，保存客户的所有联系人
 *      需要在操作主体的实体类上，配置cascade属性
 *		    @OneToMany(mappedBy = "customer",cascade =CascadeType.PERSIST)
 *  		private Set<LinkMan> linkMans = new HashSet<LinkMan>();
 */
@Test
@Transactional //配置事务
@Rollback(false) //不自动回滚
public void testCascadeAdd() {
    Customer customer = new Customer();
    customer.setCustName("百度1");

    LinkMan linkMan = new LinkMan();
    linkMan.setLkmName("小李1");

    //维护外键
    linkMan.setCustomer(customer);
    //级联保存
    customer.getLinkMans().add(linkMan);

    customerDao.save(customer);
}
```

### 2.2.3 级联删除

```java
/**
 * 级联删除：删除客户的同时，删除该客户的所有联系人
 *      需要在操作主体的实体类上，配置cascade属性
 *		    @OneToMany(mappedBy = "customer",cascade =CascadeType.REMOVE)
 *  		private Set<LinkMan> linkMans = new HashSet<LinkMan>();
 */
@Test
@Transactional //配置事务
@Rollback(false) //不自动回滚
public void testCascadeRemove() {
    //1.查询1号客户
    Customer customer = customerDao.findOne(1L);
    //2.删除1号客户
    customerDao.delete(customer);
}
```

## 2.3 多对多配置

多方

```java
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private Integer age;
    //配置多对多关系,User方维护外键关系
  	@ManyToMany
    @JoinTable(name="user_role",joinColumns = {@JoinColumn(name ="user_id" )},
               inverseJoinColumns = {@JoinColumn(name="role_id")})
    private Set<Role> roles = new HashSet<Role>();
    
}
```

另一多方

```java
@Entity
@Table(name = "sys_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String roleName;

    //配置多对多关系,Role方放弃维护外键关系
    @ManyToMany(mappedBy = "roles")  //配置多表关系
    private Set<User> users = new HashSet<User>();
}
```

### 2.3.1 一般保存

```java
/**
 * 保存一个用户，保存一个角色
 */
@Test
@Transactional
@Rollback(false)
public void  testAdd() {
    User user = new User();
    user.setUserName("小李");

    Role role = new Role();
    role.setRoleName("java程序员");

    //用户维护外键关系
    user.getRoles().add(role);

    userDao.save(user);
    roleDao.save(role);
}
```

### 2.3.2 级联保存

```java
/**
 * 测试级联添加（保存一个用户的同时保存用户的关联角色）
 *      @ManyToMany(cascade = {CascadeType.PERSIST})
 *      @JoinTable(joinColumns = {@JoinColumn(name = "user_id")}, 
 *				   inverseJoinColumns ={@JoinColumn(name = "role_id")})
 *      private Set<Role> roles = new HashSet<Role>();
 */
@Test
@Transactional
@Rollback(false)
public void  testCasCadeAdd() {
    User user = new User();
    user.setUserName("小李");

    Role role = new Role();
    role.setRoleName("java程序员");

    //配置用户到角色关系，可以对中间表中的数据进行维护     1-1
    user.getRoles().add(role);

    //保存客户,级联保存角色
    userDao.save(user);
}
```

### 2.3.3 级联删除

```java
/**
 * 案例：删除id为5的用户，同时删除他的关联角色
 *	    @ManyToMany(cascade = {CascadeType.REMOVE})
 *      @JoinTable(joinColumns = {@JoinColumn(name = "user_id")}, 
 *				   inverseJoinColumns ={@JoinColumn(name = "role_id")})
 *      private Set<Role> roles = new HashSet<Role>();
 */
@Test
@Transactional
@Rollback(false)
public void  testCasCadeRemove() {
    //查询5号用户
    User user = userDao.findOne(5L);
    //删除5号用户
    userDao.delete(user);
}
```

## 2.4 级联配置总结

```
//在关系注解上添加如下属性
增:CascadeType.PERSIST
删:CascadeType.REMOVE
改:CascadeType.MERGE
所有:CascadeType.ALL
```

# 3. 多表之间的查询

## 3.1 对象导航图查询

通过实体类中的关联属性查询关联数据

**一方查询多方**

```java
/**
 * 先查询客户
 * 再通过对象导航图方式查询对应的联系人
 */
@Test
@Transactional // 解决在java代码中的no session问题
public void  testQuery1() {
    //查询id为1的客户
    Customer customer = customerDao.getOne(6L);
    //对象导航查询，此客户下的所有联系人
    Set<LinkMan> linkMans = customer.getLinkMans();

    for (LinkMan linkMan : linkMans) {
        System.out.println(linkMan);
    }
}
```

**多方查询一方**

```java
@Test
public void  testQuery3() {
    LinkMan linkMan = linkManDao.findOne(6L);
    //对象导航查询所属的客户
    Customer customer = linkMan.getCustomer();
    System.out.println(customer);
}
```

> 一方查询多方,默认是懒加载
>
> 多方查询一方,默认是立即加载

**配置对象导航图加载方式**

```java
//在关系注解上添加如下属性
立即加载:fetch=FetchType.EAGER
懒加载:fetch=FetchType.LAZY
```

## 3.2 Specification查询

```java
/**
 * Specification的多表查询
 */
@Test
public void testFind() {
 Specification<LinkMan> spec = new Specification<LinkMan>() {
  public Predicate toPredicate(Root<LinkMan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        /**
         * Join代表连接查询，通过root对象获取
         * JoinType.INNER
         * JoinType.LEFT
         * JoinType.RIGHT
         */
        Join<LinkMan, Customer> join = root.join("customer", JoinType.INNER);
        return cb.like(join.get("custName").as(String.class),"百度");
   }
 };
 List<LinkMan> list = linkManDao.findAll(spec);
 for (LinkMan linkMan : list) {
     System.out.println(linkMan);
 }
}
```
