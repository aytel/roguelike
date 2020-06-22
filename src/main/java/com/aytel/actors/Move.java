package com.aytel.actors;

public enum Move {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    public final int dx;
    public final int dy;

    Move(int dy, int dx) {
        this.dx = dx;
        this.dy = dy;
    }
}
