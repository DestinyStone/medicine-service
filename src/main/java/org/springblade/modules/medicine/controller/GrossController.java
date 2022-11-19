package org.springblade.modules.medicine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.GrossService;
import org.springblade.modules.medicine.vo.GrossDictVO;
import org.springblade.modules.medicine.vo.MedicineVO;
import org.springblade.modules.medicine.wrapper.GrossDictWrapper;
import org.springblade.modules.medicine.wrapper.MedicineWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:59
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/gross")
@AllArgsConstructor
public class GrossController {

    @Autowired
    private GrossService grossService;

    @Autowired
    private GrossDictService grossDictService;

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<GrossDictVO>> page(GrossDictVO vo, Query query) {
        LambdaQueryWrapper<GrossDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtil.isNotBlank(vo.getName()), GrossDict::getName, vo.getName());
        IPage<GrossDict> page = grossDictService.page(Condition.getPage(query), wrapper);
        return R.data(GrossDictWrapper.build().pageVO(page));
    }
}
