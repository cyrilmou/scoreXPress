package fr.cm.scorexpress.core.model.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fr.cm.scorexpress.core.model.*;
import org.apache.commons.lang.StringUtils;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.StepUtil.findResultatByDossard;
import static fr.cm.scorexpress.core.model.StepUtil.gatherAllDossards;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.upTime;
import static fr.cm.scorexpress.core.util.CalculResultatsUtils.*;
import static fr.cm.scorexpress.core.util.PenalityUtils.*;
import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class StepUtils {

    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private StepUtils() {
    }

    static String format(final Date d) {
        try {
            return sdf.format(d);
        } catch (Exception ex) {
            return null;
        }
    }

    static Date parse(final String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ObjUserChronos getUserChronos(final IUserChronos step, final String num) {
        for (final ObjUserChronos objUserChronos : step.getUserChronos()) {
            if (StringUtils.equals(objUserChronos.getDossard(), num)) {
                return objUserChronos;
            }
        }
        return null;
    }

    public static void setArretChronoStr(final ObjStep step, final String arretChrono) {
        if (arretChrono == null) {
            return;
        }
        step.setArretChrono(parseBoolean(arretChrono));
    }

    public static String isArretChronoStr(final Step step) {
        if (!step.isArretChrono()) {
            return null;
        }
        return step.isArretChrono() + EMPTY;
    }

    public static Iterable<ObjStep> getActiveSubStep(final AbstractSteps step) {
        return filter(step.getSteps(), new Predicate<Step>() {
            @Override
            public boolean apply(final Step step) {
                return step.isActif();
            }
        });
    }

    public static boolean updateStepsResultat(final Iterable<ObjStep> steps, final boolean calculForced,
                                              final Function<ObjStep, Boolean> action) {
        boolean hasChanged = false;
        for (final ObjStep subStep : steps) {
            System.out.println("updateStepsResultat '" + subStep.getLib() + "' force=" + calculForced);
            if (calculForced || subStep.isCalculDataModify()) {
                //hasChanged = updateStepsResultat(subStep.getSteps(), calculForced, action) || hasChanged;
                hasChanged = action.apply(subStep) || hasChanged;
            }
        }
        return hasChanged;
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public static boolean updateResultat(final ObjStep step, final boolean forceCalcul, final boolean byCategory) {
        synchronized (step) {
            // Si les données d'une epreuve change tout doit être recalculé
            final boolean forceUpdate = forceCalcul || forceUpdate(step);
            /* Actualisation des étapes composant celle-ci */
            final Iterable<ObjStep> etapesActive = newArrayList(getActiveSubStep(step));
            final boolean noActiveEtape = Iterables.isEmpty(etapesActive);
            final boolean calculSubStepChanged =
                    updateStepsResultat(etapesActive, forceCalcul, toUpdateResultat(forceCalcul));

            if (!(calculSubStepChanged || forceUpdate || step.isCalculDataModify())) {
                return forceUpdate;
            }
            /*
            * Récupération du numéro de balise correspondant au départ et à
            * l'arrivée afin de calculer la durée de l'étape peut être null
            * si aucune balise n'a été définie
            */
            final String numBaliseDepart = step.getBaliseDepart();
            final String numBaliseArrivee = step.getBaliseArrivee();
            /*
            * Actualise des résultats de tous les dossards ayant participés
            * à l'étape a partir des données SportIdent
            */
            final Iterable<ObjDossard> dossards = getDefaultDossards(step);

            final Collection<ObjResultat> resultats = newArrayList();
            for (final ObjDossard dossard : dossards) {
                /* Traitement dossard par dossard */
                /* Création du résultat du dossard pour l'étape en cours */
                ObjResultat resultat = new ObjResultat();
                resultat.setParent(step);
                resultat.setDossard(dossard);
                /* Récupération des chronos du concurrent */
                final ObjUserChronos userChronos = getUserChronos(step, dossard.getNum());
                /*  Si aucune base d'information chronométre n'est définie au niveau
          du parent, alors cumuler les sous-étapes */
                if (step.isCumulerSousEtape()) {
                    /*
                    * Cumul des temps des sous-étapes
                    * resultat.setDisqualifier(true);
                    * resultat.setNotArrived(true);
                    */
                    /* S'il n'y a pas de sous-étape le concurrent est disqualifié */
                    if (noActiveEtape) {
                        resultat.setDeclasse(true);
                    }
                    /* Cumule le temps de chaque sous-étapes */
                    for (final ObjStep subStep : etapesActive) {
                        if( subStep.getUserChronos().isEmpty())
                            continue;

                        /* Résultat de chaque concurrent */
                        final ObjResultat resIter = findResultatByDossard(dossard.getNum(), subStep);
                        /* Ajout des résultats intermédiaires */
                        if (subStep.isClassementInter()) {
                            resultat.addResultatInter(resIter);
                        }
                        // Ne pas cumuler les etapes de la mauvaise catégorie
                        if (isGoodCategorie(subStep, dossard.getCategory())) {
                            // Lorsque que la personne n'est pas arrivée de la
                            // sous-étape celle-ci est disqualifié automatiquement
                            if (resIter == null || resIter.isNotArrived()) {
                                final boolean resultatsEmpty = subStep.getResultats().isEmpty();
                                if (!resultatsEmpty) {
                                    resultat.setDeclasse(true);
                                    resultat.setNotArrived(true);
                                }
                            } else {
                                if (!subStep.isArretChrono()) {
                                    upTime(resultat.getTemps(), resIter.getTemps());
                                    upTime(resultat.getTempsParcours(), resIter.getTempsParcours());
                                }
                            }
                        }
                    }
                    calculArretChronoSousEtapeIter(resultat);
                    calculPenaliteSousEtapeIter(resultat);
                    calculStatusSousEtape(resultat);
                } else {

                    final boolean error = initTempsParcours(resultat, userChronos, numBaliseDepart, numBaliseArrivee);
                    if (error) {
                        /* Détermination des balises manquées */
                        calculPenaliteBalisesManquees(resultat, userChronos, true);
                    } else {
                        /* Ajout des pénalités de l'étape */
                        calculPenalitesEtape(resultat, userChronos);
                    }

                    /* Ajout des pénalités des sous-étapes */
                    calculPenaliteSousEtapeIter(resultat);
                    /* Retranchement des arrêts chronos des sous étapes */
                    calculArretChronoSousEtapeIter(resultat);
                    /* Détermine le status classé ou abandon de l'étape */
                    calculStatusSousEtape(resultat);

                    for (final ObjStep subStep : etapesActive) {
                        /* Ajout des résultats intermédiaires */
                        if (subStep.isClassementInter()) {
                            final ObjResultat res = findResultatByDossard(dossard.getNum(), subStep);
                            resultat.addResultatInter(res);
                        }
                    }
                }
                calculPenalityBonif(resultat);
                calculStatusResultat(resultat);
                calculTemps(resultat);

                /* Remplace les résultats par ceux saisies */
                if (step.isEpreuve() || step.isPenalitySaisie()) {
                    if (!dossard.getTemps().isNull() && !dossard.getTemps().equals(createDate(0))) {
                        resultat = new ObjResultat();
                        resultat.setParent(step);
                        resultat.setDossard(dossard);
                        upTime(resultat.getTemps(), dossard.getTemps());
                        calculStatusResultat(resultat);
                        calculTemps(resultat);
                    }
                }
                // Ajoute le résultat du dossard à l'étape, uniquement si la
                // durée est calculable et appartenant à la catégorie fitré
                resultats.add(resultat);
            }
            step.setResultat(resultats);
            updateClassement(resultats, byCategory);
            return forceUpdate;
        }
    }

    private static Iterable<ObjDossard> getDefaultDossards(final ObjStep step) {
        final Iterable<ObjDossard> dossards;
        final Collection<ObjDossard> heritedDossards = gatherAllDossards(step);
        if (heritedDossards.isEmpty()) {
            dossards = step.getDossards();
        } else {
            dossards = heritedDossards;
        }
        return dossards;
    }

    private static boolean forceUpdate(final ObjStep step) {
        return step.isCalculDataModify() && step.isEpreuve();
    }

    private static Function<ObjStep, Boolean> toUpdateResultat(final boolean forceCalcul) {
        return new Function<ObjStep, Boolean>() {
            @Override
            public Boolean apply(final ObjStep step) {
                return updateResultat(step, forceCalcul, false);
            }
        };
    }

    public static void updateResultat(final ObjStep step) {
        updateResultat(step, true, false);
    }

    public static Comparator<ObjStep> getComparatorNumero() {
        return new Comparator<ObjStep>() {
            @Override
            public int compare(final ObjStep step1, final ObjStep step2) {
                return step1.getOrdre().compareTo(step2.getOrdre());
            }
        };
    }

    public static String createPath(final ObjStep etape) {
        String res = etape.getLib();
        if (etape.getParent() != null && etape.getParent() instanceof ObjStep) {
            final ObjStep etapeParent = (ObjStep) etape.getParent();
            final String tmp = createPath(etapeParent);
            res = tmp + '>' + res;
        }
        if (etape.getParent() != null && etape.getParent() instanceof ObjManifestation) {
            final ObjManifestation manif = (ObjManifestation) etape.getParent();
            res = manif.getNom() + '>' + res;
        }
        return res;
    }

    static ObjDossard createDossard(final String numDossard, final ObjStep step) {
        final ObjDossard dossard = new ObjDossard(numDossard);
        step.addDossardToStep(dossard);
        return dossard;
    }

    public static Collection<ObjStep> findStepByCategorie(final AbstractSteps step, final String cat) {
        final Collection<ObjStep> res = newArrayList();
        for (final ObjStep sousEtape : step.getSteps()) {
            /* Traite l'étape si celle-ci est active */
            if (sousEtape.isActif() && isGoodCategorie(sousEtape, cat)) {
                res.add(sousEtape);
            }
        }
        return res;
    }

    public static boolean isGoodCategorie(final ObjStep step, final String category) {
        final Collection<ObjCategorie> categories = step.getFiltreCategory();
        if (categories == null || categories.isEmpty()) {
            return true;
        }
        for (final ObjCategorie categorie : categories) {
            if (StringUtils.equals(categorie.getNom(), category)) {
                return true;
            }
        }
        return false;
    }

    public static String getConfiguredBalises(final AbstractBalises step, final String baliseType) {
        final StringBuilder builder = new StringBuilder();
        final Collection<String> balises = new ArrayList<String>();
        for (final Balise balise : step.getBalises()) {
            if (BaliseUtils.isTypeBalise(balise, baliseType)) {
                balises.add(balise.getNum());
            }
        }

        final Iterator<String> iterator = balises.iterator();
        while (iterator.hasNext()) {
            final String baliseId = iterator.next();
            builder.append(baliseId);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    public static String getConfiguredBalises2(final AbstractBalises step, final String baliseType) {
        final StringBuilder builder = new StringBuilder();
        final Collection<Balise> balises = new ArrayList<Balise>();
        for (final Balise balise : step.getBalises()) {
            if (BaliseUtils.isTypeBalise(balise, baliseType)) {
                balises.add(balise);
            }
        }

        final Iterator<Balise> iterator = balises.iterator();
        while (iterator.hasNext()) {
            final Balise balise = iterator.next();
            builder.append("\n  - ").append(balise.getNum()).append('(').append(balise.getPenalite()).append(')');
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}
