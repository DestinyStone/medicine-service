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
package org.springblade.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.common.cache.SysCache;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.TreeNode;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.modules.system.entity.Dept;
import org.springblade.modules.system.mapper.DeptMapper;
import org.springblade.modules.system.service.IDeptService;
import org.springblade.modules.system.vo.DeptVO;
import org.springblade.modules.system.wrapper.DeptWrapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {
	private static final String TENANT_ID = "tenantId";
	private static final String PARENT_ID = "parentId";

	@Override
	public List<DeptVO> lazyList(String tenantId, Long parentId, Map<String, Object> param) {
		// 设置租户ID
		if (AuthUtil.isAdministrator()) {
			tenantId = StringPool.EMPTY;
		}
		String paramTenantId = Func.toStr(param.get(TENANT_ID));
		if (Func.isNotEmpty(paramTenantId) && AuthUtil.isAdministrator()) {
			tenantId = paramTenantId;
		}
		// 判断点击搜索但是没有查询条件的情况
		if (Func.isEmpty(param.get(PARENT_ID)) && param.size() == 1) {
			parentId = 0L;
		}

		Long deptId = Func.firstLong(AuthUtil.getDeptId());
		Dept dept = SysCache.getDept(deptId);
		boolean customer = false;

		// 判断数据权限控制,非超管角色只可看到本级及以下数据
		if (Func.toLong(parentId) == 0L && !AuthUtil.isAdministrator()) {
			customer = true;
			parentId = deptId;
		}
		// 判断点击搜索带有查询条件的情况
		if (Func.isEmpty(param.get(PARENT_ID)) && param.size() > 1 && Func.toLong(parentId) == 0L) {
			parentId = null;
		}
		List<DeptVO> list = baseMapper.lazyList(tenantId, parentId, param);

		// 加入根部门
		if(customer) {
			DeptVO deptVO = new DeptVO();
			deptVO.setId(dept.getId());
			deptVO.setParentId(dept.getParentId());
			deptVO.setDeptName(dept.getDeptName());
			deptVO.setFullName(dept.getFullName());
			deptVO.setAncestors(dept.getAncestors());
			deptVO.setDeptCategory(dept.getDeptCategory());
			deptVO.setSort(dept.getSort());
			deptVO.setRemark(dept.getRemark());
			deptVO.setIsDeleted(dept.getIsDeleted());
			deptVO.setHasChildren(!list.isEmpty());
			list.add(deptVO);
		}

		return list;
	}

	@Override
	public List<TreeNode> tree(String tenantId) {
		List<TreeNode> nodes = baseMapper.tree(tenantId);
		Long deptId = Long.parseLong(AuthUtil.getDeptId());
		if (AuthUtil.isAdministrator()) {
			return ForestNodeMerger.merge(nodes);
		} else {
			for(TreeNode node : nodes) {
				if(deptId.equals(node.getKey())) {
					return Arrays.asList(node);
				}
			}
			return null;
		}
	}

	@Override
	public List<TreeNode> lazyTree(String tenantId, Long parentId) {
		if (AuthUtil.isAdministrator()) {
			tenantId = StringPool.EMPTY;
		}
		Long deptId = Func.firstLong(AuthUtil.getDeptId());
		Dept dept = SysCache.getDept(deptId);
		boolean customer = false;
		if (Func.toLong(parentId) == 0L && !AuthUtil.isAdministrator()) {
			customer = true;
			parentId = deptId;
		}
		List<TreeNode> list = baseMapper.lazyTree(tenantId, parentId);

		// 加入根部门
		if(customer) {
			TreeNode treeNode = new TreeNode();
			treeNode.setKey(dept.getId());
			treeNode.setValue(dept.getId());
			treeNode.setId(dept.getId());
			treeNode.setParentId(dept.getParentId());
			treeNode.setTitle(dept.getDeptName());
			list.add(treeNode);
		}
		return ForestNodeMerger.merge(list);
	}

	@Override
	public String getDeptIds(String tenantId, String deptNames) {
		List<Dept> deptList = baseMapper.selectList(Wrappers.<Dept>query().lambda().eq(Dept::getTenantId, tenantId).in(Dept::getDeptName, Func.toStrList(deptNames)));
		if (deptList != null && deptList.size() > 0) {
			return deptList.stream().map(dept -> Func.toStr(dept.getId())).distinct().collect(Collectors.joining(","));
		}
		return null;
	}

	@Override
	public String getDeptIdsByFuzzy(String tenantId, String deptNames) {
		LambdaQueryWrapper<Dept> queryWrapper = Wrappers.<Dept>query().lambda().eq(Dept::getTenantId, tenantId);
		queryWrapper.and(wrapper -> {
			List<String> names = Func.toStrList(deptNames);
			names.forEach(name -> wrapper.like(Dept::getDeptName, name).or());
		});
		List<Dept> deptList = baseMapper.selectList(queryWrapper);
		if (deptList != null && deptList.size() > 0) {
			return deptList.stream().map(dept -> Func.toStr(dept.getId())).distinct().collect(Collectors.joining(","));
		}
		return null;
	}

	@Override
	public List<String> getDeptNames(String deptIds) {
		return baseMapper.getDeptNames(Func.toLongArray(deptIds));
	}

	@Override
	public List<Dept> getDeptChild(Long deptId) {
		return baseMapper.selectList(Wrappers.<Dept>query().lambda().like(Dept::getAncestors, deptId));
	}

	@Override
	public boolean removeDept(String ids) {
		Integer cnt = baseMapper.selectCount(Wrappers.<Dept>query().lambda().in(Dept::getParentId, Func.toLongList(ids)));
		if (cnt > 0) {
			throw new ServiceException("请先删除子节点!");
		}
		return removeByIds(Func.toLongList(ids));
	}

	@Override
	public boolean submit(Dept dept) {
		if (Func.isEmpty(dept.getParentId())) {
			dept.setTenantId(AuthUtil.getTenantId());
			dept.setParentId(BladeConstant.TOP_PARENT_ID);
			dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
		}
		if (dept.getParentId() > 0) {
			Dept parent = getById(dept.getParentId());
			if (Func.toLong(dept.getParentId()) == Func.toLong(dept.getId())) {
				throw new ServiceException("父节点不可选择自身!");
			}
			dept.setTenantId(parent.getTenantId());
			String ancestors = parent.getAncestors() + StringPool.COMMA + dept.getParentId();
			dept.setAncestors(ancestors);
		}
		dept.setIsDeleted(BladeConstant.DB_NOT_DELETED);
		return saveOrUpdate(dept);
	}

	@Override
	public List<DeptVO> search(String deptName, Long parentId) {
		LambdaQueryWrapper<Dept> queryWrapper = Wrappers.<Dept>query().lambda();
		if (Func.isNotEmpty(deptName)) {
			queryWrapper.like(Dept::getDeptName, deptName);
		}
		if (Func.isNotEmpty(parentId) && parentId > 0L) {
			queryWrapper.eq(Dept::getParentId, parentId);
		}
		List<Dept> deptList = baseMapper.selectList(queryWrapper);
		return DeptWrapper.build().listNodeVO(deptList);
	}

	@Override
	public boolean judgeAuth(Long deptId, BladeUser bladeUser) {
		Map<Long, Long> parentMap = parentMap();
		Long iDeptId = Long.parseLong(bladeUser.getDeptId());
		Long parentId = deptId;
		while (parentId != 0 && parentId != null) {
			parentId = parentMap.get(parentId);
			if(parentId.equals(iDeptId)) {

			}
		}
		return false;
	}


	@Override
	public Map<Long, Long> parentMap() {
		Map<Long, Long> map = new HashMap<>(16);
		List<Map<String, Long>> result = baseMapper.parentMap();
		for(Map<String, Long> item : result) {
			map.put(item.get("id"), item.get("parentId"));
		}
		return map;
	}

}
