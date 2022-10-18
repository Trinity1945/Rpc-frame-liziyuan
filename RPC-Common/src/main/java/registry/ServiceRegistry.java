package registry;

import extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/16  23:40
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
