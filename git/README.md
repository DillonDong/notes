# 1. 版本控制

作用:

1. 版本控制可以记录每次数据的变更
2. 还能够帮助还原任何一次的历史变更
3. 实现团队的协同工作

# 2. svn VS git 

1. 集中式版本控制--svn

![](img/01.jpeg)

1. 分布式版本控制--git

![](img/02.jpeg)

# 3. Git

分布式的版本管理git由Linus(Linux开发者)开发

![](img/linus.png)

## 3.1 诞生过程

* 2005 年 4 月 3 日， 开始开发 Git。 
* 2005 年 4 月 6 日， 项目发布。 
* 2005 年 4 月 7 日， Git就可以作为自身的版本 控制 工具 了。 
* 2005 年 4 月 18 日，发生 第一个多分支合并。 
* 2005 年 4 月 29 日， Git的性能就已经达到 了 Linus 的 预期。

## 3.2 git特点

* 强容灾性
* 速度快
* 设计简单
* 很好的分支管理
* 大型项目的版本控制大多基于git管理

### 3.3 git工作流程

1. 从远程仓库中clone资源到本地仓库。
2. 从本地仓库checkout代码进行修改
3. 将修改后代码add到暂存区
4. commit修改到本地仓库
5. push本地仓库修改到远程仓库

![](img/3.png)

# 4. 安装

## 4.1 git

