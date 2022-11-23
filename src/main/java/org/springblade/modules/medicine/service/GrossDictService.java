package org.springblade.modules.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.medicine.entity.GrossDict;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 17:05
 * @Description:
 */
public interface GrossDictService extends IService<GrossDict> {
    /**
     * 重新构建字典
     */
    void rebuild();

    /**
     * 根据名称查询
     * @param names
     * @return
     */
    List<GrossDict> listByNames(List<String> names);
}
