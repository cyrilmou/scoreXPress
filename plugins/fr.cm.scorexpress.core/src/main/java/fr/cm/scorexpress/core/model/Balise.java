package fr.cm.scorexpress.core.model;

import java.util.Date;

public interface Balise extends ElementModel {
    String START_TYPE_BALISE       = "DEPART";
    String END_TYPE_BALISE         = "ARRIVEE";
    String TYPE_PAS_OBLIGATOIRE    = "FACULTATIVE";
    String TYPE_OBLIGATOIRE        = "OBLIGATOIRE";
    String TYPE_PENALITY           = "PENALITE";
    String TYPE_BONUS              = "BONUS";
    String TYPE_ORDONNEE           = "ORDONNEE";
    String VAR_BALISE_NUM          = "NUM";
    String VAR_BALISE_TYPE         = "TYPE";
    String VAR_DESC                = "DESC";
    String VAR_BALISE_PENALITE     = "PENALITE";
    String VAR_PREFIX_BALISE_ORDER = "BALISEORDRE";
    String VAR_PREFIX              = "BALISE_";
    String CHOICE_TYPE             = "TYPEBALISES";

    String getDescription();

    String getNum();

    String getType();

    Date getPenalite();

    int getPoints();

    int getOrdre(int nrOrdre);

    void setNum(String num);

    void setType(String type);
}
