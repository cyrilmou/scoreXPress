package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface AbstractCategories extends AbstractGetInfo<IData> {

    public Collection<ObjCategorie> getCategories();

}