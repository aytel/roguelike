package com.aytel.actors;

public class Position {
    public int x, y;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }
        return ((Position) obj).x == this.x && ((Position) obj).y == this.y;
    }
}
