package pl.rationalworks.opanalyzer.core;

/**
 * @author Adam Winciorek
 */
public enum TransactionType {
    PURCHASE("NABYCIE"), OPENING_PURCHASE("NABYCIE OTWIERAJĄCE"), SWITCH("KONWERSJA"), REDEMPTION("ODKUPIENIE");

    private String originalName;

    private TransactionType() {
        this(null);
    }

    private TransactionType(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalName() {
        if (originalName == null) {
            return name();
        }
        return originalName;
    }

    public static TransactionType createByName(String originalName) {
        try {
            return TransactionType.valueOf(originalName);
        } catch (IllegalArgumentException e) {
            for (TransactionType type : values()) {
                if (type.getOriginalName().equals(originalName)) {
                    return type;
                }
            }
        }
        return null;
    }
}
