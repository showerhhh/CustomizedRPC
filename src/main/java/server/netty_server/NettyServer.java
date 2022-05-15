package server.netty_server;

import enumeration.SerializerCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.Decoder;
import protocol.Encoder;
import registry.ServiceRegistry;
import serializer.Serializer;
import server.RequestHandler;
import server.RpcServer;

public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final ServiceRegistry serviceRegistry;
    private Serializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    public NettyServer(ServiceRegistry serviceRegistry, String serializerType) {
        this.serviceRegistry = serviceRegistry;
        int code = SerializerCode.valueOf(serializerType).getCode();
        this.serializer = Serializer.getByCode(code);
    }

    @Override
    public void start(int port) {
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
                            pipeline.addLast(new Encoder(serializer));
                            pipeline.addLast(new Decoder());
                            pipeline.addLast(new NettyServerHandler(requestHandler, serviceRegistry));
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
