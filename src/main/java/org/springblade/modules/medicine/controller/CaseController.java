package org.springblade.modules.medicine.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.medicine.dto.CaseDTO;
import org.springblade.modules.medicine.entity.Case;
import org.springblade.modules.medicine.entity.GrossDict;
import org.springblade.modules.medicine.service.BusCodeService;
import org.springblade.modules.medicine.service.CaseService;
import org.springblade.modules.medicine.service.GrossDictService;
import org.springblade.modules.medicine.util.CaseDownLoadUtils;
import org.springblade.modules.medicine.vo.CaseVO;
import org.springblade.modules.medicine.wrapper.CaseWrapper;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/24 08:11
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/case")
@AllArgsConstructor
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private GrossDictService dictService;

    @Autowired
    private BusCodeService codeService;

    @Autowired
    private IUserService userService;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

    /**
     * 新增
     */
    @PostMapping("/save")
    @Transactional
    public R save(@RequestBody @Valid CaseDTO dto) {
        Case convert = dto.to();
        convert.setCreateUser(AuthUtil.getUserId());
        convert.setCreateTime(new Date());
        convert.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        convert.setUpdateUser(AuthUtil.getUserId());
        convert.setUpdateTime(new Date());

        boolean status = caseService.save(convert);

        return R.data(status);
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<IPage<CaseVO>> page(CaseVO vo, Query query) {

        List<Long> userIds = new ArrayList<>();
        if (StrUtil.isNotBlank(vo.getCreateUserName())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(User::getName, vo.getCreateUserName());
            List<User> list = userService.list(wrapper);
            if (CollUtil.isEmpty(list)) {
                return R.data(Condition.getPage(query));
            }
            userIds = list.stream().map(User::getId).collect(Collectors.toList());
        }


        LambdaQueryWrapper<Case> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(vo.getCode()), Case::getCode, vo.getCode())
            .in(CollUtil.isNotEmpty(userIds), Case::getCreateUser, userIds)
            .like(StrUtil.isNotBlank(vo.getName()), Case::getName, vo.getName());

        IPage<Case> page = caseService.page(Condition.getPage(query), wrapper);
        return R.data(CaseWrapper.build().pageVO(page));
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    @Transactional
    public R delete(@RequestParam("ids") String ids) {
        boolean status = caseService.removeByIds(Func.toLongList(ids));
        return R.status(status);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<CaseVO> detail(@RequestParam("id") Long id) {
        CaseVO vo = CaseWrapper.build().entityVO(caseService.getById(id));
        if (StrUtil.isNotBlank(vo.getMedicineIds())) {
            List<GrossDict> medicines = dictService.listByIds(Func.toLongList(vo.getMedicineIds()));
            vo.setMedicineList(medicines);
        }
        return R.data(vo);
    }

    /**
     * 详情
     */
    @GetMapping("/detail/code")
    public R<CaseVO> detailByCode(@RequestParam("code") String code) {
        LambdaQueryWrapper<Case> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Case::getCode, code);
        CaseVO vo = CaseWrapper.build().entityVO(caseService.getOne(wrapper));
        if (StrUtil.isNotBlank(vo.getMedicineIds())) {
            List<GrossDict> medicines = dictService.listByIds(Func.toLongList(vo.getMedicineIds()));
            vo.setMedicineList(medicines);
        }
        return R.data(vo);
    }

    @GetMapping("/list")
    public R<List<CaseVO>> list(CaseVO vo) {
        LambdaQueryWrapper<Case> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(vo.getName()), Case::getName, vo.getName())
                .or()
                .like(StrUtil.isNotBlank(vo.getCode()), Case::getCode, vo.getCode());
        return R.data(CaseWrapper.build().listVO(caseService.list(wrapper)));
    }

    /**
     * 获取编号
     */
    @GetMapping("/code")
    public R<String> code() {
        Date date = new Date();
        String format = this.format.format(date);
        String code = formatCode(codeService.getCode(format));
        return R.data(format + code);
    }

    /**
     * 下载病例
     * @return
     */
    @GetMapping("/download")
    public void download(@RequestParam("id") Long id, HttpServletResponse response) {
        CaseVO vo = CaseWrapper.build().entityVO(caseService.getById(id));
        CaseDownLoadUtils.downLoad(vo, response);
    }

    /**
     * 下载病例
     * @return
     */
    @PostMapping("/download/obj")
    public void downloadObj(@RequestBody CaseDTO dto, HttpServletResponse response) {
        Case entity = BeanUtil.copy(dto, Case.class);
        CaseVO vo = CaseWrapper.build().entityVO(entity);
        CaseDownLoadUtils.downLoad(vo, response);
    }

    private String formatCode(String code) {
        Integer codeInteger = new Integer(code);
        if (codeInteger < 10) {
            return "000" + codeInteger;
        }

        if (codeInteger < 100) {
            return "00" + codeInteger;
        }

        if (codeInteger < 1000) {
            return "0" + codeInteger;
        }
        return codeInteger + "";
    }
}
