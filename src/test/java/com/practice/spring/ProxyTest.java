package com.practice.spring;

import com.practice.spring.proxy.Hello;
import com.practice.spring.proxy.HelloTarget;
import com.practice.spring.proxy.HelloUppercase;
import com.practice.spring.proxy.UppercaseHandler;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {

    @Test
    public void simpleProxy() {
        HelloTarget hello = new HelloTarget();
        assertThat(hello.sayHello("Always")).isEqualTo("Hello Always");
        assertThat(hello.sayHi("Always")).isEqualTo("Hi Always");
        assertThat(hello.sayThankYou("Always")).isEqualTo("Thank You Always");
    }

    @Test
    public void proxyUpperCase() {
        Hello proxyHello = new HelloUppercase(new HelloTarget());

        assertThat(proxyHello.sayHello("Always")).isEqualTo("HELLO ALWAYS");
        assertThat(proxyHello.sayHi("Always")).isEqualTo("HI ALWAYS");
        assertThat(proxyHello.sayThankYou("Always")).isEqualTo("THANK YOU ALWAYS");
    }

    @Test
    public void DynamicProxyUpperCase() {
        //다이나믹 프록시를 이용한 부가기능 설정(1. 구현체는 다이나믹 프록시가, 2. 부가기능은 invocation handler가 => 역할과 책임 분리)
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Always")).isEqualTo("HELLO ALWAYS");
        assertThat(proxiedHello.sayHi("Always")).isEqualTo("HI ALWAYS");
        assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("THANK YOU ALWAYS");
    }
}
