package registry.zk;

import org.apache.curator.framework.CuratorFramework;
import registry.ServiceRegistry;
import registry.zk.utils.CuratorUtil;

import java.net.InetSocketAddress;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/16  23:39
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }
}
