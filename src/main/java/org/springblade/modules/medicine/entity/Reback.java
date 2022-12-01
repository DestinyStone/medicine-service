package org.springblade.modules.medicine.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 00:22
 * @Description:
 */
@Data
@TableName("bus_reback")
public class Reback {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态 0 当前分支 1 其他分支")
    private Integer status;

    @ApiModelProperty(value = "创建用户")
    private Long createUser;

    @ApiModelProperty(value = "创建部门")
    private Long createDept;

    @ApiModelProperty(value = "创建时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新用户")
    private Long updateUser;

    @ApiModelProperty(value = "更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "备份编号")
    private String code;

    @ApiModelProperty(value = "remark")
    private String remark;
}
