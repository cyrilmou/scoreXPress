package fr.cm.common.util;

import java.util.Collection;

public class CollectionUtils {
    private CollectionUtils() {
    }

    @SuppressWarnings({"UnusedDeclaration", "TypeMayBeWeakened"})
    public static <T> Iterable<T> weakenType(final Iterable<? extends T> balisesOrdonnee, final Class<T> klass) {
        return (Iterable<T>) balisesOrdonnee;
    }

    @SuppressWarnings({"UnusedDeclaration", "TypeMayBeWeakened"})
    public static <T> Collection<T> weakenType(final Collection<? extends T> balisesOrdonnee, final Class<T> klass) {
        return (Collection<T>) balisesOrdonnee;
    }

    public static <T> Collection<T> toCollection(final Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }
}
