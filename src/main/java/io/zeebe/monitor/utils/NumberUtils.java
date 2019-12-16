package io.zeebe.monitor.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    /**
     * 保留两位小数,四舍五入
     * @param param
     * @return
     */
    public static String saveTwoFormat(double param) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(param);
    }
}
