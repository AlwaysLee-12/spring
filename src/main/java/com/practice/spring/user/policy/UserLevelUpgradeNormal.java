package com.practice.spring.user.policy;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import jakarta.websocket.Session;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.util.Properties;

public class UserLevelUpgradeNormal implements UserLevelUpgradePolicy {

    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch (currentLevel) {
            case BASIC -> {
                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            }
            case SILVER -> {
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            }
            default -> throw new IllegalArgumentException("no such level : " + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "mail.ksug.org");
        Session.getInstance(properties, null);

        MimeMessage message = new MimeMessage(s);
        try {
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("Ugrade 안내");
            message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다");

            Transport.send(message);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
