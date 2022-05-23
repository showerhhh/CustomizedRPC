package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    JDK(0),
    KRYO(1),
    HESSIAN(2),
    JACKSON(3),
    FASTJSON(4);

    private final int code;
}
