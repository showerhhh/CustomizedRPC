package registry.load_balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {
    static LoadBalancer getByType(String type) {
        switch (type) {
            case "RANDOM":
                return new RandomLoadBalancer();
            case "ROUNDROBIN":
                return new RoundRobinLoadBalancer();
            default:
                return null;
        }
    }

    Instance select(List<Instance> instances);
}
