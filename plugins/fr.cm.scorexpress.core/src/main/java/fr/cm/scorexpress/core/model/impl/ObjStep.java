package fr.cm.scorexpress.core.model.impl;

import com.google.common.base.Predicate;
import fr.cm.scorexpress.core.model.*;
import java.beans.PropertyChangeEvent;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.BaliseUtils.findNumBaliseOfType;
import static fr.cm.scorexpress.core.model.BaliseUtils.setBalise;
import static fr.cm.scorexpress.core.model.ObjDossard.*;
import static fr.cm.scorexpress.core.model.ObjPenalite.VAR_PENALITE_TIME;
import static fr.cm.scorexpress.core.model.ObjPenalite.VAR_PENALITY_ACTIVE;
import static fr.cm.scorexpress.core.model.StepUtil.getDossard;
import static fr.cm.scorexpress.core.model.i18n.Messages.i18n;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.DateUtils.downTime;
import static fr.cm.scorexpress.core.model.impl.DateUtils.upTime;
import static fr.cm.scorexpress.core.model.impl.StepUtils.createDossard;
import static fr.cm.scorexpress.core.model.impl.StepUtils.setArretChronoStr;
import static fr.cm.scorexpress.core.util.CalculResultatsUtils.filterResults;
import static fr.cm.scorexpress.core.util.PenalityUtils.TYPE_ARRET_CHRONO_MAXI;
import static java.lang.Boolean.parseBoolean;
import static java.util.Collections.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ObjStep extends IData<IData>
        implements Comparable<ObjStep>, AbstractBalises, AbstractSteps, AbstractResultats, IDossards, IUserChronos, Step, IElementVisitor {
    private static final long   serialVersionUID = -6835743728716291598L;
    private              String ordre            = "0";
    private String lib;
    private boolean actif = true;

    private       boolean                     epreuve          = false;
    private       boolean                     cumulerSousEtape = false;
    private       boolean                     classementInter  = false;
    private       boolean                     penalitySaisie   = false;
    private       int                         nextDossard      = 0;
    private final Collection<ObjBalise>       balises          = newArrayList();
    private final List<ObjStep>               steps            = newArrayList();
    private final Collection<ObjPenalite>     penalites        = newArrayList();
    private final AbstractList<ObjDossard>    dossards         = newArrayList();
    private final Map<String, ObjUserChronos> usersChronos     = new HashMap<String, ObjUserChronos>();

    private       String                  numero         = null;
    private       String                  importFileName = null;
    private       boolean                 importAuto     = false;
    private       boolean                 arretChrono    = false;
    private final Collection<ObjResultat> resultats      = newArrayList();
    private final ArrayList<ObjCategorie> filtrecategory = newArrayList();
    private       String                  categoryFilter = null;

    ObjStep(final String ordre, final String lib) {
        this.ordre = ordre;
        this.lib = lib;
        setControler(new ControlerStep(this));
    }

    ObjStep(final String lib) {
        ordre = "0";
        this.lib = lib;
        setControler(new ControlerStep(this));
    }

    ObjStep(final String ordre, final String lib, final String numero) {
        this.ordre = ordre;
        this.lib = lib;
        this.numero = numero;
        setControler(new ControlerStep(this));
    }

    @Override
    public void modifyCalculData() {
        if (!super.calculDataModified) {
            System.out.println("calcul is Modify ->" + this);
        }
        super.modifyCalculData();
    }

    @Override
    public boolean isActif() {
        return actif;
    }

    public void setActif(final String actif) {
        try {
            setActif(parseBoolean(actif));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setActif(final boolean actif) {
        firePropertyChange(VAR_ACTIF, this.actif, this.actif = actif);
    }

    @Override
    public String getLib() {
        return lib;
    }

    public void setLib(final String lib) {
        firePropertyChange(VAR_LIB_STEP, this.lib, this.lib = lib);
    }

    public String getOrdre() {
        try {
            return new Integer(ordre) + EMPTY;
        } catch (Exception ex) {
            return ordre;
        }
    }

    public void setOrdre(final String ordre) {
        firePropertyChange(VAR_ORDRE, this.ordre, this.ordre = ordre);
    }

    @Override
    public void addBalise(final ObjBalise b) {
        b.setParent(this);
        modifyCalculData();
        final Object type = b.getType();
        if (type.equals(Balise.START_TYPE_BALISE)) {
            setBaliseDepart(b.getNum());
        } else if (type.equals(Balise.END_TYPE_BALISE)) {
            setBaliseArrivee(b.getNum());
        } else {
            balises.add(b);
            b.addPropertyChangeListener(this);
            firePropertyChange(VAR_STEP_BALISES, null, b);
        }
    }

    @Override
    public boolean removeBalise(final ObjBalise balise) {
        final boolean result = balises.remove(balise);
        balise.removePropertyChangeListener(this);
        firePropertyChange(VAR_STEP_BALISES, null, balise);
        return result;
    }

    @Override
    public boolean addStep(final ObjStep subStep) {
        subStep.setParent(this);
        final boolean res = steps.add(subStep);
        subStep.addPropertyChangeListener(this);
        firePropertyChange(VAR_SUB_STEPS, null, subStep);
        return res;
    }

    @Override
    public boolean removeStep(final ObjStep subStep) {
        final boolean res = steps.remove(subStep);
        subStep.removePropertyChangeListener(this);
        firePropertyChange(VAR_SUB_STEPS, null, subStep);
        return res;
    }

    public boolean addPenalite(final ObjPenalite penality) {
        penality.setParent(this);
        final boolean res = penalites.add(penality);
        penality.addPropertyChangeListener(this);
        firePropertyChange(VAR_STEP_PENALITIES, null, penality);
        return res;
    }

    public boolean removePenalite(final ObjPenalite penality) {
        final boolean res = penalites.remove(penality);
        penality.removePropertyChangeListener(this);
        firePropertyChange(VAR_STEP_PENALITIES, null, penality);
        return res;
    }

    @Override
    public int compareTo(final ObjStep object) {
        return new Integer(ordre).compareTo(new Integer(object.getOrdre()));
    }

    private static Comparator<ObjStep> getComparatorOrder() {
        return new Comparator<ObjStep>() {
            @Override
            public int compare(final ObjStep object, final ObjStep object1) {
                return object.compareTo(object1);
            }
        };
    }

    public void sortEtapes() {
        sort(steps, getComparatorOrder());
    }

    @Override
    public Collection<ObjStep> getSteps() {
        return unmodifiableList(steps);
    }

    @Override
    public Collection<ObjBalise> getBalises() {
        return unmodifiableCollection(balises);
    }

    public Collection<ObjPenalite> getPenalites() {
        return unmodifiableCollection(penalites);
    }

    public void sortAll() {
        sortEtapes();
    }

    @Override
    public Object getInfoLocal(final String attribut) {
        if (VAR_ORDRE.equalsIgnoreCase(attribut)) {
            return new Integer(ordre);
        }
        if (VAR_ACTIF.equalsIgnoreCase(attribut)) {
            return actif;
        }
        if (VAR_LIB_STEP.equalsIgnoreCase(attribut)) {
            return lib;
        }
        if (VAR_FILENAME_IMPORT.equalsIgnoreCase(attribut)) {
            return importFileName;
        }
        if (VAR_FILTER_CATEGORY.equalsIgnoreCase(attribut)) {
            return categoryFilter;
        }
        if (VAR_ARRETCHRONO.equalsIgnoreCase(attribut)) {
            return isArretChrono();
        }
        if (VAR_BALISE_DEPART.equalsIgnoreCase(attribut)) {
            for (final Balise balise : balises) {
                if (Balise.START_TYPE_BALISE.equals(balise.getType())) {
                    return balise.getNum();
                }
            }
            return i18n("ObjEtape.Depart_general");
        }
        if (VAR_BALISE_ARRIVEE.equalsIgnoreCase(attribut)) {
            for (final Balise balise : balises) {
                if (Balise.END_TYPE_BALISE.equals(balise.getType())) {
                    return balise.getNum();
                }
            }
            return i18n("ObjEtape.Arrivee_generale");
        }
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        if (VAR_ORDRE.equalsIgnoreCase(attribut)) {
            setOrdre(EMPTY + val);
            return true;
        }
        if (VAR_ACTIF.equalsIgnoreCase(attribut)) {
            setActif(EMPTY + val);
            return true;
        }
        if (VAR_LIB_STEP.equalsIgnoreCase(attribut)) {
            setLib(EMPTY + val);
            return true;
        }
        if (VAR_FILENAME_IMPORT.equalsIgnoreCase(attribut)) {
            setImportFileName(EMPTY + val);
            return true;
        }
        if (VAR_FILTER_CATEGORY.equalsIgnoreCase(attribut)) {
            setCategoryFilter(EMPTY + val);
            return true;
        }
        if (VAR_ARRETCHRONO.equalsIgnoreCase(attribut)) {
            setArretChronoStr(this, val + EMPTY);
            return true;
        }
        return VAR_BALISE_DEPART.equalsIgnoreCase(attribut) || VAR_BALISE_ARRIVEE.equalsIgnoreCase(attribut);
    }

    @Override
    public String getPrefix() {
        return VAR_PREFIX;
    }

    public String getImportFileName() {
        return importFileName;
    }

    @SuppressWarnings({"AssignmentToNull"})
    public void setImportFileName(final String importFileName) {
        final String newImportFileName;
        if (importFileName != null && importFileName.length() == 0) {
            newImportFileName = null;
        } else {
            newImportFileName = importFileName;
        }
        firePropertyChange(VAR_FILENAME_IMPORT, this.importFileName, this.importFileName = newImportFileName);
    }

    public boolean removeParticipant(final ObjUser p) {
        /** @todo removeParticipant(ObjParticipantp) */
        return false;
    }

    public String getBaliseDepart() {
        return findNumBaliseOfType(balises, Balise.START_TYPE_BALISE);
    }

    public String getBaliseArrivee() {
        return findNumBaliseOfType(balises, Balise.END_TYPE_BALISE);
    }

    public synchronized void setBaliseArrivee(final String baliseArrivee) {
        if (setBalise(balises, baliseArrivee, Balise.END_TYPE_BALISE)) {
            firePropertyChange(VAR_BALISE_ARRIVEE, null, baliseArrivee);
        }
    }

    public void setBaliseDepart(final String baliseDepart) {
        if (setBalise(balises, baliseDepart, Balise.START_TYPE_BALISE)) {
            firePropertyChange(Balise.START_TYPE_BALISE, null, baliseDepart);
        }
    }

    Date2 getArretChronoTropPercu(final Date d) {
        final Date2 res = createDate(0);
        for (final ObjPenalite penalite : penalites) {
            if (penalite.isActivate() && penalite.getTypePenalite() == TYPE_ARRET_CHRONO_MAXI.valeur) {
                final Date2 limite = createDate(0);
                downTime(limite, penalite.getDureeMaxi());
                downTime(limite, penalite.getEchellePenalite());
                upTime(res, penalite.getDureeMaxi());
                if (d.before(limite)) {
                    upTime(res, new Date(penalite.getDureeMaxi().getTime() + d.getTime()));
                    return res;
                }
                if (d.after(limite)) {
                    downTime(res, createDate(penalite.getDureeMaxi().getTime() + d.getTime()));
                    return res;
                }
            }
        }
        return res;
    }

    @Override
    public AbstractList<ObjDossard> getDossards() {
        return dossards;
    }

    @Override
    public Collection<ObjResultat> getResultats() {
        return unmodifiableCollection(resultats);
    }

    public Collection<ObjResultat> getResultatsByEpreuve(final Predicate<ObjResultat> filter, final boolean byCategory) {
        synchronized (resultats) {
            return filterResults(resultats, filter, byCategory);
        }
    }

    public ObjStep getEpreuve() {
        if (isEpreuve()) {
            return this;
        }
        if (parent instanceof ObjStep) {
            return ((ObjStep) parent).getEpreuve();
        }
        return null;
    }

    @Override
    public boolean isArretChrono() {
        return arretChrono;
    }

    @Override
    public void setArretChrono(final boolean arretChrono) {
        firePropertyChange(VAR_ARRETCHRONO, this.arretChrono, this.arretChrono = arretChrono);
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public void setBaliseDepartGeneral(final boolean value) {
        if (value) {
            setBaliseDepart(null);
        }
    }

    @Override
    public void setBaliseArriveeGenerale(final boolean value) {
        if (value) {
            setBaliseArrivee(null);
        }
    }

    public Date2 getDateLastImport() {
        return createDate(getInfoStr(VAR_DATE_LAST_IMPORT));
    }

    public void setDateLastImport(final Date d) {
        final Date date = createDate(getInfoStr(VAR_DATE_LAST_IMPORT));
        date.setTime(d.getTime());
        setInfo(VAR_DATE_LAST_IMPORT, date);
        firePropertyChange(VAR_DATE_LAST_IMPORT, null, d);
    }

    @Override
    public boolean addDossard(final ObjDossard d) {
        ObjStep step = getEpreuve();
        if (step == null) {
            step = this;
        }
        d.setParent(this);
        try {
            nextDossard = Integer.valueOf(d.getNum()) + 1;
        } catch (NumberFormatException e) {
            nextDossard += 1;
        }
        return step.addDossardToStep(d);
    }

    public boolean addDossardToStep(final ObjDossard d) {
        d.setParent(this);
        try {
            nextDossard = Integer.valueOf(d.getNum()) + 1;
        } catch (NumberFormatException e) {
            nextDossard += 1;
        }
        final boolean res = dossards.add(d);
        d.addPropertyChangeListener(this);
        firePropertyChange("Dossards", null, d);
        return res;
    }

    @Override
    public ObjDossard nextDossard() {
        return new ObjDossard(nextDossard + EMPTY);
    }

    @Override
    public boolean removeDossard(final ObjDossard d) {
        final boolean res = dossards.remove(d);
        for (final ObjStep step : getSteps()) {
            step.removeDossard(d);
        }
        d.removePropertyChangeListener(this);
        return res;
    }

    @Override
    public boolean addUserChronos(final ObjUserChronos userChronos) {
        if (usersChronos.remove(userChronos.getDossard()) != null) {
            userChronos.removePropertyChangeListener(this);
        }
        final boolean result = usersChronos.put(userChronos.getDossard(), userChronos) != null;
        userChronos.addPropertyChangeListener(this);
        firePropertyChange("UserChronos", null, userChronos);
        return result;
    }

    @Override
    public Collection<ObjUserChronos> getUserChronos() {
        if (isEpreuve()) {
            return unmodifiableCollection(usersChronos.values());
        } else {
            if (parent != null && parent instanceof IUserChronos) {
                return ((IUserChronos) parent).getUserChronos();
            }
            return unmodifiableCollection(usersChronos.values());
        }
    }

    @Override
    public ObjUserChronos getUserChronosByDossard(final String dossard) {
        return usersChronos.get(dossard);
    }

    @Override
    public boolean removeUserChronos(final ObjUserChronos userChronos) {
        modifyCalculData();
        return usersChronos.remove(userChronos.getDossard()) != null;
    }

    @Override
    public boolean isCumulerSousEtape() {
        return cumulerSousEtape;
    }

    @Override
    public void setCumulerSousEtape(final boolean cumulerSousEtape) {
        firePropertyChange(VAR_CUMUL_SOUS_ETAPE, isCumulerSousEtape(), this.cumulerSousEtape = cumulerSousEtape);
    }

    public void setEpreuve(final boolean epreuve) {
        firePropertyChange(VAR_EPREUVE, isEpreuve(), this.epreuve = epreuve);
    }

    @Override
    public boolean isEpreuve() {
        return epreuve;
    }

    @Override
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitStep(this, data);
    }

    public ObjUserChronos getUserChronos(final String puce) {
        final ObjUserChronos userChronos = new ObjUserChronos(puce);
        for (final ObjUserChronos usersChrono : usersChronos.values()) {
            if (userChronos.getPuce().equals(usersChrono.getPuce())) {
                if (userChronos.equals(usersChrono)) {
                    return usersChrono;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isClassementInter() {
        return classementInter;
    }

    public void setClassementInter(final boolean classement) {
        firePropertyChange(VAR_STEP_CLASSEMENT_INTER, classementInter, classementInter = classement);
    }

    public Collection<ObjStep> getStepsInter() {
        final Collection<ObjStep> stepInter = newArrayList();
        for (final ObjStep step1 : steps) {
            if (!step1.getFiltreCategory().isEmpty()) {
                stepInter.addAll(step1.getStepsInter());
            }
            if (step1.isClassementInter() && step1.isActif()) {
                stepInter.add(step1);
            }
        }
        return stepInter;
    }

    public void addFiltreCategory(final ObjCategorie cat) {
        filtrecategory.add(cat);
        firePropertyChange(VAR_STEP_CATEGORIE_FILTER, null, cat);
    }

    public void removeFiltreCategory(final ObjCategorie cat) {
        filtrecategory.remove(cat);
        firePropertyChange(VAR_STEP_CATEGORIE_FILTER, null, cat);
    }

    public ArrayList<ObjCategorie> getFiltreCategoryHerite() {
        final ObjStep epreuveP = getEpreuve();
        if (epreuveP != null) {
            return epreuveP.getFiltreCategory();
        }
        return getFiltreCategory();
    }

    public ArrayList<ObjCategorie> getFiltreCategory() {
        return filtrecategory;
    }

    @Override
    public boolean isImportAuto() {
        return importAuto;
    }

    public void setImportAuto(final boolean importAuto) {
        firePropertyChange(VAR_IMPORT_AUTO, this.importAuto, this.importAuto = importAuto);
    }

    public void clearUserChronos() {
        usersChronos.clear();
        firePropertyChange("UserChronos", null, usersChronos);
    }

    @Override
    public boolean isPenalitySaisie() {
        return penalitySaisie;
    }

    public void setPenalitySaisie(final boolean penalitySaisie) {
        if (isPenalitySaisie() == penalitySaisie) {
            return;
        }
        this.penalitySaisie = penalitySaisie;
        modifyCalculData();
        hasChanged(getControler(), this, VAR_PENALITY_SAISIE);
    }

    public void balisesChanged(){
        hasChanged(getControler(), this, VAR_STEP_BALISES);
    }

    public String toString() {
        return lib;
    }

    public void setDossardPenality(final String numDossard, final Date penalite) {
        ObjDossard dossard = getDossard(numDossard, this);
        if (dossard == null) {
            dossard = createDossard(numDossard, this);
        }
        dossard.setPenality(penalite);
    }

    public void setDossardBonification(final String numDossard, final Date bonification) {
        ObjDossard dossard = getDossard(numDossard, this);
        if (dossard == null) {
            dossard = createDossard(numDossard, this);
        }
        dossard.setBonification(bonification);
    }

    @Override
    public void updateResultat() {
        StepUtils.updateResultat(this);
    }

    @Override
    public String accept(final IVisitor visitor) {
        return visitor.visite(this);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String[] modifyCalculProperties =
                {Balise.VAR_BALISE_NUM, Balise.VAR_BALISE_TYPE, VAR_PENALITY_ACTIVE, VAR_STEP_BALISES, VAR_SUB_STEPS,
                 VAR_CALCUL_DATA_MODIFIED, VAR_DOSSARD_NUM, VAR_DOSSARD_BONIFICATION, VAR_DOSSARD_PENALITY,
                 VAR_TEMPS_IMPOSE, VAR_DOSSARD_ABANDON, VAR_DOSSARD_DISQUALIFIER, VAR_PENALITE_TIME,
                 VAR_STEP_PENALITIES, VAR_STEP_BALISES, VAR_ARRETCHRONO, VAR_ACTIF, VAR_BALISE_ARRIVEE,
                 VAR_BALISE_DEPART, VAR_CUMUL_SOUS_ETAPE
                };
        if (propertyMatches(modifyCalculProperties, event)) {
            fireLocalEventChanged(VAR_CALCUL_DATA_MODIFIED, calculDataModified, calculDataModified = true);
        }
        super.propertyChange(event);
    }

    public void setResultat(final Collection<ObjResultat> resultats) {
        synchronized (this.resultats) {
            this.resultats.clear();
            this.resultats.addAll(resultats);
            calculDataModified = true;
        }
    }

    public String getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(final String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public boolean containBalise(final String numBalise) {
        for (final Balise balise : balises) {
            if( balise.getNum().equals(numBalise))
                return true;
        }
        return false;
    }
}
