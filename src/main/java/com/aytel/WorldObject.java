package com.aytel;

import com.aytel.actors.Actor;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public abstract class WorldObject {
    public abstract String getSymbol(World world, Actor toTurn);

    public SGR getSGR(World world, Actor toTurn) {
        return null;
    }

    public TextColor.ANSI getColor(World world, Actor toTurn) {
        return TextColor.ANSI.WHITE;
    }

    public void draw(TextGraphics textGraphics, World world, Actor toTurn, int col, int row, TextColor.ANSI color) {
        SGR sgr = this.getSGR(world, toTurn);
        if (color == null) {
            textGraphics.setForegroundColor(this.getColor(world, toTurn));
        } else {
            textGraphics.setForegroundColor(color);
        }
        if (sgr != null) {
            textGraphics.putString(col, row, this.getSymbol(world, toTurn), sgr);
        } else {
            textGraphics.putString(col, row, this.getSymbol(world, toTurn));
        }
    }
}
