package ru.sergeipavlov.metrologyhelper;

import java.math.BigDecimal;

/**
 * Represents a length unit with conversion coefficient to meters.
 */
public class LengthUnit {
    private final String id;
    private final String name;
    private final String symbol;
    private final BigDecimal toMetersCoefficient;
    private final UnitCategory category;
    private final boolean isSI;
    private final String description;
    private final String reference;
    private final String validityPeriod;

    public enum UnitCategory {
        SI_BASE("Основная единица СИ"),
        SI_DECIMAL("Десятичные кратные и дольные единицы СИ"),
        NON_SYSTEM("Внесистемные единицы"),
        US_BRITISH("США и Британия"),
        MARITIME("Международные морские единицы"),
        HISTORICAL("Исторические единицы");

        private final String displayName;

        UnitCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public LengthUnit(String id, String name, String symbol,
                     BigDecimal toMetersCoefficient, UnitCategory category,
                     boolean isSI, String description) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.toMetersCoefficient = toMetersCoefficient;
        this.category = category;
        this.isSI = isSI;
        this.description = description;
        reference = (category == UnitCategory.SI_BASE || category == UnitCategory.SI_DECIMAL)
                ? "BIPM: The International System of Units (SI), 9th edition"
                : "Справочный коэффициент; первоисточник требует проверки";
        if ("nautical_mile_us_pre1955".equals(id)) validityPeriod = "до 1955 года";
        else if ("nautical_mile_uk_pre1970".equals(id)) validityPeriod = "до 1970 года";
        else validityPeriod = "современное либо справочное определение";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getToMetersCoefficient() {
        return toMetersCoefficient;
    }

    public UnitCategory getCategory() {
        return category;
    }

    public boolean isSI() {
        return isSI;
    }

    public String getReference() { return reference; }
    public String getValidityPeriod() { return validityPeriod; }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}
