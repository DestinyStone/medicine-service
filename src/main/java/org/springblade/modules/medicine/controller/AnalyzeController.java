package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.medicine.dto.AnalyzeDTO;
import org.springblade.modules.medicine.dto.SynonymDTO;
import org.springblade.modules.medicine.entity.Analyze;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.entity.SynonymItem;
import org.springblade.modules.medicine.service.AnalyzeService;
import org.springblade.modules.medicine.vo.AnalyzeVO;
import org.springblade.modules.medicine.vo.SynonymVO;
import org.springblade.modules.medicine.wrapper.AnalyzeWrapper;
import org.springblade.modules.medicine.wrapper.GrossDictWrapper;
import org.springblade.modules.medicine.wrapper.SynonymWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/21 19:35
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/analyze")
@AllArgsConstructor
public class AnalyzeController {

    @Autowired
    private AnalyzeService analyzeService;

    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save(@RequestBody @Valid AnalyzeDTO dto) {
        Analyze convert = BeanUtil.copy(dto, Analyze.class);
        convert.setCreateUser(AuthUtil.getUserId());
        convert.setCreateTime(new Date());
        convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());

        boolean status = analyzeService.save(convert);

        return R.data(status);
    }

    /**
     * 更新
     */
    @PostMapping("/update/{id}")
    @Transactional
    public R update(@PathVariable("id") Long id, @RequestBody @Valid AnalyzeDTO dto) {
        Analyze convert = BeanUtil.copy(dto, Analyze.class);
        convert.setId(id);
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());
        boolean status = analyzeService.updateById(convert);

        return R.data(status);
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    @Transactional
    public R delete(@RequestParam("ids") String ids) {
        boolean status = analyzeService.removeByIds(Func.toLongList(ids));
        return R.status(status);
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<AnalyzeVO>> page(Analyze vo, Query query) {
        LambdaQueryWrapper<Analyze> wrapper = new LambdaQueryWrapper<>();
        IPage<Analyze> page = analyzeService.page(Condition.getPage(query), wrapper);
        return R.data(AnalyzeWrapper.build().pageVO(page));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<AnalyzeVO> detail(@RequestParam("id") Long id) {
        AnalyzeVO vo = AnalyzeWrapper.build().entityVO(analyzeService.getById(id));
        return R.data(vo);
    }
}
