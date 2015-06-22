package fr.cm.scorexpress.core.model;

import static com.google.common.collect.Lists.newArrayList;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.*;
import static java.util.Collections.sort;

public class BaliseUtils {
    private BaliseUtils() {
    }

    public static boolean setBalise(final Collection<ObjBalise> balises, final String baliseArrivee,
                                    final String typeBalise) {
        boolean modify = false;
        final Collection<Balise> remove = newArrayList();
        for (final Balise balise : balises) {
            if (balise.getType().equalsIgnoreCase(typeBalise)) {
                if (modify) {
                    remove.add(balise);
                } else {
                    modify = true;
                    if (!balise.getNum().equals(baliseArrivee)) {
                        balise.setNum(baliseArrivee);
                    }
                    if (baliseArrivee == null || baliseArrivee.equals(EMPTY)) {
                        remove.add(balise);
                    }
                }
            }
        }
        balises.removeAll(remove);
        if (!modify && baliseArrivee != null && !baliseArrivee.equals(EMPTY)) {
            balises.add(new ObjBalise(baliseArrivee, typeBalise, EMPTY));
            modify = true;
        }
        return modify;
    }

    public static String findNumBaliseOfType(final Iterable<ObjBalise> balises, final String type) {
        for (final Balise balise : balises) {
            if (isTypeBalise(balise, type)) {
                return balise.getNum();
            }
        }
        return null;
    }

    public static boolean isTypeBalise(final Balise balise, final String type) {
        return balise.getType().equalsIgnoreCase(type);
    }

    public static ArrayList<Balise> getBalisesOrdonnee(final String type, final int nrOrdre,
                                                       final Collection<ObjBalise> balises) {
        final Iterator<ObjBalise> iter = balises.iterator();
        final ArrayList<Balise> res = newArrayList();
        while (iter.hasNext()) {
            final Balise balise = iter.next();
            if (StringUtils.equals(type, balise.getType())) {
                if (nrOrdre == 0) {
                    res.add(balise);
                } else {
                    final int ordre = balise.getOrdre(nrOrdre);
                    if (ordre >= 0) {
                        res.add(balise);
                    }
                }
            }
        }
        final Comparator<Balise> orderComparator = new Comparator<Balise>() {
            public int compare(final Balise b1, final Balise b2) {
                try {
                    final int ordre1 = b1.getOrdre(nrOrdre);
                    final int ordre2 = b2.getOrdre(nrOrdre);
                    if (ordre2 < 0) {
                        return 1;
                    } else if (ordre1 < 0) {
                        return -1;
                    }
                    return new Integer(ordre1).compareTo(new Integer(ordre2));
                } catch (Exception e) {
                    return 0;
                }
            }
        };
        if (nrOrdre != 0) {
            sort(res, orderComparator);
        }
        return res;
    }

    public static Collection<Balise> getBalisesByOrder(final int nrOrdre, final Collection<ObjBalise> balises) {
        final List<Balise> res = newArrayList();
        res.addAll(balises);
        if (nrOrdre != 0) {
            sort(res, new Comparator<Balise>() {
                public int compare(final Balise balise1, final Balise balise2) {
                    return new Integer(balise1.getOrdre(nrOrdre)).compareTo(balise2.getOrdre(nrOrdre));
                }
            });
        }
        return res;
    }
}
