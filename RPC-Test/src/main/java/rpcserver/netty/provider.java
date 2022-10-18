package rpcserver.netty;

import config.RpcServiceConfig;
import rpcinterface.HelloService;
import transport.netty.NettyRpcServer;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/18  15:24
 */
public class provider {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyRpcServer socketRpcServer = new NettyRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}
