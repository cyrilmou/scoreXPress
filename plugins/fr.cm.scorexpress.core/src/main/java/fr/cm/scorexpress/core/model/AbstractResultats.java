package fr.cm.scorexpress.core.model;

import java.util.Collection;


public interface AbstractResultats extends AbstractGetInfo<IData> {
    public void updateResultat();

    public Collection<ObjResultat> getResultats();

}