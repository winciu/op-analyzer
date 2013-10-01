package pl.rationalworks.opanalyzer.core;

/**
 * @author Adam Winciorek
 */
public class Fund {
    private final String name;
    private Money deposit;
    private Money registryAmount;
    private final OperationsOnFundSeries operationSeries;

    public Fund(String name) {
        this.name = name;
        this.operationSeries = new OperationsOnFundSeries();
        deposit = Money.ZERO;
        registryAmount = Money.ZERO;
    }

    public String getName() {
        return name;
    }

    public FundOperationResult performOperation(FundOperation operation) {
        OperationsOnFund operationsOnFund = this.operationSeries.getCurrentOperationsOnFund();
        if (operationsOnFund.areClosed()) {
            operationsOnFund = this.operationSeries.addNewOperationsOnFund();
        }
        FundOperationResult fundOperationResult = operationsOnFund.add(operation);
        this.deposit = fundOperationResult.getDeposit();
        this.registryAmount = fundOperationResult.getLastRegistryAmount();
        return fundOperationResult;
    }

    public Money getDeposit() {
        return deposit;
    }

    public Money getRegistryAmount() {
        return registryAmount;
    }

    /**
     * How much money we put in this fund form the beginning.
     *
     * @return
     */
    public Money totalDeposit() {
        Money totalDeposit = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            totalDeposit = totalDeposit.add(operationsOnFund.getTotalDeposit());
        }
        return totalDeposit;
    }

    /**
     * Calculate balance. This is the current balance from this found which is currently in my wallet.
     *
     * @return
     */
    public Money balance() {
        OperationsOnFund operationsOnFund = this.operationSeries.getCurrentOperationsOnFund();
        if (operationsOnFund.areClosed()) {
            return Money.ZERO;
        }
        return operationsOnFund.getBalance();
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
