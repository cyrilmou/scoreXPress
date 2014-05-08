package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface IDossards extends AbstractGetInfo<IData> {

    public static final String CHOIX_CATEGORIES = "CATEGORIES";

    public static final String CHOIX_EPREUVES = "EPREUVES";

    public Collection<ObjDossard> getDossards();

    public ObjDossard nextDossard();

    public boolean addDossard(ObjDossard d);

    public boolean removeDossard(ObjDossard d);
}