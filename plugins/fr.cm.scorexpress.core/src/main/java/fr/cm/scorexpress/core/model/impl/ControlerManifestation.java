package fr.cm.scorexpress.core.model.impl;

import fr.cm.scorexpress.core.model.*;

public class ControlerManifestation extends ControlerData implements IStepControler {

    private static final long serialVersionUID = -2220154517984540663L;
    private final ObjManifestation manif;

    public ControlerManifestation(final ObjManifestation manif) {
        this.manif = manif;
    }

    public ObjManifestation getContent() {
        return manif;
    }

    public Object[] getCategories() {
        return manif.getCategories().toArray();
    }

    public void addNewCategorie() {
        manif.addCategorie(new ObjCategorie("nouvelle"));
    }

    public void addNewEtape() {
        manif.addStep(new ObjStep("etape"));
    }

    public Object[] getEtapes() {
        return manif.getSteps().toArray();
    }

    public void removeEtape(final ObjStep etape) {
        manif.removeStep(etape);
    }

    public void removeCategorie(final ObjCategorie cat) {
        manif.removeCategorie(cat);
    }

    public Object[] getImportationField() {
        return manif.getConfiguration().getConfig(ConfigType.IMPORT_PARTICIPANTS).getColTableAll().toArray();
    }
}
