package org.springblade.modules.medicine.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:16
 * @Description:
 */
@Data
public class SynonymDTO {
    private String name;

    private List<Long> dictIds;
}
