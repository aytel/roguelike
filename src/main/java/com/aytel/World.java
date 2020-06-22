package com.aytel;

import com.aytel.actors.*;
import com.aytel.actors.items.Item;
import com.aytel.actors.items.ItemBuilder;
import com.aytel.actors.mobs.MobBuilder;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;
import java.util.*;

import static com.aytel.actors.Move.*;
import static java.lang.Math.abs;

public class World {
    private static final int PLAYER_HP = 10;
    private static final int PLAYER_ATTACK = 2;
    private static final int WALL_CHANCE = 8;
    private static final int MOB_CHANCE = 15;
    private static final int ITEM_CHANCE = 15;

    private int w, h;
    private Tile[][] map;
    private List<Actor> actors;
    private Item[][] items;
    private Player me;

    static private Random random = new Random();

    private DefaultTerminalFactory defaultTerminalFactory;
    private Terminal terminal;
    private TextGraphics textGraphics;

    private World(int w, int h, Tile[][] map, Item[][] items, List<Actor> actors, Player me) {
        this.w = w;
        this.h = h;
        this.map = map;
        this.actors = actors;
        this.items = items;
        this.me = me;
    }

    public static World generate(int w, int h) {
        w += 2;
        h += 2;

        Tile[][] map = new Tile[h][w];
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

        boolean[][] itemsPlaces = new boolean[h][w];

        markPlaces(w, h, random, map, itemsPlaces, ITEM_CHANCE);

        boolean[][] mobsPlaces = new boolean[h][w];

        markPlaces(w, h, random, map, mobsPlaces, MOB_CHANCE);

        int myX, myY;

        while (true) {
            myX = random.nextInt(w - 2) + 1;
            myY = random.nextInt(h - 2) + 1;
            if (map[myY][myX] != Tile.EMPTY || mobsPlaces[myY][myX]) {
                continue;
            }
            break;
        }

        return generateFromPlaces(w, h, map, itemsPlaces, mobsPlaces, myY, myX);
    }

    public static World generateFromMap(File file) throws Exception {
        Scanner scanner = new Scanner(file);
        int w = scanner.nextInt();
        int h = scanner.nextInt();
        Tile[][] map = new Tile[h][w];
        boolean[][] itemsPlaces = new boolean[h][w];
        boolean[][] mobsPlaces = new boolean[h][w];
        int myX = -1, myY = -1;

        for (int i = 0; i < h; i++) {
            String line = scanner.nextLine();
            for (int j = 0; j < w; j++) {
                map[i][j] = Tile.EMPTY;

                switch (line.charAt(j)) {
                    case ' ':
                        break;
                    case '@':
                        if (myX != -1 || myY != -1) {
                            throw new Exception("More than one player");
                        }
                        myX = j;
                        myY = i;
                        break;
                    case 'i':
                        itemsPlaces[i][j] = true;
                        break;
                    case 'M':
                        mobsPlaces[i][j] = true;
                        break;
                    case 'X':
                        map[i][j] = Tile.WALL;
                        break;
                    default:
                        throw new Exception(String.format("Unknown tile at row %d, column %d", i, j));
                }
            }
        }

        if (myX == -1 || myY == -1) {
            throw new Exception("No player");
        }

        return generateFromPlaces(w, h, map, itemsPlaces, mobsPlaces, myY, myX);
    }

    private static World generateFromPlaces(int w, int h, Tile[][] map, boolean[][] itemsPlaces, boolean[][] mobsPlaces, int myY, int myX) {
        Item[][] items = new Item[h][w];
        List<Actor> actors = new ArrayList<>();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (itemsPlaces[i][j]) {
                    items[i][j] = ItemBuilder.generate();
                }
                if (mobsPlaces[i][j]) {
                    actors.add(MobBuilder.generate(i, j));
                }
            }
        }

        Player me = new Player(PLAYER_HP, PLAYER_ATTACK, myX, myY, new Controller());
        actors.add(me);

        return new World(w, h, map, items, actors, me);
    }

    private static void markPlaces(int w, int h, Random random, Tile[][] map, boolean[][] places, int chance) {
        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                if (map[i][j] != Tile.EMPTY) {
                    continue;
                }

                int checkItem = random.nextInt(chance);
                if (checkItem == 0) {
                    places[i][j] = true;
                }
            }
        }
    }

    public void run() throws IOException {
        this.defaultTerminalFactory = new DefaultTerminalFactory();
        this.terminal = defaultTerminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorVisible(false);
        this.textGraphics = terminal.newTextGraphics();

        while (actors.contains(me) && actors.size() > 1) {
            List<Actor> toRemove = new ArrayList<>();
            for (Actor actor: actors) {
                if (actor.getHp() <= 0) {
                    continue;
                }
                actor.beforeRender(this);
                render(actor);
                actor.act(this);
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
        textGraphics.fill(' ');
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                textGraphics.putString(j, i, map[i][j].getSymbol());
            }
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (items[i][j] != null) {
                    items[i][j].draw(textGraphics, this, toTurn, j, i, null);
                }
            }
        }

        renderMyInfo();

        for (Actor actor: actors) {
            actor.draw(textGraphics, this, toTurn, actor.getPosition().x, actor.getPosition().y, null);
        }

        terminal.flush();
    }

    public void renderMyInfo() {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

        textGraphics.putString(w + 1, 0, "HP: " + me.getHp());
        textGraphics.putString(w + 1, 1, "ARM: " + me.getArmor());
        textGraphics.putString(w + 1, 2, "ATK: " + me.getAttack());
        textGraphics.putString(w + 1, 3, "CONF: " + me.getConfusion());

        for (int i = 0; i < me.getItems().size(); i++) {
            textGraphics.putString(w + 1, 5 + i, "  " + (i + 1) + ". " + me.getItems().get(i).name);
        }

        if (me.curItem != -1) {
            textGraphics.putString(w + 1, 5 + me.curItem, "*");
        }
    }

    public List<Actor> getActors() {
        return actors;
    }

    public Item[][] getItems() {
        return items;
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

    public boolean tryMove(Actor actor, Move move) {
        int dx = move.dx;
        int dy = move.dy;
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

    public Move getMove(int dy, int dx) {
        if (dy < 0) {
            return UP;
        }
        if (dy > 0) {
            return DOWN;
        }
        return dx > 0 ? RIGHT : LEFT;
    }

    public KeyStroke getInput() throws IOException {
        return terminal.readInput();
    }

    public void flush() {
        try {
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
