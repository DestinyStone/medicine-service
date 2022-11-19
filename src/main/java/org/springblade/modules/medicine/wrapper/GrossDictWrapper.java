package org.springblade.modules.medicine.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.vo.GrossDictVO;
import org.springblade.modules.medicine.vo.GrossVO;

import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 18:54
 * @Description:
 */
public class GrossDictWrapper extends BaseEntityWrapper<GrossDict, GrossDictVO> {

    public static GrossDictWrapper build() { return new GrossDictWrapper(); }

    @Override
    public GrossDictVO entityVO(GrossDict notice) {
        GrossDictVO vo = Objects.requireNonNull(BeanUtil.copy(notice, GrossDictVO.class));
        return vo;
    }

}
