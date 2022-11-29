package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.GrossService;
import org.springblade.modules.medicine.service.SynonymService;
import org.springblade.modules.medicine.vo.GrossDictSynonymsVO;
import org.springblade.modules.medicine.vo.GrossDictVO;
import org.springblade.modules.medicine.wrapper.GrossDictWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private SynonymService synonymService;
    private List<GrossDict> synonymDict;

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

    /**
     * 查询字典
     */
    @GetMapping("/list")
    public R<List<GrossDictVO>> list(@RequestParam(value = "excludeIds", required = false) String ids) {
        LambdaQueryWrapper<GrossDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(StringUtil.isNotBlank(ids), GrossDict::getId, Func.toLongList(ids));
        List<GrossDict> list = grossDictService.list(wrapper);
        return R.data(GrossDictWrapper.build().listVO(list));
    }

    /**
     * 查询字典
     */
    @PostMapping("/list/with/{type}")
    public R<List<GrossDictSynonymsVO>> listWith(@RequestBody List<String> names, @PathVariable("type") Integer type) {
        if (CollUtil.isEmpty(names)) {
            return R.data(new ArrayList<>());
        }

        names = grossService.listByNames(names, type);
        if (CollUtil.isEmpty(names)) {
            return R.data(new ArrayList<>());
        }
        Map<String, GrossDict> collect = grossDictService.listByNames(names).stream().collect(Collectors.toMap(GrossDict::getName, Function.identity()));
        ArrayList<GrossDict> result = new ArrayList<>();
        for (String name : names) {
            GrossDict grossDict = collect.get(name);
            if (result != null) {
                result.add(grossDict);
            }
        }

        return R.data(convert(result, 0));
    }

    /**
     * 查询字典
     */
    @GetMapping("/list/synonyms")
    public R<List<GrossDictSynonymsVO>> listSynonyms(String name) {
        ArrayList<GrossDictSynonymsVO> result = new ArrayList<>();

        LambdaQueryWrapper<GrossDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtil.isNotBlank(name), GrossDict::getName, name);
        List<GrossDict> list = grossDictService.list(wrapper);
        result.addAll(convert(list, 0));

        if (CollUtil.isEmpty(list)) {
            return R.data(result);
        }
        Set<Long> dictIds = list.stream().map(GrossDict::getId).collect(Collectors.toSet());
        List<GrossDict> synonymDict = synonymService.getSynonymDict(dictIds);
        result.addAll(convert(synonymDict, 1));
        return R.data(result);
    }

    private List<GrossDictSynonymsVO> convert(List<GrossDict> list, Integer type) {
        return list.stream().map(item -> {
            GrossDictSynonymsVO vo = new GrossDictSynonymsVO();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setType(type);
            return vo;
        }).collect(Collectors.toList());
    }
}
