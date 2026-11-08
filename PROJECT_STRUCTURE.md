# Структура проекта MetrologyHelper

```
app/src/main/
├── java/ru/sergeipavlov/metrologyhelper/
│   ├── MainActivity.java                    # Главная активность с Navigation Drawer
│   ├── LengthConverterFragment.java         # Фрагмент конвертера длины с UI логикой
│   ├── LengthUnit.java                      # Модель единицы измерения
│   ├── LengthUnits.java                     # Справочник всех единиц длины
│   ├── LengthConverter.java                 # Математическая логика конвертации
│   ├── ConverterState.java                  # Модель состояния конвертера
│   └── ConverterPreferences.java            # Сохранение/загрузка через SharedPreferences
│
├── res/
│   ├── drawable/
│   │   ├── ic_menu.xml                      # Иконка меню (Navigation Drawer)
│   │   └── ic_info.xml                      # Иконка информации
│   │
│   ├── layout/
│   │   ├── activity_main.xml                # Layout главной активности с Navigation Drawer
│   │   ├── fragment_length_converter.xml    # Layout конвертера длины
│   │   ├── item_unit_field.xml              # Layout элемента единицы измерения
│   │   └── item_category_header.xml         # Layout заголовка категории
│   │
│   ├── menu/
│   │   └── nav_menu.xml                     # Меню Navigation Drawer
│   │
│   └── values/
│       ├── colors.xml                       # Цвета для SI unit выделения
│       ├── strings.xml                      # Строковые ресурсы
│       └── themes.xml                       # Тема приложения (Material 3)
│
└── AndroidManifest.xml

app/src/test/
└── java/ru/sergeipavlov/metrologyhelper/
    └── LengthConverterTest.java             # Unit-тесты для LengthConverter
```

## Описание компонентов

### Java классы

#### MainActivity
- Главная точка входа в приложение
- Реализует Navigation Drawer для навигации между конвертерами
- Содержит toolbar с кнопкой "Сброс"
- Управляет фрагментами

#### LengthConverterFragment
- UI логика конвертера длины
- Использует RecyclerView для отображения списка единиц
- Содержит адаптер с двумя типами ViewHolder (заголовки и поля ввода)
- Обрабатывает ввод пользователя через TextWatcher
- Сохраняет состояние через ConverterPreferences

#### LengthUnit
- Модель единицы измерения
- Поля: name (название), symbol (символ), toMeters (коэффициент), category (категория), isSI (флаг)
- Immutable класс

#### LengthUnits
- Статический справочник всех единиц длины
- Группирует единицы по категориям
- Метод getAllUnits() возвращает полный список
- Метод getCategorizedUnits() возвращает Map<category, List<LengthUnit>>

#### LengthConverter
- Математическая логика конвертации
- Метод convert(value, fromUnit, toUnit) - конвертирует значение
- Использует метр как промежуточную единицу (value → meters → target unit)
- Все вычисления через double для точности

#### ConverterState
- POJO класс для хранения состояния
- Map<unitName, value> - значения для каждой единицы
- Используется для сохранения/загрузки

#### ConverterPreferences
- Работа с SharedPreferences
- Методы save/load для ConverterState
- Формат: "unit_name" → value (String)

### Layout файлы

#### activity_main.xml
- DrawerLayout с Navigation Drawer
- AppBarLayout с Toolbar
- FrameLayout для фрагментов
- NavigationView для бокового меню

#### fragment_length_converter.xml
- RecyclerView для списка единиц
- SwipeRefreshLayout для потенциального обновления
- LinearLayoutManager для вертикального скролла

#### item_unit_field.xml
- MaterialCardView с выделением для SI unit
- TextView для названия единицы
- TextView для метки "Единица СИ" (видна только для метра)
- TextInputLayout с TextInputEditText для ввода значения
- ImageButton для информации
- Использует LinearLayout для горизонтального размещения

#### item_category_header.xml
- TextView с заголовком категории
- Стилизован как header (жирный шрифт, отступы)

### Ресурсы

#### colors.xml
```xml
<color name="si_unit_background_light">#FFF3E0</color>    <!-- Светлый фон для SI -->
<color name="si_unit_background_dark">#2E2416</color>     <!-- Темный фон для SI -->
<color name="si_unit_border_light">#FF9800</color>        <!-- Светлая рамка -->
<color name="si_unit_border_dark">#B96420</color>         <!-- Темная рамка -->
```

#### strings.xml
- Названия всех единиц измерения на русском
- Символы единиц
- Названия категорий
- UI тексты (заголовки, кнопки)

## Зависимости (build.gradle.kts)

```kotlin
dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
```

## Конфигурация сборки

- **compileSdk:** 35 (Android 15)
- **minSdk:** 26 (Android 8.0)
- **targetSdk:** 35
- **Gradle:** 9.4.1
- **AGP:** 8.8.1
- **Java:** 17
