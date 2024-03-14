package com.practice.spring;

import com.practice.spring.user.UserService;
import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.User;
import com.practice.spring.user.policy.UserLevelUpgradeNormal;

public class TestUserLevelUpgradePolicy extends UserLevelUpgradeNormal {

    private String id;
    private MailSender mailSender;

    public TestUserLevelUpgradePolicy(String id, UserDao userDao, MailSender mailSender) {
        this.id = id;
        super.setUserDao(userDao);
        super.setMailSender(mailSender);
    }

    @Override
    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
        sendUpgradeEmail(user);
    }
    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        mailSender.send(mailMessage);
    }
}
