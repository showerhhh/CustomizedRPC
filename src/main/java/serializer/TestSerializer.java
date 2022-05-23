package serializer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
class Example implements Serializable {
    private static final long serialVersionUID = 1512316628L;
    private Integer a = 1;
    private Long b = 2L;
    private Byte c = 3;
    private Short d = 4;
    private Double e = 5.0;
    private Float f = 6.0F;
    private Boolean g = true;
}

public class TestSerializer {
    public static void main(String[] args) {
        Example example = new Example();
        Serializer[] serializers = new Serializer[]{new JDKSerializer(), new KryoSerializer(), new HessianSerializer(), new JacksonSerializer(), new FastjsonSerializer()};
        int M = serializers.length;  // 序列化器个数
        int N = 2;  // 测试次数

        for (int i = 0; i < M; i++) {
            Serializer serializer = serializers[i];
            Long serializeTime = 0L;
            int useSpace = 0;
            Long deserializeTime = 0L;
            for (int j = 0; j < N; j++) {
                Long serializeStartTime = System.currentTimeMillis();
                byte[] ba = serializer.serialize(example);
                Long serializeEndTime = System.currentTimeMillis();
                serializeTime += serializeEndTime - serializeStartTime;
                useSpace += ba.length;

                Long deserializeStartTime = System.currentTimeMillis();
                Object example1 = serializer.deserialize(ba, Example.class);
                Long deserializeEndTime = System.currentTimeMillis();
                deserializeTime += deserializeEndTime - deserializeStartTime;
            }
            System.out.println("------------" + serializer.getClass() + "------------");
            System.out.println("serializeTime=" + ((double) serializeTime));
            System.out.println("useSpace=" + ((double) useSpace));
            System.out.println("deserializeTime=" + ((double) deserializeTime));
        }
    }
}
