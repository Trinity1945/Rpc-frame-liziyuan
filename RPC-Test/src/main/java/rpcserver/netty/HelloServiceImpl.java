package rpcserver.netty;

import lombok.extern.slf4j.Slf4j;
import rpcinterface.Hello;
import rpcinterface.HelloService;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/18  15:23
 */
@Slf4j
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}
