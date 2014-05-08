package fr.cm.scorexpress.core.model.impl;

import fr.cm.scorexpress.core.model.ElementModelVisitor;

public class ObjEpreuve extends ObjStep {

    private static final long serialVersionUID = 685550426457114557L;
    public static final String VAR_PREFIX = "EPREUVE_";

    public ObjEpreuve(final String ordre, final String nom, final String numero) {
        super(ordre, nom, numero);
        super.setEpreuve(true);
    }

    public String getPrefix() {
        return VAR_PREFIX;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitStep(this, data);
    }
}
