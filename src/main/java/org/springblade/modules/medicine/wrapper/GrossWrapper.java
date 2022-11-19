package org.springblade.modules.medicine.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.vo.GrossVO;
import org.springblade.modules.medicine.vo.MedicineVO;

import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:59
 * @Description:
 */
public class GrossWrapper extends BaseEntityWrapper<Gross, GrossVO> {

    public static GrossWrapper build() {
        return new GrossWrapper();
    }

    @Override
    public GrossVO entityVO(Gross notice) {
        GrossVO vo = Objects.requireNonNull(BeanUtil.copy(notice, GrossVO.class));
        return vo;
    }

}
