package fr.cm.scorexpress.core.model;

import static com.google.common.collect.Lists.newArrayList;
import fr.cm.scorexpress.core.model.impl.ObjStep;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class StepUtil {

    public static Collection<ObjStep> getStepWithPenaliteSaisie(final ObjStep step) {
        return requestStepWithPenaliteSaisie(step);
    }

    private static Collection<ObjStep> requestStepWithPenaliteSaisie(final ObjStep step) {
        final AbstractList<ObjStep> res = newArrayList();
        if (step.isPenalitySaisie() || step.isEpreuve()) {
            res.add(step);
        }
        for (final ObjStep objStep : step.getSteps()) {
            final Collection<ObjStep> steps = requestStepWithPenaliteSaisie(objStep);
            res.addAll(steps);
        }
        return res;
    }

    public static Collection<ObjDossard> gatherAllDossards(final ObjStep step) {
        final ObjStep epreuveP = step.getEpreuve();
        if (epreuveP != null) {
            return epreuveP.getDossards();
        }
        return new ArrayList<ObjDossard>();
    }

    public static ObjDossard findDossard(final String numDossard, final ObjStep step) {
        final Collection<ObjDossard> doss = gatherAllDossards(step);
        if (doss == null) {
            return null;
        }
        final Iterator<ObjDossard> iter = doss.iterator();
        ObjDossard dossard;
        while (iter.hasNext()) {
            dossard = iter.next();
            final String num = dossard.getNum();
            if (num != null && numDossard != null && numDossard.equals(num)) {
                return dossard;
            }
        }
        return null;
    }

    public static ObjResultat findResultatByDossard(final String dossard, final AbstractResultats step) {
        final Collection<ObjResultat> resultats = step.getResultats();
        synchronized (resultats) {
            for (final ObjResultat resultat : resultats) {
                try {
                    if (dossard != null && resultat.getDossard().getNum().equals(dossard)) {
                        return resultat;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
    }

    public static Collection<ObjStep> gatherEpreuveFromStep(final ObjStep step) {
        final Collection<ObjStep> result = newArrayList();
        if (step.isEpreuve())
            result.add(step);
        for (final ObjStep subStep : step.getSteps()) {
            result.addAll(gatherEpreuveFromStep(subStep));
        }
        return result;
    }

    public static Collection<ObjStep> gatherAllEpreuveFromStep(final ObjStep step) {
        final Collection<ObjStep> result = newArrayList();
        final ObjStep superParent = getSuperParent(step);
        result.addAll(gatherEpreuveFromStep(superParent));
        return result;
    }

    private static ObjStep getSuperParent(final ObjStep step) {
        if (step.getParent() != null && step.getParent() instanceof ObjStep) {
            return getSuperParent((ObjStep) step.getParent());
        }
        if (step.isEpreuve())
            return step;
        else
            return null;
    }

    public static ObjDossard getDossard(final String numDossard, final IDossards step) {
        final Iterator<ObjDossard> iter = step.getDossards().iterator();
        while (iter.hasNext()) {
            final ObjDossard dossard = iter.next();
            if (numDossard.equals(dossard.getNum())) {
                return dossard;
            }
        }
        return null;
    }
}
