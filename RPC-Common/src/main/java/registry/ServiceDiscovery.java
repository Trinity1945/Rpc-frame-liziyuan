package registry;

import dto.RpcRequest;
import extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  22:13
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 通过rpcServiceName查找服务
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
