package fr.cm.scorexpress.core.model;

import java.util.AbstractList;
import java.util.Collection;

import static fr.cm.scorexpress.core.model.i18n.Messages.i18n;

public interface Step extends ElementModel {
    String VAR_LIB_STEP              = "LIB";
    String VAR_ACTIF                 = "ACTIF";
    String VAR_ORDRE                 = i18n("ObjEtape.4");
    String VAR_ARRETCHRONO           = "ARRETCHRONO";
    String VAR_BALISE_DEPART         = "BALISEDEPART";
    String VAR_BALISE_ARRIVEE        = "BALISEARRIVEE";
    String VAR_DESCRIPTION           = "DESC";
    String VAR_FILENAME_IMPORT       = "FILENAMEIMPORT";
    String VAR_FILTER_CATEGORY       = "FILTER_CATEGORY";
    String VAR_DATE_LAST_IMPORT      = "DATELASTIMPORT";
    String VAR_TITLE_PRINT           = "TITLEPRINT";
    String VAR_PREFIX                = i18n("ObjEtape.10");
    String VAR_CUMUL_SOUS_ETAPE      = "CUMULSOUSETAPE";
    String VAR_PENALITY_SAISIE       = "PENALITYSAISIE";
    String VAR_EPREUVE               = "EPREUVE";
    String VAR_SUB_STEPS             = "SubSteps";
    String VAR_STEP_PENALITIES       = "Penalities";
    String VAR_STEP_CLASSEMENT_INTER = "CLASSEMENT_INTER";
    String VAR_STEP_CATEGORIE_FILTER = "CATEGORIE_FILTER";
    String VAR_IMPORT_AUTO           = "IMPORT_AUTO";
    String VAR_STEP_BALISES          = "Balises";
    String VAR_STEP_GROUP            = "stepGroup";

    AbstractList<ObjDossard> getDossards();

    Collection<ObjResultat> getResultats();

    boolean isArretChrono();

    Collection<ObjUserChronos> getUserChronos();

    boolean isCumulerSousEtape();

    boolean isEpreuve();

    boolean isClassementInter();

    boolean isImportAuto();

    boolean isPenalitySaisie();

    String getLib();

    boolean isActif();

    void setActif(boolean actif);

    void setCumulerSousEtape(boolean cumulSubSteps);

    void setBaliseArriveeGenerale(boolean generalEnd);

    void setBaliseDepartGeneral(boolean generalStart);

    void setArretChrono(boolean stopTime);
}
