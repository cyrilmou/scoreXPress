package fr.cm.scorexpress.ihm.editor.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Message {
    private static final String BUNDLE_NAME = Message.class.getName();

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME, Locale.getDefault(), Message.class
                    .getClassLoader());

    private Message() {
    }

    public static String i18n(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
