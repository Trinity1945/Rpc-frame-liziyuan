package extension;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  20:05
 */
public class Holder<T>{
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
