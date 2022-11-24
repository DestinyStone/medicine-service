package org.springblade.modules.medicine.wrapper;

import org.springblade.common.cache.UserCache;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.medicine.entity.Case;
import org.springblade.modules.medicine.vo.CaseVO;
import org.springblade.modules.system.entity.User;

import java.util.Objects;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:33
 * @Description:
 */
public class CaseWrapper extends BaseEntityWrapper<Case, CaseVO> {

    public static CaseWrapper build() {
        return new CaseWrapper();
    }

    @Override
    public CaseVO entityVO(Case entity) {
        CaseVO vo = Objects.requireNonNull(BeanUtil.copy(entity, CaseVO.class));
        User user = UserCache.getUser(entity.getCreateUser());
        vo.setCreateUserName(user != null ? user.getName() : "");

        if (vo.getSex() != null) {
            vo.setSexName(vo.getSex() == 1 ? "女" : "男");
        }
        return vo;
    }

}