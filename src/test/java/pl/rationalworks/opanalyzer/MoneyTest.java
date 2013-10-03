package pl.rationalworks.opanalyzer;

import static org.fest.assertions.Assertions.assertThat;
import pl.rationalworks.opanalyzer.core.Money;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * @author Adam Winciorek
 */
public class MoneyTest {
    
    @Test
    public void moneyCreatedFromStringShouldBeCorrect() {
        Money money = new Money("123.54");
        assertThat(money.value()).isEqualTo(new BigDecimal("123.54"));
    }
    
    @Test
    public void moneyCreatedFromDoubleShouldBeCorrect() {
        Money money = new Money(123.54);
        assertThat(money.value()).isEqualTo(BigDecimal.valueOf(123.54));
    }

    @Test
    public void moneyCreatedFromDefaultShouldBeZeroValue() {
        Money money = new Money();
        assertThat(money.value()).isEqualTo(BigDecimal.valueOf(0.00));
    }

    @Test
    public void zeroMoneyIsReallyZero() {
        assertThat(new Money(0.00).isZero()).isTrue();
        assertThat(Money.ZERO.isZero()).isTrue();
    }

    @Test
    public void sameAmountOfMoneyShouldBeEqual() {
        Money money1 = new Money("213.34");
        Money money2 = new Money("213.34");
        assertThat(money1).isEqualTo(money2);
    }
}
