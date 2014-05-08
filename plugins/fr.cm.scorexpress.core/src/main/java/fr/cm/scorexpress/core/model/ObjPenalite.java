package fr.cm.scorexpress.core.model;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.i18n.Messages.i18n;
import fr.cm.scorexpress.core.model.impl.Date2;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.beans.PropertyChangeEvent;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import java.text.SimpleDateFormat;
import java.util.Collection;
import static java.util.Collections.unmodifiableList;
import java.util.Date;
import java.util.List;

public class ObjPenalite extends IData<ObjStep> {
    private static final long serialVersionUID = 3871059704330851738L;

    private String lib;
    private final Date2 dureeMaxi = createDate(false);
    private final Date2 dureeMini = createDate(false);
    private final Date2 echellePenalite = createDate(false);
    private final Date2 penalite = createDate(false);
    private String unite = "";
    private final Date2 porteHoraire = createDate(true);
    private boolean activate = true;
    private boolean disqualifierConcurrent = false;
    private boolean baliseObjPenalityActivate = false;
    private int nbPointsBaliseMini = 0;
    private int nbBalisesMini = 0;
    private int typePenalite = 0;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final List<ObjSaisiePenalite> saisies = newArrayList();

    public static final String VAR_LIB = "LIB";
    public static final String VAR_DUREEMAXI = "DUREEMAXI";
    public static final String VAR_ECHELLEPENALITE = "ECHELLEPENALITE";
    public static final String VAR_PENALITE_TIME = "PENALITE";
    public static final String VAR_UNITE = "UNITE";
    public static final String VAR_PORTEHORAIRE = "PORTEHORAIRE";
    public static final String VAR_PENALITY_ACTIVE = "ACTIVE";
    public static final String VAR_LIB_PLUS = "LIBPLUS";
    public static final String VAR_NB_POINT_MINI = "NBPOINTMINI";
    public static final String VAR_NB_BALISE_MINI = "NBBALISEMINI";
    public static final String VAR_PENALITY_TYPE = "TYPE";
    public static final String VAR_ACTIVATION_PENALITE_BALISE = "ACTIVATIONPENALITEBALISE";
    public static final String VAR_DUREEMINI = "DUREEMINI";
    private static final String VAR_DISQUALIFY_CONCURRENT = "Disqualify_concurrent";

