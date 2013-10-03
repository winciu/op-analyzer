package pl.rationalworks.opanalyzer.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class OperationsOnFund implements Iterable<FundOperation> {

    private final List<FundOperation> operations;
    /**
     * Amount of money that was transfer to particular fund. Conversion/switch amount are included.
     */
    private Money deposit;
    private Money registryAmount;
    private Money balance;


    public OperationsOnFund() {
        this.operations = new ArrayList<FundOperation>();
        this.registryAmount = Money.ZERO;
        this.deposit = Money.ZERO;
        this.balance = Money.ZERO;
    }

    public void add(FundOperation operation) {
        this.operations.add(operation);
        switch (operation.getTransactionType()) {
            case PURCHASE:
            case OPENING_PURCHASE:
                this.deposit = this.deposit.add(operation.getAmount());
                this.registryAmount = operation.getRegistryAmount();
                this.balance = operation.getRegistryAmount().minus(this.registryAmount);
                break;
            case SWITCH:
                if (operation.getRegistryAmount().isZero()) {
                    this.balance = operation.getAmount().minus(this.registryAmount);
                    this.registryAmount = operation.getRegistryAmount();
                    break;
                }
                this.balance = operation.getRegistryAmount().minus(this.registryAmount.add(operation.getAmount()));
                this.registryAmount = operation.getRegistryAmount();
                break;
            case REDEMPTION:
                this.balance = operation.getAmount().minus(this.registryAmount);
                this.registryAmount = operation.getRegistryAmount();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown transaction type '%s'", operation.getTransactionType().name()));
        }
    }

    public Money getDeposit() {
        return deposit;
    }

    public Money getRegistryAmount() {
        return registryAmount;
    }

    public Money getBalance() {
        return balance;
    }

    @Override
    public Iterator<FundOperation> iterator() {
        return this.operations.iterator();
    }

    private boolean purchaseHasBeenDone() {
        for (FundOperation operation : operations) {
            if (TransactionType.PURCHASE.equals(operation.getTransactionType()) || TransactionType.OPENING_PURCHASE
                    .equals(operation.getTransactionType())) {
                return true;
            }
        }
        return false;
    }

    public boolean areClosed() {
        return purchaseHasBeenDone() && this.registryAmount.isZero();
    }

    public int count() {
        return this.operations.size();
    }

}
