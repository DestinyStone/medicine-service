package org.springblade.modules.medicine.controller;

import lombok.AllArgsConstructor;
import org.springblade.common.constant.ApiConstant;
import org.springblade.common.utils.FileUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/24 14:26
 * @Description:
 */
@RestController
@RequestMapping(ApiConstant.PREFIX + "/download")
@AllArgsConstructor
public class DownloadController {
    @GetMapping("/medicine/template")
    public void medicineTemplate(Integer type, HttpServletResponse response) {
        String fileName = "";
        if (Objects.equals(type, 0)) {
            fileName = "medicine_0.docx";
            String filePath = FileUtil.getFilePath(fileName);
            FileUtil.download("古医数据集-导入模板.docx", filePath, response);
        }
        if (Objects.equals(type, 1)) {
            fileName = "medicine_1.docx";
            String filePath = FileUtil.getFilePath(fileName);
            FileUtil.download("名医验案数据集-导入模板.docx", filePath, response);
        }
    }
}
