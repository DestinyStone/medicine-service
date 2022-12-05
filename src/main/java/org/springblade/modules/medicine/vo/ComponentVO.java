package org.springblade.modules.medicine.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/1 22:43
 * @Description:
 */
@Data
public class ComponentVO {
    private List<Long> ids;

    private List<Long> dictIds;
}
