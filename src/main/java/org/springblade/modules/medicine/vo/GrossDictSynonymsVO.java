package org.springblade.modules.medicine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/22 12:46
 * @Description:
 */
@Data
public class GrossDictSynonymsVO {
    private Long id;
    private String name;

    @ApiModelProperty(value = "0 字典 1同义词")
    private Integer type;
}
