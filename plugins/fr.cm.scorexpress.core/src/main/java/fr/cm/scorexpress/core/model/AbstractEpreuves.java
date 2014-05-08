package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.ObjEpreuve;

import java.util.Collection;

public interface AbstractEpreuves extends AbstractGetInfo {
    public Collection<ObjEpreuve> getEpreuves();

    public boolean addEpreuve(ObjEpreuve e);

    public boolean removeEpreuve(ObjEpreuve e);

    public void trierEpreuve();
}