package pl.rationalworks.opanalyzer.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Adam Winciorek
 */
public class OperationsOnFundSeries implements Iterable<OperationsOnFund>{
    private final List<OperationsOnFund> operationsOnFundList;


    public OperationsOnFundSeries() {
        operationsOnFundList = new ArrayList<OperationsOnFund>();
    }

    public OperationsOnFund getCurrentOperationsOnFund() {
        int size = operationsOnFundList.size();
        if (size == 0) {
            return addNewOperationsOnFund();
        }
        return this.operationsOnFundList.get(size - 1);
    }

    public OperationsOnFund addNewOperationsOnFund() {
        OperationsOnFund operationsOnFund = new OperationsOnFund();
        this.operationsOnFundList.add(operationsOnFund);
        return operationsOnFund;
    }

    @Override
    public Iterator<OperationsOnFund> iterator() {
        return this.operationsOnFundList.iterator();
    }

    public int count() {
        return this.operationsOnFundList.size();
    }
}
