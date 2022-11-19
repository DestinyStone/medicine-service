package org.springblade.modules.medicine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.mapper.MedicineMapper;
import org.springblade.modules.medicine.service.MedicineService;
import org.springframework.stereotype.Service;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 13:57
 * @Description:
 */
@Service
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {
}