    public ObjPenalite(final String lib) {
        super();
        this.lib = lib;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivateStr(final String activate) {
        if (activate == null) {
            return;
        }
        try {
            this.activate = parseBoolean(activate);
            modifyCalculData();
        } catch (Exception ignored) {
        }
    }

    public void setActivate(final boolean activate) {
        firePropertyChange(VAR_PENALITY_ACTIVE, this.activate, this.activate = activate);
    }

    public boolean isDisqualifierConcurrent() {
        return disqualifierConcurrent;
    }

    public void setDisqualifierConcurrent(final boolean disqualifierConcurrent) {
        firePropertyChange(VAR_DISQUALIFY_CONCURRENT,
                this.disqualifierConcurrent,
                this.disqualifierConcurrent = disqualifierConcurrent);
    }

    public void setDisqualifierConcurrentStr(final String disqualifierConcurrent) {
        if (disqualifierConcurrent == null) {
            return;
        }
        try {
            setDisqualifierConcurrent(parseBoolean(disqualifierConcurrent));
        } catch (Exception ignored) {
        }
    }

    public boolean isBaliseObjPenalityActivate() {
        return baliseObjPenalityActivate;
    }

    public void setBaliseObjPenalityActivateStr(final String baliseObjPenalityActivate) {
        if (baliseObjPenalityActivate == null) {
            return;
        }
        try {
            setBaliseObjPenalityActivate(parseBoolean(baliseObjPenalityActivate));
        } catch (Exception ignored) {
        }
    }

    public void setBaliseObjPenalityActivate(final boolean baliseObjPenalityActivate) {
        firePropertyChange(VAR_ACTIVATION_PENALITE_BALISE,
                this.baliseObjPenalityActivate,
                this.baliseObjPenalityActivate = baliseObjPenalityActivate);
    }

    public void setDureeMaxiStr(final String dureeMaxi) {
        setDureeMaxi(createDate(dureeMaxi));
    }

    public void setDureeMiniStr(final String dureeMini) {
        this.dureeMini.setTime(createDate(dureeMini).getTime());
        firePropertyChange(VAR_DUREEMINI, null, this.dureeMini);
    }

    public void setEchellePenaliteStr(final String echellePenalite) {
        setEchellePenalite(createDate(echellePenalite));
    }

    public void setLib(final String lib) {
        firePropertyChange(VAR_LIB, this.lib, this.lib = lib);
    }

    public void setPenaliteStr(final String penalite) {
        setPenalite(createDate(penalite));
    }

    public void setPorteHoraireStr(final String porteHoraire) {
        setPorteHoraire(createDate(porteHoraire));
    }

    public int getNbBalisesMini() {
        return nbBalisesMini;
    }

    public void setNbBalisesMiniStr(final String nbBaliseStr) {
        try {
            setNbBaliseMini(parseInt(nbBaliseStr));
        } catch (Exception ex) {
            setNbBaliseMini(0);
        }
        modifyCalculData();
    }

    public void setNbBaliseMini(final int nbBalises) {
        firePropertyChange(VAR_NB_BALISE_MINI, nbBalisesMini, nbBalisesMini = nbBalises);

    }

    public int getNbPointsBaliseMini() {
        return nbPointsBaliseMini;
    }

    public void setNbPointsBaliseMiniStr(final String nbPointsStr) {
        try {
            setNbPointsBaliseMini(parseInt(nbPointsStr));
        } catch (Exception ex) {
            setNbPointsBaliseMini(0);
        }
    }

    public void setNbPointsBaliseMini(final int nbPoints) {
        firePropertyChange(VAR_NB_POINT_MINI, nbPointsBaliseMini, nbPointsBaliseMini = nbPoints);
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(final String unite) {
        firePropertyChange(VAR_UNITE, this.unite, this.unite = unite);
    }

    public String getDureeMaxiStr() {
        return EMPTY + dureeMaxi;
    }

    public String getDureeMiniStr() {
        return EMPTY + dureeMini;
    }

    public String getEchellePenaliteStr() {
        return EMPTY + echellePenalite;
    }

    public String getLib() {
        return lib;
    }

    public String getPenaliteStr() {
        return penalite.toString();
    }

    public String getPorteHoraireStr() {
        return EMPTY + porteHoraire;
    }

    public Date2 getDureeMaxi() {
        return dureeMaxi;
    }

    public void setDureeMaxi(final Date2 dureeMaxi) {
        this.dureeMaxi.setTime(dureeMaxi.getTime());
        firePropertyChange(VAR_DUREEMAXI, null, this.dureeMaxi);
    }

    public Date getEchellePenalite() {
        return echellePenalite;
    }

    public void setEchellePenalite(final Date2 echellePenalite) {
        this.echellePenalite.setTime(echellePenalite.getTime());
        modifyCalculData();
        firePropertyChange(VAR_ECHELLEPENALITE, null, this.echellePenalite);
    }

    public Date2 getPenalite() {
        return penalite;
    }

    public void setPenalite(final Date2 penalite) {
        this.penalite.setTime(penalite.getTime());
        firePropertyChange(VAR_PENALITE_TIME, null, this.penalite);
    }

    public Date getPorteHoraire() {
        return porteHoraire;
    }

    public void setPorteHoraire(final Date2 porteHoraire) {
        this.porteHoraire.setTime(porteHoraire.getTime());
        modifyCalculData();
        firePropertyChange(VAR_PORTEHORAIRE, null, this.porteHoraire);
    }

    public void setTypePenalite(final String typePenalite) {
        try {
            setTypePenalite(nbPointsBaliseMini = parseInt(typePenalite));
        } catch (Exception ignored) {
        }
    }

    public void setTypePenalite(final int typePenalite) {
        firePropertyChange(VAR_PENALITY_TYPE, this.typePenalite, this.typePenalite = typePenalite);
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public boolean addSaisiePenalite(final ObjSaisiePenalite saisie) {
        saisie.setParent(this);
        final boolean result = saisies.add(saisie);
        firePropertyChange("Saisies", null, saisie);
        return result;
    }

    public boolean removeSaisiePenalite(final ObjSaisiePenalite saisie) {
        final boolean result = saisies.remove(saisie);
        firePropertyChange("Saisies", null, saisie);
        return result;
    }

    public Collection<ObjSaisiePenalite> getSaisies() {
        return unmodifiableList(saisies);
    }

    public int getTypePenalite() {
        return typePenalite;
    }

    public String getPrefix() {
        return "PENALITE";
    }

    public Object getInfoLocal(final String attribut) {
        if (attribut.equalsIgnoreCase(VAR_PENALITY_ACTIVE)) {
            return activate;
        } else if (attribut.equalsIgnoreCase(VAR_DUREEMAXI)) {
            return getDureeMaxiStr();
        } else if (attribut.equalsIgnoreCase(VAR_DUREEMINI)) {
            return getDureeMiniStr();
        } else if (attribut.equalsIgnoreCase(VAR_ECHELLEPENALITE)) {
            return getEchellePenaliteStr();
        } else if (attribut.equalsIgnoreCase(VAR_LIB)) {
            return lib;
        } else if (attribut.equalsIgnoreCase(VAR_PENALITE_TIME)) {
            return getPenaliteStr();
        } else if (attribut.equalsIgnoreCase(VAR_PORTEHORAIRE)) {
            return getPorteHoraireStr();
        } else if (attribut.equalsIgnoreCase(VAR_UNITE)) {
            return unite;
        } else if (attribut.equalsIgnoreCase(VAR_PENALITY_TYPE)) {
            return typePenalite;
        } else if (attribut.equalsIgnoreCase(VAR_NB_BALISE_MINI)) {
            return nbBalisesMini;
        } else {
            return null;
        }
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        if (attribut.equalsIgnoreCase(VAR_PENALITY_ACTIVE)) {
            setActivate(parseBoolean(val + EMPTY));
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_DUREEMAXI)) {
            setDureeMaxiStr(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_DUREEMINI)) {
            setDureeMiniStr(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_ECHELLEPENALITE)) {
            setEchellePenaliteStr(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_LIB)) {
            setLib(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_PENALITE_TIME)) {
            setPenaliteStr(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_PORTEHORAIRE)) {
            setPorteHoraireStr(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_UNITE)) {
            setUnite(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_PENALITY_TYPE)) {
            setTypePenalite(val + EMPTY);
            return true;
        } else if (attribut.equalsIgnoreCase(VAR_NB_BALISE_MINI)) {
            setNbBalisesMiniStr(val + EMPTY);
            return true;
        } else {
            return false;
        }
    }

    public String getLibPlus() {
        if (penalite == null) {
            return EMPTY;
        }
        if (dureeMaxi != null && echellePenalite != null) {
            return i18n("ObjPenalite.DUREE_MAXI_2") + createDate(dureeMaxi);
        }
        if (porteHoraire != null) {
            return i18n("ObjPenalite.PORTE_HORAIRE") + createDate(porteHoraire);
        }
        return EMPTY;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public Date2 getDureeMini() {
        return dureeMini;
    }

    public void propertyChange(final PropertyChangeEvent event) {
        final String[] modifyCalculProperties = {VAR_DUREEMINI, VAR_NB_BALISE_MINI, VAR_ACTIVATION_PENALITE_BALISE,
                VAR_NB_POINT_MINI, VAR_DISQUALIFY_CONCURRENT, VAR_ECHELLEPENALITE, VAR_PENALITE_TIME, VAR_PORTEHORAIRE};
        if (propertyMatches(modifyCalculProperties, event)) {
            firePropertyChange(VAR_CALCUL_DATA_MODIFIED, false, true);
        }
    }
}
