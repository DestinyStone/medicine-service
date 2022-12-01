package org.springblade.modules.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.medicine.entity.BusCode;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/1/28 11:09
 * @Description:
 */
public interface BusCodeService extends IService<BusCode> {

	/**
	 * 获取唯一码
	 * @param flag
	 * @return
	 */
	String getCode(String flag);

	String getCode(String separate, String flag);

	/**
	 * 刷新编码
	 * @param flag
	 * @param code
	 * @return
	 */
	Boolean reflush(String flag, Long code);

}
