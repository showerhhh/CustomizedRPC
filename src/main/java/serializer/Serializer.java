package serializer;

public interface Serializer {

    static Serializer getByCode(int code) {
        switch (code) {
            case 0:
                return new JDKSerializer();
            case 1:
                return new KryoSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new JacksonSerializer();
            case 4:
                return new FastjsonSerializer();
            default:
                return null;
        }
    }

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
