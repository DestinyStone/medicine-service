package org.springblade.modules.medicine.wrapper;

import org.springblade.common.cache.DictCache;
import org.springblade.common.enums.DictEnum;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.desk.entity.Notice;
import org.springblade.modules.desk.vo.NoticeVO;
import org.springblade.modules.desk.wrapper.NoticeWrapper;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.vo.MedicineVO;

import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 13:59
 * @Description:
 */
public class MedicineWrapper extends BaseEntityWrapper<Medicine, MedicineVO> {

    public static MedicineWrapper build() {
        return new MedicineWrapper();
    }

    @Override
    public MedicineVO entityVO(Medicine notice) {
        MedicineVO vo = Objects.requireNonNull(BeanUtil.copy(notice, MedicineVO.class));
        return vo;
    }

}
