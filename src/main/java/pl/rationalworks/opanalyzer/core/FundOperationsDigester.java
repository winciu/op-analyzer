package pl.rationalworks.opanalyzer.core;

import java.util.Arrays;

/**
 * @author Adam Winciorek
 */
public class FundOperationsDigester {

    private Fund switchFromFund;

    public FundOperationsDigester() {
    }

    public Funds digestOperations(Iterable<FundOperation> fundOperations) {
        return doDigestion(fundOperations);
    }

    public Funds digestOperations(FundOperation... operations) {
        return doDigestion(Arrays.asList(operations));
    }

    private Funds doDigestion(Iterable<FundOperation> fundOperations) {
        Funds funds = new Funds();
        for (FundOperation fundOperation : fundOperations) {
            performOperation(funds, fundOperation);
        }
        return funds;
    }

    private void performOperation(Funds funds, FundOperation operation) {
        Fund fund = funds.findFundByName(operation.getFoundName());
        if (fund == null) {
            fund = new Fund(operation.getFoundName());
            funds.addFund(fund);
        }
        fund.performOperation(operation);
        updateSwitchFromFund(operation, fund);
    }

    private void updateSwitchFromFund(FundOperation operation, Fund fund) {
        if (operation.isSwitchFinal()) {
            operation.setSwitchedFromFund(this.switchFromFund);
            this.switchFromFund = null;
        }
        if (operation.isSwitchInitial()) {
            this.switchFromFund = fund;
        }
    }

}
