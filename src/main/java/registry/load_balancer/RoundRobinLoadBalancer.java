package registry.load_balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private int idx = 0;

    @Override
    public Instance select(List<Instance> instances) {
        Instance instance = instances.get(idx);
        idx++;
        if (idx >= instances.size()) {
            idx %= instances.size();
        }
        return instance;
    }
}
