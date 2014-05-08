package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.ObjStep;

import java.util.Collection;


public interface AbstractSteps extends AbstractGetInfo<IData> {
    public Collection<ObjStep> getSteps();

    public boolean addStep(ObjStep e);

    public boolean removeStep(ObjStep e);

    public Object getLib();
}