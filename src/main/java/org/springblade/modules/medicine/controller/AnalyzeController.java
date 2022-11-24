package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.dto.AnalyzeDTO;
import org.springblade.modules.medicine.entity.Analyze;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.service.AnalyzeService;
import org.springblade.modules.medicine.vo.AnalyzeDetailVO;
import org.springblade.modules.medicine.vo.AnalyzeVO;
import org.springblade.modules.medicine.wrapper.AnalyzeWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
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
        Analyze convert = dto.to();
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
        Analyze convert = dto.to();
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
        wrapper.like(StringUtil.isNotBlank(vo.getName()), Analyze::getName, vo.getName());
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

    /**
     * 详情
     */
    @GetMapping("/detail/all")
    public R<List<AnalyzeDetailVO>> detail(@RequestParam("names") String names) {
        List<AnalyzeDetailVO> vo = new ArrayList<>();;
        List<String> nameList = Arrays.stream(names.split("，")).collect(Collectors.toList());
        nameList = handlerNames(nameList);

        if (CollUtil.isEmpty(nameList)) {
            throw new ServiceException("处方分析异常， 处方:" + names);
        }

        LambdaQueryWrapper<Analyze> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Analyze::getName, nameList);
        Map<String, Analyze> collect = analyzeService.list(wrapper).stream().collect(Collectors.toMap(Analyze::getName, Function.identity()));

        for (String name : nameList) {
            Analyze analyze = collect.get(name);
            if (analyze == null) {
                AnalyzeDetailVO item = new AnalyzeDetailVO();
                item.setName(name);
                item.setIsExist(0);
                vo.add(item);
            }
            if (analyze != null) {
                AnalyzeDetailVO item = BeanUtil.copyProperties(analyze, AnalyzeDetailVO.class);
                item.setIsExist(1);
                vo.add(item);
            }
        }
        return R.data(vo);
    }

    private List<String> handlerNames(List<String> names) {
        return names.stream().map(item -> {
            return handlerName(item);
        }).filter(item -> {
            return item != null && !Objects.equals("", item);
        }).collect(Collectors.toList());
    }

    private String handlerName(String name) {
        name = name.replaceAll("\n", "");
        int substringIndex = 0;
        for (int i = 0; i < name.length(); i++) {
            try {
                new Integer(name.charAt(i) + "");
                break;
            }catch (Exception e) {
                substringIndex = i;
            }
        }
        return name.substring(0, substringIndex + 1);
    }
}
