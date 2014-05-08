package fr.cm.scorexpress.core.model;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.ColTableBuilder.createObjColTable;
import static fr.cm.scorexpress.core.model.ColTableType.BOOLEAN;
import static fr.cm.scorexpress.core.model.ColTableType.DATE;
import static fr.cm.scorexpress.core.model.ConfigType.*;
import static fr.cm.scorexpress.core.model.ObjChrono.*;
import static fr.cm.scorexpress.core.model.ObjDossard.*;
import static fr.cm.scorexpress.core.model.ObjResultat.*;
import static fr.cm.scorexpress.core.model.i18n.Messages.i18n;
import static fr.cm.scorexpress.data.UserChronosLoader.VAR_CSV_CATEGORIE;

@SuppressWarnings({"MagicNumber"})
public class ObjConfig extends IData {
    private static final long                serialVersionUID = -1154607370531584813L;
    private final        ArrayList<ColTable> colTable         = newArrayList();
    private final        Collection<String>  booleans         = newArrayList();

    private final Collection<ColTable>  colTableDynamic = newArrayList();
    private final Map<String, ColTable> colTableAll     = Maps.newHashMap();
    private final ConfigType nom;

    private String lib;
    private String  titre = null;
    private Integer id    = null;

    public ObjConfig(final ConfigType nom, final String lib) {
        this.nom = nom;
        this.lib = lib;
    }

    public boolean addColTable(final ColTable c) {
        if (c.getType() != null && ColTableUtils.isBooleanType(c)) {
            booleans.add(c.getChamp());
        }
        colTableAll.put(c.getChamp(), c);
        return colTable.add(c);
    }

    public boolean removeColTable(final ColTable c) {
        colTableAll.remove(c);
        booleans.remove(c.getChamp());
        return colTable.remove(c);
    }

    public boolean addColTableDynamic(final ColTable c) {
        colTableAll.put(c.getChamp(), c);
        return colTableDynamic.add(c);
    }

    public boolean removeColTableDynamic(final ColTable c) {
        colTableAll.remove(c);
        return colTableDynamic.remove(c);
    }

    public ArrayList<ColTable> getColTable() {
        return colTable;
    }

    public ArrayList<ColTable> getColTableAll() {
        final ArrayList<ColTable> res = newArrayList(colTable);
        res.addAll(colTableDynamic);
        return res;
    }

    public String getLib() {
        return lib;
    }

    public ConfigType getNom() {
        return nom;
    }

    public void setLib(final String lib) {
        this.lib = lib;
    }

    public boolean equals(final Object object) {
        return nom.equals(((ObjConfig) object).nom);
    }

    @Override
    public String getPrefix() {
        return "CONFIG";
    }

    @Override
    public Object getInfoLocal(final String attribut) {
        return null;
    }

