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

    public void addFund(Fund fundToAdd) {
        this.funds.put(fundToAdd.getName(), fundToAdd);
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
     * More precisely it is balance from funds from the current operations series.
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

    public Money deposit() {
        Money deposit = Money.ZERO;
        for (Fund fund : currentFunds()) {
            deposit = deposit.add(fund.getInitialFundDeposit());
        }
        return deposit;
    }

    public Money amount() {
        Money amount = Money.ZERO;
        for (Fund fund : currentFunds()) {
            amount = amount.add(fund.getRegistryAmount());
        }
        return amount;
    }

    public Money income() {
        Money income = Money.ZERO;
        for (Fund fund : currentFunds()) {
            income = income.add(fund.income());
        }
        return income;
    }

    public Money loss() {
        Money loss = Money.ZERO;
        for (Fund fund : currentFunds()) {
            loss = loss.add(fund.loss());
        }
        return loss;
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

    /**
     * Returns total loss including taxes.
     *
     * @return
     */
    public Money totalLoss() {
        Money totalLoss = Money.ZERO;
        for (Fund fund : funds.values()) {
            totalLoss = totalLoss.add(fund.totalLoss());
        }
        return totalLoss;
    }

}
