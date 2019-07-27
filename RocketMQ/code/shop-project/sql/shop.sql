/*
SQLyog Ultimate v8.32 
MySQL - 5.5.49 : Database - trade
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`trade` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `trade`;

/*Table structure for table `trade_coupon` */

DROP TABLE IF EXISTS `trade_coupon`;

CREATE TABLE `trade_coupon` (
  `coupon_id` bigint(50) NOT NULL COMMENT '优惠券ID',
  `coupon_price` decimal(10,2) DEFAULT NULL COMMENT '优惠券金额',
  `user_id` bigint(50) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(32) DEFAULT NULL COMMENT '订单ID',
  `is_used` int(1) DEFAULT NULL COMMENT '是否使用 0未使用 1已使用',
  `used_time` timestamp NULL DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`coupon_id`),
  KEY `FK_trade_coupon` (`user_id`),
  KEY `FK_trade_coupon2` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_coupon` */

insert  into `trade_coupon`(`coupon_id`,`coupon_price`,`user_id`,`order_id`,`is_used`,`used_time`) values (345988230098857984,'20.00',345963634385633280,352537369385242624,1,'2019-07-27 16:58:44');

/*Table structure for table `trade_goods` */

DROP TABLE IF EXISTS `trade_goods`;

CREATE TABLE `trade_goods` (
  `goods_id` bigint(50) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品库存',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_desc` varchar(255) DEFAULT NULL COMMENT '商品描述',
  `add_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=345959443973935105 DEFAULT CHARSET=utf8;

/*Data for the table `trade_goods` */

insert  into `trade_goods`(`goods_id`,`goods_name`,`goods_number`,`goods_price`,`goods_desc`,`add_time`) values (345959443973935104,'Javase课程',999,'1000.00','传智播客出品Java视频课程','2019-07-09 20:38:00');

/*Table structure for table `trade_goods_number_log` */

DROP TABLE IF EXISTS `trade_goods_number_log`;

CREATE TABLE `trade_goods_number_log` (
  `goods_id` bigint(50) NOT NULL COMMENT '商品ID',
  `order_id` bigint(50) NOT NULL COMMENT '订单ID',
  `goods_number` int(11) DEFAULT NULL COMMENT '库存数量',
  `log_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`goods_id`,`order_id`),
  KEY `FK_trade_goods_number_log2` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_goods_number_log` */

insert  into `trade_goods_number_log`(`goods_id`,`order_id`,`goods_number`,`log_time`) values (345959443973935104,352537369385242624,-1,'2019-07-27 16:58:44');

/*Table structure for table `trade_mq_consumer_log` */

DROP TABLE IF EXISTS `trade_mq_consumer_log`;

CREATE TABLE `trade_mq_consumer_log` (
  `msg_id` varchar(50) DEFAULT NULL,
  `group_name` varchar(100) NOT NULL,
  `msg_tag` varchar(100) NOT NULL,
  `msg_key` varchar(100) NOT NULL,
  `msg_body` varchar(500) DEFAULT NULL,
  `consumer_status` int(1) DEFAULT NULL COMMENT '0:正在处理;1:处理成功;2:处理失败',
  `consumer_times` int(1) DEFAULT NULL,
  `consumer_timestamp` timestamp NULL DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`group_name`,`msg_tag`,`msg_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_mq_consumer_log` */

/*Table structure for table `trade_mq_producer_temp` */

DROP TABLE IF EXISTS `trade_mq_producer_temp`;

CREATE TABLE `trade_mq_producer_temp` (
  `id` varchar(100) NOT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `msg_topic` varchar(100) DEFAULT NULL,
  `msg_tag` varchar(100) DEFAULT NULL,
  `msg_key` varchar(100) DEFAULT NULL,
  `msg_body` varchar(500) DEFAULT NULL,
  `msg_status` int(1) DEFAULT NULL COMMENT '0:未处理;1:已经处理',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_mq_producer_temp` */

/*Table structure for table `trade_order` */

DROP TABLE IF EXISTS `trade_order`;

