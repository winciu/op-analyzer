package pl.rationalworks.opanalyzer.core;

/**
 * @author Adam Winciorek
 */
public class FundOperationResult {
    private final Money deposit;
    private final Money lastRegistryAmount;

    public FundOperationResult(Money deposit, Money lastRegistryAmount) {
        this.deposit = deposit;
        this.lastRegistryAmount = lastRegistryAmount;
    }

    public Money getDeposit() {
        return deposit;
    }

    public Money getLastRegistryAmount() {
        return lastRegistryAmount;
    }

}
