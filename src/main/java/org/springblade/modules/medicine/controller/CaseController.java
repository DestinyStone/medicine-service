package org.springblade.modules.medicine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.medicine.dto.CaseDTO;
import org.springblade.modules.medicine.entity.Case;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.service.CaseService;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.vo.CaseVO;
import org.springblade.modules.medicine.wrapper.CaseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:11
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/case")
@AllArgsConstructor
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private GrossDictService dictService;

    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save(@RequestBody @Valid CaseDTO dto) {
        Case convert = dto.to();
        convert.setCreateUser(AuthUtil.getUserId());
        convert.setCreateTime(new Date());
        convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());

        boolean status = caseService.save(convert);

        return R.data(status);
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<CaseVO>> page(CaseDTO vo, Query query) {
        LambdaQueryWrapper<Case> wrapper = new LambdaQueryWrapper<>();
        IPage<Case> page = caseService.page(Condition.getPage(query), wrapper);
        return R.data(CaseWrapper.build().pageVO(page));
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    @Transactional
    public R delete(@RequestParam("ids") String ids) {
        boolean status = caseService.removeByIds(Func.toLongList(ids));
        return R.status(status);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<CaseVO> detail(@RequestParam("id") Long id) {
        CaseVO vo = CaseWrapper.build().entityVO(caseService.getById(id));
        if (StrUtil.isNotBlank(vo.getMedicineIds())) {
            List<GrossDict> medicines = dictService.listByIds(Func.toLongList(vo.getMedicineIds()));
            vo.setMedicineList(medicines);
        }
        return R.data(vo);
    }
}
