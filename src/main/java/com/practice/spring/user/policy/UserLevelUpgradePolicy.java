package com.practice.spring.user.policy;

import com.practice.spring.user.domain.User;

public interface UserLevelUpgradePolicy {

    boolean canUpgradeLevel(User user);

    void upgradeLevel(User user);
}
