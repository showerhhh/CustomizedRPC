package registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.load_balancer.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private NamingService namingService;
    private LoadBalancer loadBalancer;

    public NacosServiceRegistry(String registryAddr, String loadBalancerType) {
        // registryAddr为Nacos注册中心地址，不同于其他服务提供者的地址。
        // loadBalancerType为负载均衡调度器的类型。
        try {
            namingService = NamingFactory.createNamingService(registryAddr);
            loadBalancer = LoadBalancer.getByType(loadBalancerType);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public <T> void register(T service, InetSocketAddress inetSocketAddress) {
        // 将接口名称和服务器地址注册到Nacos中
        try {
            Class<?>[] interfaces = service.getClass().getInterfaces();
            if (interfaces.length == 0) {
                throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
            }
            for (Class<?> i : interfaces) {
                namingService.registerInstance(i.getCanonicalName(), inetSocketAddress.getHostName(), inetSocketAddress.getPort());
            }
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生: ", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    public InetSocketAddress lookupService(String interfaceName) {
        // 根据接口名称获取服务器地址
        try {
            List<Instance> instances = namingService.getAllInstances(interfaceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生: ", e);
            return null;
        }
    }
}
