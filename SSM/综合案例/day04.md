# 1. 用户管理

## 1.1 查询用户

###1）页面

页面名称user-list.jsp

###2）UserController

```java
@RequestMapping("/findAll.do")
public ModelAndView findAll() throws Exception {
    ModelAndView mv = new ModelAndView();
    List<UserInfo> userList = userService.findAll();
    mv.addObject("userList", userList);
    mv.setViewName("user-list");
    return mv;
}
```

###3）Dao

```java
@Select("select * from user")
public List<UserInfo> findAll();
```

## 1.2 用户添加

###1）页面

user-add.jsp

###2）Controller

```java
//用户添加
@RequestMapping("/save.do")
public String save(UserInfo userInfo) throws Exception {
    userService.save(userInfo);
    return "redirect:findAll.do";
}
```

###3）Service

```java
@Override
public void save(UserInfo userInfo) throws Exception {
    //对密码进行加密处理
    userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
    userDao.save(userInfo);
}
```

前期我们的用户密码没有加密，现在添加用户时，我们需要对用户密码进行加密

```xml
<bean id="passwordEncoder" class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"/>
```

###4）Dao

```java
@Insert("insert into users(email,username,password,phoneNum,status) values(#{email},#{username},#{password},#{phoneNum},#{status})")
public void save(UserInfo userInfo) throws Exception;
```

##1.3 用户详情

###1）页面

user-show.jsp

注意：需要添加js

$("#collapse-table").treetable({ expandable : true });

###2）UserController

```java
@RequestMapping("/findById.do")
public ModelAndView findById(String id) throws Exception{
    ModelAndView mv = new ModelAndView();
    UserInfo userInfo = userService.findById(id);
    mv.addObject("user",userInfo);
    mv.setViewName("user-show1");
    return mv;
}
```

###3）Dao

* UserDao

```java
@Select("select * from users where id=#{id}")
@Results({
        @Result(id = true, property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "phoneNum", column = "phoneNum"),
        @Result(property = "status", column = "status"),
        @Result(property = "roles",column = "id",javaType = java.util.List.class,
                many = @Many(select = "com.itheima.ssm.dao.IRoleDao.findRoleByUserId"))
})
UserInfo findById(String id) throws Exception;
```

* RoleDao

```java
@Select("select * from role where id in (select roleId from users_role where userId=#{userId})")
@Results({
        @Result(id = true, property = "id", column = "id"),
        @Result(property = "roleName", column = "roleName"),
        @Result(property = "roleDesc", column = "roleDesc"),
        @Result(property = "permissions",column = "id",javaType = java.util.List.class,
                many = @Many(select = "com.itheima.ssm.dao.IPermissionDao.findPermissionByRoleId"))
})
public List<Role> findRoleByUserId(String userId) throws Exception;
```

* PermissionDao

```java
@Select("select * from permission where id in (select permissionId from role_permission where roleId=#{id} )")
public List<Permission> findPermissionByRoleId(String id) throws Exception;
```

# 2. 角色管理

## 2.1 角色查询

### 1）页面

role-list.jsp

### 2）RoleControlller

```java
@RequestMapping("/findAll.do")
public ModelAndView findAll() throws Exception {
    ModelAndView mv = new ModelAndView();
    List<Role> roleList = roleService.findAll();
    mv.addObject("roleList", roleList);
    mv.setViewName("role-list");
    return mv;
}
```

### 3）Dao

```java
@Select("select * from role")
public List<Role> findAll();
```

## 2.2 角色添加

### 1）页面

role-add.jsp

### 2）RoleControlller

```java
@RequestMapping("/save.do")
public String save(Role role) throws Exception {
    roleService.save(role);
    return "redirect:findAll.do";
}
```

### 3）Dao

```java
@Insert("insert into role(roleName,roleDesc) value(#{roleName},#{roleDesc})")
public void save(Role role);
```

# 3. 资源权限管理

## 3.1 资源权限查询

### 1）页面

permission-list.jsp

### 2）PermissionController

```java
@RequestMapping("/findAll.do")
public ModelAndView findAll() throws Exception {
    ModelAndView mv=new ModelAndView();
    List<Permission> permissionList = permissionService.findAll();
    mv.addObject("permissionList",permissionList);
    mv.setViewName("permission-list");
    return mv;
}
```

### 3）Dao

```java
@Select("select * from permission")
public List<Permission> findAll();
```

## 3.2 资源权限添加

### 1）页面

permission-add.jsp

### 2）PermissionController

```java
@RequestMapping("/save.do")
public String save(Permission permission) throws Exception {
    permissionService.save(permission);
    return "redirect:findAll.do";
}
```

### 3）Dao

```java
@Insert("insert into permission(permissionName,url) value(#{permissionName},#{url})")
public void save(Permission p);
```

