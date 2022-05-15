package server.netty_server;

import enumeration.SerializerCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.Decoder;
import protocol.Encoder;
import registry.DefaultServiceRegistry;
import registry.NacosServiceRegistry;
import serializer.Serializer;
import server.RequestHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyServerUseNacos {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerUseNacos.class);
    String host;
    int port;
    private Serializer serializer;
    private DefaultServiceRegistry localRegistry;
    private NacosServiceRegistry serviceRegistry;
    private RequestHandler requestHandler = new RequestHandler();

    public NettyServerUseNacos(String serverAddr, String serializerType, String registryAddr, String loadBalancerType) {
        String[] serverAddress = serverAddr.split(":");
        this.host = serverAddress[0];
        this.port = Integer.parseInt(serverAddress[1]);
        int code = SerializerCode.valueOf(serializerType).getCode();
        this.serializer = Serializer.getByCode(code);
        this.localRegistry = new DefaultServiceRegistry();
        this.serviceRegistry = new NacosServiceRegistry(registryAddr, loadBalancerType);
    }

    public <T> void publishService(T service) {
        // 将服务保存到本地注册表中
        localRegistry.register(service);
        // 将服务注册到Nacos注册表中
        serviceRegistry.register(service, new InetSocketAddress(host, port));
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new Encoder(serializer));
                            pipeline.addLast(new Decoder());
                            pipeline.addLast(new NettyServerHandler(requestHandler, localRegistry));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
