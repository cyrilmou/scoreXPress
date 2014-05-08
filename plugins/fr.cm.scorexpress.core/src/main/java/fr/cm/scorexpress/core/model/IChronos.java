package fr.cm.scorexpress.core.model;

import java.util.Collection;


public interface IChronos extends AbstractGetInfo {
    public Collection<ObjChrono> getChronos();

    public boolean addChrono(ObjChrono c);

    public boolean removeChrono(ObjChrono c);
}