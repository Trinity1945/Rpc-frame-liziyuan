package rpcinterface;

/**
 * PRC调用需要实现的接口,客户端通过获取该接口的代理类发起远程调用，服务端通过实现该接口实现相关服务
 *
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/18  15:17
 */
public interface HelloService {
String hello(Hello hello);
}
