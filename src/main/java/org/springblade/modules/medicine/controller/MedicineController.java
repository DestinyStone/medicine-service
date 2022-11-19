package org.springblade.modules.medicine.controller;

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
import org.springblade.modules.medicine.entity.Medicine;
import org.springblade.modules.medicine.service.GrossService;
import org.springblade.modules.medicine.service.MedicineService;
import org.springblade.modules.medicine.vo.MedicineVO;
import org.springblade.modules.medicine.wrapper.MedicineWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

}
