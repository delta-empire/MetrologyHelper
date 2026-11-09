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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LengthConverterFragment extends Fragment {
    private ConverterPreferences preferences;
    private ConverterState state;
    private UnitAdapter adapter;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle savedState) {
        return inflater.inflate(R.layout.fragment_length_converter, parent, false);
    }

    @Override public void onViewCreated(@NonNull View root, @Nullable Bundle savedState) {
        preferences = new ConverterPreferences(requireContext());
        state = preferences.loadLengthState();
        RecyclerView list = root.findViewById(R.id.units_recycler);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setItemAnimator(null);
        adapter = new UnitAdapter();
        list.setAdapter(adapter);
    }

    @Override public void onPause() {
        super.onPause();
        if (preferences != null && state != null) preferences.saveLengthState(state);
    }

    public void refreshFromReset() {
        if (preferences == null || adapter == null) return;
        state = preferences.loadLengthState();
        adapter.updateAfterReset();
    }

    private final class UnitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int HEADER = 0, UNIT = 1;
        private final List<Object> items = new ArrayList<>();
        private final Map<String, BigDecimal> calculated = new HashMap<>();
        private final EnumSet<LengthUnit.UnitCategory> collapsed =
                EnumSet.noneOf(LengthUnit.UnitCategory.class);
        private final Map<String, UnitHolder> visible = new HashMap<>();
        private boolean binding;

        UnitAdapter() {
            buildList();
            recalculate();
        }

        private void buildList() {
            items.clear();
            // SI base unit stays pinned at the top; other groups are collapsible.
            items.add(LengthUnits.getSIBaseUnit());
            LengthUnit.UnitCategory previous = null;
            for (LengthUnit unit : LengthUnits.getAllUnits()) {
                if (unit.isSI()) continue;
                if (unit.getCategory() != previous) {
                    previous = unit.getCategory();
                    items.add(previous);
                }
                if (!collapsed.contains(unit.getCategory())) items.add(unit);
            }
        }

        private void recalculate() {
            calculated.clear();
            LengthUnit source = LengthUnits.getUnitById(state.getActiveUnitId());
            if (source == null) source = LengthUnits.getSIBaseUnit();
            BigDecimal value = LengthConverter.parseInput(state.getInputValue());
            if (value == null) return;
            for (LengthUnit unit : LengthUnits.getAllUnits()) {
                calculated.put(unit.getId(), LengthConverter.convert(value, source, unit));
            }
        }

        void updateAfterReset() {
            // Reset the in-memory state and all displayed fields in one consistent pass.
            binding = true;
            View focused = requireActivity().getCurrentFocus();
            if (focused instanceof EditText) focused.clearFocus();
            recalculate();
            for (Map.Entry<String, UnitHolder> entry : new ArrayList<>(visible.entrySet())) {
                UnitHolder holder = entry.getValue();
                if (entry.getKey().equals(holder.boundId)) {
                    BigDecimal value = calculated.get(entry.getKey());
                    holder.setValue(value == null ? "" : LengthConverter.formatForDisplay(value));
                }
            }
            binding = false;
            notifyDataSetChanged();
        }

        @Override public int getItemCount() { return items.size(); }

        @Override public int getItemViewType(int position) {
            return items.get(position) instanceof LengthUnit ? UNIT : HEADER;
        }

        @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int type) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    type == UNIT ? R.layout.item_unit_field : R.layout.item_category_header,
                    parent, false);
            return type == UNIT ? new UnitHolder(v) : new HeaderHolder(v);
        }

        @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderHolder) {
                LengthUnit.UnitCategory category = (LengthUnit.UnitCategory) items.get(position);
                HeaderHolder h = (HeaderHolder) holder;
                h.name.setText((collapsed.contains(category) ? "▸ " : "▾ ") + category.getDisplayName());
                h.itemView.setOnClickListener(v -> {
                    if (!collapsed.add(category)) collapsed.remove(category);
                    buildList();
                    notifyDataSetChanged();
                });
            } else {
                ((UnitHolder) holder).bind((LengthUnit) items.get(position));
            }
        }

        @Override public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder instanceof UnitHolder) ((UnitHolder) holder).unbind();
            super.onViewRecycled(holder);
        }

        private void updateVisibleFields(String activeId) {
            binding = true;
            for (Map.Entry<String, UnitHolder> entry : new ArrayList<>(visible.entrySet())) {
                if (entry.getKey().equals(activeId)) continue;
                UnitHolder holder = entry.getValue();
                if (!entry.getKey().equals(holder.boundId)) continue;
                BigDecimal value = calculated.get(entry.getKey());
                holder.setValue(value == null ? "" : LengthConverter.formatForDisplay(value));
            }
            binding = false;
        }

        private final class HeaderHolder extends RecyclerView.ViewHolder {
            final TextView name;
            HeaderHolder(View v) {
                super(v);
                name = v.findViewById(R.id.category_title);
            }
        }

        private final class UnitHolder extends RecyclerView.ViewHolder {
            final TextView name, siLabel;
            final TextInputLayout inputLayout;
            final EditText input;
            final ImageButton info;
            final MaterialCardView card;
            String boundId;
            TextWatcher watcher;

            UnitHolder(View v) {
                super(v);
                name = v.findViewById(R.id.unit_name);
                siLabel = v.findViewById(R.id.si_label);
                inputLayout = v.findViewById(R.id.value_input_layout);
                input = v.findViewById(R.id.value_input);
                info = v.findViewById(R.id.info_button);
                card = v.findViewById(R.id.card_view);
            }

            void unbind() {
                if (watcher != null) input.removeTextChangedListener(watcher);
                watcher = null;
                if (boundId != null && visible.get(boundId) == this) visible.remove(boundId);
                boundId = null;
            }

            void setValue(String value) {
                if (!input.getText().toString().equals(value)) input.setText(value);
            }

            void bind(LengthUnit unit) {
                unbind();
                boundId = unit.getId();
                name.setText(unit.toString());
                inputLayout.setSuffixText(unit.getSymbol());
                siLabel.setVisibility(unit.isSI() ? View.VISIBLE : View.GONE);

                if (unit.isSI()) {
                    boolean dark = (getResources().getConfiguration().uiMode & 0x30) == 0x20;
                    card.setCardBackgroundColor(requireContext().getColor(dark
                            ? R.color.si_unit_background_dark : R.color.si_unit_background_light));
                    card.setStrokeWidth(4);
                    card.setStrokeColor(requireContext().getColor(dark
                            ? R.color.si_unit_border_dark : R.color.si_unit_border_light));
                } else {
                    card.setCardBackgroundColor(com.google.android.material.color.MaterialColors
                            .getColor(card, com.google.android.material.R.attr.colorSurface));
                    card.setStrokeWidth(0);
                }

                BigDecimal calculatedValue = calculated.get(unit.getId());
                String display = unit.getId().equals(state.getActiveUnitId())
                        ? state.getInputValue()
                        : (calculatedValue == null ? "" : LengthConverter.formatForDisplay(calculatedValue));
                setValue(display);
                visible.put(boundId, this);

                watcher = new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
                    @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
                    @Override public void afterTextChanged(Editable value) {
                        if (binding || !unit.getId().equals(boundId)) return;
                        state.setActiveUnitId(unit.getId());
                        state.setInputValue(value.toString());
                        preferences.saveLengthState(state);
                        recalculate();
                        updateVisibleFields(unit.getId());
                    }
                };
                input.addTextChangedListener(watcher);
                info.setOnClickListener(v -> showUnitInfo(unit));
                name.setOnClickListener(v -> showUnitInfo(unit));
            }
        }
    }

    private void showUnitInfo(LengthUnit unit) {
        BigDecimal coefficient = unit.getToMetersCoefficient();
        String text = unit.getName() + " (" + unit.getSymbol() + ")\n\n"
                + unit.getDescription() + "\n\n"
                + "Категория: " + unit.getCategory().getDisplayName() + "\n"
                + "Источник: " + unit.getReference() + "\n"
                + "Период: " + unit.getValidityPeriod() + "\n"
                + "Единица СИ: " + (unit.isSI() ? "Да" : "Нет") + "\n"
                + "1 " + unit.getSymbol() + " = " + coefficient.toPlainString() + " м\n"
                + "Формула: метры = значение × " + coefficient.toPlainString() + "\n"
                + "Пример: 2 " + unit.getSymbol() + " = "
                + LengthConverter.formatForDisplay(coefficient.multiply(new BigDecimal("2"))) + " м";
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.unit_info_title)
                .setMessage(text)
                .setPositiveButton(R.string.close, null)
                .show();
    }
}
