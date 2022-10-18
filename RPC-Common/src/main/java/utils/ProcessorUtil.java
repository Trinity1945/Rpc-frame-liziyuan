package utils;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  19:38
 */
public class ProcessorUtil {
    /**
     *  获取CPU核心
     *
     * @return CPU核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
