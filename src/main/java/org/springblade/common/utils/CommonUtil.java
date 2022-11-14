/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.common.utils;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.qiniu.util.Auth;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiaoxia
 * @Date: 2022/1/27 11:26
 * @Description: 通用工具类， 系统工具的统一维护
 */
public class CommonUtil {

	/**
	 * 获取当前用户ID
	 */
	public static Long getUserId() {
		BladeUser user = AuthUtil.getUser();
		return user.getUserId();
	}

	/**
	 * 获取用户部门
	 */
	public static Long getDeptId() {
		String deptId = AuthUtil.getDeptId();
		return firstLong(deptId);
	}

	/**
	 * 深拷贝
	 * @param entity 来源数据
	 * @param clazz 目标类
	 * @return
	 */
	public static <T> T copy(@Nullable Object entity, Class<T> clazz) {
		return BeanUtil.copy(entity, clazz);
	}

	/**
	 * 将字符串切割 切割默认符号 , 并获取第一个节点
	 */
	public static Long firstLong(String ids) {
		List<Long> result = Func.toLongList(ids);
		if (result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * 将字符串切割 切割默认符号 ，
	 * @param ids
	 * @return
	 */
	public static List<Long> toLongList(String ids) {
		return Func.toLongList(ids);
	}

	public static boolean isBlank(CharSequence str) {
		int length;
		if (str != null && (length = str.length()) != 0) {
			for(int i = 0; i < length; ++i) {
				if (!isBlankChar(str.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public static boolean isBlankChar(int c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234;
	}
}
