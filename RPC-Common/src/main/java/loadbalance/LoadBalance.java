package loadbalance;

import dto.RpcRequest;
import extension.SPI;

import java.util.List;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  22:19
 */
@SPI
public interface LoadBalance {
    /**
     * 从服务器列表中负载均衡出一个
     * @param serviceUrlList
     * @param rpcRequest
     * @return
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
