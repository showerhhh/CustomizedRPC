package serializer;

import com.alibaba.fastjson.JSON;
import enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastjsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(FastjsonSerializer.class);

    public FastjsonSerializer() {
    }

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("FASTJSON").getCode();
    }
}
