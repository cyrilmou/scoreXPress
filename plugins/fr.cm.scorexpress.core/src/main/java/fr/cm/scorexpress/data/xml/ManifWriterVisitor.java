/**
 *
 */
package fr.cm.scorexpress.data.xml;

import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Iterator;

import static fr.cm.scorexpress.core.model.Balise.VAR_PREFIX_BALISE_ORDER;

public class ManifWriterVisitor implements ElementModelVisitor {

    private final Document document;
    private final Element  root;

    public ManifWriterVisitor(final Document document) {
        this.document = document;
        root = document.getDocumentElement();
    }

    @Override
    public Object visite(final ObjManifestation manif, final Object data) {
        root.setAttribute("name", manif.getNom());
        root.setAttribute("date", manif.getDate().toString());
        root.setAttribute("description", manif.getDecription());
        final Iterator<ObjCategorie> iterCategory = manif.getCategories().iterator();
        final Iterator<ObjStep> iterStep = manif.getSteps().iterator();
        final Element categories = document.createElement("Categories");
        root.appendChild(categories);
        while (iterCategory.hasNext()) {
            final Node element = (Node) iterCategory.next().accept(this, null);
            categories.appendChild(element);
        }
        manif.getConfiguration().accept(this, root);
        while (iterStep.hasNext()) {
            final Node element = (Node) iterStep.next().accept(this, null);
            root.appendChild(element);
        }
        return root;
    }

    @Override
    public Object visite(final ObjCategorie category, final Object data) {
        final Element cat = document.createElement("Category");
        set(cat, "id", "" + category.getId());
        set(cat, "label", category.getNom());
        for (final InfoDivers infoDivers : category.getInfoDiverses()) {
            if (!infoDivers.isTemp()) {
                cat.setAttribute(infoDivers.getAttribut().toLowerCase(), infoDivers
                        .getInfoStr());

            } else {
                System.out.println("temp " + infoDivers.getAttribut());
            }
        }
        return cat;
    }

    @Override
    public Object visitStep(final ObjStep step, final Object data) {
        final Element stepElem = document.createElement("Step");
        set(stepElem, "id", step.getOrdre());
        set(stepElem, "name", step.getLib());
        set(stepElem, "desc", step.getInfo(ObjStep.VAR_DESCRIPTION));
        set(stepElem, "balisedepart", step.getBaliseDepart());
        set(stepElem, "balisearrivee", step.getBaliseArrivee());
        set(stepElem, "cumulersousetape", step.isCumulerSousEtape());
        set(stepElem, "importfilename", step.getImportFileName());
        set(stepElem, "categoryfilter", step.getCategoryFilter());
        set(stepElem, "classementinter", step.isClassementInter());
        set(stepElem, "calcul", step.isActif());
        set(stepElem, "arretchrono", step.isArretChrono());
        set(stepElem, "epreuve", step.isEpreuve());
        set(stepElem, "lastimport", step.getDateLastImport());
        set(stepElem, "importauto", step.isImportAuto());
        set(stepElem, "penalityseizure", step.isPenalitySaisie());
        // stepElem.setAttribute("startdate", step.get);
        for (final IData objStep : step.getSteps()) {
            final Node element = (Node) objStep.accept(this, null);
            stepElem.appendChild(element);
        }
        for (final IData objDossard : step.getDossards()) {
            final Node element = (Node) objDossard.accept(this, null);
            stepElem.appendChild(element);
        }
        for (final Balise objBalise : step.getBalises()) {
            final Node element = (Node) objBalise.accept(this, null);
            stepElem.appendChild(element);
        }
        final Node penalities = document.createElement("Penalities");
        stepElem.appendChild(penalities);
        for (final IData objPenalite : step.getPenalites()) {
            final Node element = (Node) objPenalite.accept(this, null);
            penalities.appendChild(element);
        }
        final Node chronos = document.createElement("Chronos");
        stepElem.appendChild(chronos);
        for (final IData objUserChronos : step.getUserChronos()) {
            final Node element = (Node) objUserChronos.accept(this,
                                                              null);
            chronos.appendChild(element);
        }
        final Node categories = document.createElement("Categories");
        stepElem.appendChild(categories);
        for (final IData objCategorie : step.getFiltreCategory()) {
            final Node element = (Node) objCategorie.accept(this, null);
            categories.appendChild(element);
        }
        return stepElem;
    }

    @Override
    public Object visite(final ObjConfiguration configuration, final Object data) {
        final Node root = (Node) data;
        for (final ObjConfig objConfig : configuration.getConfigs()) {
            final Element watchList = document.createElement("Watchlist");
            set(watchList, "id", "" + objConfig.getId());
            set(watchList, "title", objConfig.getLib());
            set(watchList, "name", objConfig.getNom());
            for (final ColTable objColTable : objConfig.getColTableAll()) {
                if (!objColTable.isTmp()) {
                    final Element column = document.createElement("Column");
                    watchList.appendChild(column);
                    set(column, "field", objColTable.getChamp());
                    set(column, "width", "" + objColTable.getWidth());
                    set(column, "show", !objColTable.isMasque());
                    set(column, "title", objColTable.getLib());
                    set(column, "type", objColTable.getType().getLabel());
                    set(column, "choice", objColTable.getChoix());
                }
            }
            root.appendChild(watchList);
        }
        return null;
    }

