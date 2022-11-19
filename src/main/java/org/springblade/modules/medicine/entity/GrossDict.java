package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 17:04
 * @Description:
 */
@Data
@TableName("bus_gross_dict")
public class GrossDict {
    private Long id;

    private String name;
}
