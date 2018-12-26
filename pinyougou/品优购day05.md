# 1. 商品分类管理

| Field     | Type                | Comment                          |
| --------- | ------------------- | -------------------------------- |
| id        | bigint(20) NOT NULL | 类目ID                           |
| parent_id | bigint(20) NULL     | 父类目ID=0时，代表的是一级的类目 |
| name      | varchar(50) NULL    | 类目名称                         |
| type_id   | bigint(11) NULL     | 类型id                           |

**查询商品分类**
    ```sql
--一级分类
SELECT * FROM tb_item_cat WHERE parent_id=0;
--二级分类
SELECT * FROM tb_item_cat WHERE parent_id=161;
--三级分类
SELECT * FROM tb_item_cat WHERE parent_id=162;
    ```

* item_cat.html

  ```html
  <script type="text/javascript" src="../js/base.js"></script>
  <script type="text/javascript" src="../js/service/itemCatService.js"></script>
  <script type="text/javascript" src="../js/controller/baseController.js"></script>
  <script type="text/javascript" src="../js/controller/itemCatController.js"></script>
  
  <body ng-app="pinyougou" ng-controller="itemCatController" ng-init="findByParentId(0)">
      ...
      <tr ng-repeat="entity in list">
          <td><input  type="checkbox" ></td>			                              
          <td>{{entity.id}}</td>
          <td>{{entity.name}}</td>									    
          <td>
              {{entity.typeId}}    
          </td>									      
          <td class="text-center">
              <button ng-click="findByParentId(entity.id)">查询下级</button>
          </td>
      </tr>
  </body>
  ```

* itemCatController.js

  ```javascript
  $scope.findByParentId=function(parentId){
  	itemCatService.findByParentId(parentId).success(
  		function(response){
  			$scope.list=response;				
  		}
  	);		
  }
  ```

* itemCatService.js

  ```javascript
  this.findByParentId=function(parentId){
  	return $http.get('../itemCat/findByParentId.do?parentId='+parentId);
  }
  ```

* ItemCatController.java

  ```java
  @RequestMapping("/findByParentId")
  public List<TbItemCat> findByParentId(Long parentId){
  	return itemCatService.findByParentId(parentId);
  }
  ```

* ItemCatServiceImpl.java

  ```java
  public List<TbItemCat> findByParentId(Long parentId) {
  	TbItemCatExample example=new TbItemCatExample();
  	Criteria criteria = example.createCriteria();
  	criteria.andParentIdEqualTo(parentId);
  	return itemCatMapper.selectByExample(example);
  }
  ```

# 2. 电商概念

# 3. 富文本编辑器的使用

# 4. 分布式文件服务器FastDFS

# 5. AngularJS文件上传

