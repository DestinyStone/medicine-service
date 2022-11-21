package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/21 19:31
 * @Description:
 */
@Data
@TableName("bus_analyze")
public class Analyze {

    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "药性")
    private String remark;

    @ApiModelProperty(value = "创建用户")
    private Long createUser;

    @ApiModelProperty(value = "创建部门")
    private Long createDept;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新用户")
    private Long updateUser;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
