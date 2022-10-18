package constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  23:17
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
