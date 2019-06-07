# 1. 基本操作

* 登录系统后所处的目录:当前用户的家目录

​	root家:  /root

​	其他用户: /home/用户名

​	

* 切换目录:cd



​	切换到上一级目录: cd ..





* 显示当前所处的位置:pwd





* 显示文件列表:	ll	ls



​	ls:列表显示文件名



​	ll:列表显示文件的详情信息

​	

​	ll -a:显示的当前目录所有的文件信息

​	

* 创建目录:mkdir 

​	mkdir -p	目录名/目录名



* 创建文件:touch

​	

* 删除文件:rm 

​	

​	删除非空的目录:rm -rf 目录				慎重使用



* 查看文件的内容:cat more less tail

​	tail可以动态查看文件的内容,常用与Tomcat日志查看等

​	

* 复制文件:cp

​	cp [-rf] 原文件 目标位置

​	

* 移动文件:mv

​	mv 原文件 目标位置

​	

​	该命令还可以进行重命名		mv 原名字  新名字

​	

* 打包/解压:tar

​	打包并压缩:tar -zcvf	打包后的文件名	需要打包的文件

​	解压:tar -xvf	压缩文件名	-C 解压后的位置

​	



* 编辑文件:vi或者vim

​		默认进入的命令模式,通过输入i进入编辑模式编辑文件

​		

​		编辑模式:i

​			

​		命令模式:esc

​			复制某一行:yy

​			复制多行:n->yy

​			粘贴:p

​			删除某一行:dd

​			回退到上一步操作:u

​			

​		ex模式:

​			保存并退出:wq!

​			退出:q!

​		

​		暂时退出vi编辑用:sh

​		继续回到vi编辑:crtl+d

​		

​		

* 在指定文件中查找字符串:grep	

​	grep 查找的字符串	文件名		--color

​	



* 查找文件:find

​	find 目录名 -name	文件名

​	

​	

* 重定向输出:将一个命令的结果输出到一个文件中

​	>:覆盖了文件原来的内容

​	>>:追加

​	

* 管道: |

​	前一个命令的输出是后一个命令的输入

​	通常用在查找进程上,比如

​		查看当前系统中是否存在java/tomcat/mysql

​				ps -ef | grep java

​				ps -ef | grep tomcat

​				ps -ef | grep mysql



* 网络命令:ifconfig

​	查看网卡信息:ifconfig

​	禁用网卡: ifconfig 网卡名 down

​	启用网卡: ifconfig 网卡名 up

​	

​	网卡的位置:/etc/sysconfig/network-scripts

​	修改了网卡的配置后重新加载网卡: service network restart



* 查看软件启动情况

​	用进程名字查看: ps -ef|grep 名称

​	用端口查看:netstat -ano|grep 端口

​	



* 用户管理

​	创建用户:useradd 用户名

​	设置密码:passwd	 用户名

​	切换用户:su - 用户名

​	删除用户:userdel -r 用户名

​	

​	

* 组管理

​	创建组:groupadd 组名

​	删除组:groupdel 组名

​	

​	创建用户时指定组: useradd 用户名 -g 组名



* 查看当前用户信息:id



* 切换用户: su - 用户名



* 权限设置:类型+当前用户的权限(3位)+同组其他用户的权限(3位)+其他用户的权限(3位)=10位

​		类型

​			d:目录/文件夹

​			-:文件

​		权限表示:

​			r:read 读		4

​			w:write 写		2

​			x:执行权限		1

​			

​	drwxr-xr-x. 3 root root  4096 5月  25 21:18 a	

​			

​	更改权限:

​		1.将文件操作人的权限设置:chmod  	777 			被改的文件/目录

​		2.设置当前文件的拥有者:chown  -R 	用户名:组名		被改文件/目录

​		

​	

* putty

​	复制:选中即复制

​	粘贴:右键即粘贴	

​     强制结束: crtl+c

# 2. 安装JDK

安装JDK

​	1.检查当前系统是否安装JDK

