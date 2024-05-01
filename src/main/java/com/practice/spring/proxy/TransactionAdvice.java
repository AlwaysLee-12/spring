package com.practice.spring.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
    /**
     * ProxyFactoryBean 방식
     * - advice와 pointcut 조합 가능, 재사용 가능(수월하게 여러 부가 기능 적용 가능)
     * - proxy factory bean이 다이나믹 프록시를 만들어 재사용 가능
     * - 비슷한 advisor를 사용하지만 타겟이 다른 경우 거의 중복된 proxy factory bean에 대한 설정 코드 중복 문제는 잔존
     * => 빈 후처리기인 DefaultAdvisorAutoProxyCreator를 통한 자동으로 프록시 객체 빈 등록
     * 전달받은 빈이 advisor의 포인트컷(프록세 적용 클래스인지 판별 + advice를 적용할 메서드인지 확인)에 해당되는 대상인지 확인
     * -> 맞다면 내장된 프록시 생성기에 해당 빈에 대한 프록시 생성 요청
     * -> 만들어진 프록시에 advisor 연결 -> 빈 후처리기는 프록시 생성 시 원래 컨테이너가 전달해준 빈 오브젝트 대신 프록시 오브젝트 전달
     * -> 컨테이너는 빈 후처리기가 돌려준 오브젝트를 빈으로 등록
     */

    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = invocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
