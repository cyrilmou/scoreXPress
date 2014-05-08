package fr.cm.scorexpress.ihm.action.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

public class Messages {
    private static final String BUNDLE_NAME = Messages.class.getName();
    private static final ResourceBundle RESOURCE_BUNDLE =
            getBundle(BUNDLE_NAME, Locale.getDefault(), Messages.class.getClassLoader());

    private Messages() {
    }

    public static String i18n(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
