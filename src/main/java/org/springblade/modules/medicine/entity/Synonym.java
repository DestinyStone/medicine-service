package org.springblade.modules.medicine.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:13
 * @Description:
 */
@Data
@TableName("bus_synonym")
public class Synonym {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

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

    @ApiModelProperty(value = "描述")
    private String remark;
}
