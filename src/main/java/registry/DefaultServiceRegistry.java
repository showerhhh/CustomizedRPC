package registry;

import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();  // 保存接口名称与提供服务的对象之间的映射
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();  // 保存已经注册的对象名

    public synchronized <T> void register(T service) {
        // 检查对象名是否已经被注册
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        // 将接口名称与提供服务的对象保存到映射表中
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
    }

    public synchronized Object getService(String interfaceName) {
        // 根据接口名称找到提供服务的对象
        Object service = serviceMap.get(interfaceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
