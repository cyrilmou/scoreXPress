package fr.cm.scorexpress.core.model;

import java.util.Comparator;

public class ChronoUtils {
    private ChronoUtils() {
    }

    public static Comparator<ObjChrono> getComparatorHeure() {
        return new Comparator<ObjChrono>() {

            public int compare(final ObjChrono chrono1, final ObjChrono chrono2) {
                return chrono1.getTemps().compareTo(chrono2.getTemps());
            }
        };
    }

    public static Comparator<ObjDossard> getComparator() {
        return new Comparator<ObjDossard>() {

            public int compare(final ObjDossard object, final ObjDossard object1) {
                return object.compareTo(object1);
            }
        };
    }
}
