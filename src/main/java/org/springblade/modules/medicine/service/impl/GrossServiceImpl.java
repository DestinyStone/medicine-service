package org.springblade.modules.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.mapper.GrossMapper;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.GrossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:35
 * @Description:
 */
@Service
public class GrossServiceImpl extends ServiceImpl<GrossMapper, Gross> implements GrossService {

    @Autowired
    private GrossDictService grossDictService;

    @Override
    @Transactional
    public synchronized void handler(Medicine medicine) {
        handler(Arrays.asList(medicine));
    }

    @Override
    @Transactional
    public void handler(List<Medicine> medicines) {
        removeByBelongIds(medicines.stream().map(Medicine::getId).collect(Collectors.toList()));

        List<Gross> collect = medicines.stream().map(item -> {
            return convert(item);
        }).flatMap(Collection::stream).collect(Collectors.toList());
        saveBatch(collect);

        grossDictService.rebuild();
    }

    @Override
    public void removeByBelongIds(List<Long> belongIds) {
        LambdaQueryWrapper<Gross> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Gross::getBelongId, belongIds);
        remove(wrapper);
        grossDictService.rebuild();
    }

    @Override
    public List<String> listByNames(List<String> names, Integer type) {
        return baseMapper.listByNames(names, type);
    }

    private List<Gross> convert(Medicine medicine) {
        String[] split = medicine.getPutUp().split("，");
        return Arrays.stream(split).map(item -> {
            Gross gross = new Gross();
            gross.setName(item.trim());
            gross.setBelongId(medicine.getId());
            gross.setBelongType(medicine.getType());
            return gross;
        }).collect(Collectors.toList());
    }
}
