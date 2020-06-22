package com.aytel.actors.mobs;

import com.aytel.actors.mobs.Mob;
import com.aytel.World;

public abstract class Strategy {
    public abstract void act(Mob me, World world);
}
