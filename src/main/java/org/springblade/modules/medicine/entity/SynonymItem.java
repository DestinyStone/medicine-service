package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:11
 * @Description:
 */
@Data
@TableName("bus_synonym_item")
public class SynonymItem {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "字典id")
    private Long dictId;

    @ApiModelProperty(value = "同义词id")
    private Long synonymId;
}
