package transport;

import dto.RpcRequest;
import extension.SPI;

import java.util.concurrent.ExecutionException;

/**
 * 抽象RPC请求接口
 *
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  21:04
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 发送RPC请求
     * @param rpcRequest 封装的消息结构体，包含请求id、请求方法名、请求参数等等信息
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest) throws ExecutionException, InterruptedException;
}
