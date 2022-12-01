package org.springblade.modules.medicine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrossDict grossDict = (GrossDict) o;
        return Objects.equals(id, grossDict.id) &&
                Objects.equals(name, grossDict.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
