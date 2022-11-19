package org.springblade.modules.medicine.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.vo.MedicineVO;
import org.springblade.modules.medicine.vo.SynonymVO;

import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:31
 * @Description:
 */
public class SynonymWrapper extends BaseEntityWrapper<Synonym, SynonymVO> {

    public static SynonymWrapper build() {
        return new SynonymWrapper();
    }

    @Override
    public SynonymVO entityVO(Synonym notice) {
        SynonymVO vo = Objects.requireNonNull(BeanUtil.copy(notice, SynonymVO.class));
        return vo;
    }

}
