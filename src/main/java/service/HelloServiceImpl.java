package service;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject object) {
        String s = "a+b=" + String.valueOf(object.getA() + object.getB());
        return s;
    }
}
