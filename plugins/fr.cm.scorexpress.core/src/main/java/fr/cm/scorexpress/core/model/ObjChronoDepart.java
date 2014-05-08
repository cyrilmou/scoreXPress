package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;

public class ObjChronoDepart extends ObjChrono {
    private static final long serialVersionUID = -1468785941280494697L;
    public static final String TYPE = "DEPART";

    public ObjChronoDepart(final Date2 temps) {
        super(TYPE);
        super.setTemps(temps);
    }

    @SuppressWarnings({"RefusedBequest"})
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }
}
