package com.aytel.actors.mobs.strategies;

import com.aytel.actors.Actor;
import com.aytel.actors.mobs.Mob;
import com.aytel.actors.Player;
import com.aytel.World;
import com.aytel.actors.mobs.Strategy;

import java.util.List;

public class Aggressive extends Strategy {
    @Override
    public void act(Mob me, World world) {
        List<Actor> actors = world.getActors();

        for (Actor actor: actors) {
            if (actor instanceof Player) {
                int[] distanceAndDir = world.getDistanceAndDir(me.getPosition(), actor.getPosition());
                if (distanceAndDir[0] < 4) {
                    world.tryMove(me, world.getMove(distanceAndDir[1], distanceAndDir[2]));
                    return;
                }
            }
        }
    }
}
