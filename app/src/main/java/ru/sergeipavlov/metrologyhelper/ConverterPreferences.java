package ru.sergeipavlov.metrologyhelper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages saving and restoring converter state using SharedPreferences.
 */
public class ConverterPreferences {
    private static final String PREF_NAME = "MetrologyHelperPrefs";
    private static final String KEY_LENGTH_UNIT = "length_unit_id";
    private static final String KEY_LENGTH_VALUE = "length_value";

    private final SharedPreferences prefs;

    public ConverterPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save length converter state.
     *
     * @param state the state to save
     */
    public void saveLengthState(ConverterState state) {
        SharedPreferences.Editor editor = prefs.edit();

        if (state.hasValue()) {
            editor.putString(KEY_LENGTH_UNIT, state.getActiveUnitId());
            editor.putString(KEY_LENGTH_VALUE, state.getInputValue());
        } else {
            // Save default: meter = 0
            LengthUnit siUnit = LengthUnits.getSIBaseUnit();
            editor.putString(KEY_LENGTH_UNIT, siUnit.getId());
            editor.putString(KEY_LENGTH_VALUE, "0");
        }

        editor.apply();
    }

    /**
     * Load length converter state.
     *
     * @return restored state or default state if not found
     */
    public ConverterState loadLengthState() {
        String unitId = prefs.getString(KEY_LENGTH_UNIT, null);
        String value = prefs.getString(KEY_LENGTH_VALUE, null);

        if (unitId == null || value == null) {
            // Return default state
            LengthUnit siUnit = LengthUnits.getSIBaseUnit();
            return new ConverterState(siUnit.getId(), "0");
        }

        return new ConverterState(unitId, value);
    }

    /**
     * Reset all converter values to default.
     */
    public void resetAllValues() {
        SharedPreferences.Editor editor = prefs.edit();
        // Remove only converter values, preserving unrelated app settings.
        editor.remove(KEY_LENGTH_UNIT);
        editor.remove(KEY_LENGTH_VALUE);
        editor.apply();
    }

    /**
     * Check if any saved state exists.
     *
     * @return true if state was previously saved
     */
    public boolean hasLengthState() {
        return prefs.contains(KEY_LENGTH_UNIT);
    }
}
