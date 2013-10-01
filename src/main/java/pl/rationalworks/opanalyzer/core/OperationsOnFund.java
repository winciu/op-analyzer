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
    private Money balance;

    public OperationsOnFund() {
        this.operations = new ArrayList<FundOperation>();
        this.closed = false;
        this.balance = Money.ZERO;
        this.lastRegistryAmount = Money.ZERO;
        this.totalDeposit = Money.ZERO;
        this.deposit = Money.ZERO;
    }

    public FundOperationResult add(FundOperation operation) {
        this.operations.add(operation);
        switch (operation.getTransactionType()) {
            case PURCHASE:
            case OPENING_PURCHASE:
                this.totalDeposit = this.totalDeposit.add(operation.getAmount());
                this.deposit = this.deposit.add(operation.getAmount());
                this.lastRegistryAmount = operation.getRegistryAmount();
                this.balance = operation.getRegistryAmount().minus(this.deposit);
                break;
            case SWITCH:
                this.lastRegistryAmount = operation.getRegistryAmount();
                if (operation.getRegistryAmount().isZero()) {
                    this.balance = operation.getAmount().minus(this.deposit);
                    close();
                    break;
                }
                this.deposit = this.deposit.add(operation.getAmount());
                this.balance = operation.getRegistryAmount().minus(this.deposit);
                break;
            case REDEMPTION:
                this.balance = operation.getAmount().minus(this.deposit);
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

    public Money getBalance() {
        return balance;
    }

    public int count() {
        return this.operations.size();
    }

    public Money getTotalDeposit() {
        return totalDeposit;
    }
}
