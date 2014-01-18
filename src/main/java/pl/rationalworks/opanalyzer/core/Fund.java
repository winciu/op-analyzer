package pl.rationalworks.opanalyzer.core;

/**
 * @author Adam Winciorek
 */
public class Fund {
    private final String name;
    /**
     * Stores value of all deposits and money transfer after switches
     */
    private Money registryAmount;
    private final OperationsOnFundSeries operationSeries;
    private final int id;

    public Fund(String name, int id) {
        this.name = name;
        this.id = id;
        this.operationSeries = new OperationsOnFundSeries();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void performOperation(FundOperation operation) {
        OperationsOnFund operationsOnFund = this.operationSeries.getCurrentOperationsOnFund();
        if (operationsOnFund.areClosed()) {
            operationsOnFund = this.operationSeries.addNewOperationsOnFund();
        }
        operationsOnFund.digest(operation);
        this.registryAmount = operationsOnFund.getRegistryAmount();
    }

    /**
     * This method returns the initial fund deposit. If purchase was the first operation on this fund, then this fund is an
     * initial fund. If conversion is the first operation on this fund it means that the initial fund is the fund this one
     * was converted from. This is the recursive check.
     * @return this fund if its first operation was purchase, or the fund this fund was converted from in case the first
     * operation on this fund is a switch operation.
     */
    public Money getInitialFundDeposit() {
        return findInitialFundDeposit(this);
    }

    private Money findInitialFundDeposit(Fund fundToCheck) {
        Money deposit = Money.ZERO;
        for (FundOperation currentOperation : fundToCheck.currentOperations()) {
            if (currentOperation.isPurchase()) {
                deposit = deposit.add(currentOperation.getAmount());
            }
            if (currentOperation.isSwitchFinal()) {
                deposit = deposit.add(findInitialFundDeposit(currentOperation.getSwitchedFromFund()));
            }
        }
        return deposit;
    }

    public Money getDeposit() {
        return currentOperations().getDeposit();
    }

    public Money getIncomings() {
        return currentOperations().getIncomings();
    }

    public Money getRegistryAmount() {
        return this.registryAmount;
    }

    public void setRegistryAmount(Money registryAmount) {
        this.registryAmount = registryAmount;
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
        return registryAmount.minus(currentOperations().getIncomings());
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

    public boolean hasId(int fundId) {
        return this.id == fundId;
    }
}
