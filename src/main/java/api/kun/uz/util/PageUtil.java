package api.kun.uz.util;

import api.kun.uz.exception.AppBadException;

public class PageUtil {

    public static Integer giveProperPageNumbering(Integer value) {
        return value <= 0 ? 1 : value - 1;
    }
}
