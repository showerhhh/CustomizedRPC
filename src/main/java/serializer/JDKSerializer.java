package serializer;

import enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JDKSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JDKSerializer.class);

    public JDKSerializer() {
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
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
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = clazz.cast(ois.readObject());
            ois.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("反序列化时有错误发生：", e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JDK").getCode();
    }
}
