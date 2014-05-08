package fr.cm.scorexpress.core.model;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Date;

public class BaliseFactory {
    private BaliseFactory() {
    }

    public static ObjBalise createBalise(final String num, final String type) {
        return createBalise(num, type, EMPTY);
    }

    public static ObjBalise createBalise(final String num, final String type, final String description) {
        return new ObjBalise(num, type, description);
    }

    public static ObjBalise createBalise(final String num, final String type, final Date penalite,
                                         final int... orders) {
        final ObjBalise balise = new ObjBalise(num, type, penalite);
        int i = 1;
        for (final int order : orders) {
            balise.setOrder(i, order);
            i++;
        }
        return balise;
    }
}
