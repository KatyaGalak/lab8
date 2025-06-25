package lab8.client.gui.localization;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Утилитный класс для локализации компонентов JavaFX.
 */
public class LocalizationUtils {

    /**
     * Создает привязку строки к ключу локализации
     *
     * @param key Ключ локализации
     * @return Привязка строки
     */
    public static StringBinding createStringBinding(String key) {
        return Bindings.createStringBinding(
                () -> LocaleManager.getInstance().getString(key),
                LocaleManager.getInstance().localeProperty()
        );
    }

    /**
     * Локализует метку с использованием слушателя
     *
     * @param label Метка
     * @param key   Ключ локализации
     */
    public static void localizeLabel(Label label, String key) {
        label.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> label.setText(LocaleManager.getInstance().getString(key)));
    }

    /**
     * Локализует вкладку с использованием слушателя
     *
     * @param tab Вкладка
     * @param key Ключ локализации
     */
    public static void localizeTab(Tab tab, String key) {
        tab.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale ->
                tab.setText(LocaleManager.getInstance().getString(key))
        );
    }

    /**
     * Локализует кнопку с использованием слушателя
     *
     * @param button Кнопка
     * @param key    Ключ локализации
     */
    public static void localizeButton(Button button, String key) {
        button.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> button.setText(LocaleManager.getInstance().getString(key)));
    }

    /**
     * Локализует пункт меню с использованием слушателя
     *
     * @param menuItem Пункт меню
     * @param key      Ключ локализации
     */
    public static void localizeMenuItem(MenuItem menuItem, String key) {
        menuItem.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> menuItem.setText(LocaleManager.getInstance().getString(key)));
    }

    /**
     * Локализует меню с использованием слушателя
     *
     * @param menu Меню
     * @param key  Ключ локализации
     */
    public static void localizeMenu(Menu menu, String key) {
        menu.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> menu.setText(LocaleManager.getInstance().getString(key)));
    }

    /**
     * Локализует столбец таблицы с использованием слушателя
     *
     * @param column Столбец таблицы
     * @param key    Ключ локализации
     */
    public static void localizeTableColumn(TableColumn<?, ?> column, String key) {
        column.setText(LocaleManager.getInstance().getString(key));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> column.setText(LocaleManager.getInstance().getString(key)));
    }

    /**
     * Форматирует дату в соответствии с локалью и шаблоном из ресурсов
     */
    public static String formatDate(java.time.temporal.TemporalAccessor date) {
        if (date == null) return "";
        try {
            String pattern = LocaleManager.getInstance().getString("status.date_format");
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern, LocaleManager.getInstance().getCurrentLocale());

            return formatter.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }

    /**
     * Форматирует число (например, цену) в соответствии с локалью
     */
    public static String formatNumber(Number number) {
        if (number == null) return "";
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(LocaleManager.getInstance().getCurrentLocale());
        return nf.format(number);
    }

    /**
     * Форматирует перечисление через локализованный ключ
     * Например, formatEnum("table.type.CONCERT") ищет ключ в ресурсах
     */
    public static String formatEnum(String key) {
        return LocaleManager.getInstance().getString(key);
    }

    /**
     * Форматирует булево значение как локализованное "Да/Нет" или "Yes/No"
     */
    public static String formatBoolean(Boolean value) {
        if (value == null) return "";
        return LocaleManager.getInstance().getString(value ? "boolean.true" : "boolean.false");
    }

    /**
     * Класс для создания локализованных ComboBox с локалями
     */
    public static class LocaleComboBox extends ComboBox<Locale> {

        private final Map<Locale, String> localeDisplayNames = new HashMap<>();

        /**
         * Создает ComboBox для выбора локали
         */
        public LocaleComboBox() {
            super();

            localeDisplayNames.put(Locale.forLanguageTag("ru-RU"), "Русский");
            localeDisplayNames.put(Locale.forLanguageTag("el-GR"), "Ελληνικά");
            localeDisplayNames.put(Locale.forLanguageTag("es-HN"), "Español");
            localeDisplayNames.put(Locale.forLanguageTag("is-IS"), "Íslenska");

            setItems(FXCollections.observableArrayList(localeDisplayNames.keySet()));
            setValue(LocaleManager.getInstance().getCurrentLocale());

            setConverter(new StringConverter<>() {
                @Override
                public String toString(Locale locale) {
                    return localeDisplayNames.getOrDefault(locale, locale.getDisplayName());
                }

                @Override
                public Locale fromString(String string) {
                    return null; // Не используется
                }
            });

            valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    LocaleManager.getInstance().setLocale(newValue);
                }
            });

            LocaleManager.getInstance().addLocaleChangeListener(locale -> {
                if (!locale.equals(getValue())) {
                    setValue(locale);
                }
            });
        }
    }
} 