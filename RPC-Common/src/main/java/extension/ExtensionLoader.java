package extension;

import lombok.extern.slf4j.Slf4j;
import utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *  SPI 加载
 *
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  20:04
 */
@Slf4j
public final class ExtensionLoader<T> {

    //SPI 扫描路径
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    //每一个类对应一个加载类对象
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    //保存类clazz 和对应的实例对象
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    //接口
    private final Class<?> type;

    //缓存SPI中的K 和对应的Holder，K对应的实现类作为Holder类的属性
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    //存放SPI文件中的K和对应的类
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if(type==null){
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        //先从缓存中获取该类型对应的扩展加载类对象
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        //不存在则生成一个
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        //返回还扩展加载类，用于加载相应的SPI实现
        return extensionLoader;
    }

    /**
     * SPI使用 k=v形式，通过该扩展类加载
     * @param name k
     * @return 实现类
     */
    public T getExtension(String name) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("扩展类的名称不能为控");
        }
        //先从缓存中获取
        Holder<Object> holder = cachedInstances.get(name);//Holder对象持有这个实现类
        //为控则创建一个Holder对象
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        //生成一个Holder对象的Value
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);//通过SPI的k 找到对应V 加载相应实现类
                    holder.set(instance);//将这个实例对象通过DI注入Holder对象
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        //从文件中加载所有类型为T的扩展类，并按名称获取特定的扩展类
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("没有匹配的扩展类 " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        //不存在则创建
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());//保存实例对象
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }


    private Map<String, Class<?>> getExtensionClasses() {
        //从缓存中获取该实现类clazz
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // 从扩展目录加载所有扩展
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();//文件路径 META-INF/extensions/文件
        System.out.println("SPI加载路径为"+fileName);
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // 读取每一行
            while ((line = reader.readLine()) != null) {
                //获取注释的坐标位置
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // 字符串后的#是注释，所以我们忽略它
                    line = line.substring(0, ci);
                }
                line = line.trim();//剩余有效部分
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');//找到分隔符号
                        String name = line.substring(0, ei).trim();//对应键 K
                        String clazzName = line.substring(ei + 1).trim();//对应值 V
                        // 我们的SPI使用键值对，所以它们不能都为空
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);//加载扩展类
                            extensionClasses.put(name, clazz);//放入缓存区
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
