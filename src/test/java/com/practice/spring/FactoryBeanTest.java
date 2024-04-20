package com.practice.spring;

import com.practice.spring.learningtest.factorybean.Message;
import com.practice.spring.learningtest.factorybean.MessageFactoryBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class FactoryBeanTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = applicationContext.getBean("message");
        assertThat(message).isEqualTo(Message.class);
        assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    public void getFactoryBean() throws Exception {
        Object factory = applicationContext.getBean("&message");
        assertThat(factory).isEqualTo(MessageFactoryBean.class);
    }
}
