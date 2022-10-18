package rpcclient.netty;

import config.RpcServiceConfig;
import rpcinterface.Hello;
import rpcinterface.HelloService;
import transport.RpcRequestTransport;
import transport.netty.NettyRpcClient;
import transport.proxy.RpcClientProxy;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/18  15:20
 */
public class consumer {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
