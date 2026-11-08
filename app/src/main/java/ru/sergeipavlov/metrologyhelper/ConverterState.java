package ru.sergeipavlov.metrologyhelper;

/**
 * Holds the state of active input in the converter.
 */
public class ConverterState {
    private String activeUnitId;
    private String inputValue;

    public ConverterState() {
        this.activeUnitId = null;
        this.inputValue = "";
    }

    public ConverterState(String activeUnitId, String inputValue) {
        this.activeUnitId = activeUnitId;
        this.inputValue = inputValue;
    }

    public String getActiveUnitId() {
        return activeUnitId;
    }

    public void setActiveUnitId(String activeUnitId) {
        this.activeUnitId = activeUnitId;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public boolean hasValue() {
        return inputValue != null && !inputValue.trim().isEmpty();
    }
}
