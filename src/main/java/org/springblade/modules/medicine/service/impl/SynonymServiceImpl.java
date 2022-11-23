package org.springblade.modules.medicine.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.entity.SynonymItem;
import org.springblade.modules.medicine.mapper.SynonymMapper;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.SynonymItemService;
import org.springblade.modules.medicine.service.SynonymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:17
 * @Description:
 */
@Service
public class SynonymServiceImpl extends ServiceImpl<SynonymMapper, Synonym> implements SynonymService {

    @Autowired
    private SynonymItemService itemService;

    @Autowired
    private GrossDictService grossDictService;

    @Override
    public List<GrossDict> getSynonymDict(Collection<Long> dictIds) {
        LambdaQueryWrapper<SynonymItem> wrapper = new LambdaQueryWrapper<SynonymItem>();
        wrapper.in(SynonymItem::getDictId, dictIds);
        List<SynonymItem> list = itemService.list(wrapper);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<Long> synonymIds = list.stream().map(SynonymItem::getSynonymId).collect(Collectors.toList());
        LambdaQueryWrapper<SynonymItem> synonymWrapper = new LambdaQueryWrapper<SynonymItem>();
        synonymWrapper.in(SynonymItem::getSynonymId, synonymIds);
        List<SynonymItem> synonymAll = itemService.list(synonymWrapper);

        List<Long> dictAllIds = synonymAll.stream().map(SynonymItem::getDictId).collect(Collectors.toList());
        List<Long> searchDitcIds = dictAllIds.stream().filter(item -> !dictIds.contains(item)).collect(Collectors.toList());
        if (CollUtil.isEmpty(searchDitcIds)) {
            return new ArrayList<>();
        }
        return grossDictService.listByIds(searchDitcIds);
    }

    @Override
    public List<GrossDict> getSynonymDictByNames(Collection<String> names) {
        LambdaQueryWrapper<GrossDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GrossDict::getName, names);
        List<GrossDict> dists = grossDictService.list(wrapper);
        if (CollUtil.isEmpty(dists)) {
            return dists;
        }
        dists.addAll(getSynonymDict(dists.stream().map(GrossDict::getId).collect(Collectors.toSet())));
        return dists;
    }
}
