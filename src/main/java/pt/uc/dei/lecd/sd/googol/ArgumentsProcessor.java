package pt.uc.dei.lecd.sd.googol;



import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgumentsProcessor {

    private String host = "localhost"; // Default host
    private int port = 1099; // Default port

    public ArgumentsProcessor(String[] args) {

        Options options = new Options();
        options.addOption(Option.builder("h")
            .longOpt("host")
            .hasArg().desc("RMI googol registry host")
            .build());
        options.addOption(Option.builder("p")
            .longOpt("port")
            .hasArg().desc("RMI googol registry port")
            .build());

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                host = cmd.getOptionValue("h");
            }

            if (cmd.hasOption("p")) {
                port = Integer.parseInt(cmd.getOptionValue("p"));
            }
        } catch (ParseException | NumberFormatException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            printUsage(options);
            System.exit(1);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Downloader", options);
    }

}
