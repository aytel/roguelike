package com.aytel.actors.mobs.strategies;

import com.aytel.World;
import com.aytel.actors.Move;
import com.aytel.actors.mobs.Mob;
import com.aytel.actors.mobs.Strategy;

import java.util.Random;

public class Confusion extends Strategy {
    private Strategy strategy;
    private int left;

    private static Random random = new Random();

    public static Strategy decorate(Strategy strategy, int left) {
        if (strategy instanceof Confusion) {
            ((Confusion) strategy).left += left;
            return strategy;
        } else {
            Confusion confusion = new Confusion();
            confusion.strategy = strategy;
            confusion.left = left;
            return confusion;
        }
    }

    @Override
    public void act(Mob me, World world) {
        if (left == 0) {
            strategy.act(me, world);
        } else {
            left--;
            int dir = random.nextInt(4);
            switch (dir) {
                case 0:
                    world.tryMove(me, Move.UP);
                    break;
                case 1:
                    world.tryMove(me, Move.DOWN);
                    break;
                case 2:
                    world.tryMove(me, Move.LEFT);
                    break;
                case 3:
                    world.tryMove(me, Move.RIGHT);
                    break;
            }
        }
    }
}
