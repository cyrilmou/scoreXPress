package fr.cm.scorexpress.core.model;

import java.util.Date;

public enum ColTableType {
    STRING(null, String.class),
    BOOLEAN("Booleen", Boolean.class),
    DATE("Date", Date.class),
    ENTIER("Entier", Integer.class);

    private final String label;
    private final Class type;

    ColTableType(final String label, final Class type) {
        this.label = label;
        this.type = type;
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static ColTableType strToColTableType(final String str) {
        if (str == null) {
            return STRING;
        }
        for (final ColTableType colTableType : values()) {
            if (colTableType.getLabel() != null && colTableType.getLabel().equals(str)) {
                return colTableType;
            }
        }
        return STRING;
    }

    public Class getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public boolean match(final Object str) {
        return label.equals(str);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static boolean isSame(final ColTableType type1, final ColTableType type2) {
        return type1.equals(type2);
    }
}
