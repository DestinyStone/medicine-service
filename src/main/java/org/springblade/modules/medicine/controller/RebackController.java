package org.springblade.modules.medicine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.medicine.dto.RebackRemarkDTO;
import org.springblade.modules.medicine.entity.Reback;
import org.springblade.modules.medicine.service.RebackService;
import org.springblade.modules.medicine.vo.RebackVO;
import org.springblade.modules.medicine.wrapper.RebackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 00:38
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/reback")
@AllArgsConstructor
public class RebackController {

    @Autowired
    private RebackService rebackService;

    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save() {
        return R.status(rebackService.saveReback());
    }

    /**
     * 还原
     */
    @PostMapping("/reback/{id}")
    @Transactional
    public R reback(@PathVariable("id") Long id) {
        return R.status(rebackService.reback(id));
    }

    /**
     * 修改描述
     */
    @PostMapping("/remark/{id}")
    @Transactional
    public R reback(@PathVariable("id") Long id, @RequestBody RebackRemarkDTO dto) {
        if (StrUtil.isBlank(dto.getRemark())) {
            return R.status(true);
        }
        LambdaUpdateWrapper<Reback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Reback::getId, id)
                .set(Reback::getRemark, dto.getRemark());
        return R.status(rebackService.update(wrapper));
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<RebackVO>> page(RebackVO vo, Query query) {
        LambdaQueryWrapper<Reback> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(vo.getCode()), Reback::getCode, vo.getCode())
                .like(StrUtil.isNotBlank(vo.getRemark()), Reback::getRemark, vo.getRemark());
        wrapper.orderByDesc(Reback::getCreateTime);
        IPage<Reback> page = rebackService.page(Condition.getPage(query), wrapper);
        return R.data(RebackWrapper.build().pageVO(page));
    }
}
