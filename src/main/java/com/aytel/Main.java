package com.aytel;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option loadGame = new Option("l", false, "load from save");
        Option loadMap = new Option("m", true, "load from map");
        Option saveFile = new Option("s", true, "file to save");

        options.addOption(loadGame);
        options.addOption(loadMap);
        options.addOption(saveFile);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        cmd = parser.parse(options, args);

        World world;

        if (cmd.hasOption("m")) {
            world = World.generateFromMap(new File(cmd.getOptionValue("m")));
        } else {
            world = World.generate(20, 10);
        }
        world.run();
    }
}
