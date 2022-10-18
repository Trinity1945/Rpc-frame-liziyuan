package transport.netty;

import codec.RpcMessageDecoder;
import codec.RpcMessageEncoder;
import constants.CompressTypeEnum;
import constants.RpcConstants;
import constants.SerializationTypeEnum;
import dto.RpcMessage;
import dto.RpcRequest;
import dto.RpcResponse;
import extension.ExtensionLoader;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import registry.ServiceDiscovery;
import transport.RpcRequestTransport;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  21:07
 */
@Slf4j
public final class NettyRpcClient implements RpcRequestTransport {

    //服务发现对象  通过SPI机制加载
    private final ServiceDiscovery serviceDiscovery;

    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        eventLoopGroup=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) //连接超时时间
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //如果5秒内没有数据发送到服务器，则发送心跳请求
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
      //  this.serviceDiscovery =new ZkServiceDiscoveryImpl();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * 获取连接的channel
     * @param inetSocketAddress 服务器地址
     * @return channel
     */
    public Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端 [{}] 建立连接成功!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        Channel channel = channelProvider.get(inetSocketAddress);//从缓冲区获取channel
        if (channel == null) {
            channel = doConnect(inetSocketAddress);//没有则建立连接
            channelProvider.set(inetSocketAddress, channel);//将服务器地址与对应的channel保存到缓冲区中
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) throws ExecutionException, InterruptedException {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        //通过负载均衡查找出一个服务地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        //通过服务器地址获取对应的channel
        Channel channel = getChannel(inetSocketAddress);
        if(channel.isActive()){
            //存放未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }
}
