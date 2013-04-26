package pl.rationalworks.opanalyzer.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class OperationsOnFund implements Iterable<FundOperation> {

    private final List<FundOperation> operations;
    private boolean closed;
    /**
     * Amount of money that was transfer to particular fund. Conversion/switch amount are included.
     */
    private Money deposit;
    /**
     * Amount of new money that was transfer to this fund. Conversion/switch amount are not included.
     */
    private Money totalDeposit;
    private Money lastRegistryAmount;
    private Money income;

    public OperationsOnFund() {
        this.operations = new ArrayList<FundOperation>();
        this.closed = false;
        this.income = Money.ZERO;
        this.lastRegistryAmount = Money.ZERO;
        this.totalDeposit = Money.ZERO;
        this.deposit = Money.ZERO;
    }

    public FundOperationResult add(FundOperation operation) {
        this.operations.add(operation);
        switch (operation.getTransactionType()) {
            case NABYCIE:
            case NABYCIE_OTWIERAJACE:
                this.totalDeposit = this.totalDeposit.add(operation.getAmount());
                this.deposit = this.deposit.add(operation.getAmount());
                this.lastRegistryAmount = operation.getRegistryAmount();
                this.income = operation.getRegistryAmount().minus(this.deposit);
                break;
            case KONWERSJA:
                this.lastRegistryAmount = operation.getRegistryAmount();
                if (operation.getRegistryAmount().isZero()) {
                    this.income = operation.getAmount().minus(this.deposit);
                    close();
                    break;
                }
                this.deposit = this.deposit.add(operation.getAmount());
                this.income = operation.getRegistryAmount().minus(this.deposit);
                break;
            case ODKUPIENIE:
                this.income = operation.getAmount().minus(this.deposit);
                this.lastRegistryAmount = operation.getRegistryAmount();
                close();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown transaction type '%s'", operation.getTransactionType().name()));
        }
        return new FundOperationResult(this.deposit, this.lastRegistryAmount);
    }

    @Override
    public Iterator<FundOperation> iterator() {
        return this.operations.iterator();
    }

    /**
     * Closes current operations. Closing means that the recently added (current) operation is the last one, so we need
     * to close this operation list. This is a result of conversion or sold of a given fund.
     */
    private void close() {
        this.closed = true;
    }

    public boolean areClosed() {
        return closed;
    }

    public Money getIncome() {
        return income;
    }

    public int count() {
        return this.operations.size();
    }

    public Money getTotalDeposit() {
        return totalDeposit;
    }
}
