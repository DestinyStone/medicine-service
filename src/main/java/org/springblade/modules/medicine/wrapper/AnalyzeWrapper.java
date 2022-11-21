package org.springblade.modules.medicine.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Analyze;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.vo.AnalyzeVO;
import org.springblade.modules.medicine.vo.GrossVO;

import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/21 19:34
 * @Description:
 */
public class AnalyzeWrapper extends BaseEntityWrapper<Analyze, AnalyzeVO> {

    public static AnalyzeWrapper build() {
        return new AnalyzeWrapper();
    }

    @Override
    public AnalyzeVO entityVO(Analyze notice) {
        AnalyzeVO vo = Objects.requireNonNull(BeanUtil.copy(notice, AnalyzeVO.class));
        return vo;
    }

}
