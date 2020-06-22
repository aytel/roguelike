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
                    world.tryMove(me, -1, 0);
                    return;
                case ArrowDown:
                    world.tryMove(me, 1, 0);
                    return;
                case ArrowLeft:
                    world.tryMove(me, 0, -1);
                    return;
                case ArrowRight:
                    world.tryMove(me, 0, 1);
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
