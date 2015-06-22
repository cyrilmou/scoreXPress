package fr.cm.scorexpress.core.util;

import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.model.impl.StepUtils;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.Balise.*;
import static fr.cm.scorexpress.core.model.ObjResultat.*;
import static fr.cm.scorexpress.core.model.ObjUserChronos.VAR_PREFIX_BALISE;
import static fr.cm.scorexpress.core.model.StepUtil.findResultatByDossard;
import static fr.cm.scorexpress.core.model.StepUtil.getDossard;
import static fr.cm.scorexpress.core.model.i18n.Messages.i18n;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.downTime;
import static fr.cm.scorexpress.core.model.impl.DateUtils.upTime;
import static fr.cm.scorexpress.core.model.impl.StepUtils.findStepByCategorie;
import static fr.cm.scorexpress.core.util.BaliseOrderUtils.calculOrdreBalise;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class PenalityUtils {
    public static final ObjChoix TYPE_AUCUNE                      = new ObjChoix(0, i18n("ObjPenalite.AUCUN"));
    public static final ObjChoix TYPE_DUREE_ETAPE_MAXI            = new ObjChoix(1, i18n("ObjPenalite.DUREE_MAXI"));
    public static final ObjChoix TYPE_DUREE_ETAPE_MINI            = new ObjChoix(2, i18n("ObjPenalite.DUREE_MINI"));
    public static final ObjChoix TYPE_NB_BALISES_MINI             = new ObjChoix(3, i18n("ObjPenalite.BALISE_MINI"));
    public static final ObjChoix TYPE_POINT_BALISE_MINI           =
            new ObjChoix(4, i18n("ObjPenalite.POINT_BALISE_MINI"));
    public static final ObjChoix TYPE_COURSE_AU_SCORE             =
            new ObjChoix(5, i18n("ObjPenalite.COURSE_AU_SCORE"));
    public static final ObjChoix TYPE_SCORE_MINI                  = new ObjChoix(6, i18n("ObjPenalite.SCORE_MINI"));
    public static final ObjChoix TYPE_BALISE_ORDONNEE             =
            new ObjChoix(7, i18n("ObjPenalite.Balises_ordonnees"));
    public static final ObjChoix TYPE_PENALITE_SAISIE             =
            new ObjChoix(8, i18n("ObjPenalite.Penalites_saisies"));
    public static final ObjChoix TYPE_PENALITE_BALISE_OBLIGATOIRE =
            new ObjChoix(9, i18n("ObjPenalite.BALISE_OBLIGATOIRE"));
    public static final ObjChoix TYPE_ARRET_CHRONO_SAISIE         = new ObjChoix(10, i18n("Arret_chrono_saisie"));
    public static final ObjChoix TYPE_ARRET_CHRONO_MAXI           =
            new ObjChoix(11, i18n("ObjPenalite.ARRET_CHRONO_MAXI"));
    public static final ObjChoix TYPE_DUREE_FIXE                  = new ObjChoix(12, i18n("ObjPenalite.DUREE_FIXE"));
    public static final ObjChoix TYPE_BALISE_ORDONNEE_MULTIPLE    =
            new ObjChoix(13, i18n("ObjPenalite.Balises_ordonnees_multiple"));

    private PenalityUtils() {
    }

    public static void calculArretChrono(final ObjResultat resultat) {
        calculArretChronoSousEtapeIter(resultat);
    }

    public static void calculArretChronoSousEtape(final ObjResultat resultat) {
        calculArretChronoSousEtapeIter(resultat);
    }

    public static void calculArretChronoSousEtapeIter(final ObjResultat resultat) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        final Date2 dureeArretChrono = resultat.getTempsArretChrono();
        final ObjStep step = (ObjStep) resultat.getParent();
        final ObjDossard dossard = resultat.getDossard();
        /* Traite l'étape si celle-ci est active */
        for (final ObjStep activeStep : findStepByCategorie(step, dossard.getCategory())) {
            /* Traite un arrêt chrono */
            final ObjResultat resSousEtape = findResultatByDossard(dossard.getNum(), activeStep);
            if (resSousEtape != null) {
                if (activeStep.isArretChrono()) {
                    if (!step.isCumulerSousEtape()) {
                        downTime(dureeArretChrono, resSousEtape.getTemps());
                    } else {
                        downTime(resultat.getTempsArretChronoResultat(), resSousEtape.getTemps());
                    }
                }
            }
        }
        for (final ObjPenalite penality : step.getPenalites()) {
            /* Traite l'étape si celle-ci est active */
            if (!StepUtils.isGoodCategorie(step, dossard.getCategory())) {
            } else if (penality.isActivate() && (TYPE_ARRET_CHRONO_SAISIE.valeur == penality.getTypePenalite())) {
                /* Traite un arrêt chrono */
                final Date arretChrono = calculArretChronoSaisie(dossard.getNum(), penality);
                if (arretChrono.getTime() != 0) {
                    upTime(dureeArretChrono, arretChrono);
                }
            }
        }
    }

    public static void calculPenalitesSousEtape(final ObjResultat resultat) {
        calculPenaliteSousEtapeIter(resultat);
    }

    public static void calculPenaliteSousEtapeIter(final ObjResultat resultat) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        final ObjStep etape = (ObjStep) resultat.getParent();
        final ObjDossard d = resultat.getDossard();
        for (final ObjStep step : findStepByCategorie(etape, d.getCategory())) {
            /* Traite l'étape si celle-ci est active */
            /* Récupération du résultat intermédiaire */
            final ObjResultat resultatEtape = findResultatByDossard(d.getNum(), step);
            if (resultatEtape != null) {
                /*
                     * Si un résultat intermédiaire existe, seul les pénalités
                     * intermédiaire et le nombre de balises manquées est récupéré
                     */
                final Date penaliteIter = resultatEtape.getPenalite();
                upTime(resultat.getPenalite(), penaliteIter);
                if (etape.isCumulerSousEtape()) {
                    upTime(resultat.getPenaliteResultat(), resultatEtape.getPenaliteResultat());
                    upTime(resultat.getBonificationResultat(), resultatEtape.getBonificationResultat());
                    upTime(resultat.getTempsArretChronoResultat(), resultatEtape.getTempsArretChronoResultat());
                } else {
                    upTime(resultat.getPenalite(), resultatEtape.getPenaliteResultat());
                    upTime(resultat.getBonification(), resultatEtape.getBonificationResultat());
                    upTime(resultat.getTempsArretChrono(), resultatEtape.getTempsArretChronoResultat());
                }
                upTime(resultat.getTempsArretChrono(), resultatEtape.getTempsArretChrono());
                upTime(resultat.getBonification(), resultatEtape.getBonification());
                upTime(resultat.getPenaliteBalise(), resultatEtape.getPenaliteBalise());
                upTime(resultat.getPenaliteAutre(), resultatEtape.getPenaliteAutre());
                resultat.addNbPenalite(resultatEtape.getNbPenalite());
                resultat.addNbBalises(resultatEtape.getNbBalises());
                appendStepDataList(resultat, step, resultatEtape, VAR_RESULTAT_BALISESMANQUEES);
                appendStepDataList(resultat, step, resultatEtape, VAR_RESULTAT_BALISES_OK);
                appendStepDataList(resultat, step, resultatEtape, VAR_RESULTAT_BALISESBONUS);
            }
        }
    }

    private static void appendStepDataList(final AbstractGetInfo resultat, final ObjStep step,
                                           final AbstractGetInfo resultatEtape, final String attribut) {
        Object valueList = resultatEtape.getInfo(attribut);
        // Détermination des balises manquées par etapes
        if (valueList != null) {
            valueList = step.getLib() + '[' + valueList + ']';
        }
                /* Concaténation des balises manquées */
        if (valueList != null) {
            final Object valueListRes = resultat.getInfo(attribut);
            if (valueListRes != null) {
                resultat.setInfo(attribut, EMPTY + valueListRes + ',' + valueList);
            } else {
                resultat.setInfo(attribut, valueList);
            }
        }
    }

    public static void calculPenaliteBalisesManquees(final ObjResultat resultat, final ObjUserChronos userChrono,
                                                     final boolean inclureBaliseOrdonnee) {
        if (resultat == null || resultat.getParent() == null || userChrono == null) {
            return;
        }
        final ObjStep etape = (ObjStep) resultat.getParent();
        // Date2 duree = resultat.getTemps();
        final Date2 penalite = resultat.getPenalite();
        final Date2 penaliteBalise = resultat.getPenaliteBalise();
        final Iterable<ObjBalise> balises = etape.getBalises();
        for (final Balise balise : balises) {
            final ObjChrono chrono = userChrono.getChronoEnableHasPossible(balise.getNum());
            final boolean isNotValidate =
                    chrono == null || chrono.getTemps() == null || chrono.getTemps().isNull() || chrono.isCancel();
            if (isNotValidate) {
                if (TYPE_OBLIGATOIRE.equalsIgnoreCase(balise.getType()) ||
                        TYPE_ORDONNEE.equalsIgnoreCase(balise.getType()) && inclureBaliseOrdonnee) {
                    // duree.add(balise.getPenalite());
                    upTime(penalite, balise.getPenalite());
                    resultat.addNbPenalite(1);
                    upTime(penaliteBalise, balise.getPenalite());
                    final String sup;
                    if (chrono != null && chrono.isCancel()) {
                        sup = "*";
                    } else {
                        sup = EMPTY;
                    }
                    appendDataList(resultat, sup, "", VAR_RESULTAT_BALISESMANQUEES, balise.getNum());
                } else if (TYPE_PAS_OBLIGATOIRE.equalsIgnoreCase(balise.getType())) {
                    appendDataList(resultat, "-", "", VAR_RESULTAT_BALISESMANQUEES, balise.getNum());
                }
            } else {
                /* Si balise valide */
                if (TYPE_PENALITY.equalsIgnoreCase(balise.getType())) {
                    upTime(penalite, balise.getPenalite());
                    upTime(resultat.getPenaliteAutre(), balise.getPenalite());
                    appendDataList(resultat, "+", "", VAR_RESULTAT_BALISESMANQUEES, balise.getNum());
                } else {
                    // Ajout des temps de balise
                    resultat.setInfoTmp(VAR_PREFIX_BALISE + chrono.getNumBalise(), chrono.getTemps() + EMPTY);
                    if (TYPE_OBLIGATOIRE.equalsIgnoreCase(balise.getType()) ||
                            TYPE_ORDONNEE.equalsIgnoreCase(balise.getType()) ||
                            TYPE_PAS_OBLIGATOIRE.equalsIgnoreCase(balise.getType())) {
                        increaseValue(resultat, VAR_NB_BALISE, 1);
                        increaseValue(resultat, VAR_NB_POINTS_BALISE, balise.getPoints());
                        appendDataList(resultat, "", "", VAR_RESULTAT_BALISES_OK, balise.getNum());
                    } else if (TYPE_BONUS.equalsIgnoreCase(balise.getType())) {
                        downTime(resultat.getBonification(), balise.getPenalite());
                        increaseValue(resultat, VAR_NB_BALISE, 1);
                        increaseValue(resultat, VAR_NB_BALISE_BONUS, 1);
                        increaseValue(resultat, VAR_NB_POINTS_BALISE, balise.getPoints());
                        appendDataList(resultat, "", "", VAR_RESULTAT_BALISESBONUS, balise.getNum());
                    }
                }
            }
        }
    }

    private static void appendDataList(final AbstractGetInfo element, final String pre, final String post,
                                       final String attribut, final String value) {
        final Object info = element.getInfo(attribut);
        if (info != null) {
            element.setInfo(attribut, info + "," + pre + value + post);
        } else {
            element.setInfo(attribut, pre + value + post);
        }
    }

    private static void increaseValue(final ObjResultat resultat, final String attribut, final int value) {
        final Object info = resultat.getInfo(attribut);
        if (info != null) {
            resultat.setInfo(attribut, new Integer((Integer) info + value));
        } else {
            resultat.setInfo(attribut, new Integer(value));
        }
    }

    public static boolean calculBaliseOrdonnee(final ObjStep step, final ObjUserChronos userChrono, final int nbOrdre) {
        return calculOrdreBalise(step, userChrono, nbOrdre);
    }

    public static void initUserChrono(final IChronos infoSportIdentParent) {
        for (final ObjChrono objChrono : infoSportIdentParent.getChronos()) {
            objChrono.setCancel(false);
        }
    }

    public static void calculPenalitesEtape(final ObjResultat resultat, final ObjUserChronos infoSportIdentParent) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        final ObjStep etape = (ObjStep) resultat.getParent();
        final Date2 tempsParcours = resultat.getTempsParcours();
        // Date2 duree = resultat.getTemps();
        final Date2 resultatPenality = resultat.getPenalite();
        final Date2 penaliteAutre = resultat.getPenaliteAutre();
        initUserChrono(infoSportIdentParent);
        boolean calculBaliseObligatoire = true;
        boolean inclureBaliseOrdonnee = true;
        for (final ObjPenalite penality : etape.getPenalites()) {
            if (penality.isActivate()) {
                // Temps mini de l'épreuve
                final Date tempsParcoursDiff = calculTempsMiniEpreuve(tempsParcours, penality);
                upTime(tempsParcours, tempsParcoursDiff);
                // duree.add(tempsParcoursDiff);
                upTime(penaliteAutre, tempsParcoursDiff);
                // Course au score
                Date dureePenalite = calculPenaliteCourseAuScrore(tempsParcours, penality);
                // duree.add(dureePenalite);
                upTime(resultatPenality, dureePenalite);
                upTime(penaliteAutre, dureePenalite);
                // Duree mini de l'épreuve
                dureePenalite = calculPenaliteDureeEpreuve(tempsParcours, penality);
                // duree.add(dureePenalite);
                upTime(resultatPenality, dureePenalite);
                upTime(penaliteAutre, dureePenalite);
                // Duree fixe
                dureePenalite = calculPenaliteDureeFixeEpreuve(tempsParcours, penality);
                // duree.add(dureePenalite);
                upTime(resultatPenality, dureePenalite);
                upTime(penaliteAutre, dureePenalite);
                // Penalite saisies
                dureePenalite = calculPenaliteSaisie(resultat.getDossard().getNum(), penality);
                // duree.add(dureePenalite);
                upTime(resultatPenality, dureePenalite);
                upTime(penaliteAutre, dureePenalite);
                // Calcul des balises obligatoires
                if (penality.getTypePenalite() == TYPE_PENALITE_BALISE_OBLIGATOIRE.valeur) {
                    calculBaliseObligatoire = true;
                }
                // Ordre des balises obligatoire
                else if (penality.getTypePenalite() == TYPE_BALISE_ORDONNEE.valeur) {
                    final int nbOrdre = penality.getNbBalisesMini();
                    if (calculBaliseOrdonnee(etape, infoSportIdentParent, nbOrdre)) {
                        dureePenalite = penality.getPenalite();
                        // duree.add(dureePenalite);
                        upTime(resultatPenality, dureePenalite);
                        upTime(penaliteAutre, dureePenalite);
                    }
                    calculBaliseObligatoire = true;
                    inclureBaliseOrdonnee = true;
                }
            } else {
                // Calcul des balises obligatoires désactivés
                if (penality.getTypePenalite() == TYPE_PENALITE_BALISE_OBLIGATOIRE.valeur) {
                    calculBaliseObligatoire = false;
                } else // Ordre des balises obligatoire
                    if (penality.getTypePenalite() == TYPE_BALISE_ORDONNEE.valeur) {
                        inclureBaliseOrdonnee = false;
                    }
            }
        }
        if (calculBaliseObligatoire) {
            calculPenaliteBalisesManquees(resultat, infoSportIdentParent, inclureBaliseOrdonnee);
        }
        for (final ObjPenalite penality : etape.getPenalites()) {
            if (penality.isActivate()) {
                // Penalite par points de balise non obtenu
                final Date dureePenalite = calculPenaliteScoreMini(resultat.getNbPointsBalise(), penality);
                // duree.add(dureePenalite);
                upTime(resultatPenality, dureePenalite);
                upTime(penaliteAutre, dureePenalite);

                // Penalite par nombre de balise non atteint
                // Bonus par nombre de balises pointees au dela
                calculPenaliteNbBaliseMini(resultat, penality);
            }
        }
    }

    public static void calculPenalityBonif(final ObjResultat resultat) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        final ObjStep etape = (ObjStep) resultat.getParent();
        // Penalités saisies
        if (etape.isPenalitySaisie() || etape.isEpreuve()) {
            final ObjDossard dossard = getDossard(resultat.getDossard().getNum(), etape);
            if (dossard != null) {
                upTime(resultat.getPenalite(), dossard.getPenality());
                downTime(resultat.getBonification(), dossard.getBonification());
            }
        }
    }

    public static void addArretChronoSousEtape(final ObjResultat resultat, final AbstractSteps currentStep,
                                               final ObjDossard dossard) {
        final Collection<ObjStep> activeSteps = newArrayList();
        for (final ObjStep subStep : currentStep.getSteps()) {
            if (subStep.isActif()) {
                activeSteps.add(subStep);
            }
        }
        // Cumul des temps des sous-étapes
        // S'il n'y a pas de sous-étape le concurrent est disqualifié
        if (activeSteps.isEmpty()) {
            resultat.setDeclasse(true);
        }
        for (final ObjStep activeStep : activeSteps) {
            final ObjResultat res = findResultatByDossard(dossard.getNum(), activeStep);
            // Lorsque que la personne n'est pas arrivée de la
            // sous-étape celle-ci est disqualifié automatiquement
            if (!res.isNotArrived()) {
                if (activeStep.isArretChrono()) {
                    downTime(resultat.getTemps(), res.getTemps());
                }
            }
        }
    }

    /**
     * Retourne la pénalité correspondant à une épreuve de course au score,
     * durée limite avec pénalité proportionnel au temps de dépassement par
     * unité de temps consommée
     *
     * @param dureeParcours Date Durée du parcours
     * @param penality      d
     * @return Date Durée de la pénalité calculée
     */
    public static Date calculPenaliteCourseAuScrore(final Date dureeParcours, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_COURSE_AU_SCORE.getValeur()) {
            if (penality.getPenalite() != null && penality.getDureeMaxi() != null) {
                if (dureeParcours.getTime() > penality.getDureeMaxi().getTime()) {
                    if (penality.getEchellePenalite() == null || penality.getEchellePenalite().getTime() == 0) {
                        return penality.getPenalite();
                    } else {
                        final long delta = dureeParcours.getTime() - penality.getDureeMaxi().getTime();
                        final int ratio = (int) (delta / penality.getEchellePenalite().getTime());
                        final int reste = (int) (delta % penality.getEchellePenalite().getTime());
                        Date res = createDate(ratio * penality.getPenalite().getTime());
                        if (reste != 0) {
                            res = createDate((ratio + 1) * penality.getPenalite().getTime());
                        }
                        return res;
                    }
                }
            }
        }
        return new Date(0);
    }

    /**
     * Retourne les pénalites d'une épreuve à durée limite
     *
     * @param dureeParcours Date durée du parcours
     * @param penality
     * @return Date Durée de la pénalité
     */
    public static Date calculPenaliteDureeEpreuve(final Date dureeParcours, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_DUREE_ETAPE_MAXI.getValeur()) {
            if (penality.getPenalite() != null && penality.getDureeMaxi() != null) {
                if (dureeParcours.getTime() > penality.getDureeMaxi().getTime()) {
                    // Alors une pénalité a été détectée
                    return penality.getPenalite();
                }
            }
            return new Date(0);
        }
        return new Date(0);
    }

    public static Date calculPenaliteDureeFixeEpreuve(final Date dureeParcours, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_DUREE_FIXE.getValeur()) {
            if (penality.getDureeMaxi() != null) {
                if (dureeParcours.getTime() <= penality.getDureeMaxi().getTime()) {
                    return new Date(-dureeParcours.getTime() + penality.getDureeMaxi().getTime());
                }
            }
            return new Date(0);
        }
        return new Date(0);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static Date2 calculPenaliteSaisie(final String dossard, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_PENALITE_SAISIE.getValeur()) {
            if (penality.getPenalite() != null) {
                for (final ObjSaisiePenalite saisy : penality.getSaisies()) {
                    if (dossard != null && dossard.equals(saisy.getDossard())) {
                        return createDate(saisy.getValeur() * penality.getPenalite().getTime());
                    }
                }
            }
        }
        return createDate(0);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static Date2 calculArretChronoSaisie(final String dossard, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_ARRET_CHRONO_SAISIE.getValeur()) {
            if (penality.getPenalite() != null) {
                for (final ObjSaisiePenalite saisy : penality.getSaisies()) {
                    if (dossard != null && dossard.equals(saisy.getDossard())) {
                        final Date2 penalite = createDate(true);
                        for (int i = 0; i < saisy.getValeur(); i++) {
                            downTime(penalite, penality.getPenalite());
                        }
                        return penalite;
                    }
                }
            }
        }
        return createDate(0);
    }

    public static Collection<ObjChoix> getListPenaliteType() {
        final Collection<ObjChoix> listTypePenalite = newArrayList();
        listTypePenalite.add(TYPE_AUCUNE);
        listTypePenalite.add(TYPE_COURSE_AU_SCORE);
        listTypePenalite.add(TYPE_PENALITE_BALISE_OBLIGATOIRE);
        listTypePenalite.add(TYPE_BALISE_ORDONNEE);
        listTypePenalite.add(TYPE_DUREE_ETAPE_MAXI);
        listTypePenalite.add(TYPE_DUREE_ETAPE_MINI);
        listTypePenalite.add(TYPE_NB_BALISES_MINI);
        listTypePenalite.add(TYPE_PENALITE_SAISIE);
        listTypePenalite.add(TYPE_POINT_BALISE_MINI);
        listTypePenalite.add(TYPE_SCORE_MINI);
        listTypePenalite.add(TYPE_ARRET_CHRONO_SAISIE);
        listTypePenalite.add(TYPE_ARRET_CHRONO_MAXI);
        listTypePenalite.add(TYPE_DUREE_FIXE);
        listTypePenalite.add(TYPE_BALISE_ORDONNEE_MULTIPLE);
        return listTypePenalite;
    }

    public static String[] getListPenaliteTypeStr() {
        final Collection<ObjChoix> col = getListPenaliteType();
        final String[] res = new String[col.size()];
        int i = 0;
        for (Iterator<ObjChoix> iter = col.iterator(); iter.hasNext(); i++) {
            final String element = iter.next().getLib();
            res[i] = element;
        }
        return res;
    }

    public static String getTypePenaliteStr(final ObjPenalite penality) {
        final Collection<ObjChoix> listTypePenalite = newArrayList();
        listTypePenalite.add(TYPE_AUCUNE);
        listTypePenalite.add(TYPE_COURSE_AU_SCORE);
        listTypePenalite.add(TYPE_PENALITE_BALISE_OBLIGATOIRE);
        listTypePenalite.add(TYPE_BALISE_ORDONNEE);
        listTypePenalite.add(TYPE_DUREE_ETAPE_MAXI);
        listTypePenalite.add(TYPE_DUREE_ETAPE_MINI);
        listTypePenalite.add(TYPE_NB_BALISES_MINI);
        listTypePenalite.add(TYPE_PENALITE_SAISIE);
        listTypePenalite.add(TYPE_POINT_BALISE_MINI);
        listTypePenalite.add(TYPE_SCORE_MINI);
        listTypePenalite.add(TYPE_ARRET_CHRONO_SAISIE);
        listTypePenalite.add(TYPE_ARRET_CHRONO_MAXI);
        listTypePenalite.add(TYPE_DUREE_FIXE);
        listTypePenalite.add(TYPE_BALISE_ORDONNEE_MULTIPLE);
        for (final ObjChoix choix : listTypePenalite) {
            if (choix.getValeur() == penality.getTypePenalite()) {
                return choix.getLib();
            }
        }
        return EMPTY;
    }

    public static boolean isPenalityType(final ObjPenalite penality, final ObjChoix penalityType) {
        return penality.getTypePenalite() == penalityType.getValeur();
    }

    /**
     * Retourne la durée de parcours en tenant compte d'une durée minimum de
     * l'épreuve
     *
     * @param dureeParcours Date
     * @param penality
     * @return Date
     */
    public static Date calculTempsMiniEpreuve(final Date dureeParcours, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_DUREE_ETAPE_MINI.getValeur()) {
            if (dureeParcours == null || penality.getDureeMaxi().isNull()) {
                return null;
            }
            final long diff = penality.getDureeMini().getTime() - dureeParcours.getTime();
            if (diff > 0) {
                return new Date(diff);
            }
        }
        return new Date(0);
    }

    /**
     * retourne les panélites correspondant à un nombre de point balise minimum
     *
     * @param nbPoints int nombre de point balise
     * @param penality d
     * @return Date Durée de la pénalité
     */
    public static Date calculPenaliteScoreMini(final int nbPoints, final ObjPenalite penality) {
        if (penality.getTypePenalite() == TYPE_SCORE_MINI.getValeur()) {
            final int diffPoints = penality.getNbPointsBaliseMini() - nbPoints;
            if (diffPoints > 0) {
                return new Date(penality.getPenalite().getTime() * diffPoints);
            }
        }
        return new Date(0);
    }

    /**
     * Determine la penalite ou les bonifications a ajouter au calcul du resultat en fonction du nombre de balise prise
     *
     * @param resultat Resultat
     * @param penality Penalité
     * @return Date Durée de la pénalité calculée
     */
    public static void calculPenaliteNbBaliseMini(final ObjResultat resultat, final ObjPenalite penality) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        if (penality.getTypePenalite() != TYPE_NB_BALISES_MINI.valeur) {
            // N'est pas le bon type recherche
            return;
        }
        final int diffBalises = penality.getNbBalisesMini() - resultat.getNbBalises();
        if (diffBalises > 0) {
            // Nombre de balise a prendre moins les balises prises et en retirant les balises obligatoires non prise
            // Les balises obligatoires sont déjà comptabilisé
            final int nbPenalite = penality.getNbBalisesMini() - resultat.getNbBalises() - resultat.getNbPenalite();
            for (int i = 0; i < nbPenalite; i++) {
                upTime(resultat.getPenalite(), penality.getPenalite());
                upTime(resultat.getPenaliteAutre(), penality.getPenalite());
            }
            if (nbPenalite > 0) {
                appendDataList(resultat, "", "", VAR_RESULTAT_BALISESMANQUEES,
                               nbPenalite + " x " + createDate(penality.getPenalite()).showSign());
            }
        } else if (diffBalises < 0) {
            final int nbBonus = resultat.getNbBalises() - penality.getNbBalisesMini() - resultat.getNbBalisesBonus();
            for (int i = 0; i < nbBonus; i++) {
                downTime(resultat.getBonification(), penality.getEchellePenalite());
            }
            if (nbBonus > 0) {
                appendDataList(resultat, "", "", VAR_RESULTAT_BALISESBONUS,
                               nbBonus + " x " + createDate(penality.getEchellePenalite()).showSign());
            }
        }
    }

    public static String getPenalityTypeDescription(final ObjPenalite penality, final String penalityTimeLabel,
                                                    final String scaleLabel, final String penalityMaxiLabel) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTypePenaliteStr(penality));

        if (isPenalityType(penality, TYPE_COURSE_AU_SCORE)) {
            builder.append(
                    String.format(" %s=%s %s=%s %s=%s", penalityMaxiLabel, penality.getDureeMaxiStr(), scaleLabel,
                                  penality.getEchellePenaliteStr(), penalityTimeLabel, penality.getPenaliteStr())
                          );
        } else if (isPenalityType(penality, TYPE_DUREE_ETAPE_MAXI)) {
            builder.append(String.format(" %s=%s", penalityMaxiLabel, penality.getDureeMaxiStr()));
        }
        return builder.toString();
    }
}
