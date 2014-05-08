package fr.cm.scorexpress.core.model;

import static fr.cm.scorexpress.core.model.ColTableType.strToColTableType;

import static java.lang.Boolean.parseBoolean;

public class ColTableUtils {
    private static final String EMPTY = "";
    private static final String NULL = "null";

    private ColTableUtils() {
    }

    public static boolean toBoolean(final String str) {
        if (str == null) {
            return false;
        }
        try {
            return parseBoolean(str);
        } catch (Exception ex) {
            return false;
        }
    }

    public static String toStringValue(final String str) {
        return toStringNotNull(str, EMPTY);
    }

    private static String toStringNotNull(final String str, final String defaultStr) {
        if (str == null || str.equals(NULL)) {
            return defaultStr;
        } else {
            return str;
        }
    }

    public static ColTableType parseType(final String type) {
        return strToColTableType(type);
    }

    public static boolean isBooleanType(final ColTable colTable) {
        return colTable.getType() != null && ColTableType.BOOLEAN.equals(colTable.getType());
    }

    public static boolean isDateType(final ColTable colTable) {
        return colTable.getType() != null && ColTableType.DATE.equals(colTable.getType());
    }
}
