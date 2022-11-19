package org.springblade.common.utils;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 12:56
 * @Description:
 */
public class WordUtil {
    @Data
    public static class Template {
        private String name;
        private String putUp;
        private String solve;
        private String remark;
    }

    interface Type {
        int DOC = 0;
        int DOCX = 1;
    }

    @SneakyThrows
    public static List<Template> readContentParseObject(InputStream inputStream) {
        ArrayList<Template> result = new ArrayList<>();
        String content = readContent(inputStream, Type.DOCX);
        String[] nams = content.split("#名称#");
        for (String nameItem : nams) {
            if (StrUtil.isBlank(nameItem)) {
                continue;
            }
            Template template = new Template();
            String[] putUp= nameItem.split("#症状#");
            template.setName(handlerText(putUp[0]));
            String[] solve = putUp[1].split("#药方#");
            template.setPutUp(handlerText(solve[0]));
            String[] remark = solve[1].split("#注解#");
            template.setSolve(handlerText(remark[0]));
            template.setRemark("    " + handlerText(remark[1].replaceAll("\n", "\n    ")));

            result.add(template);
        }
        return result;
    }

    private static String handlerText(String str) {
        if (Objects.equals(str.charAt(0) + "", "：")) {
            return str.substring(1);
        }
        return str;
    }

    @SneakyThrows
    public static List<Template> readContentParseObject(String path) {
       return readContentParseObject(new FileInputStream(path));
    }

    @SneakyThrows
    public static String readContent(String path) {
        String s = "";
        try {
            FileInputStream in = new FileInputStream(path);
            if(path.endsWith(".doc")) {
                return readContent(in, Type.DOC);
            }else if (path.endsWith("docx")) {
                return readContent(in, Type.DOCX);
            }else {
                System.out.println("文件异常");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @SneakyThrows
    public static String readContent(InputStream is, int type) {
        String s = "";
        try {
            if(type == Type.DOC) {
                WordExtractor ex = new WordExtractor(is);
                s = ex.getText();
            }else if (type == Type.DOCX) {
                POIXMLTextExtractor extractor = (POIXMLTextExtractor) ExtractorFactory.createExtractor(is);
                s = extractor.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
