package com.aytel.actors;

import com.aytel.World;
import com.aytel.WorldObject;

import java.io.IOException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class Actor extends WorldObject {
    protected int hp;
    protected int attack;
    protected int armor;
    private Position position;
    protected final World world;

    Actor(int hp, int attack, int x, int y, World world) {
        this.hp = hp;
        this.attack = attack;
        this.armor = 0;
        this.position = new Position();
        this.position.x = x;
        this.position.y = y;
        this.world = world;
    }

    public int getArmor() {
        return armor;
    }

    public int getAttack() {
        return attack;
    }

    public int getHp() {
        return hp;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void move(int dy, int dx) {
        position.x += dx;
        position.y += dy;
    }

    public Position getPosition() {
        return position;
    }

    public void performAttack(Actor a) {
        a.hp -= max(0, getAttack() - a.getArmor());
    }

    abstract public void act() throws IOException;

    public void beforeRender() {

    }
}
