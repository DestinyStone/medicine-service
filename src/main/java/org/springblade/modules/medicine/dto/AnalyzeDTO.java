package org.springblade.modules.medicine.dto;

import lombok.Data;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Analyze;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/21 19:34
 * @Description:
 */
@Data
public class AnalyzeDTO extends Analyze {

    public Analyze to() {
        Analyze convert = BeanUtil.copy(this, Analyze.class);
        convert.setName(convert.getName().replaceAll("\n", "").trim());
        return convert;
    }
}
