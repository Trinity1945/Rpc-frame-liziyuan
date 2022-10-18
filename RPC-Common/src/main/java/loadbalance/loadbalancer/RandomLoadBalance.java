package loadbalance.loadbalancer;

import dto.RpcRequest;
import loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  23:02
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
