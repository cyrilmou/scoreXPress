package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;

public class ObjChronoArrivee extends ObjChrono {
    private static final long serialVersionUID = -4299349764842394728L;
    public static final String TYPE = "ARRIVEE";

    public ObjChronoArrivee(final Date2 temps) {
        super(TYPE);
        super.setTemps(temps);
    }

    @SuppressWarnings({"RefusedBequest"})
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }
}
