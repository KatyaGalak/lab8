package lab8.client.gui.util;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lab8.client.gui.localization.LocaleManager;
import lab8.shared.ticket.TicketType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Optional;

/**
 * Утилитный класс для создания стилизованных диалоговых окон
 */
public class DialogUtils {

    /**
     * Создает стилизованный диалог подтверждения
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @return Диалог подтверждения
     */
    public static Alert createConfirmationDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LocaleManager.getInstance().getString(titleKey));
        alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
        alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));
        alert.initOwner(owner);
        styleDialog(alert);

        // Обновление текста при смене локали
        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            alert.setTitle(LocaleManager.getInstance().getString(titleKey));
            alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
            alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));

            // Обновление кнопок
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                    .setText(LocaleManager.getInstance().getString("button.ok"));
            ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL))
                    .setText(LocaleManager.getInstance().getString("button.cancel"));
        });

        return alert;
    }

    /**
     * Создает стилизованный диалог с текстовым полем ввода
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @param defaultValue   Значение по умолчанию
     * @return Диалог с текстовым полем ввода
     */
    public static TextInputDialog createTextInputDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(LocaleManager.getInstance().getString(titleKey));
        dialog.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
        dialog.setContentText(LocaleManager.getInstance().getString(contentTextKey));
        dialog.initOwner(owner);
        styleDialog(dialog);

        // Обновление текста при смене локали
        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            dialog.setTitle(LocaleManager.getInstance().getString(titleKey));
            dialog.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
            dialog.setContentText(LocaleManager.getInstance().getString(contentTextKey));

            // Обновление кнопок
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK))
                    .setText(LocaleManager.getInstance().getString("button.ok"));
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                    .setText(LocaleManager.getInstance().getString("button.cancel"));
        });

        return dialog;
    }

    /**
     * Создает стилизованный диалог выбора
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @param defaultChoice  Значение по умолчанию
     * @param choices        Варианты выбора
     * @return Диалог выбора
     */
    public static ChoiceDialog<TicketType> createChoiceDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey, TicketType defaultChoice, TicketType[] choices) {
        ChoiceDialog<TicketType> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(LocaleManager.getInstance().getString(titleKey));
        dialog.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
        dialog.setContentText(LocaleManager.getInstance().getString(contentTextKey));
        dialog.initOwner(owner);
        styleDialog(dialog);

        // Обновление текста при смене локали
        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            dialog.setTitle(LocaleManager.getInstance().getString(titleKey));
            dialog.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
            dialog.setContentText(LocaleManager.getInstance().getString(contentTextKey));

            // Обновление кнопок
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK))
                    .setText(LocaleManager.getInstance().getString("button.ok"));
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                    .setText(LocaleManager.getInstance().getString("button.cancel"));
        });

        return dialog;
    }

    /**
     * Создает стилизованный диалог с информацией
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @return Диалог с информацией
     */
    public static Alert createInformationDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleManager.getInstance().getString(titleKey));
        alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
        alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));
        alert.initOwner(owner);
        styleDialog(alert);

        // Обновление текста при смене локали
        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            alert.setTitle(LocaleManager.getInstance().getString(titleKey));
            alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
            alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));

            // Обновление кнопок
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                    .setText(LocaleManager.getInstance().getString("button.ok"));
        });

        return alert;
    }

    /**
     * Создает стилизованный диалог с ошибкой
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @return Диалог с ошибкой
     */
    public static Alert createErrorDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getInstance().getString(titleKey));
        alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
        alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));
        alert.initOwner(owner);
        styleDialog(alert);

        // Обновление текста при смене локали
        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            alert.setTitle(LocaleManager.getInstance().getString(titleKey));
            alert.setHeaderText(LocaleManager.getInstance().getString(headerTextKey));
            alert.setContentText(LocaleManager.getInstance().getString(contentTextKey));

            // Обновление кнопок
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                    .setText(LocaleManager.getInstance().getString("button.ok"));
        });

        return alert;
    }

    /**
     * Применяет стиль к диалоговому окну
     *
     * @param dialog Диалоговое окно
     */
    private static void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setMinWidth(400);

        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(DialogUtils.class.getResource("/ui/global_style.css")).toExternalForm());

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.getStyleClass().add("ok-button");
            okButton.setText(LocaleManager.getInstance().getString("button.ok"));
        }

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.getStyleClass().add("cancel-button");
            cancelButton.setText(LocaleManager.getInstance().getString("button.cancel"));
        }
    }

    /**
     * Показывает диалог подтверждения и возвращает результат
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @return true, если пользователь нажал OK, иначе false
     */
    public static boolean showConfirmationDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = createConfirmationDialog(owner, titleKey, headerTextKey, contentTextKey);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Показывает диалог с текстовым полем ввода и возвращает результат
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @param defaultValue   Значение по умолчанию
     * @return Введенный текст или null, если пользователь отменил ввод
     */
    public static String showTextInputDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey, String defaultValue) {
        TextInputDialog dialog = createTextInputDialog(owner, titleKey, headerTextKey, contentTextKey, defaultValue);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Показывает диалог выбора и возвращает результат
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     * @param defaultChoice  Значение по умолчанию
     * @param choices        Варианты выбора
     * @return Выбранное значение или null, если пользователь отменил выбор
     */
    public static TicketType showChoiceDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey, TicketType defaultChoice, TicketType[] choices) {
        ChoiceDialog<TicketType> dialog = createChoiceDialog(owner, titleKey, headerTextKey, contentTextKey, defaultChoice, choices);
        Optional<TicketType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Показывает информационный диалог
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     */
    public static void showInformationDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = createInformationDialog(owner, titleKey, headerTextKey, contentTextKey);
        alert.showAndWait();
    }

    /**
     * Показывает диалог с ошибкой
     *
     * @param owner          Родительское окно
     * @param titleKey       Ключ заголовка диалога
     * @param headerTextKey  Ключ текста заголовка
     * @param contentTextKey Ключ текста содержимого
     */
    public static void showErrorDialog(Stage owner, String titleKey, String headerTextKey, String contentTextKey) {
        Alert alert = createErrorDialog(owner, titleKey, headerTextKey, contentTextKey);
        alert.showAndWait();
    }

    /**
     * Показать диалог с сообщением об ошибке
     *
     * @param title   заголовок диалога
     * @param header  заголовок сообщения
     * @param content содержимое сообщения
     */
    public static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Показать диалог с сообщением об ошибке и стектрейсом
     *
     * @param title   заголовок диалога
     * @param header  заголовок сообщения
     * @param content содержимое сообщения
     * @param ex      исключение
     */
    public static void showExceptionDialog(String title, String header, String content, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Создаем область для отображения стектрейса
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    /**
     * Показать диалог с информационным сообщением
     *
     * @param title   заголовок диалога
     * @param header  заголовок сообщения
     * @param content содержимое сообщения
     */
    public static void showInfoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Показать диалог с запросом подтверждения
     *
     * @param title   заголовок диалога
     * @param header  заголовок сообщения
     * @param content содержимое сообщения
     * @return true, если пользователь подтвердил действие, иначе false
     */
    public static boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType buttonTypeYes = new ButtonType(LocaleManager.getInstance().getString("button.ok"));
        ButtonType buttonTypeNo = new ButtonType(LocaleManager.getInstance().getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    /**
     * Показать диалог с текстовым полем для ввода
     *
     * @param title        заголовок диалога
     * @param header       заголовок сообщения
     * @param content      содержимое сообщения
     * @param defaultValue значение по умолчанию
     * @return введенное пользователем значение или null, если диалог был отменен
     */
    public static String showTextInputDialog(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        ButtonType buttonTypeOk = new ButtonType(LocaleManager.getInstance().getString("button.ok"));
        ButtonType buttonTypeCancel = new ButtonType(LocaleManager.getInstance().getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Показать диалог выбора типа билета
     *
     * @param title   заголовок диалога
     * @param header  заголовок сообщения
     * @param content содержимое сообщения
     * @return выбранный тип билета или null, если диалог был отменен
     */
    public static TicketType showTicketTypeChoiceDialog(String title, String header, String content) {
        ChoiceDialog<TicketType> dialog = new ChoiceDialog<>(TicketType.values()[0], TicketType.values());
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        ButtonType buttonTypeOk = new ButtonType(LocaleManager.getInstance().getString("button.ok"));
        ButtonType buttonTypeCancel = new ButtonType(LocaleManager.getInstance().getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<TicketType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Показать диалог с числовым полем для ввода
     *
     * @param title        заголовок диалога
     * @param header       заголовок сообщения
     * @param content      содержимое сообщения
     * @param defaultValue значение по умолчанию
     * @return введенное пользователем значение или null, если диалог был отменен
     */
    public static Integer showNumberInputDialog(String title, String header, String content, Integer defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue != null ? defaultValue.toString() : "");
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        ButtonType buttonTypeOk = new ButtonType(LocaleManager.getInstance().getString("button.ok"));
        ButtonType buttonTypeCancel = new ButtonType(LocaleManager.getInstance().getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                showErrorDialog(
                        LocaleManager.getInstance().getString("dialog.error.invalid_number"),
                        null,
                        LocaleManager.getInstance().getString("dialog.error.invalid_number")
                );
                return null;
            }
        }
        return null;
    }
} 