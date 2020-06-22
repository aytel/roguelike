package com.aytel;

import com.aytel.actors.*;
import com.aytel.actors.strategies.Aggressive;
import com.aytel.actors.strategies.Dumb;
import com.aytel.actors.strategies.Sneaky;
import com.aytel.actors.strategies.Strategy;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.abs;

public class World {
    private int w, h;
    private Tile[][] map;
    private List<Actor> actors;
    Player me;

    private DefaultTerminalFactory defaultTerminalFactory;
    private Terminal terminal;
    private TextGraphics textGraphics;

    private static final int PLAYER_HP = 10;
    private static final int PLAYER_ATTACK = 2;
    private static final int MOB_HP = 5;
    private static final int MOB_ATTACK = 1;
    private static final int WALL_CHANCE = 8;
    private static final int MOB_CHANCE = 15;

    private World(int w, int h, Tile[][] map) throws IOException {
        this.w = w;
        this.h = h;
        this.map = map;
        this.actors = new ArrayList<>();

        this.defaultTerminalFactory = new DefaultTerminalFactory();
        this.terminal = defaultTerminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorVisible(false);
        this.textGraphics = terminal.newTextGraphics();
    }

    public static World generate(int w, int h) throws IOException {
        List<Position> used = new ArrayList<>();
        Random random = new Random();

        w += 2;
        h += 2;

        Tile[][] map = new Tile[w][h];
        for (int i = 0; i < w; i++) {
            map[0][i] = map[h - 1][i] = Tile.WALL;
        }
        for (int i = 0; i < h; i++) {
            map[i][0] = map[i][w - 1] = Tile.WALL;
        }

        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                int checkWall = random.nextInt(WALL_CHANCE);
                map[i][j] = checkWall != 0 ? Tile.EMPTY : Tile.WALL;
            }
        }

        World world = new World(w, h, map);

        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                if (map[i][j] != Tile.EMPTY) {
                    continue;
                }

                int checkMob = random.nextInt(MOB_CHANCE);
                if (checkMob == 0) {
                    Position position = new Position();
                    position.x = j;
                    position.y = i;
                    used.add(position);

                    Strategy strategy = null;

                    int strategySwitch = abs(random.nextInt() % 3);

                    switch (strategySwitch) {
                        case 0:
                            strategy = new Dumb();
                            break;
                        case 1:
                            strategy = new Aggressive();
                            break;
                        case 2:
                            strategy = new Sneaky();
                            break;
                    }

                    if (strategy == null) {
                        assert(false);
                    }

                    world.actors.add(new Mob(MOB_HP, MOB_ATTACK, position.x, position.y, world, strategy));
                    continue;
                }
            }
        }

        while (true) {
            Position position = new Position();
            position.x = random.nextInt(w - 2) + 1;
            position.y = random.nextInt(h - 2) + 1;
            if (used.contains(position) || map[position.y][position.x] != Tile.EMPTY) {
                continue;
            }
            Player me = new Player(PLAYER_HP, PLAYER_ATTACK, position.x, position.y, world,
                new Controller());
            world.me = me;
            world.actors.add(me);
            break;
        }

        return world;
    }

    public void run() throws IOException {
        while (actors.contains(me) && actors.size() > 1) {
            List<Actor> toRemove = new ArrayList<>();
            for (Actor actor: actors) {
                if (actor.getHp() <= 0) {
                    continue;
                }
                render(actor);
                actor.act();
            }
            for (Actor actor: actors) {
                if (actor.getHp() <= 0) {
                    toRemove.add(actor);
                }
            }
            for (Actor actor: toRemove) {
                actors.remove(actor);
            }
        }

        if (actors.contains(me)) {
            win();
        } else {
            lose();
        }
    }

    private void render(Actor toTurn) throws IOException {
        //terminal.clearScreen();
        textGraphics.fill(' ');
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                textGraphics.putString(j, i, map[i][j].getSymbol());
            }
        }

        textGraphics.putString(w + 1, 0, "HP: " + me.getHp() + " + " + me.getArmor());
        textGraphics.putString(w + 1, 1, "ATK: " + me.getAttack());

        for (Actor actor: actors) {
            actor.draw(textGraphics, this, toTurn, actor.getPosition().x, actor.getPosition().y, null);
        }

        terminal.flush();
    }

    public List<Actor> getActors() {
        return actors;
    }

    public Player getMe() {
        return me;
    }

    private void win() throws IOException {
        render(null);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(1, h + 1, "YOU WON. Press any key to exit.");
        terminal.flush();
        terminal.readInput();
        terminal.close();
    }

    private void lose() throws IOException {
        render(null);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(1, h + 1, "YOU LOST. Press any key to exit.");
        terminal.flush();
        terminal.readInput();
        terminal.close();
    }

    public boolean tryMove(Actor actor, int dy, int dx) {
        int nx = actor.getPosition().x + dx, ny = actor.getPosition().y + dy;

        if (nx < 0 || nx >= w || ny < 0 || ny >= w) {
            return false;
        }

        if (map[ny][nx] != Tile.EMPTY) {
            return false;
        }

        Optional<Actor> second = actors.stream().filter(cand -> cand.getPosition().x == nx && cand.getPosition().y == ny)
                                    .findFirst();

        if (second.isEmpty()) {
            actor.move(dy, dx);
            return true;
        } else {
            Actor toBeat = second.get();

            try {
                toBeat.draw(textGraphics, this, actor, toBeat.getPosition().x, toBeat.getPosition().y, TextColor.ANSI.YELLOW);
                terminal.flush();
                Thread.sleep(200);
                toBeat.draw(textGraphics, this, actor, toBeat.getPosition().x, toBeat.getPosition().y, null);
                terminal.flush();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            actor.performAttack(toBeat);
            if (toBeat.getHp() <= 0) {
                actor.move(dy, dx);
                return true;
            } else {
                return false;
            }
        }
    }

    public int[] getDistanceAndDir(Position from, Position to) {
        int[] result = new int[3];
        int dx = to.x - from.x;
        int dy = to.y - from.y;
        result[0] = abs(dx) + abs(dy);
        result[1] = dy == 0 ? 0 : dy / abs(dy);
        result[2] = dx == 0 ? 0 : dx / abs(dx);
        return result;
    }

    public KeyStroke getInput() throws IOException {
        return terminal.readInput();
    }

    public enum Tile {
        WALL("X"),
        EMPTY(" ");

        private final String symbol;

        Tile(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
