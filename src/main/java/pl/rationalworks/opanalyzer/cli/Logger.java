package pl.rationalworks.opanalyzer.cli;

/**
 * @author Adam Winciorek
 */
public class Logger {

    public static void format(String format, Object... args) {
        System.out.format(format, args);
    }
}
