package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:06
 * @Description:
 */
@Data
@TableName("bus_case")
public class Case {
    private Long id;

    @ApiModelProperty(value = "病例编号")
    private String code;

    @ApiModelProperty("就诊时间")
    private String caseTime;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "性别 0男 1女")
    private Integer sex;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "年龄")
    private  Integer age;

    @ApiModelProperty(value = "病例")
    private String medicineIds;

    @ApiModelProperty(value = "病例名称")
    private String medicineNames;

    @ApiModelProperty(value = "辩证")
    private String dialectical;

    @ApiModelProperty(value = "处方")
    private String component;

    @ApiModelProperty(value = "叮嘱")
    private String enjoin;

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
