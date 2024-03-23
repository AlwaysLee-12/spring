package com.practice.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
    //부가기능 설정(중복 코드 X, 정의 할 부가 기능만, 타겟 인터페이스의 구현체는 다이나믹 프록시가 생성해줌)
    Object target;

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, java.lang.Object[] args) throws Throwable {
        Object ret = (String) method.invoke(target, args);

        if (ret instanceof String) { //&& method.getNAme().startsWith("say") 메서드 이름이 say로 시작하는 메서드들만 해당하게도 가능
            return ((String) ret).toUpperCase();
        }
        return ret;
    }
}
