package com.practice.spring.proxy;

public class HelloUppercase implements Hello {
    /**
     * 문제의 프록시
     * 1. 구현하는 인테페이스의 모든 기능을 구현해야 한다
     * 2. 부가 기능을 이용하는 메서드들에 대해 중복 코드가 발생한다
     */
    Hello hello;

    public HelloUppercase(Hello hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
