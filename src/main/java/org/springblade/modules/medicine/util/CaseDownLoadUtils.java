package org.springblade.modules.medicine.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.PdfPageSize;

import com.spire.pdf.graphics.PdfMargins;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;

import org.springblade.common.utils.FileUtil;
import org.springblade.modules.medicine.vo.CaseVO;


import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/3/15 16:43
 * @Description:
 */
public class CaseDownLoadUtils {

    private static final String TEMPLATE_PATH = FileUtil.getFilePath("case.xlsx");
    private static final String DEFAULT_SHEET_NAME = "Sheet1";


    /**
     * 下载qpr
     *
     * @param response
     */
    public static ExcelWriter downLoad(CaseVO vo, HttpServletResponse response) {
        ExcelWriter export = ExcelUtil.getWriter(new File(TEMPLATE_PATH), DEFAULT_SHEET_NAME);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        try {
            String fileName = URLEncoder.encode("病历.pdf", "utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            handler(vo, response);
            export.flush(response.getOutputStream());
            export.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return export;
    }

    private static void handler(CaseVO vo, HttpServletResponse response) throws IOException {
        // 加载Excel文档.
        Workbook wb = new Workbook();

        wb.loadFromFile(TEMPLATE_PATH);

        wb.replace("${code}", vo.getCode());
        wb.replace("${caseTime}", vo.getCaseTime());
        wb.replace("${name}", vo.getName());
        wb.replace("${sexName}", vo.getSexName());
        wb.replace("${age}", vo.getAge() + "");
        wb.replace("${phone}", vo.getPhone());
        wb.replace("${address}", vo.getAddress());
        wb.replace("${medicineNames}", vo.getMedicineNames());
        if (StrUtil.isNotBlank(vo.getDialectical())) {
            wb.replace("${dialectical}", vo.getDialectical());
        }else {
            wb.replace("${dialectical}", "无");
        }
        wb.replace("${component}", vo.getComponent());
        if (StrUtil.isNotBlank(vo.getEnjoin())) {
            wb.replace("${enjoin}", "医嘱：" + vo.getEnjoin());
        }else {
            wb.replace("${enjoin}", "无");
        }




        PdfDocument pdfDocument = excelToPdf(wb);

        //创建一个新的PdfDocument实例
        PdfDocument newPdf = convertPdf(pdfDocument);
        newPdf.saveToStream(response.getOutputStream());
    }



    private static PdfDocument convertPdf(PdfDocument pdfDocument) {
        PdfDocument newPdf = new PdfDocument();
        for (int i = 0; i < pdfDocument.getPages().getCount(); i++) {
            PdfPageBase newPage = newPdf.getPages().add(PdfPageSize.A4, new PdfMargins(-20, -20, -20, -20));

            //将原PDF内容写入新页面
            pdfDocument.getPages().get(i).createTemplate().draw(newPage, new Point(0, 0));
        }
        newPdf.getViewerPreferences().setFitWindow(true);
        return newPdf;
    }

    private static PdfDocument excelToPdf(Workbook wb) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wb.saveToStream(outputStream, FileFormat.PDF);

        PdfDocument pdfDocument = new PdfDocument();
        pdfDocument.loadFromBytes(outputStream.toByteArray());
        return pdfDocument;
    }
}
