package utils.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  19:40
 */
@Slf4j
public class ThreadPoolFactoryUtil {

    /**
     * 通过 threadNamePrefix 来区分不同线程池，（将相同threadNamePrefix等线程池视为统一业务场景）
     * key:threadNamePrefix
     * value:threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOL=new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtil() {

    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig,threadNamePrefix,false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix,CustomThreadPoolConfig customThreadPoolConfig){
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig,threadNamePrefix,false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        ExecutorService threadpool = THREAD_POOL.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        //如果线程池已经被shutdown则重新创建一个线程池
        if(threadpool.isShutdown()||threadpool.isTerminated()){
            THREAD_POOL.remove(threadNamePrefix);
            threadpool=createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOL.put(threadNamePrefix,threadpool);
        }
        return threadpool;
    }

    /**
     * 通过线程工厂创建线程池
     * @param customThreadPoolConfig 自定义配置
     * @param threadNamePrefix 线程前缀
     * @param daemon  是否为守护线程
     * @return 线程池
     */
    public static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig,String threadNamePrefix,Boolean daemon){
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(),customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    /**
     * 创建 threadFactory 如果threadNamePrefix不为空则使用自定义的factory，否则使用defaultFactory
     * @param threadNamePrefix 线程名的前缀
     * @param daemon 指定为守护线程
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix,Boolean daemon){
        if(threadNamePrefix!=null){
            if(daemon!=null){
                return new ThreadFactoryBuilder()   //使用guava提供的线程工厂
                        .setDaemon(daemon)
                        .setNameFormat(threadNamePrefix+"-%d")
                        .build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix+"-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    /**
     * 关闭线程池
     */
    public static void shutDownAllThreadPool(){
        log.info("即将关闭线程池");
        THREAD_POOL.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]",entry.getKey(),executorService.isTerminated());
            try{
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){
        final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
