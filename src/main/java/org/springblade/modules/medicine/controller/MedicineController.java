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
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.service.GrossService;
import org.springblade.modules.medicine.service.MedicineService;
import org.springblade.modules.medicine.service.SynonymService;
import org.springblade.modules.medicine.util.AnalyzeUtil;
import org.springblade.modules.medicine.vo.ComponentVO;
import org.springblade.modules.medicine.vo.MedicineComponentVO;
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

    @Autowired
    private GrossDictService dictService;

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
            convert = handler(convert);
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
        wrapper.and(StringUtil.isNotBlank(vo.getName()), item -> {
            item.like(Medicine::getName, vo.getName())
                    .or()
                    .like(Medicine::getPutUp, vo.getName());
        });
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
        convert = handler(convert);
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
        convert = handler(convert);
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
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (CollUtil.isEmpty(names)) {
            return R.data(new ArrayList<>());
        }

        // 加载同义词
        List<GrossDict> dicts = synonymService.getSynonymDictByNames(names);
        if (CollUtil.isEmpty(dicts)) {
            return R.data(new ArrayList<>());
        }

        // 找到所有包含的症状id
        Set<String> dictNames = dicts.stream().map(GrossDict::getName).collect(Collectors.toSet());
        LambdaQueryWrapper<Gross> grossWrapper = new LambdaQueryWrapper<>();
        grossWrapper.in(Gross::getName, dictNames)
                .eq(Gross::getBelongType, type);
        List<Gross> grossList = grossService.list(grossWrapper);

        if (CollUtil.isEmpty(grossList)) {
            return R.data(new ArrayList<>());
        }

        List<Medicine> list = medicineService.listByIds(grossList.stream().map(Gross::getBelongId).collect(Collectors.toSet()));
        List<MedicineScoreVO> resultVO = list.stream().map(item -> {
            MedicineScoreVO result = BeanUtil.copy(item, MedicineScoreVO.class);
            String[] putUps = item.getPutUp().split("，");
            Set<String> existNames = Arrays.stream(putUps).filter(filterItem -> dictNames.contains(filterItem)).collect(Collectors.toSet());
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

    /**
     * 组方
     */
    @PostMapping("/component")
    public R<MedicineComponentVO> component(@RequestBody ComponentVO componentVO) {
        MedicineComponentVO vo = new MedicineComponentVO();
        if (componentVO.getIds().size() == 1) {
            Medicine medicine = medicineService.getById(componentVO.getIds().get(0));
            vo.setComponent(medicine.getSolve());
            return R.data(vo);
        }

        // 超过50%的集合
        List<Medicine> result = new ArrayList<>();

        // 病人症状
        List<String> dictNames = dictService.listByIds(componentVO.getDictIds()).stream().map(GrossDict::getName).collect(Collectors.toList());

        for (Long id : componentVO.getIds()) {
            Medicine medicine = medicineService.getById(id);
            List<String> putUps = Arrays.stream(medicine.getPutUp().split("，")).map(AnalyzeUtil::handlerName).collect(Collectors.toList());

            // 取交集
            List<String> intersection = dictNames.stream().filter(item -> putUps.contains(item)).collect(Collectors.toList());

            if (intersection.size() / new Double(putUps.size()) >= 0.5) {
                result.add(medicine);
            }
        }

        if (result.size() == 0) {
            Medicine medicine = medicineService.getById(componentVO.getIds().get(0));
            vo.setComponent(medicine.getSolve());
            vo.setDialectical(medicine.getName());
            return R.data(vo);
        }

        if (result.size() == 1) {
            Medicine medicine = result.get(0);
            vo.setComponent(medicine.getSolve());
            vo.setDialectical(medicine.getName());
            return R.data(vo);
        }
        // 药材和总量
        HashMap<String, Long> countMap = new HashMap<>();
        // 药材和出现的次数
        HashMap<String, Long> numberMap = new HashMap<>();
        for (Long id : componentVO.getIds()) {
            Medicine medicine = medicineService.getById(id);
            List<String> solve = Arrays.stream(medicine.getSolve().split("，")).collect(Collectors.toList());
            for (String solveItem : solve) {
                String solveName = AnalyzeUtil.handlerName(solveItem);
                long number = AnalyzeUtil.handlerNumber(solveItem);
                Long currentCount = countMap.get(solveName);
                if (currentCount == null) {
                    countMap.put(solveName, new Long(number));
                }else {
                    countMap.replace(solveName, currentCount + number);
                }

                Long currentNumber = numberMap.get(solveName);
                if (currentNumber == null) {
                    numberMap.put(solveName, 1L);
                }else {
                    numberMap.replace(solveName, currentNumber + 1);
                }

            }
        }
        ArrayList<String> resultName = new ArrayList<>();
        countMap.forEach((key, value) -> {
            Long number = numberMap.get(key);
            long count = Math.round(value / new Double(number));
            resultName.add(key + count);
        });
        vo.setComponent(CollUtil.join(resultName, "，"));
        return R.data(vo);
        

//        if (ids.size() == 2) {
//            Medicine medicine1 = medicineService.getById(ids.get(0));
//            Medicine medicine2 = medicineService.getById(ids.get(1));
//            // 获取交集
//            List<String> solve1 = Arrays.stream(medicine1.getSolve().split("，")).map(AnalyzeUtil::handlerName).collect(Collectors.toList());
//            List<String> solve2 = Arrays.stream(medicine2.getSolve().split("，")).map(AnalyzeUtil::handlerName).collect(Collectors.toList());
//            List<String> intersection = solve1.stream().filter(item -> solve2.contains(item)).collect(Collectors.toList());
//
//            if (intersection.size() / new Double(solve1.size()) >= 0.5 && intersection.size() / new Double(solve2.size()) >= 0.5) {
//                Map<String, String> solve1Map = Arrays.stream(medicine1.getSolve().split("，")).collect(Collectors.toMap(AnalyzeUtil::handlerName, Function.identity()));
//                Map<String, String> solve2Map = Arrays.stream(medicine2.getSolve().split("，")).collect(Collectors.toMap(AnalyzeUtil::handlerName, Function.identity()));
//                ArrayList<String> result = new ArrayList<>();
//                for (String item : intersection) {
//                    String solve1Item = solve1Map.get(item);
//                    String solve2Item = solve2Map.get(item);
//                    long number = AnalyzeUtil.aroundNumber(solve1Item, solve2Item);
//                    result.add(item + number);
//                }
//
//                vo.setComponent(CollUtil.join(result, "，"));
//                return R.data(vo);
//            }
//            vo.setComponent(medicine1.getSolve());
//            vo.setDialectical(medicine1.getName());
//        }
//
//        if (ids.size() == 3) {
//            Medicine medicine1 = medicineService.getById(ids.get(0));
//            Medicine medicine2 = medicineService.getById(ids.get(1));
//            Medicine medicine3 = medicineService.getById(ids.get(3));
//
//            // 超过50的集合
//
//
//        }
//        return R.data(vo);
    }


    private Medicine handler(Medicine medicine) {
        medicine.setPutUp(medicine.getPutUp().replaceAll("\n", "").replaceAll(",", "，"));
        medicine.setSolve(medicine.getSolve().replaceAll("\n", "").replaceAll(",", "，"));
        return medicine;
    }
}
