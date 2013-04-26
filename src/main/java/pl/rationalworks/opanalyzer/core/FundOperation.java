package pl.rationalworks.opanalyzer.core;

import com.google.common.base.Objects;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Adam Winciorek
 */
public class FundOperation {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd-MM-yyyy");
    private final DateTime orderDate;
    private final DateTime txRatingDate;
    private final String foundName;
    private final TransactionType transactionType;
    private final Money amount;
    private final Money registryAmount;

    public FundOperation(DateTime orderDate, DateTime txRatingDate, String foundName, TransactionType transactionType, Money amount, Money registryAmount) {
        this.orderDate = orderDate;
        this.txRatingDate = txRatingDate;
        this.foundName = foundName;
        this.transactionType = transactionType;
        this.amount = amount;
        this.registryAmount = registryAmount;
    }

    public FundOperation(String fundName, TransactionType transactionType, Money amount, Money registryAmount) {
        this((DateTime) null, null, fundName, transactionType, amount, registryAmount);
    }

    public FundOperation(String orderDate, String txRatingDate, String fundName, TransactionType transactionType, Money amount, Money registryAmount) {
        this(DATE_TIME_FORMATTER.parseDateTime(orderDate), DATE_TIME_FORMATTER.parseDateTime(txRatingDate), fundName, transactionType, amount, registryAmount);
    }

    public DateTime getOrderDate() {
        return orderDate;
    }

    public DateTime getTxRatingDate() {
        return txRatingDate;
    }

    public String getFoundName() {
        return foundName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getRegistryAmount() {
        return registryAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderDate, txRatingDate, foundName, transactionType, amount, registryAmount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FundOperation other = (FundOperation) obj;
        return Objects.equal(this.orderDate, other.orderDate) && Objects.equal(this.txRatingDate, other.txRatingDate) &&
                Objects.equal(this.foundName, other.foundName) && Objects.equal(this.transactionType, other.transactionType) &&
                Objects.equal(this.amount, other.amount) && Objects.equal(this.registryAmount, other.registryAmount);
    }
}
