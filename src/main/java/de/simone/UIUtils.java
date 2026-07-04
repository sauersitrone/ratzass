/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.PropertyChangeListener;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.IconSize;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.LineHeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import de.mekaso.vaadin.addon.compani.animation.AnimationBuilder;
import de.mekaso.vaadin.addon.compani.animation.AnimationTypes;
import de.mekaso.vaadin.addon.compani.effect.AttentionSeeker;
import de.mekaso.vaadin.addon.compani.effect.Delay;
import de.mekaso.vaadin.addon.compani.effect.Repeat;
import de.mekaso.vaadin.addon.compani.effect.Speed;
import de.simone.colortextfield.ColorTextField;
import de.simone.frontend.HomeView;
import de.simone.frontend.components.TTimer;
import de.simone.frontend.components.paperslider.PaperSlider;
import de.simone.frontend.components.paperslider.PaperSliderVariant;
import de.simone.vaadinplus.components.Badge;
import de.simone.vaadinplus.components.Header;
import de.simone.vaadinplus.components.InputGroup;
import de.simone.vaadinplus.components.Item;
import de.simone.vaadinplus.components.KeyValuePair;
import de.simone.vaadinplus.components.Layout;
import de.simone.vaadinplus.components.Notification2;
import de.simone.vaadinplus.components.list.ListItem2;
import de.simone.vaadinplus.themes.RadioButtonTheme;
import de.simone.vaadinplus.utilities.BadgeVariant;
import de.simone.vaadinplus.utilities.Breakpoint;
import de.simone.vaadinplus.utilities.Color;
import de.simone.vaadinplus.utilities.Font;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;

public class UIUtils {

    public static final String COLUMN_VISIBLE = "columnVisible";

    public static final String UID_NAVIGATION = "main.navigation";
    public static final String UID_FOOTER = "main.footer";

    public static final String IMG_PATH = "images/";
    public static final String ICON_PATH = "images/";

    public static final String VARIANT_SUCCESS = "success";
    public static final String VARIANT_ERROR = "error";
    public static final String VARIANT_CONTRAST = "contrast";

    public static final String IMPORT_CHECK = "import.check";
    public static final String IMPORT_COMMIT = "import.commit";

    public static final String EMPTY_IMAGE = IMG_PATH + "empty_imate.png";
    public static final String YOUR_LOGO_HERE = IMG_PATH + "your_logo_here.svg";
    public static final String YOUR_AVATAR_HERE = IMG_PATH + "your_avatar_here.svg";
    public static final String YOUR_SIGNATURE_HERE = IMG_PATH + "your_signature_here.svg";
    public static final String YOUR_IMAGE_HERE = IMG_PATH + "your_image_here.svg";
    public static final String YOUR_WIDGET_ICON_HERE = IMG_PATH + "your_widget_icon_here.svg";
    public static final String TWITTER_ICON = ICON_PATH + "twitter.png";
    public static final String FACEBOOK_ICON = ICON_PATH + "facebook.png";
    public static final String PAYPAL_ICON = ICON_PATH + "paypal.png";
    public static final String SPINNER = ICON_PATH + "infinite-spinner.svg";

    public static final String LOGGING_FORM_WITH = "22rem";
    public static final String LOGGING_FORM_HEIGHT = "36rem";

    public static final String BOT_NAME = "Sia";
    public static final String BOT_AVATAR = UIUtils.ICON_PATH + "Sia.png";

    public static final String[] VALID_IMAGES = { ".png", ".jpg", ".jpeg" }; // NOSONAR
    public static final String[] VALID_DOCUMENTS = { ".pdf", ".txt" }; // NOSONAR
    public static final String[] VALID_VIDEOS = { ".mp4", ".mov", ".mov", ".wmv", ".avi" }; // NOSONAR

    public static final String MEDIUM_IMAGE_SIZE = "60px";
    public static final String LARGE_IMAGE_SIZE = "150px";

    public static final int MOBILE_BREAKPOINT = 400;
    public static final int MOBILE_STRING_TRUNCATE = 40;

    private static final int NOTIFICATION_INFO_DURATION = 2000;
    private static final int NOTIFICATION_ERROR_DURATION = 5000;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private static NumberFormat uSNumberFormat = NumberFormat.getInstance(Locale.US);
    private static final String INVALID_IMAGE_COMPONENT = "Argument 'image' muss be instance of Avatar or Image";

    public static String getSimpleClassName(Class<? extends Component> clazz) {
        String clsName = clazz.getSimpleName();
        if (clsName.endsWith("_Subclass"))
            clsName = clsName.replace("_Subclass", "");
        return clsName;
    }

    public static String getSimpleClassName(Component component) {
        Class<? extends Component> clazz = component.getClass();
        return getSimpleClassName(clazz);
    }

    private UIUtils() {
        //
    }

    /**
     * la-lg - large (circa 20 px) la-xs - extra small la-sm - small la-lx - same as
     * 1x la-1x - same
     * as lx la-2x - 2 times large la-3x - 3 times large la-4x la-5x la-6x la-7x
     * la-8x la-9x la-10x
     * la-fw - ?
     *
     * @param size
     * @param iconClass
     * @return
     */
    public static Span getLaIcon(String size, String iconClass) {
        Span span = new Span();
        span.setClassName(size + " " + iconClass);
        return span;
    }

    public static Span getLaIcon(String iconClass) {
        return getLaIcon("la-lg", iconClass);
    }

    public static void setWidgetBorder(HasStyle hasStyle, boolean add) {
        // alwais
        hasStyle.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Overflow.HIDDEN);

