package pl.rationalworks.opanalyzer.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import com.google.common.base.Strings;
import jlibs.core.lang.Ansi;
import pl.rationalworks.opanalyzer.InputFileParser;
import pl.rationalworks.opanalyzer.core.Fund;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.FundOperationsDigester;
import pl.rationalworks.opanalyzer.core.Funds;
import pl.rationalworks.opanalyzer.core.Money;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class RootShell {

    private Funds funds;
    private static final Ansi tableHeaderFormatter = new Ansi(Ansi.Attribute.UNDERLINE, Ansi.Color.YELLOW, null);

    public RootShell() {
        this.funds = new Funds();
    }

    @Command(name = "readData", abbrev = "rd", description = "Reads data from a file")
    public void readDataFrom(@Param(name = "filePath", description = "Path to operations data file") String aFilePath) throws FileNotFoundException {
        InputFileParser fileParser = new InputFileParser();
        List<FundOperation> fundOperations = fileParser.parseDataFile(new File(aFilePath));
        FundOperationsDigester operationsDigester = new FundOperationsDigester();
        this.funds = operationsDigester.digestOperations(fundOperations);
    }

    @Command(abbrev = "pwd", description = "Prints current working directory")
    public String printWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    @Command(abbrev = "now", description = "Show funds currently in wallet")
    public void showFundsICurrentlyHave() {
        String headerFormat = "%-5s | %-50s | %-20s | %-20s | %-10s\n";
        String paddedName = Strings.padEnd(Strings.padStart("Fund name", 25, ' '), 40, ' ');
        Logger.format(headerFormat, tableHeaderFormatter.colorize("Id"), tableHeaderFormatter.colorize(paddedName),
                tableHeaderFormatter.colorize("Current"), tableHeaderFormatter.colorize("Incomings"),
                tableHeaderFormatter.colorize("Balance"));
        String rowFormat = "%-2s | %-40s | %-10s | %-10s | %-10s\n";
        for (Fund currentFund : funds.currentFunds()) {
            Logger.format(rowFormat, currentFund.getId(), currentFund.getName(), MoneyFormatter.format(currentFund.getRegistryAmount()),
                    MoneyFormatter.format(currentFund.getIncomings()), MoneyFormatter.format(currentFund.balance()));
        }
        Logger.newLine();
    }

    @Command(abbrev = "st", description = "Shows overall status (summary)")
    public void showOverallStatus() {
        showMoneyFieldValue("Current balance", funds.balance());
        showMoneyFieldValue("Current income", funds.income());
        showMoneyFieldValue("Current loss", funds.loss());
        Logger.newLine();
        showMoneyFieldValue("Current amount", funds.amount());
        showMoneyFieldValue("Current deposit", funds.deposit());
        showMoneyFieldValue("Session balance", funds.amount().minus(funds.deposit()));
        Logger.newLine();
        showMoneyFieldValue("Total balance", funds.totalBalance());
        showMoneyFieldValue("Total income", funds.totalIncome());
        showMoneyFieldValue("Total loss (incl. tax)", funds.totalLoss());
        Logger.newLine();
    }

    private static void showMoneyFieldValue(String header, Money money) {
        Logger.format("%-20s: %s\n", header, MoneyFormatter.format(money));
    }

    @Command(abbrev = "h", description = "Shows operations history in merged form")
    public void showHistory() {

    }

    @Command(abbrev = "set", description = "Sets current registry amount for a given fund")
    public void setRegistryAmountForFund(int fundId, double amount)  {
        this.funds.setRegistryAmountForFund(fundId, new Money(amount));
    }

}
