package registry.load_balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        int idx = new Random().nextInt(instances.size());
        return instances.get(idx);
    }
}
