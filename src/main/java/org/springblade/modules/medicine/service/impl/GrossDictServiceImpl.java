package org.springblade.modules.medicine.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.mapper.GrossDictMapper;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.GrossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.text.CollatorUtilities;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 17:06
 * @Description:
 */
@Service
public class GrossDictServiceImpl extends ServiceImpl<GrossDictMapper, GrossDict> implements GrossDictService {

    @Autowired
    private GrossService grossService;

    @Override
    public void rebuild() {
        List<String> resource = grossService.list().stream().map(Gross::getName).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(resource)) {
            remove(new LambdaQueryWrapper<>());
            return;
        }

        List<GrossDict> list = list();

        // 查询存在的数据
        List<GrossDict> existList = list.stream().filter(item -> resource.indexOf(item.getName()) != -1).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(existList)) {
            remove(new LambdaQueryWrapper<>());
        }else {
            LambdaQueryWrapper<GrossDict> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.notIn(GrossDict::getId, existList.stream().map(GrossDict::getId).collect(Collectors.toList()));
        }
        // 填充额外的数据
        List<String> collect = list.stream().map(GrossDict::getName).collect(Collectors.toList());
        List<String> result = resource.stream().filter(item -> collect.indexOf(item) == -1).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(resource)) {
            saveBatch(result.stream().map(item -> {
                GrossDict grossDict = new GrossDict();
                grossDict.setName(item);
                return grossDict;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    public List<GrossDict> listByNames(List<String> names) {
        LambdaQueryWrapper<GrossDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GrossDict::getName, names);
        return list(wrapper);
    }
}
