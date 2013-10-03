package pl.rationalworks.opanalyzer;

import static org.fest.assertions.Assertions.assertThat;
import pl.rationalworks.opanalyzer.core.Fund;
import pl.rationalworks.opanalyzer.core.FundOperation;
import pl.rationalworks.opanalyzer.core.Funds;
import pl.rationalworks.opanalyzer.core.Money;
import pl.rationalworks.opanalyzer.core.TransactionType;

import org.junit.Test;

/**
 * @author Adam Winciorek
 */
public class WorkingWithFundsTest {

    @Test
    public void sameFundOperationsShouldBeEqual() {
        FundOperation op1 = new FundOperation("03-07-2009", "06-07-2009", "PANEF", TransactionType.PURCHASE, new Money(100.00), new Money(99.97));
        FundOperation op2 = new FundOperation("03-07-2009", "06-07-2009", "PANEF", TransactionType.PURCHASE, new Money(100.00), new Money(99.97));
        assertThat(op1).isEqualTo(op2);
    }

    @Test
    public void afterBuyingFundTotalDepositAndLastRegistryAmountShouldBeCorrect() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("PANEF", TransactionType.PURCHASE, new Money(100.00), new Money(99.97));
        funds.performOperation(fundOperation);
        Fund fund = funds.findFundByName("PANEF");
        assertThat(fund).isNotNull();
        assertThat(fund.getIncomings()).isEqualTo(new Money(100.00));
        assertThat(fund.getRegistryAmount()).isEqualTo(new Money(99.97));
    }

    @Test
    public void afterBuyingSameFoundTwoTimesAmountsAreCorrect() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("PANEF", TransactionType.PURCHASE, new Money(100.00), new Money(99.97));
        FundOperation fundOperation2 = new FundOperation("PANEF", TransactionType.PURCHASE, new Money(100.00), new Money(214.28));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        Fund fund = funds.findFundByName("PANEF");
        assertThat(fund).isNotNull();
        assertThat(fund.getIncomings()).isEqualTo(new Money(200.00));
        assertThat(fund.getRegistryAmount()).isEqualTo(new Money(214.28));
    }

    @Test
    public void immediateSellingAfterSingleBuyingShouldPreserveCurrentDeposits() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(201.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        Fund fund = funds.findFundByName("AAS");
        assertThat(fund.getIncomings()).isEqualTo(new Money(200.00));
        assertThat(fund.getRegistryAmount()).isEqualTo(new Money(0.0));
    }

    @Test
    public void sellingShouldPreserveCurrentDeposits() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAMS", TransactionType.PURCHASE, new Money(200.00), new Money(200.00));
        FundOperation fundOperation3 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(50.00), new Money(258.63));
        FundOperation fundOperationLast = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(261.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        funds.performOperation(fundOperation3);
        funds.performOperation(fundOperationLast);
        Fund fund = funds.findFundByName("AAS");
        assertThat(fund.getIncomings()).isEqualTo(new Money(250.00));
        assertThat(fund.getRegistryAmount()).isEqualTo(new Money(0.0));
        fund = funds.findFundByName("AAMS");
        assertThat(fund.getIncomings()).isEqualTo(new Money(200.00));
        assertThat(fund.getRegistryAmount()).isEqualTo(new Money(200.0));
    }

    @Test
    public void afterSellingLastOperationsShouldBeClosed() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(201.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        Fund fund = funds.findFundByName("AAS");
        assertThat(fund.currentOperations().areClosed());
    }

    @Test
    public void afterSellingOperationsOnFundShouldBePreserved() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(201.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        Fund fund = funds.findFundByName("AAS");
        assertThat(fund).isNotNull();
        assertThat(fund.currentOperations().count()).isEqualTo(2);
    }

    @Test
    public void sellingOneFromManyFundsShouldResultInCorrectHistory() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAMS", TransactionType.PURCHASE, new Money(200.00), new Money(200.00));
        FundOperation fundOperation3 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(50.00), new Money(258.63));
        FundOperation fundOperationLast = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(261.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        funds.performOperation(fundOperation3);
        funds.performOperation(fundOperationLast);
        assertThat(funds.count()).isEqualTo(1);
        assertThat(funds.totalCount()).isEqualTo(2);
        Fund fund = funds.findFundByName("AAS");
        assertThat(fund.currentOperations().count()).isEqualTo(3);
        assertThat(fund.currentOperations().areClosed()).isTrue();
        fund = funds.findFundByName("AAMS");
        assertThat(fund.currentOperations().count()).isEqualTo(1);
        assertThat(fund.currentOperations().areClosed()).isFalse();
    }

    @Test
    public void buyingOneMOreTimeTheSameFundShouldBeReflectedInHistory() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAM S", TransactionType.PURCHASE, new Money(200.00), new Money(200.00));
        FundOperation fundOperation3 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(50.00), new Money(258.63));
        FundOperation fundOperation4 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(261.96), new Money(0.00));
        FundOperation fundOperation5 = new FundOperation("AAM S", TransactionType.PURCHASE, new Money(100.00), new Money(308.45));
        FundOperation fundOperation6 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(250.00), new Money(250.00));
        FundOperation fundOperation7 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(324.78), new Money(0.00));
        funds.performOperations(fundOperation, fundOperation2, fundOperation3, fundOperation4, fundOperation5, fundOperation6, fundOperation7);
        Fund fund = funds.findFundByName("AAS");
        Fund fund2 = funds.findFundByName("AAM S");
        assertThat(funds.count()).isEqualTo(1);
        assertThat(funds.totalCount()).isEqualTo(2);
        assertThat(fund.operationSeriesCount()).isEqualTo(2);
        assertThat(fund.currentOperations().count()).isEqualTo(2);
        assertThat(fund.currentOperations().areClosed()).isTrue();
        assertThat(fund2.operationSeriesCount()).isEqualTo(1);
        assertThat(fund2.currentOperations().count()).isEqualTo(2);
        assertThat(fund2.currentOperations().areClosed()).isFalse();
    }

    @Test
    public void incomeAfterSellingOneTimeBuyingShouldBeOk() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(201.96), new Money(0.00));
        funds.performOperation(fundOperation);
        funds.performOperation(fundOperation2);
        Fund fund = funds.findFundByName("AAS");
        assertThat(funds.balance()).isEqualTo(Money.ZERO);
        assertThat(funds.totalBalance()).isEqualTo(new Money(1.96));
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(1.96));
    }

    @Test
    public void incomeAfterSellingOneOfFundsShouldBeOk() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAMS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation3 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(50.00), new Money(258.63));
        FundOperation fundOperationLast = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(261.96), new Money(0.00));
        funds.performOperations(fundOperation, fundOperation2, fundOperation3, fundOperationLast);
        Fund fund = funds.findFundByName("AAS");
        Fund fund2 = funds.findFundByName("AAMS");
        assertThat(funds.balance()).isEqualTo(new Money(0.01));
        assertThat(funds.totalBalance()).isEqualTo(new Money(11.96).add(new Money(0.01)));
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(11.96));
        assertThat(fund2.balance()).isEqualTo(new Money(0.01));
        assertThat(fund2.totalBalance()).isEqualTo(new Money(0.01));
    }

    @Test
    public void incomeAfterMultipleSellingOneOfFundsShouldBeOk() {
        Funds funds = new Funds();
        FundOperation fundOperation = new FundOperation("AAS", TransactionType.PURCHASE, new Money(200.00), new Money(200.01));
        FundOperation fundOperation2 = new FundOperation("AAM S", TransactionType.PURCHASE, new Money(200.00), new Money(200.00));
        FundOperation fundOperation3 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(50.00), new Money(258.63));
        FundOperation fundOperation4 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(261.96), new Money(0.00));
        FundOperation fundOperation5 = new FundOperation("AAM S", TransactionType.PURCHASE, new Money(100.00), new Money(308.45));
        FundOperation fundOperation6 = new FundOperation("AAS", TransactionType.PURCHASE, new Money(250.00), new Money(250.00));
        FundOperation fundOperation7 = new FundOperation("AAS", TransactionType.REDEMPTION, new Money(324.78), new Money(0.00));
        funds.performOperations(fundOperation, fundOperation2, fundOperation3, fundOperation4, fundOperation5, fundOperation6, fundOperation7);
        Fund fund = funds.findFundByName("AAS");
        Fund fund2 = funds.findFundByName("AAM S");
        assertThat(funds.balance()).isEqualTo(new Money(8.45));
        assertThat(funds.totalBalance()).isEqualTo(new Money(11.96).add(new Money(74.78)).add(new Money(8.45)));
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(11.96).add(new Money(74.78)));
        assertThat(fund2.balance()).isEqualTo(new Money(8.45));
        assertThat(fund2.totalBalance()).isEqualTo(new Money(8.45));
    }

    @Test
    public void sellingWithLost() {
        Funds funds = new Funds();
        FundOperation operation = new FundOperation("HGTE", TransactionType.PURCHASE, new Money(14000.00), new Money(14000.00));
        FundOperation operation1 = new FundOperation("HGTE", TransactionType.REDEMPTION, new Money(13362.76), Money.ZERO);
        funds.performOperations(operation, operation1);

        assertThat(funds.balance()).isEqualTo(Money.ZERO);
        assertThat(funds.totalBalance()).isEqualTo(new Money(-637.24));
        Fund fund = funds.findFundByName("HGTE");
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(-637.24));
    }

    @Test
    public void afterConversionOperationsShouldBeOk() {
        Funds funds = new Funds();
        FundOperation operation = new FundOperation("AS OS", TransactionType.PURCHASE, new Money(5400.00), new Money(5400.00));
        FundOperation operation1 = new FundOperation("AS P", TransactionType.PURCHASE, new Money(4000.00), new Money(4000.00));
        FundOperation operation2 = new FundOperation("AS OS", TransactionType.SWITCH, new Money(5289.68), Money.ZERO);
        FundOperation operation3 = new FundOperation("AS P", TransactionType.SWITCH, new Money(5289.68), new Money(9295.86));
        funds.performOperations(operation, operation1, operation2, operation3);
        assertThat(funds.count()).isEqualTo(1);
        assertThat(funds.totalCount()).isEqualTo(2);
        Fund fund = funds.findFundByName("AS OS");
        assertThat(fund.currentlyInWallet()).isFalse();
        assertThat(fund.operationSeriesCount()).isEqualTo(1);
        Fund fund1 = funds.findFundByName("AS P");
        assertThat(fund1.currentlyInWallet()).isTrue();
        assertThat(fund1.operationSeriesCount()).isEqualTo(1);
    }

    @Test
    public void afterConversionBalanceShouldBeOk() {
        Funds funds = new Funds();
        FundOperation operation = new FundOperation("AS OS", TransactionType.PURCHASE, new Money(5400.00), new Money(5400.00));
        FundOperation operation1 = new FundOperation("AS P", TransactionType.PURCHASE, new Money(4000.00), new Money(4000.00));
        FundOperation operation2 = new FundOperation("AS OS", TransactionType.SWITCH, new Money(5289.68), Money.ZERO);
        FundOperation operation3 = new FundOperation("AS P", TransactionType.SWITCH, new Money(5289.68), new Money(9295.86));
        funds.performOperations(operation, operation1, operation2, operation3);
//        assertThat(funds.balance()).isEqualTo(new Money(6.18));
//        assertThat(funds.totalBalance()).isEqualTo(new Money(-104.14));
        //conversion/switch amount should not affect total deposit
        assertThat(funds.deposit()).isEqualTo(new Money(9400.00));
        Fund fund = funds.findFundByName("AS OS");
        assertThat(fund.totalDeposit()).isEqualTo(new Money(5400));
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(-110.32));
        Fund fund1 = funds.findFundByName("AS P");
        assertThat(fund1.balance()).isEqualTo(new Money(6.18));
        assertThat(fund1.totalBalance()).isEqualTo(new Money(6.18));
    }

    @Test
    public void purchaseAfterSwitchDoesNotInfluaceTotalDeposit() {
        Funds funds = new Funds();
        FundOperation operation = new FundOperation("AS OS", TransactionType.PURCHASE, new Money(5400.00), new Money(5400.00));
        FundOperation operation1 = new FundOperation("AS P", TransactionType.PURCHASE, new Money(4000.00), new Money(4000.00));
        FundOperation operation2 = new FundOperation("AS OS", TransactionType.SWITCH, new Money(5289.68), Money.ZERO);
        FundOperation operation3 = new FundOperation("AS P", TransactionType.SWITCH, new Money(5289.68), new Money(9295.86));
        FundOperation operation4 = new FundOperation("AS P", TransactionType.PURCHASE, new Money(300.00), new Money(9600.46));
        funds.performOperations(operation, operation1, operation2, operation3, operation4);
        assertThat(funds.balance()).isEqualTo(new Money(10.78));
        assertThat(funds.totalBalance()).isEqualTo(new Money(-99.54));
        //conversion/switch amount should not affect total deposit
        assertThat(funds.deposit()).isEqualTo(new Money(9700.00));
        Fund fund = funds.findFundByName("AS OS");
        assertThat(fund.balance()).isEqualTo(Money.ZERO);
        assertThat(fund.totalBalance()).isEqualTo(new Money(-110.32));
        assertThat(fund.totalDeposit()).isEqualTo(new Money(5400.00));
        Fund fund1 = funds.findFundByName("AS P");
        assertThat(fund1.balance()).isEqualTo(new Money(10.78));
        assertThat(fund1.totalBalance()).isEqualTo(new Money(10.78));
        assertThat(fund1.totalDeposit()).isEqualTo(new Money(4300.00));
    }

}
