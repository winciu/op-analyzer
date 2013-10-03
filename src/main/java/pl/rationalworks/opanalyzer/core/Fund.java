package pl.rationalworks.opanalyzer.core;

/**
 * @author Adam Winciorek
 */
public class Fund {
    private final String name;
    /**
     * Stores value of all deposits and money transfer after switches
     */
    private Money incomings;
    private Money deposit;
    private Money registryAmount;
    private final OperationsOnFundSeries operationSeries;

    public Fund(String name) {
        this.name = name;
        this.operationSeries = new OperationsOnFundSeries();
        incomings = Money.ZERO;
        registryAmount = Money.ZERO;
    }

    public String getName() {
        return name;
    }

    public void performOperation(FundOperation operation) {
        OperationsOnFund operationsOnFund = this.operationSeries.getCurrentOperationsOnFund();
        if (operationsOnFund.areClosed()) {
            operationsOnFund = this.operationSeries.addNewOperationsOnFund();
        }
        operationsOnFund.add(operation);
        this.deposit = operationsOnFund.getDeposit();
        this.incomings = this.incomings.add(operation.getAmount());
        this.registryAmount = operationsOnFund.getRegistryAmount();
    }

    public Money getDeposit() {
        return deposit;
    }

    public Money getIncomings() {
        return incomings;
    }

    public Money getRegistryAmount() {
        return registryAmount;
    }

    /**
     * Calculate balance. This is the current balance from this found which is currently in my wallet.
     *
     * @return
     */
    public Money balance() {
        if (!currentlyInWallet()) {
            return Money.ZERO;
        }
        return registryAmount.minus(incomings);
    }

    public Money income() {
        if(!currentlyInWallet()) {
            return Money.ZERO;
        }
        Money value = balance();
        if (value.isNegative()) {
            return Money.ZERO;
        }
        return value;
    }

    public Money loss() {
        if(!currentlyInWallet()) {
            return Money.ZERO;
        }
        Money value = balance();
        if (value.isPositive()) {
            return Money.ZERO;
        }
        return value;
    }

    /**
     * Calculate balance. This is the total balance from the very first transaction on this fund.
     *
     * @return
     */
    public Money totalBalance() {
        Money totalBalance = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            totalBalance = totalBalance.add(operationsOnFund.getBalance());
        }
        return totalBalance;
    }

    public OperationsOnFund currentOperations() {
        return this.operationSeries.getCurrentOperationsOnFund();
    }

    public boolean currentlyInWallet() {
        return !currentOperations().areClosed();
    }

    public int operationSeriesCount() {
        return this.operationSeries.count();
    }

    /**
     * How much money we put in this fund form the beginning.
     *
     * @return
     */
    public Money totalDeposit() {
        Money totalDeposit = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            totalDeposit = totalDeposit.add(operationsOnFund.getDeposit());
        }
        return totalDeposit;
    }

    public Money totalIncome() {
        Money income = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            final Money balance = operationsOnFund.getBalance();
            if (balance.isPositive()) {
                income = income.add(balance);
            }
        }
        return income;
    }

    public Money totalLoss() {
        Money loss = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            final Money balance = operationsOnFund.getBalance();
            if (balance.isNegative()) {
                loss = loss.add(balance);
            }
        }
        return loss;
    }
}
