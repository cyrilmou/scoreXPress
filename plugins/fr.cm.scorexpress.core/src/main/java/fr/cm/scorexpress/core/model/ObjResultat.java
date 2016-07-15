package fr.cm.scorexpress.core.model;

import java.util.ArrayList;
import java.util.Collection;

import fr.cm.scorexpress.core.model.i18n.Messages;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.DateUtils;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ObjResultat extends IData implements Comparable<ObjResultat> {
    public static final String                 VAR_LIB                        = "LIB";
    public static final String                 VAR_RESULTAT_TEMPS             = "TEMPS";
    public static final String                 VAR_TEMPSPARCOURS              = "TEMPSPARCOURS";
    public static final String                 VAR_TEMPS_CHRONO               = "TEMPSCHRONO";
    public static final String                 VAR_TEMPSARRETCHRONO           = "TEMPSARRETCHRONO";
    public static final String                 VAR_TEMPS_CHRONO_MINI          = "TEMPSCHRONOMINI";
    public static final String                 VAR_TEMPS_SIGNE                = "TEMPSSIGNE";
    public static final String                 VAR_RESULTAT_PLACE             = "PLACE";
    public static final String                 VAR_RESULTAT_PENALITE          = "PENALITE";
    public static final String                 VAR_RESULTAT_BALISES_PENALITES = "BALISESPENALITES";
    public static final String                 VAR_RESULTAT_BALISES_OPTIONS   = "BALISESOPTIONS";
    public static final String                 VAR_RESULTAT_BALISESMANQUEES   = "BALISESMANQUEES";
    public static final String                 VAR_RESULTAT_BALISESBONUS      = "BALISESBONUS";
    public static final String                 VAR_RESULTAT_BALISE_DISORDERED = "BALISESDISORDERED";
    public static final String                 VAR_RESULTAT_BALISES_OK        = "BALISESOK";
    public static final String                 VAR_RESULTAT_ECART             = "ECART";
    public static final String                 VAR_NB_BALISE                  = "NBBALISES";
    public static final String                 VAR_NB_BALISE_BONUS            = "NBBALISESBONUS";
    public static final String                 VAR_NB_PENALITE                = "NBPENALITE";
    public static final String                 VAR_NB_POINTS_BALISE           = "NBPOINTSBALISE";
    public static final String                 VAR_PENALITE_BALISE            = "PENALITEBALISES";
    public static final String                 VAR_PENALITE_AUTRE             = "PENALITEAUTRE";
    public static final String                 PREFIX_RES_INTER               = "RES.INTER";
    public static final String                 NR_ETAPE_SEPARATOR             = "|";
    public static final String                 VAR_BONIFICATION               = "BONIFICATION";
    public static       boolean                showError                      = false;
    private final       Date2                  tempsParcours                  = createDate(0);
    private final       Date2                  tempsResultat                  = createDate(0);
    private final       Date2                  penaliteResultat               = createDate(true);
    private final       Date2                  bonificationResultat           = createDate(true);
    private final       Date2                  tempsArretChronoResultat       = createDate(true);
    private final       Date2                  tempsChronoMiniResultat        = createDate(true);
    private final       Date2                  penaliteBalise                 = createDate(true);
    private final       Date2                  penaliteAutre                  = createDate(true);
    private final       ArrayList<ObjResultat> resultatsInter                 = new ArrayList<ObjResultat>();
    private String     lib;
    private ObjDossard dossard;
    private boolean                          abandon        = false;
    private boolean                          declasse       = false;
    private boolean                          horsClassement = false;
    private boolean                          notArrived     = false;
    private boolean                          error          = false;
    private boolean                          triche         = false;
    private Collection<String>               errors         = newArrayList();
    private Collection<ObjChrono> chronos        = newArrayList();
    private Date2 departTime;
    private Date2 arriveeTime;

    public ObjResultat() {
        penaliteBalise.setAffichage(true, false, true);
        penaliteAutre.setAffichage(true, false, true);
        penaliteResultat.setAffichage(true, false, true);
        bonificationResultat.setAffichage(true, false, true);
        tempsChronoMiniResultat.setAffichage(true, false, true);
    }

    public String getLib() {
        return lib;
    }

    public void setLib(final String lib) {
        this.lib = lib;
    }

    public Date2 getTemps() {
        return tempsResultat;
    }

    public String getTempsStr() {
        if (isAbandon()) {
            return Messages.i18n("ObjResultat.ABANDON");
        }
        if (isDeclasse()) {
            if (isHorsClassement()) {
                return "HC";
            } else {
                return Messages.i18n("ObjResultat.DECLASSE");
            }
        }
        try {
            String res = tempsResultat.toString();
            if (notArrived) {
                res += Messages.i18n("ObjResultat._EC_");
            }
            if (error && showError) {
                res += " (Err)";
            }
            if (isHorsClassement()) {
                res += "(HC)";
            }
            return res;
        } catch (final Exception ex) {
            return Messages.i18n("ObjResultat.NC");
        }
    }

    public ObjDossard getDossard() {
        return dossard;
    }

    public void setDossard(final ObjDossard dossard) {
        this.dossard = dossard;
    }

    @Override
    public String getPrefix() {
        return "RESULTAT";
    }

    @Override
    public Object getInfoLocal(String attribut) {
        if (attribut.equalsIgnoreCase(VAR_LIB)) {
            return lib;
        }
        if (attribut.equalsIgnoreCase(VAR_RESULTAT_TEMPS)) {
            return getTempsStr();
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPS_SIGNE)) {
            return getTempsStr();
        }
        if (attribut.equalsIgnoreCase(VAR_RESULTAT_PENALITE)) {
            return getPenaliteResultat();
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPSPARCOURS)) {
            return getTempsParcours();
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPS_CHRONO)) {
            final Date2 date = createDate(0);
            DateUtils.upTime(date, getTempsParcours());
            DateUtils.upTime(date, getTempsArretChronoResultat());
            return date;
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPSARRETCHRONO)) {
            return getTempsArretChronoResultat();
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPS_CHRONO_MINI)) {
            return getTempsChronoMiniResultat();
        }
        if (attribut.equalsIgnoreCase(VAR_PENALITE_BALISE)) {
            return getPenaliteBalise();
        }
        if (attribut.equalsIgnoreCase(VAR_BONIFICATION)) {
            return getBonificationResultat();
        }
        if (attribut.equalsIgnoreCase(VAR_PENALITE_AUTRE)) {
            return getPenaliteAutre();
        }
        final int index = attribut.indexOf(PREFIX_RES_INTER);
        if (index != -1) {
            attribut = attribut.substring(index + PREFIX_RES_INTER.length());
            final int index2 = attribut.indexOf(NR_ETAPE_SEPARATOR);
            final int nrEtape = new Integer(attribut.substring(0, index2));
            attribut = attribut.substring(index2 + NR_ETAPE_SEPARATOR.length());
            return getInfoResultatInter(nrEtape, attribut);
        }
        final Object obj = dossard.getInfo(attribut);
        if (obj != null) {
            return obj;
        }
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        final boolean res = dossard.setInfoLocal(attribut, val);
        if (res) {
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_LIB)) {
            lib = val + EMPTY;
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_RESULTAT_TEMPS)) {
            tempsResultat.setTime(createDate(EMPTY + val).getTime());
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_RESULTAT_PENALITE)) {
            penaliteResultat.setTime(createDate(EMPTY + val).getTime());
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_TEMPSPARCOURS)) {
            tempsParcours.setTime(createDate(EMPTY + val).getTime());
            return true;
        }
        if (attribut.equalsIgnoreCase(VAR_PENALITE_BALISE)) {
            penaliteBalise.setTime(createDate(EMPTY + val).getTime());
            return true;
        }
        return false;
    }

    public boolean isAbandon() {
        return abandon;
    }

    public void setAbandon(final boolean abandon) {
        this.abandon = abandon;
    }

    @Override
    public int compareTo(final ObjResultat object) {
        final ObjResultat res = this;
        if (res.getTemps() == null) {
            return +1;
        }
        if (object == null || object.getTemps() == null) {
            return -1;
        }
        return res.getTemps().compareTo(object.getTemps());

    }

