CREATE TABLE `t_employee`
(
    `id`           BIGINT(0)                                                  NOT NULL AUTO_INCREMENT COMMENT '员工的唯一标识',
    `name`         VARCHAR(20) CHARACTER SET `utf8` COLLATE `utf8_general_ci` NOT NULL COMMENT '姓名',
    `gender`       BIGINT(0)                                                  NOT NULL COMMENT '性别, 0——男、1——女',
    `salary`       DECIMAL(10, 2)                                             NOT NULL COMMENT '薪资',
    `birthday`     DATETIME(0)                                                NOT NULL COMMENT '生日',
    `entry_time`   DATETIME(0)                                                NOT NULL COMMENT '入职时间',
    `gmt_create`   DATETIME(0)                                                NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '数据创建时间',
    `gmt_modified` DATETIME(0)                                                NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '数据更新时间',
    PRIMARY KEY (`id`) USING BTREE
)