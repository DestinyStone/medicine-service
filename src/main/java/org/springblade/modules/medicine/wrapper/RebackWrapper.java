package org.springblade.modules.medicine.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Reback;
import org.springblade.modules.medicine.vo.RebackVO;

import java.util.Objects;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 00:41
 * @Description:
 */
public class RebackWrapper extends BaseEntityWrapper<Reback, RebackVO> {

    public static RebackWrapper build() {
        return new RebackWrapper();
    }

    @Override
    public RebackVO entityVO(Reback notice) {
        RebackVO vo = Objects.requireNonNull(BeanUtil.copy(notice, RebackVO.class));
        return vo;
    }

}