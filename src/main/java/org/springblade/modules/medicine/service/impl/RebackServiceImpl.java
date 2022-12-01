package org.springblade.modules.medicine.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.common.utils.FileUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.medicine.dto.RebackDTO;
import org.springblade.modules.medicine.entity.Reback;
import org.springblade.modules.medicine.mapper.RebackMapper;
import org.springblade.modules.medicine.service.*;
import org.springblade.modules.system.entity.Menu;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IMenuService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: DestinyStone
 * @Date: 2022/12/2 00:35
 * @Description:
 */
@Service
public class RebackServiceImpl extends ServiceImpl<RebackMapper, Reback> implements RebackService {


    private static final List<RebackDTO> rebackList = new ArrayList<>();

    static {
        IUserService userService = SpringUtil.getBean(IUserService.class);
        rebackList.add(RebackDTO.builder().service(userService).fileName("blade_user.sql").build());

        IMenuService menuService = SpringUtil.getBean(IMenuService.class);
        rebackList.add(RebackDTO.builder().service(menuService).fileName("blade_menu.sql").build());

        MedicineService medicineService = SpringUtil.getBean(MedicineService.class);
        rebackList.add(RebackDTO.builder().service(medicineService).fileName("bus_medicine.sql").build());

        AnalyzeService analyzeService = SpringUtil.getBean(AnalyzeService.class);
        rebackList.add(RebackDTO.builder().service(analyzeService).fileName("bus_analyze.sql").build());

        BusCodeService codeService1 = SpringUtil.getBean(BusCodeService.class);
        rebackList.add(RebackDTO.builder().service(codeService1).fileName("bus_code.sql").build());

        CaseService caseService = SpringUtil.getBean(CaseService.class);
        rebackList.add(RebackDTO.builder().service(caseService).fileName("bus_case.sql").build());

        GrossService grossService = SpringUtil.getBean(GrossService.class);
        rebackList.add(RebackDTO.builder().service(grossService).fileName("bus_gross.sql").build());

        GrossDictService grossDictService = SpringUtil.getBean(GrossDictService.class);
        rebackList.add(RebackDTO.builder().service(grossDictService).fileName("bus_gross_dict.sql").build());

        SynonymService synonymService = SpringUtil.getBean(SynonymService.class);
        rebackList.add(RebackDTO.builder().service(synonymService).fileName("bus_synonym.sql").build());

        SynonymItemService synonymItemService = SpringUtil.getBean(SynonymItemService.class);
        rebackList.add(RebackDTO.builder().service(synonymItemService).fileName("bus_synonym_service.sql").build());
    }

    @Autowired
    private BusCodeService codeService;

    private static final String BASIC_SAVE_DIR = "reback";
    private static final String DEFAULT_SEPARATE = "REBACK";
    private static final SimpleDateFormat YYYY_DIR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MM_DIR_FORMAT = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DD_DIR_FORMAT = new SimpleDateFormat("dd");
    private final SimpleDateFormat FORAMT = new SimpleDateFormat("yyyyMMddHH");
    @Override
    @Transactional
    public synchronized boolean saveReback() {
        LambdaUpdateWrapper<Reback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Reback::getStatus, 1);


        Date date = new Date();
        Reback reback = new Reback();
        reback.setCreateUser(AuthUtil.getUserId());
        reback.setCreateTime(date);
        reback.setCreateDept(Func.firstLong(AuthUtil.getDeptId()));
        reback.setUpdateUser(AuthUtil.getUserId());
        reback.setUpdateTime(date);

        String format = FORAMT.format(date);
        String code = formatCode(codeService.getCode(DEFAULT_SEPARATE, format));
        reback.setCode(format + code);

        String path = getBasicPath(date, code);
        reback.setPath(path);
        reback.setStatus(0);


        boolean status = false;
        // 保存用户
        for (RebackDTO rebackDTO : rebackList) {
            status = saveReback(rebackDTO.getService(), path, rebackDTO.getFileName());
            if (!status) {
                return false;
            }
        }


        save(reback);
        return true;
    }

    @Override
    @Transactional
    public boolean reback(Long id) {
        Reback reback = getById(id);
        if (reback == null) {
            return false;
        }
        // 排查文件是否均存在
        for (RebackDTO rebackDTO : rebackList) {
            String allPath =  reback.getPath() + File.separator + rebackDTO.getFileName();
            File file = new File(allPath);
            if (!file.exists()) {
                throw new ServiceException(String.format("备份文件%s不存在", allPath));
            }
        }
        boolean status = false;
        // 依次恢复文件
        for (RebackDTO rebackDTO : rebackList) {
            status = reback(rebackDTO.getService(), reback.getPath(), rebackDTO.getFileName());
            if (!status) {
                return false;
            }
        }
        return true;
    }

    private String getBasicPath(Date date, String code) {
        String yy = YYYY_DIR_FORMAT.format(date);
        String mm = MM_DIR_FORMAT.format(date);
        String dd = DD_DIR_FORMAT.format(date);
        String other = code;
        return FileUtil.getFilePath(BASIC_SAVE_DIR + File.separator + yy + File.separator + mm + File.separator + dd + File.separator + other);
    }

    private boolean reback(IService service, String path, String name) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(path + File.separator + name);
            byte[] buffs = new byte[in.available()];
            in.read(buffs);
            String json = new String(buffs, "utf-8");
            if (service instanceof IUserService) {
                List<User> arrays = JSONObject.parseArray(json, User.class);
                ((IUserService)service).deleteAll();
                service.saveBatch(arrays);
                return true;
            }
            if (service instanceof IMenuService) {
                List<Menu> arrays = JSONObject.parseArray(json, Menu.class);
                service.remove(new LambdaQueryWrapper());
                ((IMenuService)service).deleteAll();
                service.saveBatch(arrays);
                return true;
            }
            JSONArray arrays = JSONObject.parseArray(json);
            service.remove(new LambdaQueryWrapper());
            service.saveBatch(arrays);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean saveReback(IService service, String path, String name) {
        FileOutputStream out = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            List list = service.list();
            String json = JSONObject.toJSONString(list);
            String savePath = path + File.separator + name;
            out = new FileOutputStream(savePath);
            out.write(json.getBytes());
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