    @Override
    public Object visitBalise(final ObjBalise balise, final Object data) {
        final Element station = document.createElement("Station");
        set(station, "id", balise.getNum());
        set(station, "definition", balise.getDescription());
        set(station, "time", balise.getPenaliteStr());
        set(station, "type", balise.getType());
        set(station, "order1", balise.getInfoStr(VAR_PREFIX_BALISE_ORDER + 1));
        set(station, "order2", balise.getInfoStr(VAR_PREFIX_BALISE_ORDER + 2));
        set(station, "order3", balise.getInfoStr(VAR_PREFIX_BALISE_ORDER + 3));
        set(station, "order4", balise.getInfoStr(VAR_PREFIX_BALISE_ORDER + 4));
        return station;
    }

    @Override
    public Object visite(final ObjChoix parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ObjChronoArrivee parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ObjChronoDepart parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ColTable parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ObjConfig parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ObjPenalite penalite, final Object data) {
        final Element elemPenalite = document.createElement("Penality");
        set(elemPenalite, "time", penalite.getPenaliteStr());
        set(elemPenalite, "timemaxi", penalite.getDureeMaxiStr());
        set(elemPenalite, "timemini", penalite.getDureeMiniStr());
        set(elemPenalite, "timescale", penalite.getEchellePenaliteStr());
        set(elemPenalite, "lib", penalite.getLib());
        set(elemPenalite, "type", penalite.getTypePenalite());
        set(elemPenalite, "unite", penalite.getUnite());
        set(elemPenalite, "nbbalisemini", penalite.getNbBalisesMini());
        set(elemPenalite, "nbbalisepointmini", penalite.getNbPointsBaliseMini());
        return elemPenalite;
    }

    private void set(final Element element, final String attribut, final Object value) {
        if (value != null && !("" + value).equals("null")) {
            element.setAttribute(attribut, "" + value);
        }
    }

    @Override
    public Object visite(final ObjResultat parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final ObjDossard dossard, final Object data) {
        final Element elemDossard = document.createElement("Inscribe");
        set(elemDossard, "userId", dossard.getIdUser());
        set(elemDossard, "number", dossard.getNum());
        set(elemDossard, "puce", dossard.getPuce());
        set(elemDossard, "abandon", dossard.isAbandon());
        set(elemDossard, "bonification", dossard.getBonification());
        set(elemDossard, "disqualify", dossard.isDisqualifie());
        set(elemDossard, "penality", dossard.getPenality());
        set(elemDossard, "team", dossard.isTeam());
        set(elemDossard, "categorie", dossard.getCategory());
        set(elemDossard, "temps", dossard.getTemps());
        for (final InfoDivers infoDivers : dossard.getInfoDiverses()) {
            if (!infoDivers.isTemp()) {
                final String value = infoDivers.getInfoStr();
                final String property = infoDivers.getAttribut().toLowerCase();
                elemDossard.setAttribute(property, value);

            } else {
                System.out.println("temp " + infoDivers.getAttribut());
            }
        }
        return elemDossard;
    }

    @Override
    public Object visite(final ObjSaisiePenalite parent, final Object data) {
        return null;
    }

    @Override
    public Object visitTeam(final ObjTeam team, final Object data) {
        final Element elemTeam = document.createElement("Team");
        set(elemTeam, "userId", team.getId());
        set(elemTeam, "categoryId", team.getCategory());
        for (final InfoDivers infoDivers : team.getInfoDiverses()) {
            if (!infoDivers.isTemp()) {
                elemTeam.setAttribute(infoDivers.getAttribut().toLowerCase(), infoDivers
                        .getInfoStr());

            } else {
                System.out.println("temp " + infoDivers.getAttribut());
            }
        }
        for (final IData iUser : team.getUsers()) {
            final Node element = (Node) iUser.accept(this, null);
            elemTeam.appendChild(element);
        }
        return elemTeam;
    }

    @Override
    public Object visitUser(final ObjUser user, final Object data) {
        final Element elemUser = document.createElement("User");
        set(elemUser, "id", user.getId());
        for (final InfoDivers infoDivers : user.getInfoDiverses()) {
            if (!infoDivers.isTemp()) {
                elemUser.setAttribute(infoDivers.getAttribut().toLowerCase(), infoDivers
                        .getInfoStr());

            } else {
                System.out.println("temp " + infoDivers.getAttribut());
            }
        }
        return elemUser;
    }

    @Override
    public Object visitUserChronos(final ObjUserChronos chrono, final Object data) {
        final Element elemChrono = document.createElement("Chrono");
        elemChrono.setAttribute("number", chrono.getDossard());
        elemChrono.setAttribute("puce", chrono.getPuce());
        elemChrono.setAttribute("end_time", chrono.getChronoArrivee()
                                                  .getTemps().toString());
        elemChrono.setAttribute("start_time", chrono.getChronoDepart()
                                                    .getTemps().toString());
        for (final IData objChrono : chrono.getChronos()) {
            final Node element = (Node) objChrono.accept(this, null);
            if (element != null) {
                elemChrono.appendChild(element);
            }
        }
        return elemChrono;
    }

    @Override
    public Object visite(final ObjChrono chrono, final Object data) {
        final Element time = document.createElement("Time");
        set(time, "id", chrono.getNumBalise());
        set(time, "time", chrono.getTemps());
        return time;
    }

    @Override
    public Object visite(final InfoDivers parent, final Object data) {
        return null;
    }

    @Override
    public Object visite(final IData parent, final Object data) {
        return null;
    }

    public Element getRoot() {
        return root;
    }
}
