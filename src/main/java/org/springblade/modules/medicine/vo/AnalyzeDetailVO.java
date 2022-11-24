package org.springblade.modules.medicine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.modules.medicine.entity.Analyze;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/21 19:34
 * @Description:
 */
@Data
public class AnalyzeDetailVO extends Analyze {

    @ApiModelProperty(value = "0 系统不存在 1系统存在")
    private Integer isExist;
}
