package com.aytel.actors.mobs;

import com.aytel.actors.mobs.strategies.*;

import java.util.Random;

public class MobBuilder {
    private static final int MIN_HP = 5;
    private static final int MIN_ATTACK = 1;
    private static final int MAX_HP = 8;
    private static final int MAX_ATTACK = 3;

    private static Random random = new Random();

    public static Mob generate(int y, int x) {
        Strategy strategy = null;

        int strategySwitch = random.nextInt(3);

        switch (strategySwitch) {
            case 0:
                strategy = new Dumb();
                break;
            case 1:
                strategy = new Aggressive();
                break;
            case 2:
                strategy = new Sneaky();
                break;
        }

        int hp = random.nextInt(MAX_HP - MIN_HP + 1) + MIN_HP;
        int attack = random.nextInt(MAX_ATTACK - MIN_ATTACK + 1) + MIN_ATTACK;

        return new Mob(hp, attack, x, y, strategy);

    }
}
