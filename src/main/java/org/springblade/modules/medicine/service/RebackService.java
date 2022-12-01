package org.springblade.modules.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.medicine.entity.Reback;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 00:35
 * @Description:
 */
public interface RebackService extends IService<Reback> {
    boolean saveReback();

    boolean reback(Long id);
}
