package fr.cm.scorexpress.core.util;

import static com.google.common.collect.Lists.newArrayList;
import fr.cm.scorexpress.core.model.*;
import static fr.cm.scorexpress.core.model.Balise.TYPE_ORDONNEE;
import static fr.cm.scorexpress.core.model.BaliseUtils.getBalisesOrdonnee;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.*;

public class BaliseOrderUtils {
    private BaliseOrderUtils() {
    }

    public static ArrayList<ObjChrono> getChronoOrdonnee(final AbstractBalises step, final IChronos userChronos,
                                                         final int nrOrdre) {
        final Collection<Balise> baliseOrdo = getBalisesOrdonnee(TYPE_ORDONNEE, nrOrdre, step.getBalises());
        return getChronoOrdonnee(baliseOrdo, userChronos);
    }

    public static ArrayList<ObjChrono> getChronoOrdonnee(final Collection<Balise> baliseOrdo,
                                                         final IChronos userChronos) {
        final Iterator<ObjChrono> chronoIter = userChronos.getChronos().iterator();
        final ArrayList<ObjChrono> res = newArrayList();
        while (chronoIter.hasNext()) {
            final ObjChrono chrono = chronoIter.next();
            if (baliseOrdo.contains(new ObjBalise(chrono.getNumBalise(), EMPTY, EMPTY))) {
                if (!chrono.getTemps().isNull()) {
                    res.add(chrono);
                }
            }
        }
        Collections.sort(res, new Comparator<ObjChrono>() {
            public int compare(final ObjChrono chrono1, final ObjChrono chrono2) {
                return chrono1.getTemps().compareTo(chrono2.getTemps());
            }
        });
        return res;
    }

    public static boolean calculOrdreBalise(final AbstractBalises step, final ObjUserChronos userChronos, int nbOrdre) {
        if (nbOrdre <= 0) {
            nbOrdre = 2;
        }
        Date meilleurCout = null;
        Iterable<ObjChrono> chronosMeilleur = null;
        Collection<ObjChrono> solutionMeilleur = null;
        for (int nrOrdre = 0; nrOrdre < nbOrdre; nrOrdre++) {
            final ArrayList<Balise> balises = getBalisesOrdonnee(TYPE_ORDONNEE, nrOrdre, step.getBalises());
            final Collection<ObjChrono> solution = newArrayList();
            final ArrayList<ObjChrono> chronos = getChronoOrdonnee(step, userChronos, nrOrdre);
            final VerificateurOrdreBalise chemin = new VerificateurOrdreBalise(chronos, balises);
            chemin.optimize(solution);
            // System.out.print("sol=");
            final Date cout = chemin.getCout();
            if (meilleurCout == null || cout != null && (meilleurCout.before(cout) ||
                    cout.equals(meilleurCout) && solution.size() > solutionMeilleur.size())) {
                meilleurCout = cout;
                chronosMeilleur = chronos;
                solutionMeilleur = solution;
                userChronos.setOrdre(nrOrdre + EMPTY);
                // balisesMeilleur = balises;
            } else {
                if (cout != null && cout.equals(meilleurCout) && solution.size() == solutionMeilleur.size()) {
                    userChronos.setOrdre(userChronos.getOrdre() + ',' + nrOrdre);
                }
            }
        }
        boolean res = false;
        if (chronosMeilleur != null) {
            for (final ObjChrono chrono : chronosMeilleur) {
                if (solutionMeilleur.contains(chrono)) {
                    chrono.setCancel(false);
                } else {
                    chrono.setCancel(true);
                    res = true;
                }
            }
        }
        return res;
    }
}
