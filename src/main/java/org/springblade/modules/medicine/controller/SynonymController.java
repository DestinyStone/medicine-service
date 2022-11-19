package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.dto.MedicineDTO;
import org.springblade.modules.medicine.dto.SynonymDTO;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.entity.Synonym;
import org.springblade.modules.medicine.entity.SynonymItem;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.SynonymItemService;
import org.springblade.modules.medicine.service.SynonymService;
import org.springblade.modules.medicine.vo.MedicineVO;
import org.springblade.modules.medicine.vo.SynonymVO;
import org.springblade.modules.medicine.wrapper.GrossDictWrapper;
import org.springblade.modules.medicine.wrapper.GrossWrapper;
import org.springblade.modules.medicine.wrapper.MedicineWrapper;
import org.springblade.modules.medicine.wrapper.SynonymWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 19:19
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/synonym")
@AllArgsConstructor
public class SynonymController {

    @Autowired
    private SynonymService synonymService;

    @Autowired
    private SynonymItemService synonymItemService;

    @Autowired
    private GrossDictService grossDictService;


    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save(@RequestBody @Valid SynonymDTO dto) {
        Synonym convert = BeanUtil.copy(dto, Synonym.class);
        convert.setCreateUser(AuthUtil.getUserId());
        convert.setCreateTime(new Date());
        convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());

        boolean status = synonymService.save(convert);

        if (CollUtil.isNotEmpty(dto.getDictIds())) {
            List<SynonymItem> collect = dto.getDictIds().stream().map(item -> {
                SynonymItem synonymItem = new SynonymItem();
                synonymItem.setSynonymId(convert.getId());
                synonymItem.setDictId(item);
                return synonymItem;
            }).collect(Collectors.toList());
            synonymItemService.saveBatch(collect);
        }

        return R.data(status);
    }

    /**
     * 更新
     */
    @PostMapping("/update/{id}")
    @Transactional
    public R update(@PathVariable("id") Long id, @RequestBody @Valid SynonymDTO dto) {
        Synonym convert = BeanUtil.copy(dto, Synonym.class);
        convert.setId(id);
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());
        boolean status = synonymService.updateById(convert);

        LambdaQueryWrapper<SynonymItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SynonymItem::getSynonymId, id);
        synonymItemService.remove(wrapper);
        if (CollUtil.isNotEmpty(dto.getDictIds())) {
            List<SynonymItem> collect = dto.getDictIds().stream().map(item -> {
                SynonymItem synonymItem = new SynonymItem();
                synonymItem.setSynonymId(convert.getId());
                synonymItem.setDictId(item);
                return synonymItem;
            }).collect(Collectors.toList());
            synonymItemService.saveBatch(collect);
        }
        return R.data(status);
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    @Transactional
    public R delete(@RequestParam("ids") String ids) {
        boolean status = synonymService.removeByIds(Func.toLongList(ids));

        LambdaQueryWrapper<SynonymItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SynonymItem::getSynonymId, Func.toLongList(ids));
        synonymItemService.remove(wrapper);
        return R.status(status);
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<SynonymVO>> page(SynonymVO vo, Query query) {
        LambdaQueryWrapper<Synonym> wrapper = new LambdaQueryWrapper<>();
        IPage<Synonym> page = synonymService.page(Condition.getPage(query), wrapper);
        if (CollUtil.isEmpty(page.getRecords())) {
            return R.data(SynonymWrapper.build().pageVO(page));
        }
        IPage<SynonymVO> pageVO = SynonymWrapper.build().pageVO(page);
        List<SynonymVO> records = pageVO.getRecords();
        LambdaQueryWrapper<SynonymItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(SynonymItem::getSynonymId, records.stream().map(Synonym::getId).collect(Collectors.toList()));
        List<SynonymItem> list = synonymItemService.list(itemWrapper);

        Map<Long, List<SynonymItem>> group = list.stream().collect(Collectors.groupingBy(SynonymItem::getSynonymId));


        Map<Long, GrossDict> collect = grossDictService.listByIds(list.stream().map(SynonymItem::getDictId)
                .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(GrossDict::getId, Function.identity()));

        for (SynonymVO record : records) {
            List<SynonymItem> synonymItems = group.get(record.getId());
            if (CollUtil.isEmpty(synonymItems)) {
                continue;
            }
            ArrayList<GrossDict> grossDicts = new ArrayList<>();
            for (SynonymItem item : synonymItems) {
                GrossDict grossDict = collect.get(item.getDictId());
                if (grossDict != null) {
                    grossDicts.add(grossDict);
                }
            }
            record.setDictList(GrossDictWrapper.build().listVO(grossDicts));
        }


        return R.data(pageVO);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<SynonymVO> detail(@RequestParam("id") Long id) {
        SynonymVO vo = SynonymWrapper.build().entityVO(synonymService.getById(id));

        LambdaQueryWrapper<SynonymItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SynonymItem::getSynonymId, id);
        List<SynonymItem> list = synonymItemService.list(wrapper);
        if (CollUtil.isEmpty(list)) {
            return R.data(vo);
        }

        List<GrossDict> grossDicts = grossDictService.listByIds(list.stream().map(SynonymItem::getDictId).collect(Collectors.toList()));
        vo.setDictList(GrossDictWrapper.build().listVO(grossDicts));

        return R.data(vo);
    }
}
