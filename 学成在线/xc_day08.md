# 1. 分布式文件系统

## 1.1 技术应用场景

传智播客拥有大量优质的视频教程，并且免费提供给用户去下载，文件太多如何高效存储？用户访问量大如何保证下载速度？今天讲解的分布式文件系统将解决这些问题。

分布式文件系统解决了海量文件存储及传输访问的瓶颈问题，对海量视频的管理、对海量图片的管理等。

![](img/course31.png)

## 1.2 文件系统

[计算机](https://zh.wikipedia.org/wiki/%E8%AE%A1%E7%AE%97%E6%9C%BA)的**文件系统**是一种存储和组织计算机数据的方法，它使得对其访问和查找变得容易，文件系统使用**文件**和**树形目录**的抽象逻辑概念代替了硬盘和光盘等物理设备使用数据块的概念，用户使用文件系统来保存数据不必关心数据实际保存在硬盘（或者光盘）的地址为多少的数据块上，只需要记住这个文件的所属目录和文件名。在写入新数据之前，用户不必关心硬盘上的那个块地址没有被使用，硬盘上的存储空间管理（分配和释放）功能由文件系统自动完成，用户只需要记住数据被写入到了哪个文件中。

总结：文件系统是负责管理和存储文件的系统软件，它是操作系统和硬件驱动之间的桥梁，操作系统通过文件系统
提供的接口去存取文件，用户通过操作系统访问磁盘上的文件。如下图：

![](img/course32.png)

常见的文件系统：FAT16/FAT32、NTFS、HFS、UFS、APFS、XFS、Ext4等 。

思考：如果没有文件系统我们该怎么管理自己的文件？

![](img/course33.png)

## 1.3 分布式文件系统

相对于本机端的文件系统而言，**分布式文件系统**（英语：Distributed file system, DFS），或是**网络文件系统**（英语：Network File System），是一种允许[文件](https://zh.wikipedia.org/wiki/%E6%AA%94%E6%A1%88)透过网络在多台主机上分享的[文件系统](https://zh.wikipedia.org/wiki/%E6%AA%94%E6%A1%88%E7%B3%BB%E7%B5%B1)，可让多机器上的多用户分享文件和存储空间。

**为什么会有分布文件系统呢？**

分布式文件系统是面对互联网的需求而产生，互联网时代对海量数据如何存储？靠简单的增加硬盘的个数已经满足不了我们的要求，因为硬盘传输速度有限但是数据在急剧增长，另外我们还要要做好数据备份、数据安全等。

采用分布式文件系统可以将多个地点的文件系统通过**网络**连接起来，组成一个文件系统网络，结点之间通过网络进行通信，一台文件系统的存储和传输能力有限，我们让文件在多台计算机上存储，通过多台计算共同传输。如下图：

![](img/dfs1.png)

**优点:**

1. 一台计算机的文件系统处理能力扩充到多台计算机同时处理。
2. 一台计算机挂了还有另外副本计算机提供数据。
3. 每台计算机可以放在不同的地域，这样用户就可以就近访问，提高访问速度。

### 1.3.1 主流的分布式文件系统

#### 1.3.1.1 NFS

**网络文件系统**（英语：**Network File System**，缩写作 **NFS**）是一种[分布式文件系统](https://zh.wikipedia.org/wiki/%E5%88%86%E5%B8%83%E5%BC%8F%E6%96%87%E4%BB%B6%E7%B3%BB%E7%BB%9F)协议，最初由[Sun Microsystems](https://zh.wikipedia.org/wiki/Sun_Microsystems)公司开发，并于1984年发布[[1\]](https://zh.wikipedia.org/wiki/%E7%BD%91%E7%BB%9C%E6%96%87%E4%BB%B6%E7%B3%BB%E7%BB%9F#cite_note-sun85-1)。其功能旨在允许客户端主机可以像访问本地存储一样通过网络访问服务器端文件。

![](img/dfs2.png)

1. 在客户端上映射NFS服务器的驱动器。
2. 客户端通过网络访问NFS服务器的硬盘完全透明。

#### 1.3.1.2 GFS

**Google文件系统**（英语：Google File System，缩写为**GFS**或GoogleFS），一种专有[分布式文件系统](https://zh.wikipedia.org/wiki/%E5%88%86%E5%B8%83%E5%BC%8F%E6%96%87%E4%BB%B6%E7%B3%BB%E7%BB%9F)，由[Google](https://zh.wikipedia.org/wiki/Google)公司开发，运行于[Linux](https://zh.wikipedia.org/wiki/Linux)平台上[[1\]](https://zh.wikipedia.org/wiki/Google%E6%AA%94%E6%A1%88%E7%B3%BB%E7%B5%B1#cite_note-1)。尽管Google在2003年公布了该系统的一些技术细节，但Google并没有将该系统的软件部分作为开源软件发布[[2\]](https://zh.wikipedia.org/wiki/Google%E6%AA%94%E6%A1%88%E7%B3%BB%E7%B5%B1#cite_note-2)。

![](img/dfs3.png)

1. GFS采用主从结构，一个GFS集群由一个master和大量的chunkserver组成。
2. master存储了数据文件的元数据，一个文件被分成了若干块存储在多个chunkserver中。
3. 用户从master中获取数据元信息，从chunkserver存储数据。

#### 1.3.1.3 HDFS

Hadoop分布式文件系统(HDFS)被设计成适合运行在通用硬件(commodity hardware)上的分布式文件系统。它和现有的分布式文件系统有很多共同点。但同时，它和其他的分布式文件系统的区别也是很明显的。HDFS是一个高度容错性的系统，适合部署在廉价的机器上。HDFS能提供高吞吐量的数据访问，非常适合大规模数据集上的应用。HDFS放宽了一部分POSIX约束，来实现流式读取文件系统数据的目的。HDFS在最开始是作为Apache Nutch搜索引擎项目的基础架构而开发的。HDFS是Apache Hadoop Core项目的一部分。

![](img/dfs4.png)

1. HDFS采用主从结构，一个HDFS集群由一个名称结点和若干数据结点组成。

   名称结点存储数据的元信息，一个完整的数据文件分成若干块存储在数据结点。

2. 客户端从名称结点获取数据的元信息及数据分块的信息，得到信息客户端即可从数据块来存取数据。

### 1.3.2 分布式文件系统供应商

1. 阿里的OSS

阿里云对象存储服务（Object Storage Service，简称OSS），是阿里云对外提供的海量、安全、低成本、高可靠的云存储服务。您可以通过本文档提供的简单的REST接口，在任何时间、任何地点、任何互联网设备上进行上传和下载数据。基于OSS，您可以搭建出各种多媒体分享网站、网盘、个人和企业数据备份等基于大规模数据的服务。

2. 七牛云存储

3. 百度云存储

   ...

# 2. FastDFS介绍

## 2.1 概述

FastDFS是用c语言编写的一款开源的分布式文件系统，它是由淘宝资深架构师余庆编写并开源。FastDFS专为互联网量身定制，充分考虑了冗余备份、负载均衡、线性扩容等机制，并注重高可用、高性能等指标，使用FastDFS很容易搭建一套高性能的文件服务器集群提供文件上传、下载等服务。

为什么要使用fastDFS呢？

上边介绍的NFS、GFS都是通用的分布式文件系统，通用的分布式文件系统的优点的是开发体验好，但是系统复杂性高、性能一般，而专用的分布式文件系统虽然开发体验性差，但是系统复杂性低并且性能高。fastDFS非常适合存储图片等那些小文件，fastDFS不对文件进行分块，所以它就没有分块合并的开销，fastDFS网络通信采用socket，通信速度很快。

## 2.2 FastDFS工作原理

### 2.2.1 FastDFS架构图

FastDFS架构包括 Tracker server和Storageserver。客户端请求Tracker server进行文件上传、下载，通过Trackerserver调度最终由Storage server完成文件上传和下载。

![](img/dfs5.png)

1. Tracker

   Tracker Server作用是负载均衡和调度，通过Tracker server在文件上传时可以根据一些策略找到Storage server提供文件上传服务。可以将tracker称为追踪服务器或调度服务器。

2. Storage

   Storage Server作用是文件存储，客户端上传的文件最终存储在Storage服务器上，Storage server没有实现自己的文件系统而是使用操作系统的文件系统来管理文件。可以将storage称为存储服务器。

   Storage集群采用了分组存储方式。storage集群由一个或多个组构成，集群存储总容量为集群中所有组的存储容量之和。一个组由一台或多台存储服务器组成，组内的Storage server之间是平等关系，不同组的Storage server之间不会相互通信，同组内的Storage server之间会相互连接进行文件同步，从而保证同组内每个storage上的文件完全一致的。一个组的存储容量为该组内的存储服务器容量最小的那个，由此可见组内存储服务器的软硬件配置最好是一致的。

   采用分组存储方式的好处是灵活、可控性较强。比如上传文件时，可以由客户端直接指定上传到的组也可以由tracker进行调度选择。一个分组的存储服务器访问压力较大时，可以在该组增加存储服务器来扩充服务能力（纵向扩容）。当系统容量不足时，可以增加组来扩充存储容量（横向扩容）。

3. Storage状态收集

   Storage server会连接集群中所有的Tracker server，定时向他们报告自己的状态，包括磁盘剩余空间、文件同步状况、文件上传下载次数等统计信息。

## 2.2 文件上传流程

![](img/dfs6.png)

客户端上传文件后存储服务器将 文件ID 返回给客户端，此文件ID用于以后访问该文件的索引信息。文件索引信息包括：组名，虚拟磁盘路径，数据两级目录，文件名。

![](img/dfs7.png)

* 组名：文件上传后所在的 storage组名称，在文件上传成功后有 storage服务器返回，需要客户端自行保存。
* 虚拟磁盘路径： storage配置的虚拟路径，与磁盘选项 store_path对应。如果配置了store_path0则是M00，如果配置了store_path1则是M01，以此类推。
* 数据两级目录： storage服务器在每个虚拟磁盘路径下创建的两级目录，用于存储数据文件。
* 文件名：与文件上传时不同。是由存储服务器根据特定信息生成，文件名包含：源存储服务器 IP地址、文件创建时间戳、文件大小、随机数和文件拓展名等信息。

## 2.3 文件下载流程

![](img/dfs8.png)

tracker根据请求的文件路径即文件ID 来快速定义文件。

比如请求下边的文件：

![](img/dfs7.png)

1. 通过组名tracker能够很快的定位到客户端需要访问的存储服务器组是group1，并选择合适的存储服务器提供客户端访问。
2. 存储服务器根据“文件存储虚拟磁盘路径”和“数据文件两级目录”可以很快定位到文件所在目录，并根据文件名找到客户端需要访问的文件。

# 3. FastDFS入门

## 3.1 fastDFS 安装与配置

### 3.1.1 导入虚拟机

对fastDFS的安装过程不要求学生掌握，可以直接导入老师提供虚拟机。

1. 使用Vmware打开虚拟机配置文件“CentOS 7 64 位.vmx”，提示如下图：
2. 选择“我已复制该虚拟机”

![](img/dfs9.png)

3. 启动虚拟机之前启动VMware的服务：

   ![](img/dfs10.png)

4. 修改网卡的IP地址为101网段。

5. 修改虚拟机网络配置

   ![](img/dfs11.png)

6. 修改虚拟机网络配置,修改 VMnet8的子网IP为101.0。

   ![](img/dfs12.png)

   DHCP配置：

![](img/dfs13.png)

6. 启动虚拟机

   用户名：root

   密码：centos

7. 导入的虚拟机已经安装了fastDSF，直接启动fastDSF即可使用。

## 3.2 fastDFS安装

如果想自己尝试安装fastDSF也可以按照文档一步一步安装。

tracker和storage使用相同的安装包，fastDFS的下载地址在：https://github.com/happyfish100/FastDFS

本教程下载安装包： FastDFS_v5.05.tar.gz

FastDFS是C语言开发，建议在linux上运行，本教程使用CentOS7作为安装环境。

安装细节请参考 “fastDFS安装教程.doc”。

## 3.3 Tracker配置

fastDFS的配置文件目录 ：/etc/fdfs

主要的配置文件：/etc/fdfs/tracker.conf （tracker配置文件 ）；storage.conf（storage配置文件）

![](img/dfs14.png)

tracker.conf配置内容如下：

端口：port=22122

存储策略：store_lookup=取值范围：0（轮询向storage存储文件）、1（指定具体的group）、2负载均衡，选择空闲的storage存储

指定具体的group：store_group= 如果store_lookup设置为1则这里必须指定一个具体的group。

tracker 基础目录：base_path=/home/fastdfs，tracker在运行时会向此目录存储storage的管理数据。

## 3.4 storage配置

storage.conf配置 内容如下：

组名：group_name=group1

端口：port=23000

向 tracker心跳间隔（秒）：heart_beat_interval=30

storage基础目录：base_path=/home/fastdfs

磁盘存储目录，可定义多个store_path：

store_path0=/home/fastdfs/fdfs_storage 此目录下存储上传的文件，在/home/fastdfs/fdfs_storage/data下

store_path1=...

...

上报tracker的地址：tracker_server=192.168.101.64:22122

如果有多个tracker则配置多个tracker，比如：

tracker_server=192.168.101.64:22122

tracker_server=192.168.101.65:22122

## 3.5 fastDFS启动停止

fastDFS启动/停止脚本目录：

![](img/dfs15.png)

fdfs_trackerd：tracker脚本，通过此脚本对 tracker进行启动和停止

```she
/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf restart
```

fdfs_storaged：storage脚本，通过此脚本对 storage进行启动和停止

```shell
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart
```

## 3.6  文件上传下载测试

### 3.6.1 搭建环境

1. 创建maven工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring‐boot‐starter‐parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>
<groupId>com.xuecheng</groupId>
<artifactId>test‐fastDSF</artifactId>
<version>1.0‐SNAPSHOT</version>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring‐boot‐starter‐web</artifactId>
    </dependency>
    <!‐‐ https://mvnrepository.com/artifact/net.oschina.zcx7878/fastdfs‐client‐java ‐‐>
    <dependency>
        <groupId>net.oschina.zcx7878</groupId>
        <artifactId>fastdfs‐client‐java</artifactId>
        <version>1.27.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring‐boot‐starter‐test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons‐io</artifactId>
        <version>1.3.2</version>
    </dependency>
</dependencies>
```

2. 配置文件

   在classpath:config下创建fastdfs-client.properties文件

```properties
fastdfs.connect_timeout_in_seconds = 5 #http连接超时时间  
fastdfs.network_timeout_in_seconds = 30 #tracker与storage网络通信超时时间 
fastdfs.charset = UTF‐8 #字符编码 
fastdfs.tracker_servers = 192.168.101.64:22122 #tracker服务器地址，多个地址中间用英文逗号分隔
```

### 3.6.2 文件上传

```java
//上传文件
@Test
public void testUpload() {
    try {
        ClientGlobal.initByProperties("config/fastdfs‐client.properties");
        System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
        System.out.println("charset=" + ClientGlobal.g_charset);
		//创建客户端        
        TrackerClient tc = new TrackerClient();
		//连接tracker Server        
        TrackerServer ts = tc.getConnection();
        if (ts == null) {
            System.out.println("getConnection return null");
            return;
        }
		//获取一个storage server        
        StorageServer ss = tc.getStoreStorage(ts);
        if (ss == null) {
            System.out.println("getStoreStorage return null");
        }
		//创建一个storage存储客户端        
        StorageClient1 sc1 = new StorageClient1(ts, ss);
        NameValuePair[] meta_list = null;  //new NameValuePair[0];
        String item = "C:\\Users\\admin\\Desktop\\1.png";
        String fileid;
        fileid = sc1.upload_file1(item, "png", meta_list);
        System.out.println("Upload local file " + item + " ok, fileid=" + fileid);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
```

### 3.6.3 文件查询

```java
//查询文件
@Test
public void testQueryFile() throws IOException, MyException {
    ClientGlobal.initByProperties("config/fastdfs‐client.properties");
    TrackerClient tracker = new TrackerClient();
    TrackerServer trackerServer = tracker.getConnection();
    StorageServer storageServer = null;
    StorageClient storageClient = new StorageClient(trackerServer,storageServer);
    FileInfo fileInfo = storageClient.query_file_info("group1",                                                     "M00/00/01/wKhlQFrKBSOAW5AWAALcAg10vf4862.png");
    System.out.println(fileInfo);
}
```

### 3.6.4 文件下载

```java
//下载文件
@Test
public void testDownloadFile() throws IOException, MyException {
    ClientGlobal.initByProperties("config/fastdfs‐client.properties");
    TrackerClient tracker = new TrackerClient();
    TrackerServer trackerServer = tracker.getConnection();
    StorageServer storageServer = null;
    StorageClient1 storageClient1 = new StorageClient1(trackerServer,
            storageServer);
    byte[] result =storageClient1.download_file1(
        "group1/M00/00/01/wKhlQFrKBSOAW5AWAALcAg10vf4862.png");
    File file = new File("d:/1.png");
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(result);
    fileOutputStream.close();
}
```

## 3.7 搭建图片虚拟主机

### 3.7.1 在storage上安装Nginx

在 storage server 上安装 nginx 的目的是对外通过 http 访问 storage server 上的文 件。使用 nginx 的模块FastDFS-nginx-module 的作用是通过 http 方式访问 storage 中 的文件，当 storage 本机没有要找的文件时向源storage 主机代理请求文件。

在storage上安装nginx（安装FastDFS-nginx-module模块）参考：FastDFS安装教程.pdf 进行安装

安装完成启动storage上的nginx：

```shell
/usr/local/nginx/sbin/nginx ‐c /usr/local/nginx/conf/nginx‐fdfs.conf
```

### 3.7.2 配置Nginx图片服务虚拟机主机

图片服务虚拟主机的作用是负载均衡，将图片请求转发到 storage server上。

1. 通过图片服务虚拟主机请求图片流程图

![](img/dfs16.png)

2. 在nginx图片代理服务上配置图片服务器虚拟主机

```nginx
#图片服务
upstream img_server_pool{
	#server 192.168.101.64:80 weight=10;
	server 192.168.101.65:80 weight=10;
} 
#学成网图片服务
server {
	listen       80;    
	server_name img.xuecheng.com;    
   
	#个人中心    
	location /group1 {      
		proxy_pass http://img_server_pool;          
	}     
	location /group2 {      
		proxy_pass http://img_server_pool;          
	}
}
```

### 3.7.3 图片服务器启动

1. 启动tracker

```shell
/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf restart
```

2. 启动storage

```shell
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart
```

3. 启动storage上的nginx

```shell
/usr/local/nginx/sbin/nginx ‐c /usr/local/nginx/conf/nginx‐fdfs.conf
```

4. 启动图片虚拟主机 nginx

# 4. 课程图片管理

## 4.1 上传图片开发

### 4.1.1 需求分析

在很多系统都有上传图片/上传文件的需求，比如：上传课程图片、上传课程资料、上传用户头像等，为了提供系统的可重用性专门设立文件系统服务承担图片/文件的管理，文件系统服务实现对文件的上传、删除、查询等功能进行管理。

各各子系统不再开发上传文件的请求，各各子系统通过文件系统服务进行文件的上传、删除等操作。文件系统服务最终会将文件存储到fastDSF文件系统中。

下图是各各子系统与文件系统服务之间的关系：

![](img/pic1.png)

下图是课程管理中上传图片处理流程：

![](img/pic2.png)

执行流程如下：

1. 管理员进入教学管理前端，点击上传图片
2. 图片上传至文件系统服务，文件系统请求fastDFS上传文件
3. 文件系统将文件入库，存储到文件系统服务数据库中。
4. 文件系统服务向前端返回文件上传结果，如果成功则包括文件的Url路径。
5. 课程管理前端请求课程管理进行保存课程图片信息到课程数据库。
6. 课程管理服务将课程图片保存在课程数据库。

### 4.1.2 创建文件系统服务工程

导入xc-service-base-filesystem.zip工程。

1. 工程目录结构

![](img/pic3.png)

2. pom.xml

```xml
<?xml version="1.0" encoding="UTF‐8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema‐instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven‐4.0.0.xsd">
    <parent>
        <artifactId>xc‐framework‐parent</artifactId>
        <groupId>com.xuecheng</groupId>
        <version>1.0‐SNAPSHOT</version>
        <relativePath>../xc‐framework‐parent/pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>xc‐service‐base‐filesystem</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc‐service‐api</artifactId>
            <version>1.0‐SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc‐framework‐model</artifactId>
            <version>1.0‐SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.xuecheng</groupId>
                        <artifactId>xc‐framework‐common</artifactId>
            <version>1.0‐SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring‐boot‐starter‐web</artifactId>
        </dependency>
        <dependency>
            <groupId>net.oschina.zcx7878</groupId>
            <artifactId>fastdfs‐client‐java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring‐boot‐starter‐test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons‐io</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring‐boot‐starter‐data‐mongodb</artifactId>
        </dependency>
    </dependencies>
</project
```

3. 配置文件

   application.yml

```yaml
server:
  port: 22100
spring:
  application:
    name: xc‐service‐base‐filesystem
#mongo配置
  data:
    mongodb:
      database: xc_fs
      uri: mongodb://root:123@127.0.0.1:27017
#SpringMVC上传文件配置
  servlet:
    multipart:
      #默认支持文件上传.
      enabled: true
      #支持文件写入磁盘.
            file‐size‐threshold: 0
      # 上传文件的临时目录
      location:
      # 最大支持文件大小
      max‐file‐size: 1MB
      # 最大支持请求大小
      max‐request‐size: 30MB
xuecheng:
  fastdfs:
    connect_timeout_in_seconds: 5
    network_timeout_in_seconds: 30
    charset: UTF‐8
    tracker_servers: 192.168.101.64:22122
```

### 4.1.3 API接口

#### 4.1.3.1 模型类

1. 系统的文件信息（图片、文档等小文件的信息）在mongodb中存储，下边是文件信息的模型类。

```java
@Data
@ToString
@Document(collection = "filesystem")
public class FileSystem {
    @Id
    private String fileId;
    //文件请求路径
    private String filePath;
    //文件大小
    private long fileSize;
    //文件名称
    private String fileName;
    //文件类型
    private String fileType;
    //图片宽度
    private int fileWidth;
    //图片高度
    private int fileHeight;
    //用户id，用于授权暂时不用
    private String userId;
    //业务key
    private String businesskey;
    //业务标签
    private String filetag;
    //文件元信息
    private Map metadata;
}
```

说明：

​	fileId：fastDFS返回的文件ID。

​	filePath：请求fastDFS浏览文件URL。

​	filetag：文件标签，由于文件系统服务是公共服务，文件系统服务会为使用文件系统服务的子系统分配文件标签，用于标识此文	   件来自哪个系统。

​	businesskey：文件系统服务为其它子系统提供的一个业务标识字段，各子系统根据自己的需求去使用，比如：课程管理会在此字段中存储课程id用于标识该图片属于哪个课程。

​	metadata：文件相关的元信息。

2. collection

   在mongodb创建数据库xc_fs（文件系统数据库），并创建集合 filesystem。

#### 4.1.3.2 Api接口

在api工程下创建com.xuecheng.api.filesystem包

```java
public interface FileSystemControllerApi {
  
    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metedata 元信息,json格式
     * @return
     */
    public UploadFileResult upload(MultipartFile multipartFile,
                    String filetag,
                    String businesskey,
                    String metadata);
} 
```

#### 4.1.3.3 Dao

将文件信息存入数据库，主要存储文件系统中的文件路径。

```java
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
```

#### 4.1.3.4 Service

```java
@Service
public class FileSystemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);
    
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;
    //加载fdfs的配置
    private void initFdfsConfig(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            //初始化文件系统出错
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
    //上传文件
    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata){
        if(file == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //上传文件到fdfs
        String fileId = fdfs_upload(file);
        //创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileId);
        //文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元数据
        if(StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //名称
        fileSystem.setFileName(file.getOriginalFilename());
        //大小
        fileSystem.setFileSize(file.getSize());
        //文件类型
        fileSystem.setFileType(file.getContentType());
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }
    //上传文件到fdfs，返回文件id
    public String fdfs_upload(MultipartFile file) {
        try {
            //加载fdfs的配置
            initFdfsConfig();
            //创建tracker client
            TrackerClient trackerClient = new TrackerClient();
            //获取trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storage client
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            //文件字节
            byte[] bytes = file.getBytes();
            //文件原始名称
            String originalFilename = file.getOriginalFilename();
            //文件扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //文件id
            String file1 = storageClient1.upload_file1(bytes, extName, null);
                        return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

#### 4.1.3.5 Controller

```java
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {
    @Autowired
    FileSystemService fileSystemService;
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "filetag", required = true) 
                                   String filetag,
                                   @RequestParam(value = "businesskey", required = false) 
                                   String businesskey,
                                   @RequestParam(value = "metedata", required = false) 
                                   String metadata) {
      return fileSystemService.upload(file,filetag,businesskey,metadata);
    }
}
```

#### 4.1.3.6 测试

使用swagger-ui或postman进行测试。

下图是使用swagger-ui进行测试的界面：

![](img/pic5.png)

### 4.1.4 上传课程图片前端

#### 4.1.4.1 需求

上传图片界面如下图：

![](img/pic6.png)

点击“加号”上传图片，图片上传成功自动显示；点击“删除”将删除图片。

#### 4.1.4.2 页面

使用Element-UI的Upload上传组件实现上边的效果。

1. template

```html
<el‐upload
  action="/filesystem/upload"
  list‐type="picture‐card"
  :before‐upload="setbusinesskey"
  :on‐success="handleSuccess"
  :file‐list="fileList"
  :limit="picmax"
  :name="multipartFile"
  :on‐exceed="rejectupload"
  :data="{"key":value,"key":""}">
  <i class="el‐icon‐plus"></i>
</el‐upload>
```

el-upload参数说明：
action：必选参数，上传的地址
list-type：文件列表的类型（text/picture/picture-card）
before-upload：上传前执行钩子方法 ，function(file)
on-success：上传成功 执行的钩子方法 ，function(response, file, fileList)
on-error：上传失败的钩子方法，function(err, file, fileList)
on-remove：文件删除的钩子方法，function(file, fileList)
file-list：文件列表，此列表为上传成功 的文件
limit：最大允许上传个数
on-exceed：文件超出个数限制时的钩子，方法为：function(files, fileList)
data：提交上传的额外参数，需要封装为json对象，最终提交给服务端为key/value串

2. 数据模型

```javascript
<script>
  import * as sysConfig from '@/../config/sysConfig';
  import * as courseApi from '../../api/course';
  import utilApi from '../../../../common/utils';
  import * as systemApi from '../../../../base/api/system';
  export default {
    data() {
      return {
        picmax:1,
        courseid:'',
        dialogImageUrl: '',
        dialogVisible: false,
        fileList:[],
        uploadval:{filetag:"course"},
        imgUrl:sysConfig.imgUrl
      }
    },
          methods: {
      //超出文件上传个数提示信息
      rejectupload(){
        this.$message.error("最多上传"+this.picmax+"个图片");
      },
      //在上传前设置上传请求的数据
      setuploaddata(){
      },
      //删除图片
      handleRemove(file, fileList) {
        console.log(file)
        alert('删除')
      },
      //上传成功的钩子方法
      handleSuccess(response, file, fileList){
        console.log(response)
        alert('上传成功')
      },
      //上传失败执行的钩子方法
      handleError(err, file, fileList){
        this.$message.error('上传失败');
        //清空文件队列
        this.fileList = []
      }
    },
    mounted(){
      //课程id
      this.courseid = this.$route.params.courseid;
    }
  }
</script>
```

## 4.2 保存图片开发

### 4.2.1 需求分析

图片上传到文件系统后，其它子系统如果想使用图片可以引用图片的地址，课程管理模块使用图片的方式是将图片地址保存到课程数据库中。

业务流程如下：

1. 上传图片到文件系统服务
2. 保存图片地址到课程管理服务

在课程管理服务创建保存课程与图片对应关系的表 course_pic。

![](img/pic7.png)

3. 在course_pic保存图片成功后方可查询课程图片信息。

   通过查询course_pic表数据则查询到某课程的图片信息。

### 4.2.2 课程管理服务端开发

#### 4.2.2.1 API

课程管理需要使用图片则在课程管理服务中要提供保存课程图片的api。

```java
@ApiOperation("添加课程图片")
public ResponseResult addCoursePic(String courseId,String pic);
```

#### 4.2.2.2 Dao

模型类

```java
@Data
@ToString
@Entity
@Table(name="course_pic")
@GenericGenerator(name = "jpa‐assigned", strategy = "assigned")
public class CoursePic implements Serializable {
    private static final long serialVersionUID = ‐916357110051689486L;
    @Id
    @GeneratedValue(generator = "jpa‐assigned")
    private String courseid;
    private String pic;
}
```

```java
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {
}
```

#### 4.2.2.4 Service

```java
 //添加课程图片
@Transactional
public ResponseResult saveCoursePic(String courseId,String pic){
    //查询课程图片
    Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
    CoursePic coursePic = null;
    if(picOptional.isPresent()){
        coursePic = picOptional.get();
    }
    //没有课程图片则新建对象
    if(coursePic == null){
        coursePic = new CoursePic();
    }
    coursePic.setCourseid(courseId);
    coursePic.setPic(pic);
    //保存课程图片
    coursePicRepository.save(coursePic);
    return new ResponseResult(CommonCode.SUCCESS);
}
```

#### 4.2.3.4 Controller

```java
@Override
@PostMapping("/coursepic/add")
public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic")
                                   String pic) {
    //保存课程图片
    return courseService.saveCoursePic(courseId,pic);
}
```

### 4.2.3 前端开发

前端需要在上传图片成功后保存课程图片信息。

#### 4.2.3.1 Api方法

```java
//添加课程图片
export const addCoursePic= (courseId,pic) => {
  return http.requestPost(apiUrl+'/course/coursepic/add?courseId='+courseId+"&pic="+pic)
}
```

#### 4.2.3.2  页面

1. 添加上传成功的钩子 :on-success="handleSuccess"

```html
<el‐upload
  action="/api/filesystem/upload"
  list‐type="picture‐card"
  :on‐success="handleSuccess">
  <i class="el‐icon‐plus"></i>
</el‐upload>
```

2. 在钩子方法 中保存课程图片信息

   如果保存图片失败则上传失败，清除文件列表。

```java
//上传成功的钩子方法
handleSuccess(response, file, fileList){
  console.log(response)
  if(response.success){
   //alert('上传成功')
    //图片上传成功将课程图片地址保存到课程数据库
    let pic = response.fileSystem.filePath
    courseApi.addCoursePic(this.courseid,pic).then((res) => {
      if(res.success){
        this.$message.success('上传成功');
      }else{
        this.handleError()
      }
    });
  }else{
    this.handleError()
  }
},
//上传失败执行的钩子方法
handleError(err, file, fileList){
  this.$message.error('上传失败');
  //清空文件队列
  this.fileList = []
}
```

# 5. 图片查询

## 5.1 需求分析

课程图片上传成功，再次进入课程上传页面应该显示出来已上传的图片。

### 5.1.1 API

在课程管理服务定义查询方法

```java
@ApiOperation("获取课程基础信息")
public CoursePic findCoursePic(String courseId);
```

### 5.1.2 课程管理服务开发

#### 5.1.2.1 Dao

使用CoursePicRepository即可，无需再开发。

#### 5.1.2.2 Service

根据课程 id查询课程图片

```java
public CoursePic findCoursepic(String courseId) {
	return coursePicRepository.findOne(courseId);    
}
```

#### 5.1.2.3 Controller

 ```java
@Override
@GetMapping("/coursepic/list/{courseId}")
public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
    return courseService.findCoursepic(courseId);
}
 ```

### 5.1.3 前端开发

#### 5.1.3.1 API方法

```javascript
//查询课程图片
export const findCoursePicList = courseId => {
  return http.requestQuickGet(apiUrl+'/course/coursepic/list/'+courseId)
}
```

#### 5.1.3.2 页面

在课程图片页面的mounted钩子方法 中查询课程图片信息，并将图片地址赋值给数据对象

1. 定义图片查询方法

```javascript
 //查询图片
list(){
  courseApi.findCoursePicList(this.courseid).then((res) => {
    console.log(res)
    if(res.pic){
      let name = '图片';
      let url = this.imgUrl+res.pic;
      let fileId = res.courseid;
      //先清空文件列表，再将图片放入文件列表
      this.fileList = []
      this.fileList.push({name:name,url:url,fileId:fileId});
    }
    console.log(this.fileList);
  });
 }
```

2. mounted钩子方法

   在mounted钩子方法中调用服务端查询文件列表并绑定到数据对象。

```javascript
mounted(){
   //课程id
   this.courseid = this.$route.params.courseid;
   //查询图片
   this.list()
}
```

#### 5.1.3.3 测试

测试流程：

1. 上传图片成功
2. 进入上传图片页面，观察图片是否显示

# 6. 课程图片删除

## 6.1 需求分析

课程图片上传成功后，可以重新上传，方法是先删除现有图片再上传新图片。

注意：此删除只删除课程数据库的课程图片信息，不去删除文件数据库的文件信息及文件系统服务器上的文件，由于课程图片来源于该用户的文件库，所以此图片可能存在多个地方共用的情况，所以要删除文件系统中的文件需要到图片库由用户确认后再删除。

## 6.2 API

```java
@ApiOperation("删除课程图片")
public ResponseResult deleteCoursePic(String courseId);
```

## 6.3 服务端开发

#### 6.3.1 Dao

CoursePicRepository父类提供的delete方法没有返回值，无法知道是否删除成功，这里我们在CoursePicRepository下边自定义方法：

```java
//删除成功返回1否则返回0
long deleteByCourseid(String courseid);
```

### 6.3.2 Service

```java
//删除课程图片
@Transactional
public ResponseResult deleteCoursePic(String courseId) {
    //执行删除，返回1表示删除成功，返回0表示删除失败
    long result = coursePicRepository.deleteByCourseid(courseId);
    if(result>0){
        return new ResponseResult(CommonCode.SUCCESS);
    }
    return new ResponseResult(CommonCode.FAIL);
}
```

### 6.3.3 Controller

```java
@Override
@DeleteMapping("/coursepic/delete")
public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
    return courseService.deleteCoursePic(courseId);
}
```

## 6.4 前端开发

### 6.4.1 API 调用

```javascript
//删除课程图片
export const deleteCoursePic= courseId => {
  return http.requestDelete(apiUrl+'/course/coursepic/delete?courseId='+courseId)
}
```

### 6.4.2 页面测试

1. before-remove钩子方法

   在upload组件的before-remove钩子方法 中实现删除动作。

```html
<el‐upload
  action="/filesystem/upload"
  list‐type="picture‐card"
  :before‐remove="handleRemove">
  <i class="el‐icon‐plus"></i>
</el‐upload>
```

​	before-remove说明：删除文件之前的钩子，参数为上传的文件和文件列表，若返回 false 或者返回 Promise 且被reject，则停止删除。

2. 定义handleRemove方法进行测试：

   handleRemove 返回true则删除页面的图片，返回false则停止删除页面的图片。

```javascript
//删除图片
handleRemove(file, fileList) {
        console.log(file)
        alert('删除成功')
      return true;   
｝
```

### 6.4.3 promise异步调用

在handleRemove方法调用删除图片的api方法，删除成功时return true，删除失败时return false;

```javascript
// 删除图片
handleRemove(file, fileList) {
  console.log(file)
//alert('删除')
//return true;
  //删除图片
    courseApi.deleteCoursePic('1').then((res) => {
      if(res.success){
        this.$message.success('删除成功');
        return true;
      }else{
        this.$message.error(res.message);
        return false;
      }
    });
},
```

在上边代码中将提交的课程id故意写错，按照我们预期应该是删除失败，而测试结果却是图片在页面上删除成功。

问题原因：

通过查询deleteCoursePic方法的底层代码，deleteCoursePic最终返回一个promise对象。

Promise 是ES6提供的用于异步处理的对象，因为axios提交是异步提交，这里使用promise作为返回值。

Promise的使用方法如下

Promise对象在处理过程中有三种状态：

1. pending：进行中
2. resolved：操作成功
3. rejected: 操作失败

Promise的构建方法如下：

```javascript
const promise = new Promise(function(resolve,reject){
     //...TODO...
    if(操作成功){
        resolve(value);
    }else{
        reject(error);
    }
})
```

上边的构造方法function(resolve,reject)执行流程如下：

1. 方法执行一些业务逻辑。
2. 如果操作成功将Promise的状态由pending变为resolved，并将操作结果传出去
3. 如果操作失败会将promise的状态由pending变为rejected，并将失败结果传出去。

上边说的操作成功将操作结果传给谁了呢？操作失败将失败结果传给谁了呢？

通过promise的then、catch来指定

```javascript
promise.then(function (result) {
    console.log('操作成功：' + result);
});
promise.catch(function (reason) {
    console.log('操作失败：' + reason);
});
```

#### 举例

1. 定义一个方法，返回promise对象

```javascript
testpromise(i){
  return new Promise((resolve,reject)=>{
    if(i % 2==0){
      resolve('成功了')
    }else{
      reject('拒绝了')
    }
  })
}
```

2. 调用此方法

   向方法传入偶数、奇数进行测试。

```javascript
this.testpromise(3).then(res=>{// 在then中对成功结果进行处理
    alert(res)
}).catch(res=>{//在catch中对操作失败结果进行处理
    alert(res)
})
```

3. 最终将handleRemove方法修改如下

   handleRemove方法返回promise对象，当删除成功则resolve，删除失败则reject。

```java
// 删除图片
  handleRemove(file, fileList) {
    console.log(file)
    return new Promise((resolve,reject)=>{
    //删除图片
      courseApi.deleteCoursePic(this.courseid).then((res) => {
        if(res.success){
          this.$message.success('删除成功');
          resolve()//通过
        }else{
          this.$message.error(res.message);
          reject()//拒绝
        }
      });
    })
}
```
