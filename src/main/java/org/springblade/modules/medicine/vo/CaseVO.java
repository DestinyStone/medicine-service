package org.springblade.modules.medicine.vo;

import lombok.Data;
import org.springblade.modules.medicine.entity.Case;
import org.springblade.modules.medicine.entity.GrossDict;

import java.util.List;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:32
 * @Description:
 */
@Data
public class CaseVO extends Case {
    private String createUserName;

    private String sexName;

    private List<GrossDict> medicineList;
}
