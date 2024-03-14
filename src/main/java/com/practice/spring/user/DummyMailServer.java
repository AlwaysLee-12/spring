package com.practice.spring.user;

public class DummyMailServer implements MailSender {

    public void send(SimpleMailMessage mailMessage) throws MailException {

    }

    public void send(SimpleMailMessage[] mailMessage) throws MailException {

    }
}
