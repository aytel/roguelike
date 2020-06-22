package com.aytel.actors.mobs;

import com.aytel.actors.Actor;
import com.aytel.World;
import com.googlecode.lanterna.TextColor;

public class Mob extends Actor {
    private Strategy strategy;

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Mob(int hp, int attack, int x, int y, Strategy strategy) {
        super(hp, attack, x, y);
        this.strategy = strategy;
    }

    @Override
    public void act(World world) {
        strategy.act(this, world);
    }

    @Override
    public String getSymbol(World world, Actor toTurn) {
        return "M";
    }

    @Override
    public TextColor.ANSI getColor(World world, Actor toTurn) {
        return TextColor.ANSI.RED;
    }
}
