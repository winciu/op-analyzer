package pl.rationalworks.opanalyzer.core;

import com.google.common.base.Objects;

import java.math.BigDecimal;

/**
 * @author Adam Winciorek
 */
public class Money {

    private final BigDecimal amount;
    private static final double ZERO_VALUE = 0.00;
    private static final BigDecimal ZERO_AMOUNT = BigDecimal.valueOf(ZERO_VALUE);
    public static final Money ZERO = new Money();

    private Money(BigDecimal value) {
        this.amount = value;
    }

    public Money() {
        this(ZERO_VALUE);
    }

    public Money(String value) {
        this(Double.parseDouble(value.replaceAll(" ","").replaceFirst(",", ".")));
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount));
    }

    public Money add(Money amount) {
        return new Money(this.amount.add(amount.amount));
    }

    public boolean isZero() {
        return ZERO_AMOUNT.compareTo(this.amount) == 0;
    }

    public BigDecimal value() {
        return this.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Money other = (Money) obj;
        return Objects.equal(this.amount, other.amount);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(this.value()).toString();
    }

    public Money minus(Money money) {
        return new Money(this.amount.subtract(money.amount));
    }

    public boolean isPositive() {
        return this.amount.signum() == 1;
    }

    public boolean isNegative() {
        return this.amount.signum() == -1;
    }

    public String asText() {
        return String.format("%,.2f", value());
    }
}
