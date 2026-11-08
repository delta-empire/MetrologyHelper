package ru.sergeipavlov.metrologyhelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all available length units with their conversion coefficients.
 */
public class LengthUnits {
    private static final List<LengthUnit> UNITS = new ArrayList<>();

    static {
        // SI Base
        UNITS.add(new LengthUnit(
                "meter",
                "Метр",
                "м",
                BigDecimal.ONE,
                LengthUnit.UnitCategory.SI_BASE,
                true,
                "Основная единица длины в СИ. Метр — расстояние, которое свет проходит в вакууме за 1/299 792 458 секунды."
        ));

        // SI Decimal multiples and submultiples
        UNITS.add(new LengthUnit(
                "nanometer",
                "Нанометр",
                "нм",
                new BigDecimal("0.000000001"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Одна миллиардная метра. Используется для измерения длины световых волн, размеров молекул."
        ));

        UNITS.add(new LengthUnit(
                "micrometer",
                "Микрометр",
                "мкм",
                new BigDecimal("0.000001"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Одна миллионная метра. Используется в микробиологии, для толщины волоса (~70 мкм)."
        ));

        UNITS.add(new LengthUnit(
                "millimeter",
                "Миллиметр",
                "мм",
                new BigDecimal("0.001"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Одна тысячная метра. Часто используется в технических чертежах и измерениях."
        ));

        UNITS.add(new LengthUnit(
                "centimeter",
                "Сантиметр",
                "см",
                new BigDecimal("0.01"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Одна сотая метра. Удобная единица для измерения роста, размеров предметов."
        ));

        UNITS.add(new LengthUnit(
                "decimeter",
                "Дециметр",
                "дм",
                new BigDecimal("0.1"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Одна десятая метра. Реже используется в быту, применяется в образовательных целях."
        ));

        UNITS.add(new LengthUnit(
                "kilometer",
                "Километр",
                "км",
                new BigDecimal("1000"),
                LengthUnit.UnitCategory.SI_DECIMAL,
                false,
                "Тысяча метров. Используется для измерения расстояний между городами, в навигации."
        ));

        // Non-system units
        UNITS.add(new LengthUnit(
                "angstrom",
                "Ангстрем",
                "Å",
                new BigDecimal("0.0000000001"),
                LengthUnit.UnitCategory.NON_SYSTEM,
                false,
                "Одна стомиллионная сантиметра (10⁻¹⁰ м). Используется в физике и химии для атомных размеров."
        ));

        // US and Britain
        UNITS.add(new LengthUnit(
                "league",
                "Лига",
                "lea",
                new BigDecimal("4828.032"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Историческая единица, около 3 миль. В разных странах могла различаться."
        ));

        UNITS.add(new LengthUnit(
                "mile",
                "Миля",
                "mi",
                new BigDecimal("1609.344"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Сухопутная миля США и Британии. 1 миля = 1760 ярдов = 5280 футов."
        ));

        UNITS.add(new LengthUnit(
                "furlong",
                "Фурлонг",
                "fur",
                new BigDecimal("201.168"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Одна восьмая мили. Используется в скачках и сельском хозяйстве. 1 фурлонг = 220 ярдов."
        ));

        UNITS.add(new LengthUnit(
                "chain",
                "Чейн",
                "ch",
                new BigDecimal("20.1168"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Геодезическая цепь. 1 чейн = 66 футов = 22 ярда."
        ));

        UNITS.add(new LengthUnit(
                "rod",
                "Род",
                "rd",
                new BigDecimal("5.0292"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Также называется перч или поул. 1 род = 16.5 футов."
        ));

        UNITS.add(new LengthUnit(
                "yard",
                "Ярд",
                "yd",
                new BigDecimal("0.9144"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Основная единица в британской системе. 1 ярд = 3 фута = 36 дюймов."
        ));

        UNITS.add(new LengthUnit(
                "cubit",
                "Кубит",
                "cu",
                new BigDecimal("0.4572"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Древняя единица, от локтя до кончиков пальцев. Около 18 дюймов."
        ));

        UNITS.add(new LengthUnit(
                "foot",
                "Фут",
                "ft",
                new BigDecimal("0.3048"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Международный фут. 1 фут = 12 дюймов = 30.48 см."
        ));

        UNITS.add(new LengthUnit(
                "span",
                "Спан",
                "sp",
                new BigDecimal("0.2286"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Расстояние от кончика большого пальца до мизинца. Около 9 дюймов."
        ));

        UNITS.add(new LengthUnit(
                "hand",
                "Хэнд",
                "hd",
                new BigDecimal("0.1016"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Используется для измерения роста лошадей. 1 хэнд = 4 дюйма."
        ));

        UNITS.add(new LengthUnit(
                "palm",
                "Палм",
                "plm",
                new BigDecimal("0.0762"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Ширина ладони. 1 палм = 3 дюйма."
        ));

        UNITS.add(new LengthUnit(
                "inch",
                "Дюйм",
                "in",
                new BigDecimal("0.0254"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Международный дюйм. 1 дюйм = 2.54 см. Широко используется в США."
        ));

        UNITS.add(new LengthUnit(
                "line",
                "Линия",
                "ln",
                new BigDecimal("0.00211666667"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Одна двенадцатая дюйма. 1 линия ≈ 2.12 мм."
        ));

        UNITS.add(new LengthUnit(
                "caliber",
                "Калибр",
                "cl",
                new BigDecimal("0.000254"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Одна сотая дюйма. Используется для измерения диаметра пуль и снарядов."
        ));

        UNITS.add(new LengthUnit(
                "mil",
                "Мил, Тоу",
                "mil",
                new BigDecimal("0.0000254"),
                LengthUnit.UnitCategory.US_BRITISH,
                false,
                "Одна тысячная дюйма (thou). Используется в промышленности для точных измерений."
        ));

        // Maritime units
        UNITS.add(new LengthUnit(
                "nautical_league",
                "Морская лига",
                "nl",
                new BigDecimal("5556"),
                LengthUnit.UnitCategory.MARITIME,
                false,
                "Три морские мили. 1 морская лига = 5556 м."
        ));

        UNITS.add(new LengthUnit(
                "nautical_mile",
                "Морская миля",
                "nmi",
                new BigDecimal("1852"),
                LengthUnit.UnitCategory.MARITIME,
                false,
                "Международная морская миля. 1 морская миля = 1852 м = 1 минута дуги по меридиану."
        ));

        UNITS.add(new LengthUnit(
                "cable",
                "Кабельтов",
                "cbl",
                new BigDecimal("185.2"),
                LengthUnit.UnitCategory.MARITIME,
                false,
                "Одна десятая морской мили. 1 кабельтов = 185.2 м."
        ));

        UNITS.add(new LengthUnit(
                "fathom",
                "Морская сажень, фатом",
                "ftm",
                new BigDecimal("1.8288"),
                LengthUnit.UnitCategory.MARITIME,
                false,
                "Используется для измерения глубины. 1 фатом = 6 футов = 1.8288 м."
        ));

        // Historical units
        UNITS.add(new LengthUnit(
                "nautical_mile_us_pre1955",
                "Морская миля США (до 1955 г.)",
                "nmi",
                new BigDecimal("1853.248"),
                LengthUnit.UnitCategory.HISTORICAL,
                false,
                "Историческая морская миля США до 1955 года. Позже заменена на международную."
        ));

        UNITS.add(new LengthUnit(
                "nautical_mile_uk_pre1970",
                "Британская морская миля (до 1970 г.)",
                "nmi",
                new BigDecimal("1853.184"),
                LengthUnit.UnitCategory.HISTORICAL,
                false,
                "Историческая британская адмиралтейская миля до 1970 года."
        ));
    }

    public static List<LengthUnit> getAllUnits() {
        return new ArrayList<>(UNITS);
    }

    public static LengthUnit getUnitById(String id) {
        for (LengthUnit unit : UNITS) {
            if (unit.getId().equals(id)) {
                return unit;
            }
        }
        return null;
    }

    public static LengthUnit getSIBaseUnit() {
        for (LengthUnit unit : UNITS) {
            if (unit.getCategory() == LengthUnit.UnitCategory.SI_BASE) {
                return unit;
            }
        }
        return UNITS.get(0); // fallback to meter
    }
}
