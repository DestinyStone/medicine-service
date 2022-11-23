package org.springblade.modules.medicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.medicine.entity.Gross;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 16:35
 * @Description:
 */
public interface GrossMapper extends BaseMapper<Gross> {
    List<String> listByNames(@Param("names") List<String> names, @Param("type") Integer type);
}
