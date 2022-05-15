package registry;

public interface ServiceRegistry {
    public <T> void register(T service);

    public Object getService(String interfaceName);
}
