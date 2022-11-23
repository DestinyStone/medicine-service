package org.springblade.modules.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.Medicine;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:34
 * @Description:
 */
public interface GrossService extends IService<Gross> {
    void handler(Medicine convert);

    void handler(List<Medicine> convert);

    void removeByBelongIds(List<Long> toLongList);

    List<String> listByNames(List<String> names, Integer type);
}
