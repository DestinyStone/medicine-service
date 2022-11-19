package org.springblade.modules.medicine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.mapper.SynonymMapper;
import org.springblade.modules.medicine.service.SynonymService;
import org.springframework.stereotype.Service;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:17
 * @Description:
 */
@Service
public class SynonymServiceImpl extends ServiceImpl<SynonymMapper, Synonym> implements SynonymService {
}
