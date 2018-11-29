import org.apache.commons.cli.*;

/**
 * Command line runner class
 */
public class PlayWikIRMan {

    public static void main(String[] args) {
        ExtractorOptions eo = ExtractorOptions.INFO_TABLE;
        OutputOptions oo = OutputOptions.TERM_TEXT;
        Options options = new Options();
        options.addOption("help", false, "prints this message");
        options.addOption("asciiArt", false, "print out ascii art after each round");
        options.addOption("fromBody", false, "extract word bank from the html body of Wikipedia");
        options.addOption("hint", true, "how many correct guesses before a hint is displayed");
        options.addOption("turns", true, "how many turns you have per round");
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse( options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error parsing command line arguments");
            System.exit(-1);
        }
        if (cmd.hasOption("help")) {
            new HelpFormatter().printHelp("WikIRMan [options]", options);
        } else {
            if (cmd.hasOption("fromBody")) {
                eo = ExtractorOptions.BODY;
            }
            if (cmd.hasOption("asciiArt")) {
                oo = OutputOptions.TERM_ASCII_ART;
            }
            new GameLoop(eo, oo, Integer.parseInt(cmd.getOptionValue("t", "7")),
                    Integer.parseInt(cmd.getOptionValue("h", "0")));
        }
    }

}
