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
    private Money incomings;

    public OperationsOnFund() {
        this.operations = new ArrayList<FundOperation>();
        this.registryAmount = Money.ZERO;
        this.deposit = Money.ZERO;
        this.balance = Money.ZERO;
        this.incomings = Money.ZERO;
    }


    public void digest(FundOperation operation) {
        addToOperations(operation);
        switch (operation.getTransactionType()) {
            case PURCHASE:
            case OPENING_PURCHASE:
                this.deposit = this.deposit.add(operation.getAmount());
                this.balance = operation.getRegistryAmount().minus(operation.getAmount()).minus(this.registryAmount);
                this.incomings = this.incomings.add(operation.getAmount());
                this.registryAmount = operation.getRegistryAmount();
                break;
            case SWITCH:
                if (operation.getRegistryAmount().isZero()) {
                    this.balance = operation.getAmount().minus(this.registryAmount);
                    this.registryAmount = operation.getRegistryAmount();
                    break;
                }
                this.incomings = this.incomings.add(operation.getAmount());
                this.balance = operation.getRegistryAmount().minus(this.registryAmount.add(operation.getAmount()));
                this.registryAmount = operation.getRegistryAmount();
                break;
            case REDEMPTION:
                this.balance = operation.getAmount().minus(this.incomings);
                this.registryAmount = operation.getRegistryAmount();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown transaction type '%s'", operation.getTransactionType().name()));
        }
    }

    private boolean addToOperations(FundOperation operation) {
        if (this.operations.isEmpty() && TransactionType.REDEMPTION.equals(operation.getTransactionType())) {
            throw new IllegalArgumentException(String.format("'%s' operation cannot be the first one", TransactionType.REDEMPTION.name()));
        }
        return this.operations.add(operation);
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

    public Money getIncomings() {
        return incomings;
    }

    @Override
    public Iterator<FundOperation> iterator() {
        return this.operations.iterator();
    }

    public boolean areClosed() {
        //it is enough just to check if there are some operations, since addToOperations method already checks if
        //redemption operation is not the first one. Any other operation is actually a fund purchase operation
        return !this.operations.isEmpty() && this.registryAmount.isZero();
    }

    public int count() {
        return this.operations.size();
    }

}
