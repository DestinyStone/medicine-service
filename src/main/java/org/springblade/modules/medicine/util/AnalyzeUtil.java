package org.springblade.modules.medicine.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: DestinyStone
 * @Date: 2022/11/29 12:25
 * @Description:
 */
public class AnalyzeUtil {

    public static long aroundNumber(String name1, String name2) {
        Integer number1 = handlerNumber(name1);
        Integer number2 = handlerNumber(name2);
        Double result = (number1 + number2) / new Double(2);
        return Math.round(result.doubleValue());
    }

    public static Integer handlerNumber(String name) {
        name = name.replaceAll("\n", "");
        int substringIndex = 0;
        int end = 0;
        for (int i = 0; i < name.length(); i++) {
            try {
                new Integer(name.charAt(i) + "");
                substringIndex = i;
                break;
            }catch (Exception e) {
            }
        }

        for (int i =  name.length() - 1; i >= 0; i--) {
            try {
                new Integer(name.charAt(i) + "");
                end = i;
                break;
            }catch (Exception e) {
            }
        }
        name = name.substring(substringIndex, end + 1);
        try {
            return new Integer(name);
        }catch (Exception e) {
            return 1;
        }
    }

    public static List<String> handlerNames(List<String> names) {
        return names.stream().map(item -> {
            return handlerName(item);
        }).filter(item -> {
            return item != null && !Objects.equals("", item);
        }).collect(Collectors.toList());
    }

    public static String handlerName(String name) {
        name = name.replaceAll("\n", "");
        int substringIndex = 0;
        for (int i = 0; i < name.length(); i++) {
            try {
                new Integer(name.charAt(i) + "");
                break;
            }catch (Exception e) {
                substringIndex = i;
            }
        }
        return name.substring(0, substringIndex + 1);
    }
}
