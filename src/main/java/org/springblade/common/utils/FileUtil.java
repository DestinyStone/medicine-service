package org.springblade.common.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Author: zhouxiaofeng
 * @Date: 2022/11/24 14:28
 * @Description:
 */
public class FileUtil {

    private static final String pre = System.getProperty("user.dir");

    public static String getFilePath(String fileName) {
        return pre + File.separator + fileName;
    }

    public static void download(String fileName, InputStream inputStream, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        OutputStream out = null;
        try {
            fileName = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void download(String fileName, String filePath, HttpServletResponse response) {
        try {
            download(fileName, new FileInputStream(filePath), response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
