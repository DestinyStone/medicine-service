package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.common.constant.ApiConstant;
import org.springblade.common.utils.WordUtil;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.dto.MedicineDTO;
import org.springblade.modules.medicine.entity.Gross;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.service.GrossService;
import org.springblade.modules.medicine.service.MedicineService;
import org.springblade.modules.medicine.service.SynonymService;
import org.springblade.modules.medicine.vo.MedicineScoreVO;
import org.springblade.modules.medicine.vo.MedicineVO;
import org.springblade.modules.medicine.wrapper.MedicineWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 13:58
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/medicine")
@AllArgsConstructor
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private GrossService grossService;

    @Autowired
    private SynonymService synonymService;

    /**
     * 上传文件
     */
    @SneakyThrows
    @PostMapping("/upload")
    @Transactional
    public R upload(MultipartFile file, Integer type) {
        List<WordUtil.Template> templates = WordUtil.readContentParseObject(file.getInputStream());
        ArrayList<Medicine> medicines = new ArrayList<>();
        for (WordUtil.Template template : templates) {
            Medicine convert = BeanUtil.copy(template, Medicine.class);
            convert.setCreateUser(AuthUtil.getUserId());
            convert.setCreateTime(new Date());
            convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
            convert.setUpdateUser(AuthUtil.getUserId());
            convert.setUpdateTime(new Date());
            convert.setType(type);
            medicines.add(convert);
        }
        boolean status = medicineService.saveBatch(medicines);
        grossService.handler(medicines);
        return R.status(status);
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<MedicineVO>> page(MedicineVO vo, Query query) {
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(vo.getType() != null, Medicine::getType, vo.getType());
        wrapper.like(StringUtil.isNotBlank(vo.getName()), Medicine::getName, vo.getName());
        IPage<Medicine> page = medicineService.page(Condition.getPage(query), wrapper);
        return R.data(MedicineWrapper.build().pageVO(page));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MedicineVO> detail(@RequestParam("id") Long id) {
        MedicineVO vo = MedicineWrapper.build().entityVO(medicineService.getById(id));
        return R.data(vo);
    }
    /**
     * 删除
     */
    @GetMapping("/delete")
    @Transactional
    public R delete(@RequestParam("ids") String ids) {
        boolean status = medicineService.removeByIds(Func.toLongList(ids));
        grossService.removeByBelongIds(Func.toLongList(ids));
        return R.status(status);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save(@RequestBody @Valid MedicineDTO medicineDTO) {
        Medicine convert = BeanUtil.copy(medicineDTO, Medicine.class);
        convert.setCreateUser(AuthUtil.getUserId());
        convert.setCreateTime(new Date());
        convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());

        boolean status = medicineService.save(convert);
        grossService.handler(convert);
        return R.data(status);
    }

    /**
     * 更新
     */
    @PostMapping("/update/{id}")
    @Transactional
    public R update(@PathVariable("id") Long id, @RequestBody @Valid MedicineDTO medicineDTO) {
        Medicine convert = BeanUtil.copy(medicineDTO, Medicine.class);
        convert.setId(id);
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());
        boolean status = medicineService.updateById(convert);
        grossService.handler(convert);
        return R.data(status);
    }

    /**
     * 名称
     */
    @PostMapping("/list/gross/{type}")
    @Transactional
    public R<List<MedicineScoreVO>> listGross(@RequestBody List<String> names, @PathVariable("type") Integer type) {
        if (CollUtil.isEmpty(names)) {
            return R.data(new ArrayList<>());
        }

        // 加载同义词
        List<GrossDict> dicts = synonymService.getSynonymDictByNames(names);
        if (CollUtil.isEmpty(dicts)) {
            return R.data(new ArrayList<>());
        }

        // 找到所有包含的症状id
        LambdaQueryWrapper<Gross> grossWrapper = new LambdaQueryWrapper<>();
        grossWrapper.in(Gross::getName, dicts.stream().map(GrossDict::getName).collect(Collectors.toSet()))
                .eq(Gross::getBelongType, type);
        List<Gross> grossList = grossService.list(grossWrapper);

        if (CollUtil.isEmpty(grossList)) {
            return R.data(new ArrayList<>());
        }

        List<Medicine> list = medicineService.listByIds(grossList.stream().map(Gross::getBelongId).collect(Collectors.toSet()));
        List<MedicineScoreVO> resultVO = list.stream().map(item -> {
            MedicineScoreVO result = BeanUtil.copy(item, MedicineScoreVO.class);
            String[] putUps = item.getPutUp().split("，");
            Set<String> existNames = Arrays.stream(putUps).filter(filterItem -> names.contains(filterItem)).collect(Collectors.toSet());
            result.setScore(existNames.size() / new Double(putUps.length));
            return result;
        }).collect(Collectors.toList());
        resultVO = resultVO.stream().sorted((x, y) -> {
            if (Objects.equals(x.getScore(), y.getScore())) {
                return 0;
            }
            return x.getScore() < y.getScore() ? 1 : -1;
        }).limit(20).collect(Collectors.toList());

        return R.data(resultVO);
    }

}
