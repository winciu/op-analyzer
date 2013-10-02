package pl.rationalworks.opanalyzer;

import org.junit.BeforeClass;
import org.junit.Test;
import pl.rationalworks.opanalyzer.core.Fund;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Funds;
import pl.rationalworks.opanalyzer.core.Money;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Adam Winciorek
 */
public class OperationsTest {
    private static final File INPUT_FILE = new File("src/test/resources", "operationsTest.in");
    private static final Funds funds = new Funds();


    @BeforeClass
    public static void onlyOnce() throws FileNotFoundException {
        InputFileParser parser = new InputFileParser();
        List<FundOperation> fundsOperations = parser.parseDataFile(INPUT_FILE);
        funds.performOperations(fundsOperations);
    }

    @Test
    public void incomeShouldBeCorrect() {
        assertThat(funds.totalBalance()).isEqualTo(new Money(170.97));
        assertThat(funds.balance()).isEqualTo(Money.ZERO);
    }

    @Test
    public void resultsForPANEFundShouldBeCorrect() {
        Fund paneFund = funds.findFundByName("PANE");
        assertThat(paneFund.totalDeposit()).isEqualTo(new Money(1000));
        assertThat(paneFund.totalBalance()).isEqualTo(new Money(39.00));
        assertThat(paneFund.balance()).isEqualTo(Money.ZERO);
    }

    @Test
    public void resultsForAAMISSFundShouldBeCorrect() {
        Fund aamissFund = funds.findFundByName("AAMISS");
        assertThat(aamissFund.operationSeriesCount()).isEqualTo(2);
        assertThat(aamissFund.totalBalance()).isEqualTo(new Money(2.75).add(new Money(7.56)));
        assertThat(aamissFund.totalDeposit()).isEqualTo(new Money(250).add(new Money(300)));
    }

    @Test
    public void resultsForSwitchedFundShouldBeCorrect() {
        Fund aosFund = funds.findFundByName("AOS");
        assertThat(aosFund.totalDeposit()).isEqualTo(new Money(5400));
        assertThat(aosFund.totalBalance()).isEqualTo(new Money(-110.32));
    }
}
