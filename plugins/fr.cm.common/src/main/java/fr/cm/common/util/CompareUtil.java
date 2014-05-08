package fr.cm.common.util;

public class CompareUtil {
    private CompareUtil() {
    }

    public static int compare(final Object elem1, final Object elem2) {
        if (elem1 == null) {
            return -1;
        }
        if (elem2 == null) {
            return 1;
        }
        if (!elem1.getClass().isInstance(elem2)) {
            throw new RuntimeException("Not comparable");
        }
        if (elem1 instanceof Comparable) {
            return ((Comparable) elem1).compareTo(elem2);
        }
        return elem1.toString().compareTo(elem2.toString());
    }
}
