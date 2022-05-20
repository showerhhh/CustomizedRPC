package client.netty_client;

import client.RpcClient;
import enumeration.SerializerCode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.Decoder;
import protocol.Encoder;
import protocol.RpcRequest;
import protocol.RpcResponse;
import serializer.Serializer;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final Bootstrap bootstrap = new Bootstrap();
    private static final EventLoopGroup group = new NioEventLoopGroup();
    private String host;
    private int port;
    private Serializer serializer;

    public NettyClient(String host, int port, String serializerType) {
        this.host = host;
        this.port = port;

        int code = SerializerCode.valueOf(serializerType).getCode();
        this.serializer = Serializer.getByCode(code);

        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new Decoder());
                pipeline.addLast(new Encoder(serializer));
                pipeline.addLast(new NettyClientHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器{}:{}", host, port);
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                    } else {
                        logger.error("客户端发送消息时有错误发生：", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse;
            }
        } catch (InterruptedException e) {
            logger.error("客户端发送消息时有错误发生：", e);
        }
        return null;
    }
}
