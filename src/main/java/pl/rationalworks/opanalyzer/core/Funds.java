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
     * Calculates balance. This is the balance from funds I currently have.
     * More prcisely it is balance from funds from the current operations series.
     *
     * @return
     */
    public Money balance() {
        Money balance = Money.ZERO;
        for (Fund fund : currentFunds()) {
            balance = balance.add(fund.balance());
        }
        return balance;
    }

    /**
     * Calculates balance. This is the total balance I have from the beginning.
     *
     * @return
     */
    public Money totalBalance() {
        Money totalBalance = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalBalance = totalBalance.add(fund.totalBalance());
        }
        return totalBalance;
    }

    public Money totalIncome() {
        Money totalIncome = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalIncome = totalIncome.add(fund.totalIncome());
        }
        return totalIncome;
    }

    public Money totalLoss() {
        Money totalLoss = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalLoss = totalLoss.add(fund.totalLoss());
        }
        return totalLoss;
    }
}
