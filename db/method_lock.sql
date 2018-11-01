
DROP TABLE IF EXISTS `method_lock`;

CREATE TABLE `method_lock`(
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `method_name` varchar(64) NOT NULL DEFAULT '' COMMENT '锁定的方法名',
  `desc` varchar(512) NOT NULL DEFAULT '备注信息',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '保存的数据时间，自动生成',
  primary key(`id`),
  UNIQUE KEY `uidx_method_name` (`method_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='锁定中的方法记录表';

/**
 *  InnoDB 引擎在加锁的时候，只有通过索引进行检索的时候，才会使用行级索，否则使用表级锁
 */