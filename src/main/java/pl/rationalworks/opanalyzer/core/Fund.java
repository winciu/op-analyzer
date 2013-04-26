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
     * Calculate income. This is the current income from this found which is currently in my wallet.
     * @return
     */
    public Money income() {
        OperationsOnFund operationsOnFund = this.operationSeries.getCurrentOperationsOnFund();
        if (operationsOnFund.areClosed()) {
            return Money.ZERO;
        }
        return operationsOnFund.getIncome();
    }

    /**
     * Calculate income. This is the total income from the very first transaction on this fund.
     * @return
     */
    public Money totalIncome() {
        Money totalIncome = Money.ZERO;
        for (OperationsOnFund operationsOnFund : this.operationSeries) {
            totalIncome = totalIncome.add(operationsOnFund.getIncome());
        }
        return totalIncome;
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
}
