package utils;

import java.util.Collection;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  19:35
 */
public class CollectionUtil {
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
