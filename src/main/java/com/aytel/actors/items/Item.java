package com.aytel.actors.items;

import com.aytel.World;
import com.aytel.WorldObject;
import com.aytel.actors.Actor;
import com.googlecode.lanterna.TextColor;

public class Item extends WorldObject {
    public int attack;
    public int armor;
    public int confusion;
    public final String name;

    Item(String name, int attack, int armor, int confusion) {
        this.attack = attack;
        this.armor = armor;
        this.name = name;
        this.confusion = confusion;
    }

    @Override
    public String getSymbol(World world, Actor toTurn) {
        return "i";
    }

    @Override
    public TextColor.ANSI getColor(World world, Actor toTurn) {
        return TextColor.ANSI.BLUE;
    }
}
