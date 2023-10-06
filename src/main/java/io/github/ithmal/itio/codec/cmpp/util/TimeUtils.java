package io.github.ithmal.itio.codec.cmpp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 公用的工具类
 *
 * @author liuyanning
 */
public final class TimeUtils {

    public static int getTimestamp() {
        String str = new SimpleDateFormat("MMddHHmmss").format(new Date());
        return Integer.parseInt(str);
    }

}
