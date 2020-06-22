package com.aytel.actors.strategies;

import com.aytel.actors.Mob;
import com.aytel.World;

public abstract class Strategy {
    public abstract void act(Mob me, World world);
}
