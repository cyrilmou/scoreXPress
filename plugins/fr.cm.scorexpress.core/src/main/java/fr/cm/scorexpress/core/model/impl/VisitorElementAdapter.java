package fr.cm.scorexpress.core.model.impl;

import fr.cm.scorexpress.core.model.ObjDossard;
import fr.cm.scorexpress.core.model.ObjManifestation;

public class VisitorElementAdapter implements IVisitor {

    public String visite(final Object element) {
        throw new NoSuchMethodError();
    }

    public String visite(final ObjStep step) {
        throw new NoSuchMethodError();
    }

    public String visite(final ObjEpreuve epreuve) {
        throw new NoSuchMethodError();
    }

    public String visite(final ObjManifestation manif) {
        throw new NoSuchMethodError();
    }

    public String visite(final ObjDossard dossard) {
        throw new NoSuchMethodError();
    }

}
