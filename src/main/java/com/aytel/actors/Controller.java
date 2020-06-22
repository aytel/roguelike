package com.aytel.actors;

import com.aytel.World;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

public class Controller {
    void act(Player me, World world) throws IOException {
        while (true) {
            KeyStroke keyStroke = world.getInput();
            switch (keyStroke.getKeyType()) {
                case Enter:
                    return;
                case ArrowUp:
                    world.tryMove(me, Move.UP);
                    return;
                case ArrowDown:
                    world.tryMove(me, Move.DOWN);
                    return;
                case ArrowLeft:
                    world.tryMove(me, Move.LEFT);
                    return;
                case ArrowRight:
                    world.tryMove(me, Move.RIGHT);
                    return;
                case Character:
                    Character ch = keyStroke.getCharacter();
                    if (Character.isDigit(ch)) {
                        me.selectItem(Integer.parseInt(ch.toString()) - 1);
                    }
                    world.renderMyInfo();
                    world.flush();
                    break;
            }
        }
    }
}