    @Override
    public boolean setInfoLocal(final String attribut, final Object val) {
        return false;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(final String titre) {
        this.titre = titre;
    }

    @Override
    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public boolean isBoolean(final String property) {
        return booleans.contains(property);
    }

    public void clearColDynamic() {
        colTableDynamic.clear();
    }

    public static ObjConfig create(final ConfigType configType) {
        if (RESULTATS.equals(configType)) {
            return createResultat();
        } else if (RESULTATS_INTER.equals(configType)) {
            return createResultatInter();
        } else if (DOSSARD_PARTICIPANTS.equals(configType)) {
            return createDossardParticipant();
        } else if (BALISES.equals(configType)) {
            return createBalise();
        } else if (CHRONOS.equals(configType)) {
            return createChronos();
        } else if (IMPORT_PARTICIPANTS.equals(configType)) {
            return createImportParticipant();
        } else if (INFOSPORTIDENTS.equals(configType)) {
            return createInfoSportIdent();
        } else if (RESULTATS_EXPERT.equals(configType)) {
            return createResultatExpert();
        } else if (RESULTATS_EXPERT_INTER.equals(configType)) {
            return createResultatExpertInter();
        } else if (PENALITY.equals(configType)) {
            return createPenality();
        }
        return null;
    }

    public boolean isTemplate() {
        if (RESULTATS.equals(nom)) {
            return false;
        } else if (RESULTATS_INTER.equals(nom)) {
            return false;
        } else if (DOSSARD_PARTICIPANTS.equals(nom)) {
            return false;
        } else if (BALISES.equals(nom)) {
            return true;
        } else if (CHRONOS.equals(nom)) {
            return true;
        } else if (IMPORT_PARTICIPANTS.equals(nom)) {
            return true;
        } else if (INFOSPORTIDENTS.equals(nom)) {
            return true;
        } else if (RESULTATS_EXPERT.equals(nom)) {
            return false;
        } else if (RESULTATS_EXPERT_INTER.equals(nom)) {
            return false;
        } else if (PENALITY.equals(nom)) {
            return true;
        }
        return false;
    }

    private static ObjConfig createPenality() {
        final ObjConfig config = new ObjConfig(PENALITY, i18n("ObjConfig.SAISIE_PENALITES"));
        final ColTable cNum = createObjColTable(VAR_DOSSARD_NUM, i18n("ObjConfig.DOSSARD"), 90);
        final ColTable cFirstName = createObjColTable("S.FIRSTNAME", i18n("ObjConfig.NOM"), 120);
        config.addColTable(cNum);
        config.addColTable(cFirstName);
        return config;
    }

    private static ObjConfig createChronos() {
        final ObjConfig config = new ObjConfig(CHRONOS, i18n("ObjConfig.GESTIONNAIRE_CHRONOS"));
        final ColTable cCodeEtape =
                new ColTableBuilder(VAR_CHRONO_DOSSARD, i18n("ObjConfig.ETAPE")).withWidth(70).create();
        final ColTable cPuce =
                new ColTableBuilder(VAR_CHRONO_DOSSARD_SPORTIDENT, i18n("ObjConfig.NO_PUCE")).withWidth(70).create();
        final ColTable cBalise =
                new ColTableBuilder(VAR_CHRONO_NUMBALISE, i18n("ObjConfig.NO_PUCE")).withWidth(70).create();
        final ColTable cTemps = new ColTableBuilder(VAR_CHRONO_HEURE, i18n("ObjConfig.NO_PUCE")).withWidth(70).create();
        config.addColTable(cCodeEtape);
        config.addColTable(cPuce);
        config.addColTable(cBalise);
        config.addColTable(cTemps);
        return config;
    }

    private static ObjConfig createInfoSportIdent() {
        final ObjConfig config = new ObjConfig(INFOSPORTIDENTS, i18n("ObjConfig.GESTION_INFOS_RECUPEREES"));
        final ColTable cOrdre = createObjColTable("ORDRE", i18n("ObjConfig.ORDRE"), 40);
        final ColTable cDossard = createObjColTable("DOSSARD", i18n("ObjConfig.DOSSARD"), 70);
        config.addColTable(cOrdre);
        config.addColTable(cDossard);
        return config;
    }

    private static ObjConfig createResultat() {
        final ObjConfig config = new ObjConfig(RESULTATS, i18n("ObjConfig.RESULTATS"));
        final ColTable cPlace = createObjColTable(VAR_RESULTAT_PLACE, i18n("ObjConfig.CLASSEMENT"), 70);
        final ColTable cNum = createObjColTable(VAR_DOSSARD_NUM, i18n("ObjConfig.DOSSARD"), 70);
        final ColTable cFirstName = createObjColTable("S.FIRSTNAME", i18n("ObjConfig.NOM"), 120);
        final ColTable cTemps = createObjColTable(VAR_RESULTAT_TEMPS, i18n("ObjConfig.TEMPS"), 90);
        final ColTable cTempsEpreuve = createObjColTable(VAR_TEMPS_CHRONO, i18n("ObjConfig.TEMPS_EPREUVE"), 90);
        final ColTable cPenality = createObjColTable(VAR_RESULTAT_PENALITE, i18n("ObjConfig.PENALITES"), 90);
        final ColTable cEcart = createObjColTable(VAR_RESULTAT_ECART, i18n("ObjConfig.ECART"), 90);
        final ColTable cCategory = createObjColTable(VAR_DOSSARD_CATEGORIE, i18n("ObjConfig.CATEGORIE"), 100);
        config.addColTable(cPlace);
        config.addColTable(cNum);
        config.addColTable(cFirstName);
        config.addColTable(cTemps);
        config.addColTable(cTempsEpreuve);
        config.addColTable(cPenality);
        config.addColTable(cEcart);
        config.addColTable(cCategory);
        return config;
    }

    private static ObjConfig createResultatExpert() {
        final ObjConfig config = new ObjConfig(RESULTATS_EXPERT, i18n("ObjConfig.RESULTATS"));
        final ColTable cPlace = createObjColTable(VAR_RESULTAT_PLACE, i18n("ObjConfig.CLASSEMENT"), 70);
        final ColTable cNum = createObjColTable(VAR_DOSSARD_NUM, i18n("ObjConfig.DOSSARD"), 70);
        final ColTable cFirstName = createObjColTable("S.FIRSTNAME", i18n("ObjConfig.NOM"), 120);
        final ColTable cTemps = createObjColTable(VAR_RESULTAT_TEMPS, i18n("ObjConfig.TEMPS"), 90);
        final ColTable cTempsParcours = createObjColTable(VAR_TEMPSPARCOURS, i18n("ObjConfig.TEMPS_PARCOURS"), 90);
        final ColTable cPenality = createObjColTable(VAR_RESULTAT_PENALITE, i18n("ObjConfig.CUMUL_PENALITE"), 90);
        final ColTable cPenalityBalise = createObjColTable(VAR_PENALITE_BALISE, i18n("ObjConfig.PENALITE_BALISE"), 90);
        final ColTable cArretChronos = createObjColTable(VAR_TEMPSARRETCHRONO, i18n("ObjConfig.ARRET_CHRONO"), 90);
        final ColTable cBonif = createObjColTable(VAR_BONIFICATION, i18n("ObjConfig.BONIFICATION"), 90);
        final ColTable cEcart = createObjColTable(VAR_RESULTAT_ECART, i18n("ObjConfig.ECART"), 90);
        final ColTable cCategory = createObjColTable(VAR_DOSSARD_CATEGORIE, i18n("ObjConfig.CATEGORIE"), 100);
        final ColTable cBalisesManquees =
                createObjColTable(VAR_RESULTAT_BALISESMANQUEES, i18n("ObjConfig.BALISE_MANQUEES"), 90);
        config.addColTable(cPlace);
        config.addColTable(cNum);
        config.addColTable(cFirstName);
        config.addColTable(cTemps);
        config.addColTable(cTempsParcours);
        config.addColTable(cPenality);
        config.addColTable(cPenalityBalise);
        config.addColTable(cArretChronos);
        config.addColTable(cBonif);
        config.addColTable(cEcart);
        config.addColTable(cCategory);
        config.addColTable(cBalisesManquees);
        return config;
    }

    private static ObjConfig createResultatExpertInter() {
        final ObjConfig config = new ObjConfig(RESULTATS_EXPERT_INTER, i18n("ObjConfig.RESULTATS"));
        final ColTable cTemps = createObjColTable(VAR_RESULTAT_TEMPS, i18n("ObjConfig.TEMPS"), 90);
        final ColTable cBalisesOk = createObjColTable(VAR_RESULTAT_BALISES_OK, i18n("ObjConfig.BALISE_OK"), 90);
        config.addColTable(cTemps);
        config.addColTable(cBalisesOk);
        return config;
    }

    private static ObjConfig createResultatInter() {
        final ObjConfig config = new ObjConfig(RESULTATS_INTER, i18n("ObjConfig.RESULTATS"));
        final ColTable cPlace = createObjColTable(VAR_RESULTAT_PLACE, i18n("ObjConfig.CLASSEMENT"), 70);
        final ColTable cTemps = createObjColTable(VAR_RESULTAT_TEMPS, i18n("ObjConfig.TEMPS"), 90);
        final ColTable cPenality = createObjColTable(VAR_RESULTAT_PENALITE, i18n("ObjConfig.PENALITES"), 90);
        config.addColTable(cPlace);
        config.addColTable(cTemps);
        config.addColTable(cPenality);
        return config;
    }

    private static ObjConfig createBalise() {
        final ObjConfig config = new ObjConfig(BALISES, i18n("ObjConfig.GESTION_BALISES"));
        config.addColTable(createObjColTable(Balise.VAR_BALISE_NUM, i18n("ObjConfig.TEMPS"), 90));
        config.addColTable(
                new ColTableBuilder(Balise.VAR_BALISE_TYPE, i18n("ObjConfig.TYPE")).withChoice(Balise.CHOICE_TYPE)
                                                                                   .withWidth(90).create());
        config.addColTable(createObjColTable(Balise.VAR_BALISE_PENALITE, i18n("ObjConfig.PENALITES"), 90));
        config.addColTable(createObjColTable(Balise.VAR_DESC, i18n("ObjConfig.DESCRIPTION"), 90));
        for (int i = 1; i < 4; i++) {
            final ColTable cOrdre =
                    createObjColTable(Balise.VAR_PREFIX_BALISE_ORDER + i, i18n("ObjConfig.ORDRE") + i, 50);
            config.addColTable(cOrdre);
        }
        return config;
    }

    private static ObjConfig createDossardParticipant() {
        final ObjConfig config = new ObjConfig(DOSSARD_PARTICIPANTS, i18n("ObjConfig.SAISIE_UTILISATEURS"));
        config.addColTable(createObjColTable(VAR_DOSSARD_NUM, i18n("ObjConfig.DOSSARD"), 70));
        config.addColTable(createObjColTable("S.FIRSTNAME", i18n("ObjConfig.NOM"), 120));
        config.addColTable(createObjColTable("S.LASTNAME", i18n("ObjConfig.PRENOM"), 110));
        config.addColTable(new ColTableBuilder(VAR_DOSSARD_CATEGORIE, i18n("ObjConfig.CATEGORIE")).withWidth(100)
                                                                                                  .withChoice(
                                                                                                          "CATEGORIES")
                                                                                                  .create());
        config.addColTable(
                new ColTableBuilder(VAR_DOSSARD_ABANDON, i18n("ObjConfig.ABANDON")).withWidth(100).withType(BOOLEAN)
                                                                                   .create());
        config.addColTable(new ColTableBuilder(VAR_DOSSARD_DISQUALIFIER, i18n("ObjConfig.HORS-COURSE")).withWidth(100)
                                                                                                       .withType(
                                                                                                               BOOLEAN)
                                                                                                       .create());
        config.addColTable(new ColTableBuilder(VAR_DOSSARD_BONIFICATION, i18n("ObjConfig.BONIFICATIONS")).withWidth(100)
                                                                                                         .withType(DATE)
                                                                                                         .create());
        config.addColTable(
                new ColTableBuilder(VAR_DOSSARD_PENALITY, i18n("ObjConfig.PENALITES")).withWidth(100).withType(DATE)
                                                                                      .create());
        config.addColTable(createObjColTable("S.NOTE", i18n("ObjConfig.NOTE"), 100));
        return config;
    }

    private static ObjConfig createImportParticipant() {
        final ObjConfig config = new ObjConfig(IMPORT_PARTICIPANTS, i18n("ObjConfig.SAISIE_UTILISATEURS"));
        config.addColTable(
                new ColTableBuilder(VAR_DOSSARD_PREFIX + VAR_DOSSARD_NUM, i18n("ObjConfig.DOSSARD")).withWidth(70)
                                                                                                    .withChoice("0")
                                                                                                    .create());
        config.addColTable(new ColTableBuilder("PUCE", i18n("ObjConfig.PUCE")).withWidth(120).withChoice("1").create());
        config.addColTable(
                new ColTableBuilder("S.FIRSTNAME", i18n("ObjConfig.NOM")).withWidth(120).withChoice("3").create());
        config.addColTable(
                new ColTableBuilder("S.LASTNAME", i18n("ObjConfig.PRENOM")).withWidth(110).withChoice("4").create());
        config.addColTable(
                new ColTableBuilder(VAR_CSV_CATEGORIE, i18n("ObjConfig.CATEGORIE")).withWidth(100).withChoice("19")
                                                                                   .create());
        return config;
    }

    public ColTable getColTable(final String property) {
        return colTableAll.get(property);
    }
}
