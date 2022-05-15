package server;

import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import server.netty_server.NettyServer;
import server.netty_server.NettyServerUseNacos;
import server.socket_server.SocketServer;
import service.HelloService;
import service.HelloServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        TestServer test = new TestServer();
        test.test_netty_server();
    }

    void test_netty_server_use_nacos() {
        HelloService helloService = new HelloServiceImpl();
        NettyServerUseNacos server = new NettyServerUseNacos("127.0.0.1:9000", "KRYO", "223.3.88.100:8848", "ROUNDROBIN");
        server.publishService(helloService);
        server.start();
    }

    void test_netty_server() {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer server = new NettyServer(serviceRegistry, "KRYO");
        server.start(9000);
    }

    void test_socket_server() {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer server = new SocketServer(serviceRegistry);
        server.start(9000);
    }
}
