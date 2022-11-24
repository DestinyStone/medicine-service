package org.springblade.modules.medicine.dto;

import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Case;

import java.util.List;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:13
 * @Description:
 */
@Data
public class CaseDTO {

    private Integer type;

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
    private List<Long> medicineIds;

    @ApiModelProperty(value = "病例名称")
    private String medicineNames;

    @ApiModelProperty(value = "辩证")
    private String dialectical;

    @ApiModelProperty(value = "处方")
    private String component;

    @ApiModelProperty(value = "叮嘱")
    private String enjoin;

    public Case to() {
        Case obj = BeanUtil.copyProperties(this, Case.class);
        obj.setMedicineIds(CollUtil.join(medicineIds, ","));
        return obj;
    }
}
