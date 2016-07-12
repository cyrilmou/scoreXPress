package fr.cm.scorexpress.core.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.ObjStep;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.ObjResultat.VAR_RESULTAT_ECART;
import static fr.cm.scorexpress.core.model.ObjResultat.VAR_RESULTAT_PLACE;
import static fr.cm.scorexpress.core.model.StepUtil.findResultatByDossard;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.equalsDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.upTime;
import static fr.cm.scorexpress.utils.ResultatComparator.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class CalculResultatsUtils {
    private static final String TOUS = "Tous";

    private CalculResultatsUtils() {
    }

    public static void getResultatOfEtape() {
    }

    public static boolean initTempsParcours(final ObjResultat result, final ObjUserChronos userChronos, final String numBaliseDepart, final String numBaliseArrivee) {
        final ObjStep step = (ObjStep) result.getParent();
        if (userChronos != null) {
            boolean errorDepart = false;
            /* Récupération de l'heure de départ et d'arrivée du dossard */
            final ObjChrono startChrono;
            if (numBaliseDepart != null) {
                startChrono = userChronos.getChrono(numBaliseDepart);
                if (startChrono == null || startChrono.isNull()) {
                    // Si le concurrent n'est pas au départ de l'épreuve
                    // il est disqualifié
                    result.setDeclasse(true);
                    errorDepart = true;
                }
            } else {
                startChrono = userChronos.getChronoDepart();
                if (startChrono == null || startChrono.isNull()) {
                    // Si le concurrent n'est pas au départ de l'épreuve
                    // il est disqualifié
                    result.setDeclasse(true);
                    errorDepart = true;
                }
            }
            boolean error = false;
            if (errorDepart) {
                error = true;
            }
            final ObjChrono endChrono;
            boolean errorEnd = false;
            if (numBaliseArrivee != null) {
                if (step.isArretChrono()) {
                    endChrono = userChronos.getChronoLast(numBaliseArrivee);
                } else {
                    endChrono = userChronos.getChrono(numBaliseArrivee);
                }
                if (endChrono == null || endChrono.isNull()) {
                    // Si le concurrent n'est pas arrivée
                    result.setNotArrived(true);
                    errorEnd = true;
                }
            } else {
                endChrono = userChronos.getChronoArrivee();
                if (endChrono == null || endChrono.isNull()) {
                    // Si le concurrent n'est pas arrivée
                    errorEnd = true;
                }
            }
            if (errorEnd) {
                error = true;
                if(!step.isEpreuve()){
                    result.setDeclasse(true);
                }
            }
            if (errorDepart && !errorEnd) {
                result.setError(true);
                result.setDepartError();
            }
            if (errorDepart || errorEnd) {
                result.setNotArrived(true);
            }
            result.getTemps().setTime(0);
            // Calcul de la durée de l'étape
            if (!error) {
                result.getTemps().setTime(endChrono.getTemps().getTime() - startChrono.getTemps().getTime());
                boolean errorDureeNegative = false;
                if (startChrono.getTemps().getTime() > endChrono.getTemps().getTime()) {
                    result.setError(true);
                    errorDureeNegative = true;
                    result.addError("Duree negative");
                    result.getTemps().setAffichage(true, false, true);
                    result.setDeclasse(true);
                }
                /* Calcul du temps de parcours de l'étape sauvegardée */
                result.getTempsParcours().setTime(result.getTemps().getTime());
                /* Détermination de l'affichage */
                if (errorDureeNegative) {
                    result.getTempsParcours().setAffichage(true, false, true);
                }
                updateDepartArriveeStep(result, errorDepart, startChrono, endChrono, errorEnd);
                return false;
            }
            updateDepartArriveeStep(result, errorDepart, startChrono, endChrono, errorEnd);
        } else {
            result.setNotArrived(true);
            result.setDeclasse(true);
        }
        return true;
    }

    private static void updateDepartArriveeStep(final ObjResultat result, final boolean errorDepart, final ObjChrono startChrono, final ObjChrono endChrono, final boolean errorEnd) {
        if (errorDepart) {
            result.setDepartTime(createDate(true));
        } else {
            result.setDepartTime(createDate(startChrono.getTemps()));
        }
        if (errorEnd) {
            result.setArriveeTime(createDate(true));
        } else {
            result.setArriveeTime(createDate(endChrono.getTemps()));
        }
    }

    public static void calculStatusSousEtape(final ObjResultat resultat) {
        if (resultat == null || resultat.getParent() == null) {
            return;
        }
        final AbstractSteps steps   = (AbstractSteps) resultat.getParent();
        final ObjDossard    dossard = resultat.getDossard();
        for (final ObjStep subStep : steps.getSteps()) {
            /* Traite l'étape si celle-ci est active */
            if (subStep.isActif()) {
                /* Récupération du résultat intermédiaire */
                final ObjResultat resultatEtape = findResultatByDossard(dossard.getNum(), subStep);
                if (resultatEtape != null) {
                    /* Ajout des erreurs des sous-étapes */
                    resultat.addErrors(resultatEtape.getErrors());
                    if (resultatEtape.isError()) {
                        resultat.setError(true);
                    }
                    // Déclassement du resultat si le résultat de la sous-étape est déclassé
                    if (resultatEtape.isDeclasse()) {
                        //resultat.addError("{Declasse dans une sous-etape}");
                        resultat.setError(true);
                    }
                }
            }
        }
    }

    public static void calculTemps(final ObjResultat resultat) {
        final Date2 arretChrono = resultat.getTempsArretChrono();
        upTime(resultat.getTemps(), arretChrono);
        upTime(resultat.getTempsArretChronoResultat(), arretChrono);
        arretChrono.setNull();

        final Date2 chronoMini = resultat.getTempsChronoMini();
        upTime(resultat.getTempsChronoMiniResultat(), chronoMini);
        chronoMini.setNull();

        final Date2 penality = resultat.getPenalite();
        upTime(resultat.getTemps(), penality);
        upTime(resultat.getPenaliteResultat(), penality);
        penality.setNull();

        final Date2 bonification = resultat.getBonification();
        upTime(resultat.getTemps(), bonification);
        upTime(resultat.getBonificationResultat(), bonification);
        bonification.setNull();
    }

    public static Date2 getDepart() {
        return null;
    }

    public static void calculStatusResultat(final ObjResultat resultat) {
        final ObjDossard d = resultat.getDossard();
        if (resultat.isNotArrived() && d.isAbandon()) {
            resultat.setAbandon(true);
        }
        if (d.isDisqualifie()) {
            resultat.setHorsClassement(true);
        }
        if (d.isAbandon()) {
            resultat.setAbandon(true);
        }
    }

    public static Predicate<ObjResultat> withCategory(final String category) {
        final Pattern pattern = Pattern.compile(category);
        return new Predicate<ObjResultat>() {
            @Override
            public boolean apply(final ObjResultat objResultat) {
                if (category == null || category.equals(TOUS) || category.equals(EMPTY)) {
                    return true;
                }
                final CharSequence resultCategory = objResultat.getDossard().getCategory();
                final Matcher      matcher        = pattern.matcher(resultCategory);
                return matcher.matches();
            }
        };
    }

    private static Function<ObjResultat, ObjResultat> toNumeroteResult(final boolean byCategory) {
        return new Function<ObjResultat, ObjResultat>() {
            private final Map<String, Integer> categoryNr = new HashMap<String, Integer>();
            private ObjResultat first = null;
            private ObjResultat last = null;
            private int nr = 1;

            private int incrNr(final ObjResultat resultat) {
                final String cat    = resultat.getDossard().getCategory();
                int          result = 1;
                if (categoryNr.containsKey(cat)) {
                    final Integer value = categoryNr.get(cat);
                    result = value.intValue() + 1;
                }
                categoryNr.put(cat, result);
                return result;
            }

            @Override
            public ObjResultat apply(final ObjResultat resultat) {
                if (resultat == null) {
                    return null;
                }
                if (last != null && equalsDate(last.getTemps(), resultat.getTemps())) {
                    resultat.setInfo(VAR_RESULTAT_PLACE, noNumberForAbandonAndDisqualify(resultat, last.getInfoStr(VAR_RESULTAT_PLACE)));
                } else {
                    if (byCategory) {
                        resultat.setInfo(VAR_RESULTAT_PLACE, noNumberForAbandonAndDisqualify(resultat, incrNr(resultat) + EMPTY));
                    } else {
                        resultat.setInfo(VAR_RESULTAT_PLACE, noNumberForAbandonAndDisqualify(resultat, nr + EMPTY));
                    }
                }
                if (first == null) {
                    first = resultat;
                    resultat.setInfo(VAR_RESULTAT_ECART, EMPTY);
                } else {
                    if (last != null && resultat.getTemps().after(last.getTemps()) && !resultat.isAbandon()) {
                        final Date2 ecart = createDate(resultat.getTemps().getTime() - first.getTemps().getTime());
                        ecart.setAffichage(true, false, true);
                        resultat.setInfo(VAR_RESULTAT_ECART, ecart.toString());
                    } else {
                        resultat.setInfo(VAR_RESULTAT_ECART, EMPTY);
                    }
                }
                if (!resultat.isDeclasse() && !resultat.isHorsClassement() && !resultat.isAbandon()) {
                    nr++;
                }
                last = resultat;
                return resultat;
            }
        };
    }

    private static String noNumberForAbandonAndDisqualify(final ObjResultat resultat, final String info) {
        if (resultat.isAbandon() || resultat.isDeclasse() || resultat.isHorsClassement()) {
            return "---";
        } else {
            return info;
        }
    }

    public static Collection<ObjResultat> filterResults(final Iterable<ObjResultat> results, final Predicate<ObjResultat> filter, final boolean byCategory) {
        final List<ObjResultat> newResults = newArrayList(results);
        Collections.sort(newResults, byTime());
        Collections.sort(newResults, byDeclassed());
        Collections.sort(newResults, byAbandon());
        return newArrayList(transform(filter(newResults, filter), toNumeroteResult(byCategory)));
    }

    public static void updateClassement(final Iterable<ObjResultat> results, final boolean byCategory) {
        filterResults(results, new Predicate<ObjResultat>() {
                          @Override
                          public boolean apply(final ObjResultat input) {
                              return true;
                          }
                      }, byCategory);
    }
}

