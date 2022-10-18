package constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  22:56
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
