package com.practice.spring;

import com.practice.spring.proxy.HelloUppercase;
import com.practice.spring.proxy.UppercaseHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {

    @Test
    public void simpleProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Always")).isEqualTo("Hello Always");
        assertThat(proxiedHello.sayHi("Always")).isEqualTo("Hi Always");
        assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("Thank You Always");
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

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Always")).isEqualTo("HELLO ALWAYS");
        assertThat(proxiedHello.sayHi("Always")).isEqualTo("HI ALWAYS");
        assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("THANK YOU ALWAYS");
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new com.practice.spring.proxy.HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Always")).isEqualTo("HELLO ALWAYS");
        assertThat(proxiedHello.sayHi("Always")).isEqualTo("HI ALWAYS");
        assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("Thank You ALWAYS");
    }

    @Test
    public void classNamePointcutAdvisor() {
        //포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new         class HelloWorld extends HelloTarget {
        };
        classMethodPointcut.setMappedName("sayH*");

        //테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloThere extends HelloTarget {
        }
        ;
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        }
        ;
        checkAdviced(new HelloThere(), classMethodPointcut, true);
    }


    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Always")).isEqualTo("HELLO ALWAYS");
            assertThat(proxiedHello.sayHi("Always")).isEqualTo("HI ALWAYS");
            assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("Thank You Always");
        } else {
            assertThat(proxiedHello.sayHello("Always")).isEqualTo("Hello Always");
            assertThat(proxiedHello.sayHi("Always")).isEqualTo("Hi Always");
            assertThat(proxiedHello.sayThankYou("Always")).isEqualTo("Thank You Always");
        }
    }

    static interface Hello {

        String sayHello(String name);

        String sayHi(String name);

        String sayThankYou(String name);
    }

    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();

            return ret.toUpperCase();
        }
    }

    static class HelloTarget implements Hello {

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }

}
