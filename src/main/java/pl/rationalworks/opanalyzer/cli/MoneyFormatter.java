package pl.rationalworks.opanalyzer.cli;

import jlibs.core.lang.Ansi;
import pl.rationalworks.opanalyzer.core.Money;

/**
 * @author Adam Winciorek
 */
public class MoneyFormatter {

    private static final Ansi negativeFormatter = new Ansi(Ansi.Attribute.BRIGHT, Ansi.Color.RED, null);

    public static String format(Money money) {
        String formattedValue = money.asText();
        if (money.isNegative()) {
            formattedValue = negativeFormatter.colorize(formattedValue);
        }
        return formattedValue;
    }
}
