package serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    public HessianSerializer() {
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Hessian2Output ho = new Hessian2Output(baos);
            ho.writeObject(obj);
            ho.close();
            return baos.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生：", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Hessian2Input hi = new Hessian2Input(bais);
            Object obj = clazz.cast(hi.readObject());
            hi.close();
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生：", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
