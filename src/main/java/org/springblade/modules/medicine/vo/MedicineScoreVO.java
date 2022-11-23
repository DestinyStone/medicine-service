package org.springblade.modules.medicine.vo;

import lombok.Data;
import org.springblade.modules.medicine.entity.Medicine;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/22 16:05
 * @Description:
 */
@Data
public class MedicineScoreVO extends Medicine {
    private Double score;
}
