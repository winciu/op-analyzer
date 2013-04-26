package pl.rationalworks.opanalyzer;

import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Money;
import pl.rationalworks.opanalyzer.core.TransactionType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Adam Winciorek
 */
public class InputFileParser {
    private static final String ENTRY_ITEM_SEPARATOR = ";";

    public List<FundOperation> parseDataFile(File inputFile) throws FileNotFoundException {
        List<FundOperation> entries = new ArrayList<FundOperation>();
        Scanner scanner = new Scanner(inputFile, "UTF-8");
        while (scanner.hasNextLine()) {
            String entryLine = scanner.nextLine();
            entries.add(createFundOperationFromFileLine(entryLine));
        }
        return entries;
    }

    private static FundOperation createFundOperationFromFileLine(String entryLine) {
        String[] entryFields = entryLine.split(ENTRY_ITEM_SEPARATOR);
        TransactionType transactionType = TransactionType.createByName(entryFields[3]);
        return new FundOperation(entryFields[0], entryFields[1], entryFields[2], transactionType,
                new Money(entryFields[4]), new Money(entryFields[5]));
    }
}
