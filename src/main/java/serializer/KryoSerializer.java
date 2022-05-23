package serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.RpcRequest;
import protocol.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public KryoSerializer() {
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            output.close();
            kryoThreadLocal.remove();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("序列化时有错误发生：", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais);
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            input.close();
            kryoThreadLocal.remove();
            return obj;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生：", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