        if (add) {
            hasStyle.addClassNames(
                    LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_30, LumoUtility.BoxShadow.SMALL);
        } else {
            hasStyle.removeClassNames(
                    LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_30, LumoUtility.BoxShadow.SMALL);
        }
    }

    public static Scroller getScroller(Component component) {
        Scroller scroller = new Scroller(component);
        UIUtils.setGroupStyle(scroller);
        scroller.addClassName(LumoUtility.Border.ALL);
        scroller.setHeightFull();
        return scroller;
    }

    public static void setResponsiveSteps(FormLayout formLayout, int columns) {
        List<ResponsiveStep> steps = new ArrayList<>();

        // Use one column by default
        steps.add(new ResponsiveStep("0", 1));

        if (columns >= 2)
            // Use two columns, if the layout's width exceeds 320px
            steps.add(new ResponsiveStep("320px", 2));

        if (columns >= 3)
            // Use three columns, if the layout's width exceeds 500px
            steps.add(new ResponsiveStep("500px", 3));

        if (columns >= 4)
            steps.add(new ResponsiveStep("750px", 4));

        formLayout.setResponsiveSteps(steps);
    }

    public static FormLayout getResponsiveFormLayout() {
        List<ResponsiveStep> steps = new ArrayList<>();
        FormLayout formLayout = new FormLayout();

        steps.add(new ResponsiveStep("600px", 1));
        steps.add(new ResponsiveStep("900px", 2));
        steps.add(new ResponsiveStep("1240px", 3));
        steps.add(new ResponsiveStep("1440px", 4));

        formLayout.setResponsiveSteps(steps);
        return formLayout;
    }

    public static Span getHTMLSpan(String htmlText) {
        Span body = new Span();
        body.getElement().setProperty("innerHTML", htmlText == null ? "" : htmlText);
        return body;
    }

    /**
     * return and Span with the innerHTML set to the text passes as argument (if
     * text is html) or a
     * simple text component if text is normal text. if the text is blank or
     * represent a not found
     * constant "!{" retrun a empty component
     *
     * @param text - the content
     * @return the Span
     */
    public static Span getSpan(String text, boolean secondary) {
        Span span = new Span();
        // if explanation is not found or empty, return empty component
        if (StringUtils.isBlank(text) || text.startsWith("!{"))
            return span;

        // is html?
        if (Pattern.compile("<[^>]*>").matcher(text).find()) {
            span.getElement().setProperty("innerHTML", text);
        } else {
            span.setText(text);
        }
        if (secondary) {
            span.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        }
        return span;
    }


    public static Span getSpanByKey(String bundleKey, Object... parms) {
        String text = TranslationProvider.getTranslation(bundleKey, parms);
        return getSpan(text, false);
    }

    public static VerticalLayout getWidgetFormArea() {
        VerticalLayout widgetComponent = UIUtils.getCompactVerticalLayout();
        UIUtils.setWidgetBorder(widgetComponent, true);
        return widgetComponent;
    }


    /**
     * if the given string is great as {@link #MOBILE_STRING_TRUNCATE} trucate it
     * and append "..." as
     * sufix
     *
     * @param str - the string to trucate
     * @return truncate string with sufix if apply
     */
    public static String truncateMovile(final String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > MOBILE_STRING_TRUNCATE) {
            String result = str.substring(0, MOBILE_STRING_TRUNCATE);
            return result + " ...";
        }
        return str;
    }

    public static String getFormatedAmount(Double amount) {
        return getFormatedAmount(amount, true);
    }

    public static String getFormatedAmount(Long amount) {
        return getFormatedAmount(amount == null ? null : BigDecimal.valueOf(amount), false, false);
    }

    public static String getFormatedAmount(Integer amount) {
        return getFormatedAmount(amount == null ? null : BigDecimal.valueOf(amount), false, false);
    }

    public static String getFormatedAmount(Double amount, boolean withDecimal) {
        return getFormatedAmount(amount == null ? null : BigDecimal.valueOf(amount), withDecimal);
    }

    public static String getFormatedAmount(BigDecimal amount) {
        return getFormatedAmount(amount, true, false);
    }

    public static String getFormatedAmount(BigDecimal amount, boolean replaceZero) {
        return getFormatedAmount(amount, true, replaceZero);
    }

    /**
     * Return a formated strin represtation of the amount argument
     *
     * @param amount       - the amount
     * @param withDiecimal - true if 2 decimal placed are requered
     * @param replaceZero  - if true and amount = 0, replace the 0 string with "--"
     * @return the formatted amount
     */
    public static String getFormatedAmount(
            BigDecimal amount, boolean withDiecimal, boolean replaceZero) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        if (VaadinSession.getCurrent() != null)
            numberFormat = NumberFormat.getInstance(VaadinSession.getCurrent().getLocale());
        numberFormat.setMaximumFractionDigits(withDiecimal ? 2 : 0);
        numberFormat.setMinimumFractionDigits(withDiecimal ? 2 : 0);
        BigDecimal amountToFormat = BigDecimal.valueOf(0);
        if (amount != null)
            amountToFormat = amount;

        String res = numberFormat.format(amountToFormat);
        if (replaceZero && amountToFormat.doubleValue() == 0)
            res = "--";

        return res;
    }

    public static String getUSFormatedAmount(Double amount) {
        return getUSFormatedAmount(amount == null ? null : BigDecimal.valueOf(amount));
    }

    public static String getUSFormatedAmount(BigDecimal amount) {
        uSNumberFormat.setMaximumFractionDigits(2);
        uSNumberFormat.setMinimumFractionDigits(2);
        if (amount == null)
            return uSNumberFormat.format(0);

        return uSNumberFormat.format(amount);
    }

    public static String getFormatedAmount(BigDecimal amount, String currecyCode) {
        // in some situations, the currency is not available
        String sym = StringUtils.isBlank(currecyCode) ? "?" : Currency.getInstance(currecyCode).getSymbol();
        return sym + " " + getFormatedAmount(amount);
    }

    /**
     * utility method for custom message password confirmation.
     *
     * @param oldPassword     - old password component (can be null)
     * @param newPassword     - new password component
     * @param confirmPassword - confirmation password component
     * @return true if all is ok
     */
    public static boolean confirmPasswordFields(
            PasswordField oldPassword, PasswordField newPassword, PasswordField confirmPassword) {
        boolean ok = true;
        if (oldPassword != null && oldPassword.getValue().equals("")) {
            UIUtils.showErrorForField(oldPassword, "invalid.oldPassword");
            ok = false;
        }

        if (newPassword.getValue().equals("")) {
            UIUtils.showErrorForField(newPassword, "invalid.newPassword");
            ok = false;
        }

        if (confirmPassword.getValue().equals("")) {
            UIUtils.showErrorForField(confirmPassword, "invalid.confirmPassword");
            ok = false;
        }

        if (oldPassword != null && oldPassword.getValue().equals(newPassword.getValue())) {
            UIUtils.showErrorForField(oldPassword, "invalid.newAndOld");
            UIUtils.showErrorForField(newPassword, "invalid.newAndOld");
            ok = false;
        }

        if (!confirmPassword.getValue().equals(newPassword.getValue())) {
            UIUtils.showErrorForField(newPassword, "invalid.newAndConfirm");
            UIUtils.showErrorForField(confirmPassword, "invalid.newAndConfirm");
            ok = false;
        }

        return ok;
    }

    public static Image getLargeImage(String source) {
        return getImage(source, LARGE_IMAGE_SIZE);
    }

    public static Image getMediumImage(String source) {
        return getImage(source, MEDIUM_IMAGE_SIZE);
    }

    public static Image getImage(String source, String size) {
        Image image = new Image();
        image.getStyle().set("object-fit", "cover");
        if (source != null)
            image.setSrc(source);
        if (size != null) {
            image.setWidth(size);
            image.setHeight(size);
        }

        return image;
    }

    public static VerticalLayout getCompactVerticalLayout(Component... components) {
        VerticalLayout verticalLayout = new VerticalLayout(components);
        setCompatStyle(verticalLayout);
        return verticalLayout;
    }

    public static void setCompatStyle(VerticalLayout verticalLayout) {
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setMargin(false);
        verticalLayout.setWidthFull();
    }

    public static PasswordField getPasswordField(String fieldName) {
        return getPasswordField(fieldName, false);
    }

    public static PasswordField getPasswordField(String fieldName, boolean withHelper) {
        PasswordField field = new PasswordField();
        field.setLabel(field.getTranslation(fieldName));
        field.setRevealButtonVisible(false);
        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        return field;
    }

    @SuppressWarnings("unchecked")
    public static boolean showResponseInFields(
            Response response, String messageId, HasValidation... fields) {
        @SuppressWarnings("rawtypes")
        Map<String, Object> map = (Map) response.getEntity();
        if (messageId.equals(map.get("messageId"))) {
            return showResponseInFields(response, fields);
        }

        return true;
    }

    /**
     * show the response message (any) on the error message area of the fiels pased
     * as argument. if is
     * there no error, this method return silenty
     *
     * @param response - the response
     * @param fields   - the fiels
     * @return true for no error, false there was an error
     */
    public static boolean showResponseInFields(Response response, HasValidation... fields) {
        boolean isOk = response.getStatus() < 400;
        if (isOk)
            return true;
        String message = ((Map<?, ?>) response.getEntity()).get("message").toString();
        for (HasValidation field : fields) {
            field.setErrorMessage(message);
            field.setInvalid(true);
        }
        return isOk;
    }

    public static void showErrorForField(HasValidation textField, String bundleKey, Object... parms) {
        textField.setErrorMessage(new Span().getTranslation(bundleKey, parms));
        textField.setInvalid(true);
    }

    public static Icon createPrimaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.PRIMARY);
        return i;
    }

    public static Icon createSecondaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.SECONDARY);
        return i;
    }

    public static Icon createTertiaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.TERTIARY);
        return i;
    }

    public static Icon createDisabledIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.DISABLED);
        return i;
    }

    public static Icon createSuccessIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.SUCCESS);
        return i;
    }

    public static Icon createErrorIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.TextColor.ERROR);
        return i;
    }

    public static Icon createBadgeIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.addClassNames(LumoUtility.Padding.XSMALL);
        return icon;
    }

    public static Badge getBooleanBadge(boolean b, boolean withText, boolean withIcon) {
        String group = withText ? "boolean.enabled" : null;
        return getBooleanBadge(b, group, withIcon);
    }

    public static Badge getBooleanBadge(boolean b, String group, boolean withIcon) {
        Badge badge = new Badge();
        if (withIcon) {
            Icon icon = b ? createBadgeIcon(VaadinIcon.CHECK) : createBadgeIcon(VaadinIcon.MINUS);
            badge.add(icon);
        }
        if (group != null) {
            badge.add(new Span(TranslationProvider.getString(group, "" + b)));
        }
        badge.getElement().getThemeList().add(b ? VARIANT_SUCCESS : VARIANT_CONTRAST);
        return badge;
    }

    public static Badge getMessageBadge(int number) {
        Badge messageBadge = getMessageBadge();
        messageBadge.setText("" + number);
        return messageBadge;
    }

    public static Badge getMessageBadge() {
        Badge messageBadge = new Badge();
        messageBadge.addClassNames("end-xs", LumoUtility.Position.ABSOLUTE, "top-xs");
        messageBadge.addThemeVariants(
                BadgeVariant.SUCCESS, BadgeVariant.PILL, BadgeVariant.PRIMARY, BadgeVariant.SMALL);
        return messageBadge;
    }

    public static Badge getBadge(String bundleGroup, String bundleId, String variant) {
        return getBadge(bundleGroup, bundleId, variant, null);
    }

    public static Badge getBadge(String bundleGroup, String bundleId, String variant, Icon icon) {
        Badge badge = new Badge();
        if (icon != null) {
            badge.add(icon);
        }

        badge.add(bundleId == null
                ? new Span()
                : new Span(TranslationProvider.getString(bundleGroup, bundleId)));

        if (variant != null)
            badge.getElement().getThemeList().add(variant);
        return badge;
    }

    /**
     * create and return a standar timer
     *
     * @param listener - {@link PropertyChangeListener} of the timer
     * @param seconds  - notification duration interval
     * @return the timer
     */
    public static TTimer getTimer(PropertyChangeListener listener, int seconds) {
        TTimer timer = new TTimer(3600 * 24);
        timer.setFractions(false);
        timer.setVisible(false);
        timer.setCountUp(true);
        timer.addCurrentTimeChangeListener(listener, seconds, TimeUnit.SECONDS);
        return timer;
    }

    public static Span getSocialMediaIcon(String laIcon, String text, String color) {
        Span label = new Span(text);
        Span span = new Span(UIUtils.getLaIcon(laIcon), label);
        span.getStyle().set("color", color);
        return span;
    }

    public static Dialog getDialog(
            String titleId,
            String messageId,
            List<Button> acceptButtons,
            String cancelTextId,
            Component... components) {
        Dialog dialog = new Dialog();

        if (titleId != null)
            dialog.setHeaderTitle(dialog.getTranslation(titleId));

        if (components != null) {
            VerticalLayout dialogBody = UIUtils.getCompactVerticalLayout();
            if (messageId != null)
                dialogBody.add(UIUtils.getSecondarySmallLabel(messageId));

            dialogBody.setId("dialogBody");
            dialogBody.add(components);
            dialogBody.setAlignItems(FlexComponent.Alignment.STRETCH);
            dialogBody.setSizeFull();
            dialog.add(dialogBody);
        }

        if (cancelTextId != null) {
            Button cancelButton = UIUtils.getButton(cancelTextId, null);
            cancelButton.addClickListener(e -> dialog.close());
            dialog.getFooter().add(cancelButton);
        }
        if (!acceptButtons.isEmpty())
            dialog.getFooter().add(acceptButtons.toArray(new Button[0]));
        return dialog;
    }

    public static ConfirmDialog getDeleteDialog(Class<?> entitiyClass, String prefix) {
        String elementName = TranslationProvider.getTranslation(entitiyClass.getSimpleName());
        String title = TranslationProvider.getTranslation(prefix + ".title", elementName);
        String message = TranslationProvider.getTranslation(prefix + ".message", elementName);
        ConfirmDialog dialog = getConfirmDialog(title, message, prefix + ".cancel", prefix + ".accept");
        dialog.setConfirmButtonTheme("error primary");
        return dialog;
    }

    public static ConfirmDialog getConfirmDialog(
            String title, String message, String cancelMsgId, String confirmMsgId) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(title);
        dialog.setText(message);
        dialog.setCancelable(true);
        dialog.setCancelText(dialog.getTranslation(cancelMsgId));
        dialog.setConfirmText(dialog.getTranslation(confirmMsgId));

        return dialog;
    }

    public static <T> Grid<T> getGrid(Class<T> clazz) {
        Grid<T> grid = new Grid<>(clazz, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassName(Border.TOP);

        return grid;
    }

    public static EmailField getEmailField(String fieldName) {
        return getEmailField(fieldName, false, false);
    }

    public static EmailField getEmailField(String fieldName, boolean withHelper, boolean readOnly) {
        EmailField emailField = new EmailField();
        emailField.setLabel(emailField.getTranslation(fieldName));
        emailField.setRequiredIndicatorVisible(true);
        emailField.getElement().setAttribute("name", "email");
        if (withHelper) {
            String helper = emailField.getTranslation(fieldName + ".tt");
            emailField.setHelperText(helper);
            emailField.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        emailField.setReadOnly(readOnly);
        emailField.setId(fieldName);
        return emailField;
    }

    public static RadioButtonGroup<String> getToggleButtonGroup(
            String fieldName, String group, boolean withHelper) {
        List<String> keys = TranslationProvider.getKeys(group);
        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setItems(keys);

        radioButtonGroup.setRenderer(
                new ComponentRenderer<>(
                        item -> {
                            Span span = new Span(item);
                            span.addClassNames(Padding.Horizontal.SMALL);
                            return span;
                        }));

        if (withHelper) {
            String helper = radioButtonGroup.getTranslation(fieldName + ".tt");
            radioButtonGroup.setHelperText(helper);
            radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        return radioButtonGroup;
    }

    public static ColorTextField getColorTextField(String fieldName) {
        return getColorTextField(fieldName, false, false);
    }

    public static ColorTextField getColorTextField(String fieldName, boolean withHelper, boolean readOnly) {
        ColorTextField field = new ColorTextField();
        field.setLabel(field.getTranslation(fieldName));
        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        field.setReadOnly(readOnly);
        field.setId(fieldName);
        return field;
    }

    public static TextField getTextField(String fieldName) {
        return getTextField(fieldName, false, false);
    }

    public static TextField getTextField(String fieldName, boolean withHelper, boolean readOnly) {
        TextField field = new TextField();
        field.setLabel(field.getTranslation(fieldName));
        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        field.setReadOnly(readOnly);
        field.setId(fieldName);
        return field;
    }

    public static PaperSlider getPaperSlider(String fieldName) {
        return getPaperSlider(fieldName, false, false);
    }

    public static PaperSlider getPaperSlider(String fieldName, boolean withHelper, boolean readOnly) {
        PaperSlider field = new PaperSlider();
        field.setLabel(field.getTranslation(fieldName));
        field.setMin(0);
        field.setMax(10);
        field.setSnaps(true);
        field.setMaxMarkers(8);
        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(CustomFieldVariant.LUMO_HELPER_ABOVE_FIELD);
            field.addThemeVariants(PaperSliderVariant.LUMO_SUCCESS);
        }
        field.setEnabled(!readOnly);
        field.setId(fieldName);
        return field;
    }

    public static void setGroupStyle(HasStyle hasStyle) {
        hasStyle.addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_20,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.SMALL);
    }

    public static <T> MultiSelectListBox<T> getMultiSelectListBox(String fieldName) {
        MultiSelectListBox<T> listBox = new MultiSelectListBox<>();
        listBox.setId(fieldName);
        listBox.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_20,
                LumoUtility.BorderRadius.MEDIUM);
        listBox.setHeight("300px");
        return listBox;
    }

    public static VerticalLayout getWrapForMultiSelectListBox(MultiSelectListBox<?> listBox) {
        String fieldName = listBox.getId().orElse("no id");
        ListItem2 item = new ListItem2(fieldName, fieldName + ".tt");
        listBox.setWidthFull();
        VerticalLayout layout = new VerticalLayout(item, listBox);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.addClassName(LumoUtility.Margin.Top.MEDIUM);
        return layout;
    }

    public static VerticalLayout getWrapForGrid(Grid<?> grid) {
        String fieldName = grid.getId().orElse("null");
        ListItem2 item = new ListItem2(fieldName, fieldName + ".tt");
        grid.setWidthFull();
        VerticalLayout layout = getCompactVerticalLayout(item, grid);
        layout.addClassName(LumoUtility.Margin.Top.MEDIUM);
        return layout;
    }

    public static VerticalLayout getVerticalLayout(Component... children) {
        VerticalLayout layout = children == null ? new VerticalLayout() : new VerticalLayout(children);
        layout.setSpacing(false);
        layout.setPadding(false);
        return layout;
    }

    public static HorizontalLayout getHorizontalLayout2(Component... children) {
        HorizontalLayout layout = children == null ? new HorizontalLayout() : new HorizontalLayout(children);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        layout.setPadding(false);
        return layout;
    }

    public static BigDecimalField getBigDecimalField(String fieldName) {
        return getBigDecimalField(fieldName, false, false);
    }

    public static BigDecimalField getBigDecimalField(
            String fieldName, boolean withHelper, boolean readOnly) {
        BigDecimalField big = new BigDecimalField();
        big.setLabel(big.getTranslation(fieldName));
        big.setId(fieldName);
        if (withHelper) {
            String helper = big.getTranslation(fieldName + ".tt");
            big.setHelperText(helper);
            big.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        big.setReadOnly(readOnly);
        big.setWidthFull();
        return big;
    }

    public static NumberField getNumberField(String fieldName, boolean withHelper, boolean readOnly) {
        NumberField numberF = new NumberField();
        numberF.setLabel(numberF.getTranslation(fieldName));
        numberF.setId(fieldName);
        if (withHelper) {
            String helper = numberF.getTranslation(fieldName + ".tt");
            numberF.setHelperText(helper);
            numberF.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        numberF.setReadOnly(readOnly);
        numberF.setWidthFull();
        return numberF;
    }

    public static DatePicker getDatePicker(String fieldName) {
        return getDatePicker(fieldName, false, false);
    }

    public static DatePicker getDatePicker(String fieldName, boolean withHelper, boolean readOnly) {
        DatePicker picker = new DatePicker();
        picker.setLabel(picker.getTranslation(fieldName));
        if (withHelper) {
            String helper = picker.getTranslation(fieldName + ".tt");
            picker.setHelperText(helper);
            picker.addThemeVariants(DatePickerVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        picker.setReadOnly(readOnly);

        DateFormatSymbols symbols = new DateFormatSymbols();
        DatePicker.DatePickerI18n germanI18n = new DatePicker.DatePickerI18n();
        germanI18n.setMonthNames(Arrays.asList(symbols.getMonths()));

        List<String> list = Arrays.asList(symbols.getWeekdays());
        // in german the first element is blank O.o !?!
        List<String> weekdays = list.stream().filter(StringUtils::isNoneBlank).toList();
        germanI18n.setWeekdays(weekdays);

        list = Arrays.asList(symbols.getShortWeekdays());
        // in german the first element is blank O.o !?!
        List<String> weekdaysShort = list.stream().filter(StringUtils::isNoneBlank).toList();
        germanI18n.setWeekdaysShort(weekdaysShort);

        germanI18n.setToday(picker.getTranslation("DatePicker.Today"));
        germanI18n.setCancel(picker.getTranslation("DatePicker.Cancel"));
        picker.setI18n(germanI18n);
        return picker;
    }

    public static DateTimePicker getDateTimePicker(String fieldName) {
        return getDateTimePicker(fieldName, false, false);
    }

    public static DateTimePicker getDateTimePicker(
            String fieldName, boolean withHelper, boolean readOnly) {
        DateTimePicker picker = new DateTimePicker();
        picker.setLabel(picker.getTranslation(fieldName));
        if (withHelper) {
            String helper = picker.getTranslation(fieldName + ".tt");
            picker.setHelperText(helper);
            picker.addThemeVariants(DateTimePickerVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        picker.setReadOnly(readOnly);

        DateFormatSymbols symbols = new DateFormatSymbols();
        DatePicker.DatePickerI18n germanI18n = new DatePicker.DatePickerI18n();
        germanI18n.setMonthNames(Arrays.asList(symbols.getMonths()));

        List<String> list = Arrays.asList(symbols.getWeekdays());
        // in german the first element is blank O.o !?!
        List<String> weekdays = list.stream().filter(StringUtils::isNoneBlank).toList();
        germanI18n.setWeekdays(weekdays);

        list = Arrays.asList(symbols.getShortWeekdays());
        // in german the first element is blank O.o !?!
        List<String> weekdaysShort = list.stream().filter(StringUtils::isNoneBlank).toList();
        germanI18n.setWeekdaysShort(weekdaysShort);

        germanI18n.setToday(picker.getTranslation("DatePicker.Today"));
        germanI18n.setCancel(picker.getTranslation("DatePicker.Cancel"));
        // picker.setI18n(germanI18n); WTF !!?!?!?!?!?!!?

        return picker;
    }

    public static Notification2 getNotification(String messageId, Object... params) {
        String title = TranslationProvider.getTranslation(messageId + ".title", params);
        String msg = TranslationProvider.getTranslation(messageId + ".message", params);
        Notification2 notification = new Notification2(
                title, msg,
                Notification2.Type.INFO);
        return notification;
    }

    public static TextArea getTextArea(String fieldName) {
        return getTextArea(fieldName, false, false);
    }

    public static void setTextAreaHeight(TextArea textArea, int lines) {
        int lineHeight = 10;
        if (lines < 1)
            return;
        textArea.setWidthFull();
        textArea.setHeight((lines * lineHeight) + "px");
        textArea.setMaxHeight((lines * lineHeight) + "px");
    }

    public static TextArea getTextArea(String fieldName, boolean withHelper, boolean readOnly) {
        TextArea area = new TextArea();
        area.setLabel(area.getTranslation(fieldName));
        area.setId(fieldName);
        if (withHelper) {
            String helper = area.getTranslation(fieldName + ".tt");
            area.setHelperText(helper);
            area.addThemeVariants(TextAreaVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        area.setReadOnly(readOnly);

        return area;
    }

    public static Checkbox getCheckbox(String fieldName) {
        return getCheckbox(fieldName, false);
    }

    public static Checkbox getCheckbox(String fieldName, boolean readOnly) {
        Checkbox checkbox = new Checkbox();
        checkbox.setLabel(checkbox.getTranslation(fieldName));
        checkbox.setId(fieldName);
        checkbox.setReadOnly(readOnly);
        return checkbox;
    }

    public static IntegerField getStepperIntegerField(
            String fieldName, boolean withHelper, int min, int max, int step) {
        IntegerField field = new IntegerField();
        field.setLabel(field.getTranslation(fieldName));
        field.setMin(min);
        field.setMax(max);
        field.setStep(step);
        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        return field;
    }

    public static IntegerField getIntegerField(String fieldName) {
        return getIntegerField(fieldName, null, false, false);
    }

    public static IntegerField getIntegerField(
            String fieldName, boolean withHelper, boolean readOnly) {
        return getIntegerField(fieldName, null, withHelper, readOnly);
    }

    public static IntegerField getIntegerField(
            String fieldName, String withErrorMessage, boolean withHelper, boolean readOnly) {
        IntegerField field = new IntegerField();
        field.setLabel(field.getTranslation(fieldName));

        if (withErrorMessage != null)
            field.setErrorMessage(field.getTranslation(withErrorMessage));

        if (withHelper) {
            String helper = field.getTranslation(fieldName + ".tt");
            field.setHelperText(helper);
            field.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        field.setReadOnly(readOnly);
        return field;
    }

    public static Component getHelperComponent(String fieldName, Object... params) {
        String text = TranslationProvider.getTranslation(fieldName + ".tt", params);
        return getHelperComponent(text, false);
    }

    public static Component getHelperComponent(String text, boolean shortHelp) {
        if (shortHelp) {
            int punt = text.indexOf(".");
            // if the tooltip sentence dont contain "." or the point is an end of the
            // sentence, icon
            // will
            // be no visible
            boolean vis = punt > 0 && punt + 1 < text.length();
            String helper = vis ? text.split("[.]")[0] : text;
            Icon icon = VaadinIcon.QUESTION_CIRCLE_O.create();
            icon.addClassName(LumoUtility.IconSize.SMALL);
            icon.getElement().setProperty("title", text);
            icon.setVisible(vis);
            Div div = new Div(new Text(helper + "  "), icon);
            return div;
        } else {
            return new Div(new Text(text));
        }
    }

    public static CheckboxGroup<String> getCheckboxGroup(String fieldName) {
        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel(checkboxGroup.getTranslation(fieldName + ".tt"));
        checkboxGroup.setItems(checkboxGroup.getTranslation(fieldName));
        return checkboxGroup;
    }

    public static Span getSecondaryMediumLabel(String fieldName, Object... params) {
        return getSecondaryLabel(fieldName, LumoUtility.FontSize.MEDIUM, params);
    }

    public static Span getSecondaryXsmallLabel(String fieldName, Object... params) {
        return getSecondaryLabel(fieldName, LumoUtility.FontSize.XSMALL, params);
    }

    public static Span getSecondarySmallLabel(String fieldName, Object... params) {
        return getSecondaryLabel(fieldName, LumoUtility.FontSize.SMALL, params);
    }

    public static Span getSecondaryLabel(String fieldName, String fontSize, Object... params) {
        Span label = new Span(TranslationProvider.getTranslation(fieldName, params));
        label.addClassNames(LumoUtility.TextColor.SECONDARY);
        label.addClassNames(LumoUtility.LineHeight.SMALL); // <-- better line height for small text
        label.removeClassName(LumoUtility.FontSize.SMALL);
        label.addClassNames(fontSize);
        return label;
    }

    /**
     * return a Help Styled Label
     *
     * @param ttkey  - the key (with .tt) sufix
     * @param params - the parameters
     * @return the label
     */
    public static Span getHelpLabel(String ttkey, Object... params) {
        Span help = new Span();
        String helper = help.getTranslation(ttkey, params);
        help.setText(helper);
        help.addClassNames(
                LumoUtility.FontSize.XSMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.LineHeight.XSMALL);
        return help;
    }

    public static VerticalLayout getLabelAndHelp(
            String labelKey, boolean withHelper, Object... params) {
        Span label = new Span();
        if (labelKey != null) {
            label.setText(label.getTranslation(labelKey, params));
            label.addClassNames(
                    LumoUtility.FontSize.SMALL,
                    LumoUtility.TextColor.SECONDARY,
                    LumoUtility.FontWeight.SEMIBOLD);
        }

        Span help = new Span();
        if (withHelper) {
            help = getHelpLabel(labelKey + ".tt", params);
        }

        return getCompactVerticalLayout(label, help);
    }

    public static Div getOneLineComponentRenderer(String label, String text) {
        Span label2 = new Span(label + ": ");
        Span text2 = new Span(text);
        text2.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        return new Div(label2, text2);
    }

    public static boolean isImage(String value) {
        boolean ok = false;
        for (String ext : VALID_IMAGES) {
            if (value.endsWith(ext.trim()))
                ok = true;
        }
        return ok;
    }

    public static Upload getFileUpload(int sizeInMb, String... fileTypes) {
        MemoryBuffer buffer2 = new MemoryBuffer();
        Upload upload = new Upload(buffer2);
        upload.setAutoUpload(true);
        upload.setAcceptedFileTypes(fileTypes);
        upload.setMaxFileSize(sizeInMb * 1024 * 1024);
        upload.setMaxFiles(1);
        return upload;
    }

    public static VerticalLayout getTitleH(String bundleKey, boolean withHelper) {
        H4 title = new H4();
        title.setText(title.getTranslation(bundleKey));
        title.addClassNames(LumoUtility.Margin.Bottom.XSMALL, Margin.Top.MEDIUM);

        // save space for help line
        if (!withHelper)
            title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        VerticalLayout layout = getCompactVerticalLayout(title);

        if (withHelper) {
            Span text = new Span();
            text.setText(text.getTranslation(bundleKey + ".tt"));
            text.addClassNames(
                    LumoUtility.TextColor.SECONDARY,
                    LumoUtility.FontSize.SMALL,
                    LumoUtility.Margin.Left.MEDIUM);
            layout.add(text);
        }
        return layout;
    }

    public static VerticalLayout getTitle(String bundleKey, boolean withHelper) {
        Span title = new Span();
        title.setText(title.getTranslation(bundleKey));
        title.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.LARGE);

        Span text = new Span();
        text.setText(text.getTranslation(bundleKey + ".tt"));
        text.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);
        text.setVisible(withHelper);

        VerticalLayout layout = new VerticalLayout(title, text);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName(LumoUtility.Border.BOTTOM);
        return layout;
    }

    public static Button getIconButton(LineAwesomeIcon icon, String ttText) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        if (ttText != null) {
            button.setAriaLabel(ttText);
            button.setTooltipText(ttText);
        }
        return button;
    }

    public static Button getButton(String bundleKey, ButtonVariant variant) {
        return getButton(bundleKey, false, variant);
    }

    public static Button getButton(String bundleKey, boolean withToolTip, ButtonVariant variant) {
        Button button = new Button();
        button.setId(bundleKey);
        if (bundleKey != null)
            button.setText(button.getTranslation(bundleKey));
        if (variant != null)
            button.addThemeVariants(variant);
        if (withToolTip)
            button.setTooltipText(bundleKey + ".tt");

        return button;
    }

    public static KeyValuePair getKeyValuePair(String fieldName, Object value) {
        String valueString = value.toString();
        if (value instanceof LocalDate localDate)
            valueString = getFormatedLocalDate(localDate);

        KeyValuePair keyValuePair = new KeyValuePair(TranslationProvider.getTranslation(fieldName), valueString);
        return keyValuePair;
    }

    public static KeyValuePair getKeyValuePair(String group, String fieldName, Object value) {
        KeyValuePair keyValuePair = new KeyValuePair(
                TranslationProvider.getTranslation(fieldName),
                TranslationProvider.getString(group, value.toString()));
        return keyValuePair;
    }

    public static <T extends Enum<T>> MultiSelectComboBox<T> getEnumMultiSelectComboBox(String fieldName,
            Class<T> enumObj) {
        return getEnumMultiSelectComboBox(fieldName, enumObj, false, false);
    }

    public static <T extends Enum<T>> MultiSelectComboBox<T> getEnumMultiSelectComboBox(
            String fieldName, Class<T> enumObj, boolean withHelper, boolean readOnly) {
        MultiSelectComboBox<T> select = new MultiSelectComboBox<>();
        select.setItemLabelGenerator(
                value -> TranslationProvider.getString(enumObj.getSimpleName(), value.toString()));
        T[] ts = enumObj.getEnumConstants();
        select.setItems(ts);
        select.setItemLabelGenerator(key -> TranslationProvider.getString(enumObj.getSimpleName(), key.toString()));
        select.setLabel(select.getTranslation(fieldName));
        if (withHelper) {
            select.setHelperComponent(getHelperComponent(fieldName));
            select.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        select.setReadOnly(readOnly);
        return select;
    }

    public static MultiSelectComboBox<String> getMultiSelectComboBox(String group, String fieldName,
            boolean wihtHelper) {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        List<String> elements = TranslationProvider.getKeys(group);
        comboBox.setItems(elements);
        comboBox.setItemLabelGenerator(key -> TranslationProvider.getString(group, key));
        comboBox.setLabel(comboBox.getTranslation(fieldName));
        if (wihtHelper) {
            comboBox.setHelperComponent(getHelperComponent(fieldName));
            comboBox.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        return comboBox;
    }

    public static <T> MultiSelectComboBox<T> getMultiSelectComboBoxTemplate(
            String fieldName, ItemLabelGenerator<T> generator) {
        return getMultiSelectComboBoxTemplate(fieldName, false, generator);
    }

    public static <T> MultiSelectComboBox<T> getMultiSelectComboBoxTemplate(
            String fieldName, boolean wihtHelper, ItemLabelGenerator<T> generator) {
        MultiSelectComboBox<T> comboBox = getMultiSelectComboBoxTemplate(fieldName, wihtHelper, false);
        comboBox.setItemLabelGenerator(generator);
        return comboBox;
    }

    public static <T> MultiSelectComboBox<T> getMultiSelectComboBoxTemplate(
            String fieldName, boolean wihtHelper, boolean readOnly) {
        MultiSelectComboBox<T> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel(comboBox.getTranslation(fieldName));
        comboBox.setId(fieldName);
        if (wihtHelper) {
            comboBox.setHelperComponent(getHelperComponent(fieldName));
            comboBox.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        comboBox.setReadOnly(readOnly);
        return comboBox;
    }

    public static RadioButtonGroup<String> getRadioButtonGroup(String group, String fieldName) {
        return getRadioButtonGroup(group, fieldName, false, false);
    }

    public static RadioButtonGroup<String> getRadioButtonGroup(
            String group, String fieldName, boolean withHelper, boolean readOnly) {
        List<String> elements = TranslationProvider.getKeys(group);
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel(radioGroup.getTranslation(fieldName));
        if (withHelper) {
            String helper = radioGroup.getTranslation(fieldName + ".tt");
            radioGroup.setHelperText(helper);
            radioGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        radioGroup.setRenderer(
                new ComponentRenderer<>(
                        item -> {
                            Text text = new Text(TranslationProvider.getString(group, item));
                            return text;
                        }));
        radioGroup.setItems(elements);
        radioGroup.setValue(elements.get(0));
        radioGroup.setReadOnly(readOnly);
        return radioGroup;
    }

    public static <T extends Enum<T>> Select<T> getEnumSelect(String fieldName, Class<T> enumObj) {
        return getEnumSelect(fieldName, enumObj, false, false);
    }

    public static <T extends Enum<T>> Select<T> getEnumSelect(
            String fieldName, Class<T> enumObj, boolean withHelper, boolean readOnly) {
        Select<T> select = new Select<>();
        setSelectProperties(select, fieldName, withHelper, readOnly);
        select.setItemLabelGenerator(
                value -> TranslationProvider.getString(enumObj.getSimpleName(), value.toString()));
        T[] ts = enumObj.getEnumConstants();
        select.setItems(ts);
        return select;
    }

    public static <T> Select<T> getSelectTemplate(String fieldName) {
        return getSelectTemplate(fieldName, false, null);
    }

    public static <T> Select<T> getSelectTemplate(String fieldName, boolean withHelper, String emptySelection) {
        Select<T> select = new Select<>();
        setSelectProperties(select, fieldName, withHelper, false);
        if (emptySelection != null) {
            select.setEmptySelectionAllowed(true);
            select.setEmptySelectionCaption(select.getTranslation(emptySelection));
        }
        return select;
    }

    public static <T> ComboBox<T> getComboBoxTemplate(
            String fieldName, boolean withHelper, boolean readOnly) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setAllowCustomValue(true);

        comboBox.setLabel(comboBox.getTranslation(fieldName));
        if (withHelper) {
            String helper = comboBox.getTranslation(fieldName + ".tt");
            comboBox.setHelperText(helper);
            comboBox.addThemeVariants(ComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        comboBox.setReadOnly(readOnly);

        return comboBox;
    }

    public static Select<String> getSelect(List<String> items, String fieldName) {
        return getSelect(items, fieldName, false);
    }

    public static Select<String> getSelect(List<String> items, String fieldName, boolean withHelper) {
        Select<String> select = new Select<>();
        select.setItems(items);
        setSelectProperties(select, fieldName, withHelper, false);
        return select;
    }

    public static Select<String> getSelect(String group, String fieldName) {
        return getSelect(group, fieldName, false, false);
    }

    public static void setSelectProperties(
            Select<?> select, String fieldName, boolean withHelper, boolean readOnly) {
        select.setLabel(select.getTranslation(fieldName));
        if (withHelper) {
            String helper = select.getTranslation(fieldName + ".tt");
            select.setHelperText(helper);
            select.addThemeVariants(SelectVariant.LUMO_HELPER_ABOVE_FIELD);
        }
        select.setReadOnly(readOnly);
    }

    public static Select<String> getSelect(
            String group, String fieldName, boolean withHelper, boolean readOnly) {
        Select<String> select = UIUtils.getSelect(group);
        setSelectProperties(select, fieldName, withHelper, readOnly);
        return select;
    }

    /**
     * create and return a {@link Select} with supportet languages. on selection
     * this component
     * trigger a change in the session language and set the current Locale to the
     * selected language
     *
     * @return language selector
     */
    public static Select<String> getLanguageSelect() {
        Select<String> languages = UIUtils.getSelect("languages");
        String currLang = VaadinSession.getCurrent().getLocale().getLanguage();
        languages.setValue(currLang);
        languages.addValueChangeListener(
                evt -> {
                    String lan = languages.getValue();
                    VaadinSession.getCurrent().setLocale(Locale.forLanguageTag(lan));
                    UI.getCurrent().getPage().reload();
                });
        return languages;
    }

    public static Select<String> getSelect(String group) {
        List<String> items = TranslationProvider.getKeys(group);
        Select<String> select = new Select<>();

        int idx = items.indexOf("000");
        if (idx >= 0) {
            String p1 = items.remove(idx);
            select.setPlaceholder(p1);
        }
        select.setItems(items);
        select.setValue(items.get(0));
        select.setItemLabelGenerator(key -> TranslationProvider.getString(group, key));
        return select;
    }

    public static boolean showNotification(Response response) {
        return showNotification(response, false, false);
    }

    /**
     * show a standar backend Response as Vaadin Notification. this method also
     * return true if the
     * response was a positive response. (not an error)
     *
     * @param response        - the response
     * @param onlyOnError     - true if you want only show error responses. not ok
     *                        responses
     * @param whitCloseButton - append a close button
     * @return true for positive, false in case the response was an error responce
     */
    public static boolean showNotification(
            Response response, boolean onlyOnError, boolean whitCloseButton) {
        boolean isOk = response.getStatus() < 400;
        if (isOk && onlyOnError)
            return true;
        String title = isOk
                ? TranslationProvider.getTranslation("Response.ok.title")
                : TranslationProvider.getTranslation("Response.error.title");
        VaadinIcon icon = isOk ? VaadinIcon.INFO_CIRCLE_O : VaadinIcon.WARNING;
        String color = isOk ? LumoUtility.TextColor.SUCCESS : LumoUtility.TextColor.ERROR;
        String message = ((Map<?, ?>) response.getEntity()).get("message").toString();
        int duration = 0;
        if (!whitCloseButton)
            duration = isOk ? NOTIFICATION_INFO_DURATION : NOTIFICATION_ERROR_DURATION;
        showNotification(icon, color, title, message, duration);
        return isOk;
    }

    public static void showErrorNotification(String messageId, Object... parms) {
        String title = (new Span()).getTranslation(messageId + ".title", parms);
        String message = (new Span()).getTranslation(messageId + ".message", parms);
        showNotification(
                VaadinIcon.WARNING,
                LumoUtility.TextColor.ERROR,
                title,
                message,
                NOTIFICATION_ERROR_DURATION);
    }

    public static void showErrorNotification(String title, String message) {
        showNotification(
                VaadinIcon.WARNING,
                LumoUtility.TextColor.ERROR,
                title,
                message,
                NOTIFICATION_ERROR_DURATION);
    }

    public static void showSuccesNotification(String messageId) {
        String title = (new Span()).getTranslation(messageId + ".title");
        String message = (new Span()).getTranslation(messageId + ".message");
        showNotification(
                VaadinIcon.INFO_CIRCLE_O,
                LumoUtility.TextColor.SUCCESS,
                title,
                message,
                NOTIFICATION_INFO_DURATION);
    }

    public static void showNotification(
            VaadinIcon icon, String titleColor, String title, String message, int duration) {
        Notification notification = new Notification();
        notification.setDuration(duration);
        notification.setPosition(Position.BOTTOM_END);

        Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.addClickListener(event -> notification.close());

        Icon icon2 = icon.create();
        icon2.addClassNames(titleColor, LumoUtility.IconSize.LARGE);

        Div title2 = new Div(new Text(title));
        title2.addClassNames(titleColor, LumoUtility.FontSize.LARGE);

        if (!message.startsWith("<"))
            message = "<p>" + message + "</p>";
        Div info = new Div(title2, new Html(message));

        HorizontalLayout layout;
        if (duration <= 0)
            layout = new HorizontalLayout(icon2, info, closeButton);
        else
            layout = new HorizontalLayout(icon2, info);

        notification.add(layout);
        notification.open();
    }

    public static String getFormatedLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return "";
        return dateTimeFormatter.format(localDateTime);
    }

    public static String getFormatedInstant(Instant instant) {
        ZoneId zoneId = ZoneId.systemDefault();
        return getFormatedLocalDateTime(LocalDateTime.ofInstant(instant, zoneId));
    }

    public static String getFormatedLocalDate(LocalDate localDate) {
        if (localDate == null)
            return "";
        return dateFormatter.format(localDate);
    }

    public static ListItem2 getListItem(String primaryKey, String secondaryKey, String image, Object... parms) {
        String primary = TranslationProvider.getTranslation(primaryKey, parms);
        String secondary = TranslationProvider.getTranslation(secondaryKey, parms);
        ListItem2 item = new ListItem2(primary, secondary);
        if (image != null) {
            Avatar avatar = new Avatar();
            avatar.setImage(image);
            item.setPrefix(avatar);
        }
        return item;
    }

    public static ListItem2 getSwitchListItem(String field, RadioButtonGroup<Boolean> switch2, Object... parms) {
        String primary = TranslationProvider.getTranslation(field, parms);
        String secondary = TranslationProvider.getTranslation(field + ".tt", parms);
        ListItem2 item = new ListItem2(primary, secondary);
        if (switch2 != null) {
            item.setSuffix(switch2);
        }
        // add top space (assuming ui input in forms)
        item.addClassName(LumoUtility.Margin.Top.MEDIUM);
        return item;
    }

    public static RadioButtonGroup<Boolean> getYesNoRadioButtonGroup() {
        return getBooleanRadioButtonGroup("Boolean.yes", "Boolean.no");
    }

    public static RadioButtonGroup<Boolean> getBooleanRadioButtonGroup(String trueText, String falseText) {
        RadioButtonGroup<Boolean> radioButtonGroup = getBooleanRadioButtonGroup();
        radioButtonGroup.setRenderer(new ComponentRenderer<>(b -> {
            String key = b.booleanValue() ? TranslationProvider.getTranslation(trueText)
                    : TranslationProvider.getTranslation(falseText);
            Span span = new Span(key);
            span.addClassNames(Padding.Horizontal.SMALL);
            return span;
        }));
        return radioButtonGroup;

    }

    public static RadioButtonGroup<Boolean> getBooleanRadioButtonGroup() {
        RadioButtonGroup<Boolean> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setItems(Boolean.TRUE, Boolean.FALSE);
        radioButtonGroup.setRenderer(new ComponentRenderer<>(
                b -> b.booleanValue() ? new Span(LineAwesomeIcon.CHECK_SOLID.create())
                        : new Span(LineAwesomeIcon.CIRCLE.create())));
        radioButtonGroup.addThemeName(RadioButtonTheme.TOGGLE);
        radioButtonGroup.getChildren()
                .forEach(component -> component.getElement().getThemeList().add(RadioButtonTheme.TOGGLE));
        return radioButtonGroup;
    }

    /**
     * Gets the number of seconds in this duration.
     *
     * @param duration - the string representing a duration
     * @return the seconds
     */
    public static int getSeconds(String duration) {
        Duration d = Duration.parse(duration);
        return (int) d.getSeconds();
    }

    /**
     * return the icon name retrived from the value. the value argument generaly is
     * a internal value.
     * this value will be transformed to lowercase and the icon must be a png in
     * icons folder
     *
     * @param value - original valus
     * @return the name
     */
    public static String getIconFrom(String value) {
        return ICON_PATH + value.toLowerCase() + ".png";
    }

    public static Div getXSmallSeparator() {
        Div div = new Div();
        div.addClassNames(LumoUtility.Height.XSMALL);
        return div;
    }

    public static Image getImageIcon(String source) {
        return getImageIcon(source, "30px");
    }

    public static Image getImageIcon(String source, String size) {
        return getImageIcon(source, size, size);
    }

    /**
     * return a standar image with all attributtet setted
     *
     * @param source - image source. if null, the returned image has no source
     * @param size   - Height und Width for the image
     * @return a image
     */
    public static Image getImageIcon(String source, String sizeWith, String sizeHeight) {
        Image image = new Image();
        if (sizeWith != null)
            image.setWidth(sizeWith);
        if (sizeHeight != null)
            image.setHeight(sizeHeight);
        if (source != null)
            setImage(image, source);

        return image;
    }

    public static Hr getHrSeparator() {
        Hr hr = new Hr();
        hr.setHeight("2px");
        hr.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.BorderColor.CONTRAST_30);
        return hr;
    }

    public static HorizontalLayout getOrSeparator(String bundleId) {
        VerticalLayout vl = new VerticalLayout(new Hr());
        vl.setWidth("50%");
        vl.setPadding(false);
        vl.setMargin(false);

        VerticalLayout vr = new VerticalLayout(new Hr());
        vr.setWidth("50%");
        vr.setPadding(false);
        vr.setMargin(false);

        Span span = new Span();
        if (bundleId != null) {
            span.setText(span.getTranslation(bundleId));
        }

        HorizontalLayout layout = new HorizontalLayout(vl, span, vr);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    public static void setSize(HasSize hasSize, int size) {
        hasSize.setWidth(size + "px");
        hasSize.setHeight(size + "px");
    }

    public static StreamResource getStreamResource(String source) {
        File file = new File(source);
        StreamResource resource = new StreamResource(
                "image",
                () -> {
                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        return inputStream;
                    } catch (Exception e) {
                        Log.error(e);
                        return null;
                    }
                });
        return resource;
    }

    public static void setImage(Component image, String source) {
        if (image == null || source == null)
            return;

        if (!(image instanceof Image || image instanceof Avatar))
            throw new IllegalArgumentException(INVALID_IMAGE_COMPONENT);

        // static content or external url source
        if (image instanceof Image image2)
            image2.setSrc(source);
        else
            ((Avatar) image).setImage(source);
    }

    public static Component getOnlyTrueBooleanIcon(boolean b) {
        if (!b)
            return new Span();
        return getBooleanIcon(b);
    }

    public static Icon getBooleanIcon(boolean b) {
        Icon icon = new Icon(b ? VaadinIcon.CHECK : VaadinIcon.MINUS);
        icon.addClassNames(
                LumoUtility.IconSize.SMALL,
                (b ? LumoUtility.TextColor.SUCCESS : LumoUtility.TextColor.DISABLED));
        return icon;
    }

    /**
     * create a standar footer component
     *
     * @return footer component
     */
    public static HorizontalLayout getFooter() {
        HorizontalLayout footer = getCompactHorizontalLayout();
        footer.setPadding(true);
        footer.setSpacing(true);
        footer.setJustifyContentMode(JustifyContentMode.END);
        footer.addClassNames(
                LumoUtility.Border.TOP,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.Background.CONTRAST_5);
        return footer;
    }

    public static HorizontalLayout getCompactHorizontalLayout(Component... childrens) {
        return getCompactHorizontalLayout(false, childrens);
    }

    public static HorizontalLayout getCompactHorizontalLayout(
            boolean centered, Component... childrens) {
        HorizontalLayout layout = new HorizontalLayout(childrens);
        setCompatStyle(layout);
        if (centered) {
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        }
        return layout;
    }

    public static void setCompatStyle(HorizontalLayout layout) {
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setWidthFull();
    }

    public static Image getAppLogo(String theme) {
        String logoName = "sitrone-logo-" + theme;
        Image image = new Image(ICON_PATH + logoName + ".svg", logoName);
        image.setWidth("200px");
        image.setHeight("64px");
        return image;
    }

    public static void animateError(HasStyle hasStyle) {
        AnimationBuilder.createBuilderFor(hasStyle)
                .create(AnimationTypes.AttentionSeekerAnimation.class)
                .withEffect(AttentionSeeker.shakeX)
                .withSpeed(Speed.fast) // optional
                .withDelay(Delay.noDelay) // optional
                .withRepeat(Repeat.Once) // optional
                .start();
    }


    public static Component findComponentById(Component parent, String id) {
        List<Component> components = new ArrayList<>();
        components.addAll(parent.getChildren().toList());
        for (Component child : components) {
            Optional<String> optional = child.getId();
            if (optional.isPresent() && optional.get().equals(id)) {
                return child; // found it!
            } else {
                Component result = findComponentById(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null; // none was found
    }

    /**
     * return the area designated as input(login) or ouput 404 page info
     *
     * @return the area
     */
    public static VerticalLayout getInputOuputArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getStyle().set("background", "hsla(0, 100%, 100%, 0.93)");
        layout.setMinWidth(UIUtils.LOGGING_FORM_WITH);
        layout.setMinHeight(UIUtils.LOGGING_FORM_HEIGHT);
        layout.setWidth(UIUtils.LOGGING_FORM_WITH);
        setGroupStyle(layout);
        return layout;
    }

    /**
     * return a Thumbnail of the youtube url main video
     *
     * @param url - the url
     * @return the Thumbnail.s url
     */
    public static String getVideoThumbnail(String url) {
        String pattern = "watch/?.*v=([^&]*)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            String id = matcher.group(1);
            String url3 = "https://img.youtube.com/vi/" + id + "/0.jpg";
            return url3;
        }
        return null;
    }

    public static String getSocialMediaIcon(String url) {
        String icon = ICON_PATH + "no_social_network_icon.png";
        if (url.contains("facebook.com"))
            icon = ICON_PATH + "facebook.png";
        if (url.contains("instagram.com"))
            icon = ICON_PATH + "instagram.png";
        if (url.contains("tiktok.com"))
            icon = ICON_PATH + "tiktok.png";
        if (url.contains("linkedin.com"))
            icon = ICON_PATH + "linkedin.png";
        if (url.contains("youtube.com"))
            icon = ICON_PATH + "youtube.png";
        if (url.contains("twitter.com"))
            icon = ICON_PATH + "twitter.png";
        return icon;
    }

    /**
     * retrun true if the fileName is a member of the files types fileExt
     *
     * @param fileName - the file name (with extention)
     * @param fileExt  - the list of extentions
     * @return true if the file is a member
     */
    public static boolean isFileMemberOf(String fileName, String... fileExt) {
        boolean ok = false;
        for (String ext : fileExt) {
            if (fileName.endsWith(ext.trim()))
                ok = true;
        }
        return ok;
    }

    public static VerticalLayout getWrapForCheckBox(Checkbox checkbox) {
        Span label = UIUtils.getSecondaryXsmallLabel(checkbox.getId().orElse("") + ".tt");
        label.addClassNames(LumoUtility.Margin.Left.LARGE);
        VerticalLayout layout = new VerticalLayout(checkbox, label);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.LineHeight.NONE);
        return layout;
    }

    public static Component getSmallImageRender(String image, String firstLine, String secondLine) {
        return getImageRender(image, firstLine, secondLine, "20px");
    }

    public static Component getImageRender(String image, String firstLine, String secondLine) {
        return getImageRender(image, firstLine, secondLine, "30px");
    }

    public static Component getImageRender(
            String image, String firstLine, String secondLine, String size) {
        Image image2 = getImageIcon(image, size);

        Span firstLine2 = new Span(firstLine);

        Span secondLine2 = new Span(secondLine);
        secondLine2.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        Div firstAndSecod = new Div(firstLine2, secondLine2);
        firstAndSecod.addClassNames(Display.FLEX, FlexDirection.COLUMN);

        Div owner = new Div(image2, firstAndSecod);
        owner.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.MEDIUM);
        return owner;
    }

    public static ListItem2 getAvatarRender(String url, String firstLine, String secondLine) {
        ListItem2 item = new ListItem2(new Avatar(firstLine, url), firstLine, secondLine);
        return item;
    }

    public static Component getH2(String fieldId) {
        return getH2(fieldId, true);
    }

    public static Component getH2(String fieldId, boolean withHelp) {
        H2 h2 = new H2(TranslationProvider.getTranslation(fieldId));
        h2.addClassNames(FontSize.LARGE);
        h2.setId(fieldId);

        Component component = h2;
        if (withHelp) {
            Span description = UIUtils.getSecondaryXsmallLabel(fieldId + ".tt");
            component = UIUtils.getCompactVerticalLayout(h2, description);
        }

        return component;
    }

    public static Component getH4(String title, String help) {
        H4 h4 = new H4(title);
        h4.addClassNames(FontSize.MEDIUM);
        h4.setId(title.replace(" ", "-").toLowerCase());

        Component component = h4;
        if (help != null) {
            Paragraph description = new Paragraph(help);
            description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
            component = new Div(h4, description);
        }

        return component;
    }

    /**
     * create a Layout with standar parameters
     *
     * @param components - the components
     * @return the Layout
     */
    public static Layout getLayout(Component... components) {
        Layout layout = new Layout(components);
        setDefaults(layout);
        return layout;
    }

    /**
     * set the standars values for a Layout
     *
     * @param layout - the layout
     */
    public static void setDefaults(Layout layout) {
        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumns(Layout.GridColumns.COLUMNS_4);
    }

    public static Anchor getAnchor(String field, String href) {
        Anchor anchor = new Anchor();
        anchor.setText(TranslationProvider.getTranslation(field));
        anchor.getElement().setAttribute("target", "_blank");

        setAnchorhRef(anchor, href);
        return anchor;
    }

    public static void setAnchorhRef(Anchor anchor, String href) {
        String href2 = href == null ? "" : href;
        anchor.setHref(href2);
        anchor.setEnabled(!"".equals(href2));
    }

    public static Item getItem(String text, LineAwesomeIcon icon) {
        return getItem(text, icon.create());
    }

    public static Item getItem(String text, SvgIcon icon) {
        Item item = new Item(text);
        if (icon != null)
            item = new Item(text, icon);

        item.addClassNames(BorderRadius.MEDIUM, LineHeight.XSMALL, Padding.SMALL, "hover:bg-contrast-5");
        item.getStyle().set("cursor", "pointer");
        return item;
    }

    public static ListItem getListItem(String text, LineAwesomeIcon icon, Class<? extends Component> navigationTarget) {
        Item item = getItem(text, icon);
        RouterLink link = new RouterLink(navigationTarget);
        link.addClassNames(TextColor.BODY, "no-underline");
        link.add(item);
        return new ListItem(item);
    }

    public static Popover getPopover(Component target) {
        Popover popover = new Popover();
        if (target != null)
            popover.setTarget(target);

        popover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        popover.setModal(true);
        return popover;
    }

    public static Component getFileIcon(String fileName) {
        Div corner = new Div();
        corner.addClassNames(Background.CONTRAST_50, Display.FLEX,
                com.vaadin.flow.theme.lumo.LumoUtility.Position.ABSOLUTE, "end-0", "top-0");
        corner.setHeight(30, Unit.PERCENTAGE);
        corner.setWidth(30, Unit.PERCENTAGE);

        Div fileIcon = new Div(corner, new Span(fileName.substring(fileName.lastIndexOf(".") + 1)));
        fileIcon.addClassNames(AlignItems.END, Background.PRIMARY, BorderRadius.MEDIUM, Display.FLEX, FontSize.XXSMALL,
                FontWeight.MEDIUM, Height.LARGE, JustifyContent.CENTER, Padding.XSMALL,
                com.vaadin.flow.theme.lumo.LumoUtility.Position.RELATIVE,
                TextColor.PRIMARY_CONTRAST, Width.MEDIUM);
        fileIcon.getStyle().set("clip-path", "polygon(0% 0%, 70% 0%, 100% 30%, 100% 100%, 0% 100%)");
        return fileIcon;
    }

    public static RouterLink getRouterLink() {
        RouterLink link = new RouterLink(HomeView.class);
        link.add(LumoIcon.ARROW_LEFT.create());
        link.addClassNames(AlignItems.CENTER, BorderRadius.MEDIUM, Display.FLEX, Height.MEDIUM, JustifyContent.CENTER,
                Width.MEDIUM);
        return link;
    }

    public static Button getNavigateBackButton() {
        Button button = new Button(LumoIcon.ARROW_LEFT.create(), e -> UI.getCurrent().getPage().getHistory().back());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    public static Header getHeader(Class<? extends Component> clazz) {
        String clsName = UIUtils.getSimpleClassName(clazz);
        Header header = new Header(TranslationProvider.getTranslation(clsName));
        header.setGap(Layout.Gap.MEDIUM);
        header.setHeadingFontSize(Font.Size.XXLARGE);
        header.removeClassName(Border.BOTTOM);
        header.setPrefix(UIUtils.getNavigateBackButton());
        header.addClassName(LumoUtility.Display.FLEX);
        return header;
    }


    public static Component createIcon(
            LineAwesomeIcon icon, Color.Background background, Color.Text color) {
        SvgIcon icon2 = icon.create();
        icon2.addClassNames(IconSize.MEDIUM);

        Layout container = new Layout(icon2);
        container.addClassNames(BorderRadius.MEDIUM, Height.LARGE, Width.LARGE);

        if (background != null)
            container.addClassName(background.getClassName());

        if (color != null)
            container.addClassName(color.getClassName());

        container.setAlignItems(Layout.AlignItems.CENTER);
        container.setJustifyContent(Layout.JustifyContent.CENTER);
        return container;
    }

    public static InputGroup createSearchInputGroup(TextField media, Button searchB) {
        InputGroup mediaInputGroup = new InputGroup(media, searchB);
        mediaInputGroup.setWidthFull();
        media.setWidthFull();
        return mediaInputGroup;
    }
}
