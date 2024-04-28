package com.practice.spring.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

/**
 * 스프링 빈으로 다이나믹 프록시 관리 가능 -> 해당 기능을 가진 다이나믹 프록시 생성을 재사용 가능
 * 방대한 설정 파일 -> 여러 객체에 적용 시, 하나의 객체의 여러 공통 기능 적용 시
 * TransactionHandler의 타켓이 변경될 때마다 객체 계속 생성 -> TransactionHandler를 모든 타켓에 적용 가능한 싱글톤 빈으로 등록한다면?
 */
public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler transactionHandler = new TransactionHandler();
        transactionHandler.setTarget(this.target);
        transactionHandler.setTransactionManager(this.transactionManager);
        transactionHandler.setPattern(this.pattern);

        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{this.serviceInterface},
                transactionHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        //팩토리 빈이 생성하는 객체의 타입은 DI 받은 인터페이스 타입에 따라 달라짐
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
