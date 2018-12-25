[TOC]

#1. SpringSecurity权限框架

### 1.1 概念

SpringSecurity控制系统的访问权限

**Who|What|How**

控制Who对What可以进行How的操作

**RBAC**

Role Base Access Controller

权限授予给角色,角色授予给用户

### 1.2 入门Demo

* POM依赖

  ```xml
  <properties>
      <spring.version>4.2.4.RELEASE</spring.version>
  </properties>
  <dependencies>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-core</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-webmvc</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-context-support</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-test</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-jdbc</artifactId>
          <version>${spring.version}</version>
      </dependency>
      <dependency>
          <groupId>org.springframework.security</groupId>
          <artifactId>spring-security-web</artifactId>
          <version>4.2.7.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework.security</groupId>
          <artifactId>spring-security-config</artifactId>
          <version>4.2.7.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>javax.servlet</groupId>
          <artifactId>servlet-api</artifactId>
          <version>2.5</version>
          <scope>provided</scope>
      </dependency>
  </dependencies>
  ```

* web.xml

  ```xml
  <!--初始化Spring容器-->
  <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:spring-security.xml</param-value>
  </context-param>
  <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
  
  <!--加载Spring-security权限过滤器-->
  <filter>
      <filter-name>springSecurityFilterChain</filter-name>
      <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
  </filter>
  <filter-mapping>
      <filter-name>springSecurityFilterChain</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
  ```

* spring-security.xml

  ```xml
  <!--设置不拦截的页面-->
  <http pattern="/login.html" security="none"/>
  <http pattern="/login_failur.html" security="none"/>
  
  <!--配置页面的访问权限,use-expressions使用SPEL表达式,简化配置,否则需要hasRole(...)等-->
  <!--1.配置XXX资源访问的时候应该具备的角色才能访问-->
  <http use-expressions="false">
      <!--访问/**根目录下的任意资源(包括子目录),需要有ROLE_USER权限-->
      <intercept-url pattern="/**" access="ROLE_USER"/>
      <form-login 
                  login-page="/login.html" 
                  default-target-url="/index.html" 
                  authentication-failure-url="/login_failur.html"/>
     		
      	<!--CSRF（Cross-site request forgery）跨站请求伪造-->
          <csrf disabled="true"/>
      </http>
  
  <!-- 认证管理器 -->
  <!--2.配置了系统的用户拥有的角色-->
  <authentication-manager>
      <authentication-provider>
          <user-service>
              <!--配置User,指定用户名和密码及角色-->
              <user name="admin" password="123456" authorities="ROLE_USER"/>
              <user name="zs" password="123456" authorities="ROLE_PRODUCT"/>
          </user-service>
      </authentication-provider>
  </authentication-manager>
  ```

* 自定义登录页

  ```html
  <form action='/login' method='POST'>
      <table>
          <tr>
              <td>用户名:</td>
              <td><input type='text' name='username' value=''></td>
          </tr>
          <tr>
              <td>密码:</td>
              <td><input type='password' name='password'/></td>
          </tr>
          <tr>
              <td colspan='2'><input name="submit" type="submit" value=" 登陆 "/></td>
          </tr>
      </table>
  </form>
  ```

# 2. 运营商系统集成SpringSecurity

# 3. 商家入驻

# 4. 商家审核

# 5. 商家系统集成SpringSecurity

