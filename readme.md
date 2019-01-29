# 乐淘商城

其实就是网上已经臭名昭著的淘淘商城，自己拿来练习，顺带也当成毕设了，网上一些功能没有写全，自己尽量完善。

## 需要完善

1. ~~首页商品展示~~
   1. ~~现在能从数据库中查询出一条数据显示到前台界面，其他属性已经设置好，还需要修改**链接**。~~
   2. ~~需要封装一个json的pojo，具体查看protal-web。~~
   3. ~~数据显示的属性看home.js中的438行~~
   4. ~~最终需要有五个分类，每个分类需要显示10条数据。~~
2. 首页页脚广告位。
3. 支付宝支付。
4. 单点登录换成域名后其他系统能否显示用户的信息。
5. 更换静态图片大小。
6. 注册表单校验。
7. ~~改造购物车，使用redis实现：~~
   1. ~~使用hash数据结构：Key：用户id，hash的field：商品id，value：商品信息(json)。~~
   2. ~~在用户未登录情况下写cookie。当用户登录后，访问购物车列表时。~~
      1. ~~把cookie中的数据同步到redis。~~
      2. ~~把cookie中的数据删除。~~
      3. ~~展示购物车列表时以redis为准。~~
      4. ~~如果redis中有数据cookie中也有数据，需要做数据合并。相同商品数量相加，不同商品添加一个新商品。~~
   3. ~~如果用户登录状态，展示购物车列表以redis为准。如果未登录，以cookie为准。~~
8. ~~"我的订单",(查询出当前登录用户的所有订单)。~~，只是能看见所有订单。

## 项目

- letao-parent 父工程，管理框架的版本号(pom)
  - letao-common 通用工具，pojo等(jar)
  - letao-content 内容管理(pom)
    - letao-content-interface 内容管理接口(jar)。
    - letao-content-service 内容管理实现类(war)。
  - letao-manager 商品管理(pom)
    - letao-manager-dao 数据库dao层，mybatis逆向工程生成的mapper代码(jar)。
    - letao-manager-pojo pojo层，mybatis逆向工程生成的，需要都实现序列化接口(jar)。
    - letao-manager-interface 商品管理的接口(jar)。
    - letao-manager-service 商品管理的实现类(war)。
  - letao-manager-web 后台界面，表现层，pom中加入manager和content的interface，(war)。
  - letao-protal-web 前台界面，表现层(war)。
  - letao-search 搜索系统服务层(pom)。
    - letao-search-interface (jar) 搜索接口，自定义mapper。
    - letao-search-service 搜索服务实现，消息队列(war)
  - letao-search-web 搜索表现层(war)。
  - letao-item-web 商品详情(war)。
  - letao-sso 单点登录服务层(pom)。
    - letao-sso-interface 登录注册接口(jar)。
    - letao-sso-service 登录注册实现(war)。
  - letao-sso-web 登录注册实现(war)。
  - letao-cart-web 购物车实现(war)。
  - letao-order 订单服务层(pom)。
    - letao-order-interface 订单接口(jar)。
    - letao-order-service 订单接口实现(war)。
  - letao-order-web 订单表现层(war)。
  - 图片服务器

## 系统bug

- 在左边全部分类中，点击某一个分类会根据分类去查询商品，但是，在查询出的商品界面中，点击分页，会没有数据。
  - 原因是：由于没有对分类查询单独写页面，在solr指定默认搜索域的时候，是根据商品标题搜索，而在此页面点击分类，会拿当前分类名称在商品标题中，就有可能没有数据显示出来。