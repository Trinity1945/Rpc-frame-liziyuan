package compress;

import extension.SPI;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/16  23:08
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
