package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:33
 * @Description:
 */
@Data
@TableName("bus_gross")
public class Gross {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "来源")
    private Long belongId;

    @ApiModelProperty(value = "0 古医数据集 1 名医数据集")
    private Integer belongType;
}
