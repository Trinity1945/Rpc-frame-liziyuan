package utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  19:15
 */
@Slf4j
public class PropertiesFileUtil {

    public PropertiesFileUtil() {
    }

    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfig = "";
        if (url != null) {
            rpcConfig = url.getPath() + fileName;
        }
        Properties properties = null;
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(rpcConfig), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            log.info("读取 {}.properties 文件出现异常", fileName);
        }
        return properties;
    }

}
