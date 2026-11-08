package ru.sergeipavlov.metrologyhelper;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LengthConverterFragment extends Fragment {

    private RecyclerView recyclerView;
    private UnitsAdapter adapter;
    private ConverterPreferences preferences;
    private ConverterState currentState;

    private boolean isUpdating = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_length_converter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = new ConverterPreferences(requireContext());
        currentState = preferences.loadLengthState();

        recyclerView = view.findViewById(R.id.units_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new UnitsAdapter(LengthUnits.getAllUnits(), currentState);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.saveLengthState(currentState);
    }

    public void refreshFromReset() {
        currentState = preferences.loadLengthState();
        adapter.updateValues(currentState);
    }

    private boolean isNightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private class UnitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_UNIT = 1;

        private final List<Object> items = new ArrayList<>();
        private final Map<String, BigDecimal> values = new HashMap<>();
        private final Map<String, EditText> editTexts = new HashMap<>();

        public UnitsAdapter(List<LengthUnit> units, ConverterState state) {
            buildItemsList(units);
            recalculateAll(state);
        }

        private void buildItemsList(List<LengthUnit> units) {
            items.clear();

            LengthUnit.UnitCategory currentCategory = null;

            for (LengthUnit unit : units) {
                if (unit.getCategory() != currentCategory) {
                    currentCategory = unit.getCategory();
                    items.add(currentCategory.getDisplayName());
                }
                items.add(unit);
            }
        }

        private void recalculateAll(ConverterState state) {
            if (state == null || !state.hasValue()) {
                // Use default: meter = 0
                LengthUnit siUnit = LengthUnits.getSIBaseUnit();
                values.put(siUnit.getId(), BigDecimal.ZERO);
                calculateAllFrom(siUnit, BigDecimal.ZERO);
            } else {
                // Parse saved value and recalculate
                BigDecimal sourceValue = LengthConverter.parseInput(state.getInputValue());
                if (sourceValue == null) {
                    sourceValue = BigDecimal.ZERO;
                }

                LengthUnit sourceUnit = LengthUnits.getUnitById(state.getActiveUnitId());
                if (sourceUnit == null) {
                    sourceUnit = LengthUnits.getSIBaseUnit();
                }

                values.put(sourceUnit.getId(), sourceValue);
                calculateAllFrom(sourceUnit, sourceValue);
            }
        }

        private void calculateAllFrom(LengthUnit sourceUnit, BigDecimal sourceValue) {
            List<LengthUnit> allUnits = LengthUnits.getAllUnits();

            for (LengthUnit targetUnit : allUnits) {
                if (!targetUnit.getId().equals(sourceUnit.getId())) {
                    BigDecimal converted = LengthConverter.convert(
                            sourceValue, sourceUnit, targetUnit);
                    values.put(targetUnit.getId(), converted);
                }
            }
        }

        public void updateValues(ConverterState state) {
            recalculateAll(state);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_UNIT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_unit_field, parent, false);
                return new UnitViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                String categoryName = (String) items.get(position);
                ((HeaderViewHolder) holder).bind(categoryName);
            } else if (holder instanceof UnitViewHolder) {
                LengthUnit unit = (LengthUnit) items.get(position);
                ((UnitViewHolder) holder).bind(unit);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView categoryTitle;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryTitle = itemView.findViewById(R.id.category_title);
            }

            public void bind(String categoryName) {
                categoryTitle.setText(categoryName);
            }
        }

        private class UnitViewHolder extends RecyclerView.ViewHolder {
            TextView unitName;
            TextView siLabel;
            TextInputLayout inputLayout;
            EditText valueInput;
            ImageButton infoButton;
            View cardView;

            public UnitViewHolder(@NonNull View itemView) {
                super(itemView);
                unitName = itemView.findViewById(R.id.unit_name);
                siLabel = itemView.findViewById(R.id.si_label);
                inputLayout = itemView.findViewById(R.id.value_input_layout);
                valueInput = itemView.findViewById(R.id.value_input);
                infoButton = itemView.findViewById(R.id.info_button);
                cardView = itemView.findViewById(R.id.card_view);
            }

            public void bind(LengthUnit unit) {
                unitName.setText(unit.toString());
                inputLayout.setSuffixText(unit.getSymbol());

                // Show SI label and highlight card if this is SI base unit
                boolean isSI = unit.isSI();
                siLabel.setVisibility(isSI ? View.VISIBLE : View.GONE);

                if (isSI && cardView instanceof com.google.android.material.card.MaterialCardView) {
                    com.google.android.material.card.MaterialCardView materialCard =
                            (com.google.android.material.card.MaterialCardView) cardView;

                    // Get colors based on theme
                    int backgroundColorResId = isNightMode() ?
                            R.color.si_unit_background_dark : R.color.si_unit_background_light;
                    int strokeColorResId = isNightMode() ?
                            R.color.si_unit_border_dark : R.color.si_unit_border_light;

                    materialCard.setCardBackgroundColor(
                            requireContext().getColor(backgroundColorResId));
                    materialCard.setStrokeWidth(4);
                    materialCard.setStrokeColor(
                            requireContext().getColor(strokeColorResId));
                }

                // Store EditText reference
                editTexts.put(unit.getId(), valueInput);

                // Remove old TextWatcher
                if (valueInput.getTag() instanceof TextWatcher) {
                    valueInput.removeTextChangedListener((TextWatcher) valueInput.getTag());
                }

                // Set value
                BigDecimal value = values.get(unit.getId());
                if (value != null) {
                    String formatted = LengthConverter.formatForDisplay(value);
                    if (!valueInput.getText().toString().equals(formatted)) {
                        valueInput.setText(formatted);
                    }
                }

                // Add new TextWatcher
                TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (isUpdating) return;

                        String input = s.toString();
                        currentState.setActiveUnitId(unit.getId());
                        currentState.setInputValue(input);

                        BigDecimal parsed = LengthConverter.parseInput(input);
                        if (parsed != null) {
                            values.put(unit.getId(), parsed);
                            updateOtherFields(unit, parsed);
                        }
                    }
                };

                valueInput.addTextChangedListener(watcher);
                valueInput.setTag(watcher);

                // Info button
                infoButton.setOnClickListener(v -> showUnitInfo(unit));
            }

            private void updateOtherFields(LengthUnit sourceUnit, BigDecimal sourceValue) {
                isUpdating = true;

                List<LengthUnit> allUnits = LengthUnits.getAllUnits();
                for (LengthUnit targetUnit : allUnits) {
                    if (!targetUnit.getId().equals(sourceUnit.getId())) {
                        BigDecimal converted = LengthConverter.convert(
                                sourceValue, sourceUnit, targetUnit);
                        values.put(targetUnit.getId(), converted);

                        EditText targetEdit = editTexts.get(targetUnit.getId());
                        if (targetEdit != null) {
                            String formatted = LengthConverter.formatForDisplay(converted);
                            targetEdit.setText(formatted);
                        }
                    }
                }

                isUpdating = false;
            }

            private void showUnitInfo(LengthUnit unit) {
                String message = getString(R.string.unit_name_label) + " " + unit.getName() + "\n\n" +
                        getString(R.string.unit_symbol_label) + " " + unit.getSymbol() + "\n\n" +
                        getString(R.string.unit_category_label) + " " + unit.getCategory().getDisplayName() + "\n\n" +
                        getString(R.string.unit_coefficient_label) + " " + unit.getToMetersCoefficient().toPlainString() + "\n\n" +
                        getString(R.string.unit_description_label) + "\n" + unit.getDescription();

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.unit_info_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.close, null)
                        .show();
            }
        }
    }
}
