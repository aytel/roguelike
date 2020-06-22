package com.aytel.actors;

import com.aytel.World;
import com.aytel.actors.items.Item;
import com.aytel.actors.mobs.Mob;
import com.aytel.actors.mobs.strategies.Confusion;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends Actor {
    private Controller controller;
    private List<Item> items = new ArrayList<>();
    public int curItem = -1;

    public Player(int hp, int attack, int x, int y, Controller controller) {
        super(hp, attack, x, y);
        this.controller = controller;
    }

    @Override
    public void act(World world) throws IOException {
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

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void selectItem(int i) {
        if (i < 0 || i >= items.size()) {
            return;
        }
        if (curItem == i) {
            curItem = -1;
        } else {
            curItem = i;
        }
    }

    @Override
    public void beforeRender(World world) {
        if (world.getItems()[getPosition().y][getPosition().x] != null && items.size() < 10) {
            this.addItem(world.getItems()[getPosition().y][getPosition().x]);
            world.getItems()[getPosition().y][getPosition().x] = null;
        }
    }

    @Override
    public int getArmor() {
        return curItem == -1 ? 0 : items.get(curItem).armor;
    }

    @Override
    public int getAttack() {
        return attack + (curItem == -1 ? 0 : items.get(curItem).attack);
    }

    public int getConfusion() {
        return curItem == -1 ? 0 : items.get(curItem).confusion;
    }

    @Override
    public void performAttack(Actor a) {
        super.performAttack(a);
        if (a instanceof Mob && getConfusion() > 0) {
            ((Mob)a).setStrategy(Confusion.decorate(((Mob) a).getStrategy(), getConfusion()));
        }
    }
}
