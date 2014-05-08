package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.ObjStep;

public interface ElementModelVisitor {

    public Object visite(ObjManifestation parent, Object data);

    public Object visite(ObjCategorie parent, Object data);

    public Object visitStep(ObjStep parent, Object data);

    public Object visite(ObjConfiguration parent, Object data);

    public Object visitBalise(ObjBalise parent, Object data);

    public Object visite(ObjChoix parent, Object data);

    public Object visite(ObjChrono parent, Object data);

    public Object visite(ObjChronoArrivee parent, Object data);

    public Object visite(ObjChronoDepart parent, Object data);

    public Object visite(ColTable parent, Object data);

    public Object visite(ObjConfig parent, Object data);

    public Object visite(ObjPenalite parent, Object data);

    public Object visite(ObjResultat parent, Object data);

    public Object visite(ObjDossard parent, Object data);

    public Object visite(ObjSaisiePenalite parent, Object data);

    public Object visitTeam(ObjTeam parent, Object data);

    public Object visitUser(ObjUser parent, Object data);

    public Object visitUserChronos(ObjUserChronos parent, Object data);

    public Object visite(InfoDivers parent, Object data);

    public Object visite(IData parent, Object data);
}