[下载地址](https://git-scm.com/download),windows版双击默认安装即可

## 4.2 TortoiseGit

git图形化客户端

[下载地址](https://tortoisegit.org/)

# 5. Git操作

## 5.1 创建版本库

```shell
git init
```

## 5.2 添加文件

```shell
# 1.添加文件到暂存区 .(或者文件名)
git add .
# 2.提交文件到master分支
git commit -m '提交日志'
# 3. 查看本地仓库的状态
git status
```

> 工作区:电脑里能看到的目录
>
> 版本库:工作区有一个隐藏目录`.git`
>
> 暂存区:版本库中有一个叫做stage（或者叫index）

![](img/4.jpg)



## 5.3 修改文件

### 5.3.1 提交修改

```shell
# 1.添加文件到暂存区
git add .
# 2.提交文件
git commit -m '日志'
```

### 5.3.2 查看修改日志

![](img/log1.png)

![](img/log2.png)

### 5.3.3 比较差异

![](img/diff1.png)

![](img/diff2.png)

### 5.2.4 还原修改

![](img/revert1.png)

![](img/revert2.png)

![](img/revert3.png)

## 5.4 删除文件

![](img/rm1.png)

删除之后,如果提交修改,那么版本库的文件就会删除

## 5.5 文件急救

查看提交日志

![](img/rm2.png)

恢复master分支到上一个版本

![](img/rm3.png)

![](img/rm4.png)

至此,仓库中master分支的文件已经恢复,在工作去还原master分支

![](img/rm5.png)

## 5.6 忽略文件

![](img/ignore1.png)

![](img/ignore2.png)

.gitignore文件参考内容

```properties
.idea
*.iml
out
gen
target
*.class
*.log
```

# 6. 远程仓库
* [github](https://github.com/)
* [码云](https://gitee.com/)

## 6.1 创建远程仓库

![](img/github1.png)

![](img/github2.png)

## 6.2 关联方式

### 6.2.1 SSH配置

1. 右键打开git bash

   ![](img/ssh1.png)

2. 生成密钥对

   ```shell
   ssh-keygen -t rsa
   ```

   ![](img/ssh2.png)

   ![](img/ssh2.5.png)

3. 在github配置公钥信息

![](img/ssh3.png)



![](img/ssh4.png)

### 6.2.2 HTTPS配置

不需要额外配置,在同步时候提供github的用户名和密码即可

## 6.3 同步远程仓库

### 6.3.1 SSH方式

将本地仓库和进行同步,以便将本地仓库的数据备份到远程仓库

#### 6.3.1.1  命令行

```shell
# orgin是远程仓库的别名
git remote add origin 仓库的ssh地址
```

如果以前同步过会出现如下错误

![](img/remote1.png)

删除以前同步记录即可,然后重新同步即可

```shell
git remote rm origin
```

![](img/remote2.png)

推送本地仓库到远程仓库

```shell
# 将本地仓库master分支到远程仓库
git push -u origin master
```

#### 6.3.1.2  图形界面

* 配置ssh.exe可执行文件

  ![](img/remote3.1.png)

* 填写远程仓库同步信息

  ![](img/remote3.2.png)

* 开始同步

  ![](img/remote3.3.png)

  ![](img/remote3.4.png)

### 6.3.2 HTTPS

#### 6.3.2.1 命令行

```shell
# 添加远程仓库
git remote add origin 仓库的https地址
# 提交代码
git push -u origin master
```

#### 6.3.2.2 图形界面

* 添加基于https路径的远端仓库信息

![](img/https1.png)

* 点击推送或者拉取按钮

![](img/https2.png)

* 输入用户名和密码

![](img/https3.png)

![](img/https4.png)

![](img/https5.png)



## 6.4 克隆远程仓库

将远程仓库克隆到本地,克隆会在本地创建一个新的仓库

### 6.4.1 命令行方式

```shell
$ git clone git@github.com:sublun/mytest.git
```

### 6.4.2 图形界面方式

* 右键选择clone

![](img/clone1.png)

* 输入克隆地址

![](img/clone2.png)

![](img/clone3.png)



## 6.5 拉取远程仓库代码

* fech:从远程仓库获取最新版本到工作区，不会自动merge（合并代码）
* pull:相当于是从远程获取最新版本并merge到本地工作区

> fetch更加的安全,假如pull和fetch的结果一样,pull更加方便

![](img/fetch和pull.png)

​										**git pull=git fetch+git merge**

### 6.5.1 使用fetch和merge

```shell
# 查看远程仓库
git remote -v
# 获取远程仓库代码到本地仓库,并且创建一个新的分支temp
git fetch origin master:temp
# 比较temp与本地master分支的不同
git diff temp
# 合并temp分支到master
git merge temp
# 删除temp分支
git branch -d temp
```

## 6.6 解决冲突

### 6.6.1 冲突过程

1. A和B两个人编辑同一个文件
2. A提交修改到本地仓库,推送远程仓库
3. B在未从远程仓库更新情况下,然后修改相同文件(已过期),并且提交本地修改,推送到远程仓库时冲突发生

### 6.6.2 解决方案

1. 尽量避免不同人修改同一个文件
2. 如果修改相同文件,应该先更新再修改
3. 如果重复发生,相关人员在发生冲突方协商解决
4. 冲突解决完成后,标记已解决,提交代码,双方都更新最新代码



* 冲突文件

![](img/bug2.png)

* 标记已解决

![](img/bug1.png)

## 6.7 私有git服务

###  6.7.1 搭建私有服务

1. 安装git服务环境准备

   ```shell
   yum -y install curl curl-devel zlib-devel openssl-devel perl cpio expat-devel gettext-devel gcc cc
   ```

2. 下载git-2.5.0.tar.gz,并安装

   ```shell
   1）tar -zvxf XXXX.tar.gz 
   2）cd git-2.5.0
   3）autoconf
   4）./configure
   5）make
   6）make install
   ```

3. 添加用户

   ```shell
   adduser -r -c 'git version control' -d /home/git -m git
   ```

4. 设置密码

   ```shell
   passwd git
   ```

5. 切换到git用户

   ```shell
   su git
   ```

6. 创建git仓库

   ```shell
   git --bare init /home/git/first
   ```

### 6.7.2 使用私有服务

操作方式基本和连接github一样,我们的git服务器并没有配置密钥登录，所以每次连接时需要输入密码

```shell
$ git remote add origin git@192.168.25.156:first
```

# 7. 分支管理

**HEAD:**指向当前正在使用分支的指针

**master:**创建仓库时,git默认就会创建一个master分支

* master指向当前最新的提交,HEAD指向master,master就是一条线

  ```shell
  # 查看当前分支
  git branch
  ```

  ​

![](img/branch1.png)

* 创建新分支dev

  ```shell
  # dev:新分支名称 -b:创建并切换到dev
  git checkout -b dev
  ```

  ![](img/branch2.png)

* 在dev分支上进行开发,master分支不变

  ```shell
  git add readme.txt 
  git commit -m "branch test"
  ```

  ![](img/branch3.png)

* 此时,变更发生在dev分支上,如果切回master,看不到变更信息

  ```shell
  # 切回到master分支,不会看到dev分支上的修改内容
  git checkout master
  ```

* master分支合并dev分支

  ```shell
  # 切回master分支,再进行合并
  git merge dev
  ```

  ![](img/branch4.png)

* 删除dev分支

  ```shell
  # 切回master分支,删除dev分支
  git branch -d dev
  ```

  ![](img/branch5.png)

# 8. IDEA中使用Git

## 8.1 配置git

![](img/idea1.png)

![](img/idea1.1.png)

## 8.2 添加工程到git

### 8.3.1 创建工程

![](img/idea3.png)

### 8.3.2 创建本地仓库

![](img/idea2.png)

​								选择存放本地仓库的位置

![](img/idea4.png)

### 8.3.3 提交工程

​											点击提交按钮

![](img/idea5.png)

​												输入日志信息

![](img/idea6.png)

### 8.3.4 推送到远程仓库

![](img/idea7.png)

![](img/idea8.png)

![](img/idea9.png)

![](img/idea10.png)

## 8.3 克隆远程仓库

![](img/idea11.png)

![](img/idea12.png)

## 8.4 拉取远程仓库代码

![](img/idea13.png)