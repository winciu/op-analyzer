package pl.rationalworks.opanalyzer.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import com.google.common.base.Strings;
import jlibs.core.lang.Ansi;
import pl.rationalworks.opanalyzer.InputFileParser;
import pl.rationalworks.opanalyzer.core.Fund;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Funds;
import pl.rationalworks.opanalyzer.core.Money;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class RootShell {

    private final Funds funds;
    private static final Ansi tableHeaderFormatter = new Ansi(Ansi.Attribute.UNDERLINE, Ansi.Color.YELLOW, null);

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
        String headerFormat = "%-50s | %-14s | %-10s | %-10s\n";
        String paddedName = Strings.padEnd(Strings.padStart("Fund name", 25, ' '), 40, ' ');
        Logger.format(headerFormat, tableHeaderFormatter.colorize(paddedName), tableHeaderFormatter.colorize("Current value"),
                tableHeaderFormatter.colorize("Deposit"), tableHeaderFormatter.colorize("Balance"));
        String rowFormat = "%-40s | %-14s | %-10s | %-10s\n";
        for (Fund currentFund : currentFunds) {
            Logger.format(rowFormat, currentFund.getName(), MoneyFormatter.format(currentFund.getRegistryAmount()),
                    MoneyFormatter.format(currentFund.getDeposit()), MoneyFormatter.format(currentFund.balance()));
        }
    }

    @Command(abbrev = "st", description = "Shows overall status (summary)")
    public void showOverallStatus() {
        showMoneyFieldValue("Current balance", funds.balance());
        showMoneyFieldValue("Total balance", funds.totalBalance());
        showMoneyFieldValue("Total income", funds.totalIncome());
        showMoneyFieldValue("Total loss", funds.totalLoss());
    }

    private static void showMoneyFieldValue(String header, Money money) {
        Logger.format("%-15s: %s\n", header, MoneyFormatter.format(money));
    }

}
