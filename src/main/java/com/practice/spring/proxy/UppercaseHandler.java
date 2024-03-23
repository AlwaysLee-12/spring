package com.practice.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
    //부가기능 설정(중복 코드 X, 정의 할 부가 기능만, 타겟 인터페이스의 구현체는 다이나믹 프록시가 생성해줌)
    Hello target;

    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String ret = (String) method.invoke(target, args);

        return ret.toUpperCase();
    }
}
