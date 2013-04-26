package pl.rationalworks.opanalyzer;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Test;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Money;
import pl.rationalworks.opanalyzer.core.TransactionType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class InputFileParserTest {
    private static final File INPUT_FILE = new File("src/test/resources", "parserTest.in");

    @Test
    public void shouldImportAllLines() throws FileNotFoundException {
        InputFileParser parser = new InputFileParser();
        List<FundOperation> fundOperations = parser.parseDataFile(INPUT_FILE);
        Assert.assertEquals("incorrect number of rows", 4, fundOperations.size());
    }

    @Test
    public void dataShouldBeImportedCorrectly() throws FileNotFoundException {
        InputFileParser parser = new InputFileParser();
        List<FundOperation> operationsFromFile = parser.parseDataFile(INPUT_FILE);
        FundOperation op1 = new FundOperation("03-07-2009", "06-07-2009", "PANEF", TransactionType.NABYCIE, new Money(100.00), new Money(99.97));
        FundOperation op2 = new FundOperation("29-07-2009", "30-07-2009", "AAS", TransactionType.NABYCIE, new Money(200.00), new Money(200.01));
        FundOperation op3 = new FundOperation("29-07-2009", "30-07-2009", "AAMS", TransactionType.NABYCIE, new Money(200.00), new Money(200.00));
        FundOperation op4 = new FundOperation("27-08-2009", "28-08-2009", "PANEF", TransactionType.NABYCIE, new Money(100.00), new Money(214.28));
        Assertions.assertThat(operationsFromFile).containsExactly(op1,op2,op3,op4);
    }
}
