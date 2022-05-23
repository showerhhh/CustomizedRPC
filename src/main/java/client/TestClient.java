package client;

import client.netty_client.NettyClient;
import client.netty_client.NettyClientUseNacos;
import client.socket_client.SocketClient;
import service.HelloObject;
import service.HelloService;

public class TestClient {
    public static void main(String[] args) {
        TestClient test = new TestClient();
        test.test_netty_client();
    }

    void test_netty_client_use_nacos() {
        RpcClient client = new NettyClientUseNacos("223.3.71.21:8848", "KRYO", "ROUNDROBIN");
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, 3);
        String res = helloService.hello(object);
        System.out.println(res);
    }

    void test_netty_client() {
        RpcClient client = new NettyClient("127.0.0.1", 9000, "KRYO");
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, 3);
        String res = helloService.hello(object);
        System.out.println(res);
    }

    void test_socket_client() {
        RpcClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, 3);
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
