package fr.cm.scorexpress.core.model;

public interface AbstractGetInfo<Parent extends AbstractGetInfo> {

    public Object getInfo(String attribut);

    public String getInfoStr(String attribut);

    public Object setInfo(String attribut, Object val);

    public Object setInfoTmp(String attribut, Object val);

    public Object getInstance();

    public ObjManifestation getManif();

    public Parent getParent();
}
