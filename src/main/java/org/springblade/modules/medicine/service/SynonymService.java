package org.springblade.modules.medicine.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Synonym;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:16
 * @Description:
 */
public interface SynonymService extends IService<Synonym> {
    /**
     * 获取同义词
     * @param dictIds
     * @return
     */
    List<GrossDict> getSynonymDict(Collection<Long> dictIds);

    /**
     * 获取同义词
     * @param names
     * @return
     */
    List<GrossDict> getSynonymDictByNames(Collection<String> names);
}
