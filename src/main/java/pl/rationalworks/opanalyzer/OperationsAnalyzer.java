package pl.rationalworks.opanalyzer;

import asg.cliche.ShellFactory;
import pl.rationalworks.opanalyzer.cli.RootShell;

import java.io.IOException;

/**
 * @author Adam Winciorek
 */
public class OperationsAnalyzer {

    public static void main(String[] args) throws IOException {
        ShellFactory.createConsoleShell("OpAnalyzer", "", new RootShell()).commandLoop();
    }

}
