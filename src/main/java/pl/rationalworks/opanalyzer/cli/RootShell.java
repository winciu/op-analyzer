package pl.rationalworks.opanalyzer.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import pl.rationalworks.opanalyzer.InputFileParser;
import pl.rationalworks.opanalyzer.core.Fund;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Funds;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class RootShell {

    private final Funds funds;

    public RootShell() {
        this.funds = new Funds();
    }

    @Command(name = "readData", abbrev = "rd", description = "Reads data from a file")
    public void readDataFrom(@Param(name = "filePath", description = "Path to operations data file") String aFilePath) throws FileNotFoundException {
        InputFileParser fileParser = new InputFileParser();
        List<FundOperation> fundOperations = fileParser.parseDataFile(new File(aFilePath));
        processOperations(fundOperations);
    }

    private void processOperations(List<FundOperation> fundOperations) {
        for (FundOperation fundOperation : fundOperations) {
            this.funds.performOperation(fundOperation);
        }
    }

    @Command(abbrev = "pwd", description = "Prints current working directory")
    public String printWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    @Command(abbrev = "now", description = "Show funds currently in wallet")
    public void showFundsICurrentlyHave() {
        Collection<Fund> currentFunds = this.funds.currentFunds();
        Logger.format("%-30s | %-8s | %-10s | %-10s\n", "Fund name", " Current value", "Deposit", "Income");
        for (Fund currentFund : currentFunds) {
            currentFund.getName();
        }
    }

    @Command(abbrev = "st", description = "Shows overall status (summary)")
    public void showOverallStatus() {
        showKeyValuePair("Total income", funds.totalIncome().value());
        showKeyValuePair("Total deposit", funds.totalDeposit().value());
    }

    private static void showKeyValuePair(String header, double value) {
        Logger.format("%-15s: %.2f\n", header, value);
    }
}