//    public Date2 getPenalite() {
//        return penalite;
//    }

    public Date2 getPenaliteBalise() {
        return penaliteBalise;
    }

    public boolean isDeclasse() {
        return declasse;
    }

    public void setDeclasse(final boolean declasse) {
        this.declasse = declasse;
    }

    public void addNbPenalite(final int nbPenalite) {
        Integer nb = (Integer) getInfo(VAR_NB_PENALITE);
        if (nb == null) {
            nb = nbPenalite;
        } else {
            nb += nbPenalite;
        }
        setInfo(VAR_NB_PENALITE, nb);
    }

    public int getNbPenalite() {
        final Object obj = getInfo(VAR_NB_PENALITE);
        if (obj != null && obj instanceof Integer) {
            return (Integer) obj;
        }
        return 0;
    }

    public void addNbBalises(final int nbBalises) {
        Integer nb = (Integer) getInfo(VAR_NB_BALISE);
        if (nb == null) {
            nb = nbBalises;
        } else {
            nb += nbBalises;
        }
        setInfo(VAR_NB_BALISE, nb);
    }

    public int getNbBalises() {
        final Object obj = getInfo(VAR_NB_BALISE);
        if (obj != null && obj instanceof Integer) {
            return (Integer) obj;
        }
        return 0;
    }

    public void addNbBalisesBonus(final int nbBalises) {
        Integer nb = (Integer) getInfo(VAR_NB_BALISE_BONUS);
        if (nb == null) {
            nb = nbBalises;
        } else {
            nb += nbBalises;
        }
        setInfo(VAR_NB_BALISE_BONUS, nb);
    }

    public int getNbBalisesBonus() {
        final Object obj = getInfo(VAR_NB_BALISE_BONUS);
        if (obj != null && obj instanceof Integer) {
            return (Integer) obj;
        }
        return 0;
    }

    public int getNbPointsBalise() {
        final Object obj = getInfo(VAR_NB_POINTS_BALISE);
        if (obj != null && obj instanceof Integer) {
            return (Integer) obj;
        }
        return 0;
    }

    public Date2 getTempsParcours() {
        return tempsParcours;
    }

    public boolean isNotArrived() {
        return notArrived;
    }

    public void setNotArrived(final boolean notArrived) {
        this.notArrived = notArrived;
    }

    public boolean isError() {
        return error;
    }

    public void setError(final boolean error) {
        this.error = error;
    }

    public void addError(final String errorMessage) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(errorMessage);
    }

    public void setDepartError() {
        addError("{DEPART}");
        setError(true);
    }

    public void addErrors(final Collection<String> errors) {
        if (errors == null) {
            return;
        }
        this.errors.addAll(errors);
    }

    public Collection<String> getErrors() {
        return unmodifiableCollection(errors);
    }

    public void showErrors() {
        if (errors != null) {
            for (final String errorMessage : errors) {
                System.out.println(errorMessage + " [dossard " + dossard.getNum() + "]");
            }
        }
    }

    public Date2 getPenaliteAutre() {
        return penaliteAutre;
    }

    @Override
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public void addResultatInter(final ObjResultat resultatInter) {
        resultatsInter.add(resultatInter);
    }

    private Object getInfoResultatInter(final int nrEtape, final String attribut) {
        if (nrEtape < resultatsInter.size()) {
            final ObjResultat resultatInter = resultatsInter.get(nrEtape);
            if (resultatInter != null) {
                return resultatInter.getInfo(attribut);
            }
        }
        return null;
    }

    public Iterable<ObjResultat> getResultatsInter() {
        return unmodifiableList(resultatsInter);
    }

    public boolean isHorsClassement() {
        return horsClassement;
    }

    public void setHorsClassement(final boolean horsClassement) {
        this.horsClassement = horsClassement;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (dossard != null) {
            builder.append(dossard.getNum());
        }
        builder.append(",tps=").append(tempsResultat);
        if (!penaliteResultat.isNull()) {
            builder.append(",penalRes=").append(penaliteResultat);
        }
        if (!bonificationResultat.isNull()) {
            builder.append(",bonifRes=").append(bonificationResultat);
        }
        if (!tempsArretChronoResultat.isNull()) {
            builder.append(",arretRes=").append(tempsArretChronoResultat);
        }
        if (!tempsChronoMiniResultat.isNull()) {
            builder.append(",chronoMiniRes=").append(tempsChronoMiniResultat);
        }
        if (abandon) {
            builder.append(",ABANDON");
        }
        if (declasse) {
            builder.append(",DECLASSE");
        }
        if (horsClassement) {
            builder.append(",HC");
        }
        return builder.toString();
    }

    public Date2 getPenaliteResultat() {
        return penaliteResultat;
    }

    public Date2 getBonificationResultat() {
        return bonificationResultat;
    }

    public Date2 getTempsArretChronoResultat() {
        return tempsArretChronoResultat;
    }

    public Date2 getTempsChronoMiniResultat() {
        return tempsChronoMiniResultat;
    }

    public void addChrono(final ObjChrono chrono) {
        chronos.add(chrono);
    }

    public Collection<ObjChrono> getChronos() {
        return chronos;
    }

    public Date2 getArriveeTime() {
        return arriveeTime;
    }

    public void setArriveeTime(final Date2 arriveeTime) {
        this.arriveeTime = arriveeTime;
    }

    public Date2 getDepartTime() {
        return departTime;
    }

    public void setDepartTime(final Date2 departTime) {
        this.departTime = departTime;
    }

    public boolean isTriche() {
        return triche;
    }

    public void setTriche(final boolean triche) {
        this.triche = triche;
    }
}
