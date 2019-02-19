# 1. Vue.js

## 1.1 Vue.js介绍

### 1.1.1 Vue.js是什么?

Vue (读音 /vjuː/，类似于 view) 是一套用于构建用户界面的渐进式框架。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。Vue 的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。另一方面，当与现代化的工具链以及各种 支持类库结合使用时， Vue 也完全能够为复杂的单页应用提供驱动。

**渐进式框架(Progressive)：**，说明vue.js的轻量，是指一个前端项目可以使用vue.js一两个特性也可以整个项目都用vue.js。

**自底向上逐层应用：**作为渐进式框架要实现的目标就是方便项目增量开发。

### 1.1.2 Vue.js与ECMAScript

Vue 不支持 IE8 及以下版本，因为 Vue 使用了 IE8 无法模拟的 ECMAScript 5 特性。

> **ECMAScript**是一种由[Ecma国际](https://zh.wikipedia.org/wiki/Ecma%E5%9B%BD%E9%99%85)（前身为[欧洲计算机制造商协会](https://zh.wikipedia.org/wiki/%E6%AC%A7%E6%B4%B2%E8%AE%A1%E7%AE%97%E6%9C%BA%E5%88%B6%E9%80%A0%E5%95%86%E5%8D%8F%E4%BC%9A)）通过ECMA-262标准化的[脚本](https://zh.wikipedia.org/wiki/%E8%84%9A%E6%9C%AC%E8%AF%AD%E8%A8%80)[程序设计语言](https://zh.wikipedia.org/wiki/%E7%A8%8B%E5%BA%8F%E8%AE%BE%E8%AE%A1%E8%AF%AD%E8%A8%80)。这种语言在[万维网](https://zh.wikipedia.org/wiki/%E4%B8%87%E7%BB%B4%E7%BD%91)上应用广泛，它往往被称为[JavaScript](https://zh.wikipedia.org/wiki/JavaScript)或[JScript](https://zh.wikipedia.org/wiki/JScript)，但实际上后两者是ECMA-262标准的实现和扩展。

ECMAScript（简称ES）是一种规范，我们平常所说的Js/Javascript是ECMAScript的实现，早期主要应用的ES3，当
前主流浏览器都支持ES5、ES6，ES8已于2017年发布。

ES6：[http://www.ecma-international.org/ecma-262/6.0/](http://www.ecma-international.org/ecma-262/6.0/)
ES7 ：[http://www.ecma-international.org/ecma-262/7.0/](http://www.ecma-international.org/ecma-262/7.0/)

### 1.1.3 Vue.js的使用

1. 在html页面使用script引入vue.js的库即可使用。
2. 使用Npm管理依赖，使用webpack打包工具对vue.js应用打包。
3. Vue-CLI脚手架
4. 使用 vue.js官方提供的CLI脚本架很方便去创建vue.js工程雏形。

### 1.1.4 Vue.js的功能

1. 声明式渲染

   Vue.js 的核心是一个允许采用简洁的模板语法来声明式地将数据渲染进 DOM 的系统。

   ```html
   <div id="app">
     {{ message }}
   </div>
   ```

   ```javascript
   var app = new Vue({
     el: '#app',
     data: {
       message: 'Hello Vue!'
     }
   })
   ```

2. 条件与循环

   dom中可以使用vue.js提供的v-if、v-for等标签，方便对数据进行判断、循环。

   * v-if:判断

   ```html
   <div id="app-3">
     <p v-if="seen">现在你看到我了</p>
   </div>
   ```

   ```javascript
   var app3 = new Vue({
     el: '#app-3',
     data: {
       seen: true
     }
   })
   ```

   * v-for:循环

   ```html
   <div id="app-4">
     <ol>
       <li v-for="todo in todos">
         {{ todo.text }}
       </li>
     </ol>
   </div>
   ```

   ```javascript
   var app4 = new Vue({
     el: '#app-4',
     data: {
       todos: [
         { text: '学习 JavaScript' },
         { text: '学习 Vue' },
         { text: '整个牛项目' }
       ]
     }
   })
   ```

3. 双向数据绑定
   Vue 还提供了 v-model 指令，它能轻松实现表单输入和应用状态之间的双向绑定。

   ```html
   <div id="app-6">
     <p>{{ message }}</p>
     <input v-model="message">
   </div>
   ```

   ```javascript
   var app6 = new Vue({
     el: '#app-6',
     data: {
       message: 'Hello Vue!'
     }
   })
   ```

4. 处理用户输入

   为了让用户和你的应用进行交互，我们可以用 `v-on` 指令添加一个事件监听器，通过它调用在 Vue 实例中定义的方法

   ```html
   <div id="app-5">
     <p>{{ message }}</p>
     <button v-on:click="reverseMessage">逆转消息</button>
   </div>
   ```

   ```javascript
   var app5 = new Vue({
     el: '#app-5',
     data: {
       message: 'Hello Vue.js!'
     },
     methods: {
       reverseMessage: function () {
         this.message = this.message.split('').reverse().join('')
       }
     }
   })
   ```
5. 组件化应用构建
   组件系统是 Vue 的另一个重要概念，因为它是一种抽象，允许我们使用小型、独立和通常可复用的组件构建大型应用。仔细想想，几乎任意类型的应用界面都可以抽象为一个组件树：
   ![](img/vue1.png)
## 1.2 Vue.js基础
### 1.2.1 MVVM模式
vue.js是一个MVVM的框架，理解MVVM有利于学习vue.js。
* MVVM 拆分解释为：
  * Model: 负责数据存储
  * View: 负责页面展示
  * View Model: 负责业务逻辑处理（比如Ajax请求等），对数据进行加工后交给视图展示
* MVVM 要解决的问题是将业务逻辑代码与视图代码进行完全分离，使各自的职责更加清晰，后期代码维护更加简单

![](img/vue2.jpg)

从上图看出，VM(ViewModel)可以把view视图和Model模型解耦合，VM的要做的工作就是vue.js所承担的。

MVVM拆开来即为Model-View-ViewModel，有View，ViewModel，Model三部分组成。View层代表的是视图、模版，负责将数据模型转化为UI展现出来。Model层代表的是模型、数据，可以在Model层中定义数据修改和操作的业务逻辑。ViewModel层连接Model和View。

在MVVM的架构下，View层和Model层并没有直接联系，而是通过ViewModel层进行交互。ViewModel层通过双向数据绑定将View层和Model层连接了起来，使得View层和Model层的同步工作完全是自动的。因此开发者只需关注业务逻辑，无需手动操作DOM，复杂的数据状态维护交给MVVM统一来管理。

### 1.2.2 入门程序

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>vue.js测试程序</title>
    <script src="vue.min.js"></script>
</head>
<body>
<!--实现在body区域显示一个传智播客名称-->
<div id="app">
    {{name}}<!--相当于MVVM的view视图-->
</div>
</body>
<script>
    //编写MVVM中的model部分及VM（ViewModel）部分
    var VM = new Vue({
        el:'#app',//vm接管了app区域的管理
        data:{//model数据
            name:'黑马程序员'
        }
    });
</script>
</html>
```

vue程序编写步骤:

1. 定义html，引入vue.js
2. 定义app div，此区域作为vue的接管区域
3. 定义vue实例，接管app区域。
4. 定义model（数据对象）
5. VM完成在app中展示数据

### 1.2.3 vue事件定义

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>vue.js常用指令的测试</title>
</head>
<body>
<!--实现在body区域显示一个传智播客名称-->
<div id="app">
    <!--相当于MVVM的view视图-->
    <!--{{name}}-->
    <a v-bind:href="url">
        <span v-text="name"></span>
    </a>
    <input type="text" v-model="num1"/> +
    <input type="text" v-model="num2"/>=
    <!-- <span v-text="Number.parseInt(num1)+Number.parseInt(num2)"></span>-->
    <span v-text="result"></span>
    <!--{{Number.parseInt(num1)+Number.parseInt(num2)}}-->
    <button v-on:click="change">计算</button>
    <div v-bind:style="{ fontSize: size + 'px' }">javaEE培训</div>
</div>
</body>
<script src="vue.min.js"></script>
<script>
    //编写MVVM中的model部分及VM（ViewModel）部分
    var VM = new Vue({
        el: '#app',//vm接管了app区域的管理
        data: {//model数据
            name: '黑马程序员',
            num1: 0,
            num2: 0,
            result: 0,
            url: 'http://www.itcast.cn',
            size: 11
        },
        methods: {
            change: function () {
                this.result = Number.parseInt(this.num1) + Number.parseInt(this.num2)
                //alert("计算结果："+this.result)
            }
        }
    });
</script>
</html>
```

### 1.2.4 判断和循环

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>vue.js常用指令的测试</title>
</head>
<body>
<!--实现在body区域显示一个传智播客名称-->
<div id="app">
    <!--相当于MVVM的view视图-->
    <ul>
        <li v-for="(item,index) in list" :key="index" v-if="index % 2 ==0">{{index}}--{{item}}</li>
        <li v-for="(value,key) in user">{{key}}--{{value}}</li>
        <li v-for="(item,index) in userlist" :key="item.user.uname">
            <div v-if="item.user.uname == 'itheima'" style="background: #00f50c">
                {{index}}--{{item.user.uname}}--{{item.user.age}}
            </div>
            <div v-else="">
                {{index}}--{{item.user.uname}}--{{item.user.age}}
            </div>

        </li>
    </ul>
</div>
</body>
<script src="vue.min.js"></script>
<script>
    //编写MVVM中的model部分及VM（ViewModel）部分
    var VM = new Vue({
        el:'#app',//vm接管了app区域的管理
        data:{//model数据
            list:[1,2,3,4,5],
            user:{uname:'itcast',age:10},
            userlist:[
                {user:{uname:'itcast',age:10}},
                {user:{uname:'itheima',age:11}}
            ]
        }
    });
</script>
</html>
```

### 1.2.5 总结

1. v-model:在表单控件或者组件上创建双向绑定,适用于如下元素

   ```html
   input
   select
   textarea
   components（Vue中的组件）
   ```

2. v-text:解决插值表达式闪烁问题

3. v-on:绑定一个按钮的单击事件

4. v-bind:可以将数据对象绑定在dom的任意属性中

   ```html
   <!--举例-->
   <img v‐bind:src="imageSrc">  
   <div v‐bind:style="{ fontSize: size + 'px' }"></div>
   <!--缩写形式-->
   <img :src="imageSrc">
   <div :style="{ fontSize: size + 'px' }"></div>
   ```

5. v-if:判断
6. v-for:循环

# 2. Webpack

## 2.1 介绍

[Webpack](https://webpack.github.io/) is [one of the available module bundlers](https://www.toptal.com/front-end/webpack-browserify-gulp-which-is-better) that processes [JavaScript](https://www.toptal.com/javascript) code, as well as all static assets, such as stylesheets, images, and fonts, into a bundled file. Processing can include all the necessary tasks for managing and optimizing code dependencies, such as compilation, concatenation, minification, and compression.

![](img/webpack02.png)

![](img/webpack01.png)

* 优点

1. 模块化开发
   程序员在开发时可以分模块创建不同的js、 css等小文件方便开发，最后使用webpack将这些小文件打包成一个文件，减少了http的请求次数。
2. 编译typescript、ES6等高级js语法
   随着前端技术的强大，开发中可以使用javascript的很多高级版本，比如：typescript、ES6等，方便开发，webpack可以将打包文件转换成浏览器可识别的js语法。
3. CSS预编译
   webpack允许在开发中使用Sass 和 Less等原生CSS的扩展技术，通过sass-loader、less-loader将Sass 和 Less的语法编译成浏览器可识别的css语法。

* 不足

1. 配置有些繁琐
2. 文档不丰富

## 2.2 安装Webpack

### 2.2.1 安装node.js

webpack基于node.js运行，首先需要安装node.js。

> **Node.js**是一个能够在[服务器](https://zh.wikipedia.org/wiki/%E4%BC%BA%E6%9C%8D%E5%99%A8)端运行[JavaScript](https://zh.wikipedia.org/wiki/JavaScript)的[开放源代码](https://zh.wikipedia.org/wiki/%E9%96%8B%E6%94%BE%E5%8E%9F%E5%A7%8B%E7%A2%BC)、[跨平台](https://zh.wikipedia.org/wiki/%E8%B7%A8%E5%B9%B3%E5%8F%B0)JavaScript [运行环境](https://zh.wikipedia.org/wiki/%E6%89%A7%E8%A1%8C%E7%8E%AF%E5%A2%83)。Node.js采用[Google](https://zh.wikipedia.org/wiki/Google)开发的[V8](https://zh.wikipedia.org/wiki/V8_(JavaScript%E5%BC%95%E6%93%8E))运行代码，使用[事件驱动](https://zh.wikipedia.org/wiki/%E4%BA%8B%E4%BB%B6%E9%A9%85%E5%8B%95)、[非阻塞](https://zh.wikipedia.org/w/index.php?title=%E9%9D%9E%E9%98%BB%E5%A1%9E&action=edit&redlink=1)和 [异步输入输出](https://zh.wikipedia.org/w/index.php?title=%E9%9D%9E%E5%90%8C%E6%AD%A5%E8%BC%B8%E5%85%A5%E8%BC%B8%E5%87%BA&action=edit&redlink=1)模型等技术来提高性能，可优化应用程序的传输量和规模。这些技术通常用于数据密集的即时应用程序。
>
> Node.js大部分基本模块都用JavaScript语言编写。在Node.js出现之前，JavaScript通常作为客户端程序设计语言使用，以JavaScript写出的程序常在用户的浏览器上运行。Node.js的出现使JavaScript也能用于服务端编程。Node.js含有一系列内置模块，使得程序可以脱离[Apache HTTP Server](https://zh.wikipedia.org/wiki/Apache_HTTP_Server)或[IIS](https://zh.wikipedia.org/wiki/IIS)，作为独立服务器运行。

1. [官网](https://nodejs.org/en/download/)下载

2. 默认安装即可

3. 测试

   ```shell
   node ‐v
   ```

### 2.2.2 安装npm

#### 2.2.2.1 npm介绍

**npm**（全称 Node Package Manager，即“node包管理器”）是[Node.js](https://zh.wikipedia.org/wiki/Node.js)默认的、以[JavaScript](https://zh.wikipedia.org/wiki/JavaScript)编写的[软件包管理系统](https://zh.wikipedia.org/wiki/%E8%BB%9F%E9%AB%94%E5%A5%97%E4%BB%B6%E7%AE%A1%E7%90%86%E7%B3%BB%E7%B5%B1)。npm会随着Node.js自动安装。

```shell
npm -v
```

npm可以管理本地项目的所需模块并自动维护依赖情况，也可以管理全局安装的JavaScript工具

#### 2.2.2.2 设置包路径

包路径就是npm从远程下载的js包所存放的路径

```shell
# 查看npm管理包路径
npm config ls
# 更改npm包管理目录
npm config set prefix "C:\work\develop\nodejs\npm_modules"
npm config set cache "C:\work\develop\nodejs\npm_cache"
```

### 2.2.3 安装cnmp

npm默认会去国外的镜像去下载js包，在开发中通常我们使用国内镜像，这里我们使用淘宝镜像下边我们来安装cnpm

```shell
# 安装淘宝cnpm镜像
npm install -g cnpm --registry=https://registry.npm.taobao.org
# 查看cnpm版本
cnpm -v
```

如果cnmp执行失败,将cnpm的命令目录配置到环境变量

### 2.2.4 安装nrm

nrm可以用来查看npm的镜像源

```shell
# 安装nrm
cnpm install -g nrm
# 查看npm镜像
nrm ls
# 切换镜像
nrm use XXX
```

### 2.2.5 安装webpack

* 本地安装:使用于某个具体项目
* 全局安装:使用于全局项目

```shell
cnpm install webpack -g
```

# 3. CMS前端工程