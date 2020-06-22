package com.aytel.actors.items;

import java.util.Random;

public class ItemBuilder {
    private static final String[] names = new String[] {
        "SWORD",
        "DAGGER",
        "RIFLE",
        "PISTOL",
        "BOW",
        "HELMET",
        "CUIRASS",
        "SHIELD"
    };

    private static final int MAX_ATTACK = 3;
    private static final int MAX_ARMOR = 1;
    private static final int MAX_CONFUSION = 4;

    static final Random random = new Random();

    private Item item;

    ItemBuilder() {
        item = new Item(names[random.nextInt(names.length)], 0, 0, 0);
    }

    ItemBuilder setAttack(int val) {
        item.attack = val;
        return this;
    }

    ItemBuilder setArmor(int val) {
        item.armor = val;
        return this;
    }

    ItemBuilder setConfusion(int val) {
        item.confusion = val;
        return this;
    }

    Item build() {
        return item;
    }

    public static Item generate() {
        ItemBuilder builder = new ItemBuilder().setArmor(random.nextInt(MAX_ARMOR + 1)).setAttack(random.nextInt(MAX_ATTACK + 1));
        if (random.nextInt(2) == 0) {
            builder.setConfusion(random.nextInt(MAX_CONFUSION + 1));
        }
        return builder.build();
    }

}
