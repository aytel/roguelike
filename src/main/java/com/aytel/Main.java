package com.aytel;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        World world = World.generate(20, 10);
        world.run();
    }
}
