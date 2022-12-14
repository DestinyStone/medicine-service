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
package org.springblade.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.node.TreeNode;
import org.springblade.modules.system.entity.Dept;
import org.springblade.modules.system.vo.DeptVO;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IDeptService extends IService<Dept> {

	/**
	 * 懒加载部门列表
	 *
	 * @param tenantId
	 * @param parentId
	 * @param param
	 * @return
	 */
	List<DeptVO> lazyList(String tenantId, Long parentId, Map<String, Object> param);

	/**
	 * 树形结构
	 *
	 * @param tenantId
	 * @return
	 */
	List<TreeNode> tree(String tenantId);

	/**
	 * 懒加载树形结构
	 *
	 * @param tenantId
	 * @param parentId
	 * @return
	 */
	List<TreeNode> lazyTree(String tenantId, Long parentId);

	/**
	 * 获取部门ID
	 *
	 * @param tenantId
	 * @param deptNames
	 * @return
	 */
	String getDeptIds(String tenantId, String deptNames);

	/**
	 * 获取部门ID
	 *
	 * @param tenantId
	 * @param deptNames
	 * @return
	 */
	String getDeptIdsByFuzzy(String tenantId, String deptNames);

	/**
	 * 获取部门名
	 *
	 * @param deptIds
	 * @return
	 */
	List<String> getDeptNames(String deptIds);

	/**
	 * 获取子部门ID
	 *
	 * @param deptId
	 * @return
	 */
	List<Dept> getDeptChild(Long deptId);

	/**
	 * 删除部门
	 *
	 * @param ids
	 * @return
	 */
	boolean removeDept(String ids);

	/**
	 * 提交
	 *
	 * @param dept
	 * @return
	 */
	boolean submit(Dept dept);

	/**
	 * 部门信息查询
	 *
	 * @param deptName
	 * @param parentId
	 * @return
	 */
	List<DeptVO> search(String deptName, Long parentId);

	/**
	 * 判断此用户是否在此部门下
	 * @param deptId 部门ID
	 * @param bladeUser 用户
	 * @return
	 */
	boolean judgeAuth(Long deptId, BladeUser bladeUser);

	/**
	 * 获取父子树ID关联的Map Key-id,Value-parentId
	 * @return
	 */
	Map<Long, Long> parentMap();

}