CREATE TABLE `trade_order` (
  `order_id` bigint(50) NOT NULL COMMENT '订单ID',
  `user_id` bigint(50) DEFAULT NULL COMMENT '用户ID',
  `order_status` int(1) DEFAULT NULL COMMENT '订单状态 0未确认 1已确认 2已取消 3无效 4退款',
  `pay_status` int(1) DEFAULT NULL COMMENT '支付状态 0未支付 1支付中 2已支付',
  `shipping_status` int(1) DEFAULT NULL COMMENT '发货状态 0未发货 1已发货 2已收货',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `consignee` varchar(255) DEFAULT NULL COMMENT '收货人',
  `goods_id` bigint(50) DEFAULT NULL COMMENT '商品ID',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_amount` decimal(10,0) DEFAULT NULL COMMENT '商品总价',
  `shipping_fee` decimal(10,2) DEFAULT NULL COMMENT '运费',
  `order_amount` decimal(10,2) DEFAULT NULL COMMENT '订单价格',
  `coupon_id` bigint(50) DEFAULT NULL COMMENT '优惠券ID',
  `coupon_paid` decimal(10,2) DEFAULT NULL COMMENT '优惠券',
  `money_paid` decimal(10,2) DEFAULT NULL COMMENT '已付金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `add_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `confirm_time` timestamp NULL DEFAULT NULL COMMENT '订单确认时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`order_id`),
  KEY `FK_trade_order` (`user_id`),
  KEY `FK_trade_order2` (`goods_id`),
  KEY `FK_trade_order3` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_order` */

insert  into `trade_order`(`order_id`,`user_id`,`order_status`,`pay_status`,`shipping_status`,`address`,`consignee`,`goods_id`,`goods_number`,`goods_price`,`goods_amount`,`shipping_fee`,`order_amount`,`coupon_id`,`coupon_paid`,`money_paid`,`pay_amount`,`add_time`,`confirm_time`,`pay_time`) values (352537369385242624,345963634385633280,1,2,NULL,'北京',NULL,345959443973935104,1,'1000.00',NULL,'0.00','1000.00',345988230098857984,'20.00','100.00','880.00','2019-07-27 16:58:44','2019-07-27 16:58:44',NULL);

/*Table structure for table `trade_pay` */

DROP TABLE IF EXISTS `trade_pay`;

CREATE TABLE `trade_pay` (
  `pay_id` bigint(50) NOT NULL COMMENT '支付编号',
  `order_id` bigint(50) DEFAULT NULL COMMENT '订单编号',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_paid` int(1) DEFAULT NULL COMMENT '是否已支付 0:未付款 1正在付款 2已经付款',
  PRIMARY KEY (`pay_id`),
  KEY `FK_trade_pay` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_pay` */

insert  into `trade_pay`(`pay_id`,`order_id`,`pay_amount`,`is_paid`) values (352542415984402432,352537369385242624,'880.00',2);

/*Table structure for table `trade_user` */

DROP TABLE IF EXISTS `trade_user`;

CREATE TABLE `trade_user` (
  `user_id` bigint(50) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户姓名',
  `user_password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `user_mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `user_score` int(11) DEFAULT NULL COMMENT '积分',
  `user_reg_time` timestamp NULL DEFAULT NULL COMMENT '注册时间',
  `user_money` decimal(10,0) DEFAULT NULL COMMENT '用户余额',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=345963634385633281 DEFAULT CHARSET=utf8;

/*Data for the table `trade_user` */

insert  into `trade_user`(`user_id`,`user_name`,`user_password`,`user_mobile`,`user_score`,`user_reg_time`,`user_money`) values (345963634385633280,'zs','123456','18888888888',100,'2020-10-10 00:00:00','900');

/*Table structure for table `trade_user_money_log` */

DROP TABLE IF EXISTS `trade_user_money_log`;

CREATE TABLE `trade_user_money_log` (
  `user_id` bigint(50) NOT NULL COMMENT '用户ID',
  `order_id` bigint(50) NOT NULL COMMENT '订单ID',
  `money_log_type` int(1) NOT NULL COMMENT '日志类型 1订单付款 2 订单退款',
  `use_money` decimal(10,2) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL COMMENT '日志时间',
  PRIMARY KEY (`user_id`,`order_id`,`money_log_type`),
  KEY `FK_trade_user_money_log2` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `trade_user_money_log` */

insert  into `trade_user_money_log`(`user_id`,`order_id`,`money_log_type`,`use_money`,`create_time`) values (345963634385633280,352537369385242624,1,'100.00','2019-07-27 16:58:44');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
