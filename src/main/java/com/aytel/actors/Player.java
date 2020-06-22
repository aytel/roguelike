package com.aytel.actors;

import com.aytel.World;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

public class Player extends Actor {
    private Controller controller;

    public Player(int hp, int attack, int x, int y, World world, Controller controller) {
        super(hp, attack, x, y, world);
        this.controller = controller;
    }

    @Override
    public void act() throws IOException {
        controller.act(this, world);
    }

    @Override
    public String getSymbol(World world, Actor toTurn) {
        return "@";
    }

    @Override
    public TextColor.ANSI getColor(World world, Actor toTurn) {
        if (world.getMe() == this) {
            return TextColor.ANSI.GREEN;
        } else {
            return TextColor.ANSI.YELLOW;
        }
    }

    @Override
    public SGR getSGR(World world, Actor toTurn) {
        if (toTurn == this) {
            return SGR.BLINK;
        } else {
            return null;
        }
    }
}
