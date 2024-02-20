package com.practice.spring;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class JunitTest {

    @Autowired
    ApplicationContext context;

    static Set<JunitTest> testObjects = new HashSet<JunitTest>();
    static ApplicationContext contextObject = null;

    @Test
    public void test1() {
        //테스트 메서드 실행 시마다 테스트 객체를 새로 생성할까?
        assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        //application context가 싱글톤 빈으로 등록 됐을까?
        assertThat(contextObject == null || contextObject == this.context).isEqualTo(true);
        contextObject = this.context;
    }

    @Test
    public void test2() {
        assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        assertThat(contextObject == null || contextObject == this.context).isTrue();
        contextObject = this.context;
    }

    @Test
    public void test3() {
        assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        assertThat(contextObject).isIn(null, this.context);
        contextObject = this.context;
    }
}
