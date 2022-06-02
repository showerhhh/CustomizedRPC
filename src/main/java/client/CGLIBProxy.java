package client;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import protocol.RpcRequest;
import protocol.RpcResponse;

import java.lang.reflect.Method;

public class CGLIBProxy implements MethodInterceptor {
    private RpcClient rpcClient;

    public CGLIBProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args).paramTypes(method.getParameterTypes())
                .build();
        RpcResponse rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest);
        return rpcResponse.getData();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // 创建动态代理增强类
        Enhancer enhancer = new Enhancer();
        // 设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(clazz);
        // 设置方法拦截器
        enhancer.setCallback(this);
        // 创建代理对象
        return (T) enhancer.create();
    }
}
