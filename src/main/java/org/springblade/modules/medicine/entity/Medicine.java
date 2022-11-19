package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 13:54
 * @Description:
 */
@Data
@TableName("bus_medicine")
public class Medicine {
    @Id
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "症状")
    private String putUp;

    @ApiModelProperty(value = "药方")
    private String solve;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "0 古医数据集 1 名医数据集")
    private Integer type;

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

    public void setPutUp(String putUp) {
        this.putUp = putUp.replace(",", "，");
    }
}
