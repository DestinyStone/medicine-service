package org.springblade.modules.medicine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.Case;
import org.springblade.modules.medicine.mapper.CaseMapper;
import org.springblade.modules.medicine.service.CaseService;
import org.springframework.stereotype.Service;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:11
 * @Description:
 */
@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, Case> implements CaseService {
}