​		java -version

​		rpm -qa|grep -i java

​		

​	2.卸载已经安装的jdk

​		rpm -ev XXX

​		

​	3.将jdk上传到Linux中

​	

​	4.解压安装

​	

​	5.进入到bin目录下测试: ./java -version

​		注意:如果执行失败需要安装依赖

​			 yum install glibc.i686

​			 

​	6.配置环境变量:vi /etc/profile

​		末尾行加入:

​			#set java environment

​			JAVA_HOME=/usr/local/src/java/jdk1.7.0_71

​			CLASSPATH=.:$JAVA_HOME/lib/tools.jar

​			PATH=\$JAVA_HOME/bin:\$PATH

​			export JAVA_HOME CLASSPATH PATH

​		

​	7.重新加载环境变量:source /etc/profile

​	

# 3. 安装Mysql

​	1.检查是否安装了mysql

​		rpm -qa|grep -i mysql

​	

​	2.卸载已经安装的mysql

​		rpm -ev XXX

​		

​	3.将mysql的安装包上传到linux

​	

​	5.解压安装包

​		tar -xvf XXX 

​	

​	6.安装服务端

​		-安装依赖	yum -y install libaio.so.1 libgcc_s.so.1 libstdc++.so.6

​		-升级依赖	yum  update libstdc++-4.4.7-4.el6.x86_64

​		

​		安装mysql服务端 rpm -ivh XXX-server

​		

​	7. 安装客户端

​		--安装依赖	yum -y install libncurses.so.5 libtinfo.so.5

​		

​		安装客户端  rpm -ivh XXX-client

​	

​	8.查看mysql启动状态

​		 service mysql status

​	

​	9.启动mysql

​		service mysql start

​		

​	10.查看安装时随机生成的mysql密码

​		 cat /root/.mysql_secret

​		

​	11.登录mysql

​		 mysql -u root -p

​		 

​	12.更改root用户的登录密码

​		 SET PASSWORD = PASSWORD('123456');

​		

​	13.将mysql加入到系统服务,并开机自动

​		chkconfig --add mysql

​		chkconfig mysql on

​		

​	14.设置远程连接:

​		14.1 登录mysql中,设置远程访问的权限:

​			grant all privileges on *.* to 'root' @'%' identified by '123456'; 授权

​			flush privileges;		刷新权限

​			

​		14.2 将linux的3306端口开放

​			/sbin/iptables -I INPUT -p tcp --dport 3306 -j ACCEPT		开放3306端口

​			/etc/rc.d/init.d/iptables save								将配置写入配置文件

​			/etc/init.d/iptables status									查看状态

# 4. Mysql卸载	

​	1.查看mysql的安装状态

​		rpm -qa|grep -i mysql

​	

​	2.卸载软件:

​		rpm -ev XXX

​		或者

​		rpm -e --nodeps XXX

​		

​	3.查看Mysql的相关文件

​		find / -name mysql

​	

​	4.删除查到的msyql相关文件

​		rm -rf XXX

​		

​	5.删除mysql用户和组

​		查看系统用户文件: cat /ect/passwd

​			删除用户:userdel mysql

​		查看组文件:cat /etc/group

​			删除组:groupdel mysql

​			

​	6.检查是否卸载干净

# 5. 安装Tomcat

​	1.上传Tomcat到服务器	

​	2.解压Tomcat安装包

​		tar -xvf  XXX 

​	

​	3.启动Tomcat

​		./bin/startup.bat

​		

​	4.查看日志

​		tail -f ./logs/catalina.out

​		

​	5.访问tomcat

​		

​	6.查看Tomcat进程并关闭

​		ps -ef|grep tomcat

​		

​		kill -9 XXX 

​	

​	7.发布项目到 webapps下



开放端口:

​	/sbin/iptables -I INPUT -p tcp --dport 端口号 -j ACCEPT

​	/etc/rc.d/init.d/iptables save

​	

​	/etc/init.d/iptables status