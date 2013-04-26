package pl.rationalworks.opanalyzer.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Adam Winciorek
 */
public class Funds {

    private final Map<String, Fund> funds;

    public Funds() {
        funds = new HashMap<String, Fund>();
    }

    public Fund findFundByName(String fundName) {
        return this.funds.get(fundName);
    }

    public void performOperation(FundOperation operation) {
        String fundName = operation.getFoundName();
        Fund fund = findFundByName(fundName);
        if (fund == null) {
            fund = new Fund(fundName);
            this.funds.put(fundName, fund);
        }
        fund.performOperation(operation);
    }

    public void performOperations(FundOperation... operations) {
        for (FundOperation operation : operations) {
            performOperation(operation);
        }
    }

    public void performOperations(Iterable<FundOperation> operations) {
        for (FundOperation operation : operations) {
            performOperation(operation);
        }
    }

    /**
     * Returns total number of distinct funds from the beginning.
     *
     * @return
     */
    public int totalCount() {
        return funds.size();
    }

    public Collection<Fund> currentFunds() {
        Collection<Fund> currentFunds = new HashSet<Fund>();
        for (Fund fund : funds.values()) {
            if (fund.currentlyInWallet()) {
                currentFunds.add(fund);
            }
        }
        return Collections.unmodifiableCollection(currentFunds);
    }

    /**
     * Returns number of funds I currently have.
     *
     * @return
     */
    public int count() {
        return currentFunds().size();
    }

    /**
     * Calculates income. This is the income from funds I currently have.
     *
     * @return
     */
    public Money income() {
        Money income = Money.ZERO;
        for (Fund fund : currentFunds()) {
            income = income.add(fund.income());
        }
        return income;
    }

    /**
     * Calculates income. This is the total income I have from the beginning.
     *
     * @return
     */
    public Money totalIncome() {
        Money totalIncome = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalIncome = totalIncome.add(fund.totalIncome());
        }
        return totalIncome;
    }

    public Money totalDeposit() {
        Money totalDeposit = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalDeposit = totalDeposit.add(fund.totalDeposit());
        }
        return totalDeposit;
    }
}
