package fr.cm.scorexpress.utils;

import fr.cm.scorexpress.core.model.ObjResultat;
import java.util.Comparator;

public class ResultatComparator {
    private ResultatComparator() {
    }

    public static Comparator<ObjResultat> byTime() {
        return new Comparator<ObjResultat>() {
            @Override
            public int compare(final ObjResultat res, final ObjResultat res1) {
                if (res1 == null) {
                    return -1;
                }
                if (res == null) {
                    return +1;
                }
                return res.compareTo(res1);
            }
        };
    }

    public static Comparator<ObjResultat> byDeclassed() {
        return new Comparator<ObjResultat>() {
            @Override
            public int compare(final ObjResultat res, final ObjResultat res1) {
                if (res1 == null) {
                    return -1;
                }
                if (res == null) {
                    return +1;
                }
                if (res1.isDeclasse() || res1.isHorsClassement()) {
                    return -1;
                }
                if (res.isDeclasse() || res.isHorsClassement()) {
                    return +1;
                }
                if (res1.isNotArrived()) {
                    return -1;
                }
                if (res.isNotArrived()) {
                    return +1;
                }
                return 0;
            }
        };
    }

    public static Comparator<ObjResultat> byAbandon() {
        return new Comparator<ObjResultat>() {
            @Override
            public int compare(final ObjResultat res, final ObjResultat res1) {
                if (res1 == null) {
                    return -1;
                }
                if (res == null) {
                    return +1;
                }
                if (res1.isAbandon()) {
                    return -1;
                }
                if (res.isAbandon()) {
                    return +1;
                }
                return 0;
            }
        };
    }

}
