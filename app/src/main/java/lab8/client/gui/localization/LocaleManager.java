package lab8.client.gui.localization;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;
import java.util.function.Consumer;

/**
 * Менеджер локализации приложения.
 * Позволяет управлять текущей локалью и получать локализованные строки.
 */
public class LocaleManager {
    private static LocaleManager instance;

    private final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();

    private final List<Consumer<Locale>> localeChangeListeners = new ArrayList<>();

    private final List<Locale> availableLocales = new ArrayList<>();

    private ResourceBundle bundle;

    private LocaleManager() {
        Locale russianLocale = Locale.forLanguageTag("ru-RU");

        availableLocales.add(russianLocale);
        availableLocales.add(Locale.forLanguageTag("el-GR"));
        availableLocales.add(Locale.forLanguageTag("es-HN"));
        availableLocales.add(Locale.forLanguageTag("is-IS"));

        setLocale(russianLocale);
    }

    /**
     * Получение экземпляра менеджера локализации
     *
     * @return Экземпляр LocaleManager
     */
    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    /**
     * Установка текущей локали
     *
     * @param locale Локаль для установки
     */
    public void setLocale(Locale locale) {
        if (!availableLocales.contains(locale)) {
            throw new IllegalArgumentException("Unsupported locale: " + locale);
        }

        if (!locale.equals(currentLocale.get())) {
            bundle = ResourceBundle.getBundle("lab8/client/gui/localization/UILabels", locale);
            currentLocale.set(locale);

            // Уведомление всех слушателей о смене локали
            notifyLocaleChangeListeners(locale);
        }
    }

    /**
     * Получение локализованной строки по ключу
     *
     * @param key Ключ строки
     * @return Локализованная строка
     */
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * Получение текущей локали
     *
     * @return Текущая локаль
     */
    public Locale getCurrentLocale() {
        return currentLocale.get();
    }

    /**
     * Получение свойства текущей локали
     *
     * @return Свойство текущей локали
     */
    public ObjectProperty<Locale> localeProperty() {
        return currentLocale;
    }

    /**
     * Получение списка доступных локалей
     *
     * @return Список доступных локалей
     */
    public List<Locale> getAvailableLocales() {
        return Collections.unmodifiableList(availableLocales);
    }

    /**
     * Добавление слушателя смены локали
     *
     * @param listener Слушатель смены локали
     */
    public void addLocaleChangeListener(Consumer<Locale> listener) {
        localeChangeListeners.add(listener);
    }

    /**
     * Удаление слушателя смены локали
     *
     * @param listener Слушатель смены локали
     */
    public void removeLocaleChangeListener(Consumer<Locale> listener) {
        localeChangeListeners.remove(listener);
    }

    /**
     * Уведомление всех слушателей о смене локали
     *
     * @param locale Новая локаль
     */
    private void notifyLocaleChangeListeners(Locale locale) {
        localeChangeListeners.forEach(listener -> listener.accept(locale));
    }

    /**
     * Получение отображаемого имени локали
     *
     * @param locale Локаль
     * @return Отображаемое имя локали
     */
    public String getLocaleDisplayName(Locale locale) {
        return locale.getDisplayName(getCurrentLocale());
    }
}