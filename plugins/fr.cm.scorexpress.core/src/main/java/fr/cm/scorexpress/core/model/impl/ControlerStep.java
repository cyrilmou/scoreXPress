package fr.cm.scorexpress.core.model.impl;

import fr.cm.scorexpress.core.model.*;
import static fr.cm.scorexpress.core.model.Balise.*;
import org.apache.commons.lang.StringUtils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

public class ControlerStep extends ControlerData implements IStepControler {
    private static final long serialVersionUID = -5905430131318824006L;
    private static final String VIDE = "";
    private final ObjStep step;

    public ControlerStep(final ObjStep step) {
        this.step = step;
    }

    public ObjStep getContent() {
        return step;
    }

    public Collection<ObjDossard> getUsers() {
        final Collection<ObjDossard> users = new ArrayList<ObjDossard>();
        for (final ObjDossard objDossard : StepUtil.gatherAllDossards(step)) {
            if (!objDossard.isTeam()) {
                users.add(objDossard);
            }
        }
        return users;
    }

    public Collection<ObjDossard> getTeams() {
        final Collection<ObjDossard> teams = new ArrayList<ObjDossard>();
        for (final ObjDossard objDossard : step.getDossards()) {
            if (objDossard.isTeam()) {
                teams.add(objDossard);
            }
        }
        return teams;
    }

    public void addNewUser() {
        step.addDossard(step.nextDossard());
    }

    public void addNewTeam() {
        step.getManif().addUser(new ObjTeam());
    }

    public void removeUser(final ObjDossard d) {
        step.removeDossard(d);
    }

    public Object[] getPenalities() {
        return step.getPenalites().toArray();
    }

    public Iterable<ObjPenalite> getDescendentPenalities() {
        return getDescendentPenalities(step);
    }

    private static Collection<ObjPenalite> getDescendentPenalities(final ObjStep step) {
        final Collection<ObjPenalite> res = new ArrayList<ObjPenalite>();
        for (final ObjStep s : step.getSteps()) {
            res.addAll(getDescendentPenalities(s));
        }
        if (!step.getPenalites().isEmpty()) {
            res.addAll(step.getPenalites());
        }
        return res;
    }

    public static void addNewPenality(final ObjStep s) {
        s.addPenalite(new ObjPenalite("new" + (s.getPenalites().size() + 1)));
    }

    public void removePenality(final ObjPenalite penalite) {
        ObjStep s = penalite.getParent();
        if (s == null) {
            s = step;
        }
        s.removePenalite(penalite);
    }

    public Object[] getCategories() {
        return step.getFiltreCategory().toArray();
    }

    public Object[] getEtapes() {
        return step.getSteps().toArray();
    }

    public void addNewCategorie() {
        step.addFiltreCategory(new ObjCategorie("nouvelle"));
    }

    public void removeCategorie(final ObjCategorie cat) {
        step.removeFiltreCategory(cat);
    }

    public void removeStep(final ObjStep st) {
        AbstractSteps s = (AbstractSteps) st.getParent();
        if (s == null) {
            s = step;
        }
        s.removeStep(st);
    }

    public static void addNewStep(final AbstractSteps step) {
        step.addStep(new ObjStep("Etape"));
    }

    public AbstractList<ObjStep> getStepWithPenaliteSaisie() {
        return getStepWithPenaliteSaisie(step);
    }

    private static AbstractList<ObjStep> getStepWithPenaliteSaisie(final ObjStep step) {
        final AbstractList<ObjStep> res = new ArrayList<ObjStep>();
        if (step.isPenalitySaisie() || step.isEpreuve()) {
            res.add(step);
        }
        for (final ObjStep objStep : step.getSteps()) {
            final Collection<ObjStep> steps = getStepWithPenaliteSaisie(objStep);
            res.addAll(steps);
        }
        return res;
    }

    public static ObjDossard addNewDossard(final IDossards step, final String num) {
        final ObjDossard d = new ObjDossard(num);
        step.addDossard(d);
        return d;
    }

    public static ObjDossard createDossard(final ObjStep step, final String num) {
        final ObjDossard d = new ObjDossard(num);
        step.addDossardToStep(d);
        return d;
    }

    public static void addNewBalise(final AbstractBalises etape) {
        String nr = "31";
        final ObjBalise newBalise = BaliseFactory.createBalise(nr, TYPE_OBLIGATOIRE, VIDE);
        for (final Balise objBalise : etape.getBalises()) {
            try {
                nr = VIDE + (new Integer(objBalise.getNum()) + 1);
                newBalise.setNum(nr);
                newBalise.setPenalite(objBalise.getPenalite());
                if (!StringUtils.equals(objBalise.getType(), START_TYPE_BALISE) &&
                        !StringUtils.equals(objBalise.getType(), END_TYPE_BALISE)) {
                    newBalise.setType(objBalise.getType());
                }
            } catch (Exception ignored) {
            }
        }
        newBalise.setType(TYPE_OBLIGATOIRE);
        etape.addBalise(newBalise);
    }
}
