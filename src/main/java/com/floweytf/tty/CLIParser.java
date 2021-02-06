package com.floweytf.tty;

import org.apache.commons.cli.*;

public class CLIParser {
    public static CommandLine parse(String... args) {
        // parse cli arguments
        Options options = new Options();

        Option r1 = new Option("d", "debug", false, "runs in debug mode");
        r1.setRequired(false);

        Option r2 = new Option(null, "no-timeout", false, "forces no timeout");
        r2.setRequired(false);

        Option r3 = new Option(null, "config", true, "sets tty file to open");
        r3.setRequired(false);

        options.addOption(r1);
        options.addOption(r2);
        options.addOption(r3);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ttyserver", options);
            System.exit(1);
        }

        return cmd;
    }
}