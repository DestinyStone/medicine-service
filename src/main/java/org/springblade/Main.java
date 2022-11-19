package org.springblade;

import org.springblade.common.utils.WordUtil;

import java.util.List;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/18 12:59
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        List<WordUtil.Template> templates = WordUtil.readContentParseObject("C:\\Users\\admin\\Desktop\\storage\\名医验案数据集.docx");
        System.out.println(templates);
    }
}
