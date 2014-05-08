package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface AbstractBalises extends AbstractGetInfo<IData> {
    public Collection<Balise> getBalises();

    public void addBalise(ObjBalise b);

    public boolean removeBalise(ObjBalise b);
}
