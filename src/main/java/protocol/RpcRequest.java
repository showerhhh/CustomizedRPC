package protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    // 对象的接口名称
    private String interfaceName;
    // 接口的方法名称
    private String methodName;
    // 方法的参数
    private Object[] parameters;
    // 方法的参数类型
    private Class<?>[] paramTypes;
}
