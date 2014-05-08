package fr.cm.scorexpress.core.model;

import java.util.Collection;


public interface AbstractInfoDivers<Parent extends AbstractGetInfo> extends AbstractGetInfo<Parent> {

    public Collection<InfoDivers> getInfoDiverses();

}