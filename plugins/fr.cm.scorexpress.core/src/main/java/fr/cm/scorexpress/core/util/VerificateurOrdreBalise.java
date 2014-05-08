package fr.cm.scorexpress.core.util;

import static com.google.common.collect.Lists.newArrayList;
import fr.cm.scorexpress.core.model.Balise;
import static fr.cm.scorexpress.core.model.Balise.TYPE_ORDONNEE;
import fr.cm.scorexpress.core.model.BaliseFactory;
import fr.cm.scorexpress.core.model.ObjChrono;
import fr.cm.scorexpress.core.model.impl.Date2;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.downTime;
import static fr.cm.scorexpress.core.model.impl.DateUtils.upTime;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class VerificateurOrdreBalise {
    private final ArrayList<Balise> balises;
    private final ArrayList<ObjChrono> chronos;
    private Collection<ObjChrono> solution;
    private Collection<ObjChrono> current = newArrayList();
    private AbstractList<Balise> currentBalises = newArrayList();

    private Date2 maxFundCost = null;

    public VerificateurOrdreBalise(final Iterable<ObjChrono> chronos, final Iterable<Balise> balises) {
        this.chronos = newArrayList(chronos);
        this.balises = newArrayList(balises);
    }

    protected static String message(final Iterable<ObjChrono> sol) {
        String res = "sol" + '=';
        for (final ObjChrono objChrono : sol) {
            res += objChrono.getNumBalise() + ',';
        }
        return res;
    }

    public Date2 getPenality() {
        final Date2 maxCost = getCout(balises);
        if (maxFundCost != null) {
            downTime(maxCost, maxFundCost);
        }
        return maxCost;
    }

    private static Date2 getCout(final AbstractList<Balise> balises) {
        final Iterator<Balise> iter = balises.iterator();
        final Date2 res = createDate(0);
        while (iter.hasNext()) {
            upTime(res, iter.next().getPenalite());
        }
        return res;
    }

    private void next(final int idxChrono, final int idxBalise) {
        final Collection<ObjChrono> sav = new ArrayList<ObjChrono>();
        sav.addAll(current);
        final AbstractList<Balise> savBalises = newArrayList();
        savBalises.addAll(currentBalises);
        if (idxChrono < chronos.size()) {
            if (idxBalise < balises.size()) {
                final ObjChrono chrono = chronos.get(idxChrono);
                final Balise balise = balises.get(idxBalise);
                if (chrono.getNumBalise().equals(balise.getNum())) {
                    if (!currentBalises.contains(balise)) {
                        current.add(chrono);
                        currentBalises.add(balise);
                    }
                    next(idxChrono + 1, idxBalise + 1);
                    current = sav;
                    currentBalises = savBalises;
                    return;
                } else {
                    final int idxFund =
                            balises.lastIndexOf(BaliseFactory.createBalise(chrono.getNumBalise(), TYPE_ORDONNEE, ""));
                    if (idxFund != -1 && idxFund > idxBalise) {
                        /* 1 er solution : on passe à la balise suivante */
                        next(idxChrono, idxFund);
                    }
                    current = sav;
                    currentBalises = savBalises;
                    /* 2eme solution : on passe au chrono suivant */
                    next(idxChrono + 1, idxBalise);
                    current = sav;
                    currentBalises = savBalises;
                    return;
                }
            }
        }
        final Date2 cout = getCout(currentBalises);
        if (maxFundCost == null || cout.after(maxFundCost) ||
                cout.equals(maxFundCost) && current.size() > solution.size()) {
            solution.clear();
            solution.addAll(current);
            maxFundCost = cout;
        }
    }

    public void optimize(final Collection<ObjChrono> solution) {
        solution.clear();
        this.solution = solution;
        for (int i = 0; i < chronos.size(); i++) {
            current.clear();
            currentBalises.clear();
            next(i, 0);
        }
    }

    public Date2 getCout() {
        return maxFundCost;
    }

}
