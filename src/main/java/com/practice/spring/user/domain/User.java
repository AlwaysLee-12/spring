package com.practice.spring.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    String id;
    String name;
    String password;
    Level level;
    int login;
    int recommend;

    public void upgradeLevel() {
        Level nextLevel = level.nextLevel();

        if (nextLevel == null) {
            throw new IllegalStateException("can not upgrade level : " + level);
        }
        level = nextLevel;
    }
}
