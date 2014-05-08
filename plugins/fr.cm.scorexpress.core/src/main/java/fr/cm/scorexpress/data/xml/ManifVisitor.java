package fr.cm.scorexpress.data.xml;

import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.data.node.*;

import static fr.cm.scorexpress.core.model.ColTableBuilder.createObjColTable;
import static fr.cm.scorexpress.core.model.ColTableUtils.parseType;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static fr.cm.scorexpress.core.model.impl.StepFactory.createStep;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ManifVisitor implements IManifVisitor {

    @Override
    public Object visiteCategories(final NodeCategories parent, final Object data) {
        if (data instanceof ObjManifestation) {
            final ObjManifestation manif = (ObjManifestation) data;
            for (int i = 0;
                 i < parent.getNumChild();
                 i++) {
                final ObjCategorie cat = (ObjCategorie) parent.getChild(i).accept(
                        this, data);
                manif.addCategorie(cat);
            }
        }
        if (data instanceof ObjStep) {
            final ObjStep step = (ObjStep) data;
            for (int i = 0;
                 i < parent.getNumChild();
                 i++) {
                final ObjCategorie cat = (ObjCategorie) parent.getChild(i).accept(this, data);
                step.addFiltreCategory(cat);
            }
        }
        return null;
    }

    @Override
    public Object visiteCategory(final NodeCategory parent, final Object data) {
        final AbstractGetInfo cat = new ObjCategorie(parent._label, parent._id);
        cat.setInfo("AGE", parent._age);
        return cat;
    }

    @Override
    public Object visiteTeam(final NodeTeam parent, final Object data) {
        final ObjTeam team = new ObjTeam();
        team.setId(parent._userId);
        team.setCategory(parent._categoryId);
        team.setInfo("LEADERNAME", parent._leadername);
        for (int i = 0;
             i < parent.getNumChild();
             i++) {
            final IUser user = (IUser) parent.getChild(i).accept(this, null);
            team.addUser(user);
        }
        return team;
    }

    @Override
    public Object visiteTeams(final NodeTeams parent, final Object data) {
        final IUsers manif = (IUsers) data;
        for (int i = 0;
             i < parent.getNumChild();
             i++) {
            final IUser team = (IUser) parent.getChild(i).accept(this, null);
            manif.addUser(team);
        }
        return null;
    }

    @Override
    public Object visiteUser(final NodeUser parent, final Object data) {
        final IUser user = new ObjUser();
        user.setInfo("FIRSTNAME", parent._firstname);
        user.setInfo("LASTNAME", parent._lastname);
        user.setId(parent._id);
        user.setInfo("SEXE", parent._sexe);
        loadObject(user, parent);
        return user;
    }

    @Override
    public Object visiteUsers(final NodeUsers parent, final Object data) {
        final IUsers manif = (IUsers) data;
        for (int i = 0;
             i < parent.getNumChild();
             i++) {
            final IUser user = (IUser) parent.getChild(i).accept(this, null);
            manif.addUser(user);
        }
        return null;
    }

    @Override
    public Object visiteChronos(final NodeChronos nodeChronos, final Object data) {
        final IUserChronos step = (IUserChronos) data;
        for (int i = 0;
             i < nodeChronos.getNumChild();
             i++) {
            final ObjUserChronos userChronos = (ObjUserChronos) nodeChronos.getChild(i).accept(this, null);
            step.addUserChronos(userChronos);
        }
        return null;
    }

    @Override
    public Object visiteTime(final NodeTime nodeTime, final Object data) {
        final ObjChrono chrono = new ObjChrono(nodeTime._id);
        chrono.setTemps(createDate(nodeTime._time));
        return chrono;
    }

    @Override
    public Object visiteChrono(final NodeChrono nodeChrono, final Object data) {
        final ObjUserChronos userChrono = new ObjUserChronos(nodeChrono._puce);
        userChrono.setDossard(nodeChrono._number);
        userChrono.setDepart(createDate(nodeChrono._start_time));
        userChrono.setArrivee(createDate(nodeChrono._end_time));
        for (int i = 0;
             i < nodeChrono.getNumChild();
             i++) {
            final ObjChrono chrono = (ObjChrono) nodeChrono.getChild(i).accept(this,
                                                                               null);
            userChrono.addChrono(chrono);
        }
        return userChrono;
    }

    @Override
    public Object visiteManif(final NodeManif parent, final Object data) {
        final ObjManifestation manif = new ObjManifestation(parent._name);
        manif.setDate(createDate(parent._date));
        manif.setDescription(parent._description);
        for (int i = 0;
             i < parent.getNumChild();
             i++) {
            parent.getChild(i).accept(this, manif);
        }
        return manif;
    }

    @Override
    public Object visiteStep(final NodeStep nodeStep, final Object data) {
        final ObjStep step = createStep(nodeStep._name);
        step.setCumulerSousEtape(nodeStep._cumulersousetape);
        step.setActif(nodeStep._calcul);
        step.setArretChrono(nodeStep._arretchrono);
        step.setOrdre(EMPTY + nodeStep._id);
        step.setBaliseDepart(nodeStep._balisedepart);
        step.setBaliseArrivee(nodeStep._balisearrivee);
        step.setInfo(ObjStep.VAR_DESCRIPTION, nodeStep._desc);
        step.setInfo(ObjStep.VAR_FILENAME_IMPORT, nodeStep._importfilename);
        step.setCategoryFilter(nodeStep._categoryfilter);
        step.setClassementInter(nodeStep._classementinter);
        step.setEpreuve(nodeStep._epreuve);
        step.setDateLastImport(createDate(nodeStep._lastimport));
        step.setImportAuto(nodeStep._importauto);
        step.setPenalitySaisie(nodeStep._penalityseizure);
        loadObject(step, nodeStep);
        final AbstractSteps parent = (AbstractSteps) data;
        for (int i = 0;
             i < nodeStep.getNumChild();
             i++) {
            nodeStep.getChild(i).accept(this, step);
        }
        parent.addStep(step);
        return null;
    }

    @Override
    public Object visiteStation(final NodeStation station, final Object data) {
        final ObjBalise balise = BaliseFactory.createBalise(station._id, station._type, station._definition);
        balise.setPenaliteStr(station._time);
        balise.setInfo(Balise.VAR_PREFIX_BALISE_ORDER + 1, station._order1);
        balise.setInfo(Balise.VAR_PREFIX_BALISE_ORDER + 2, station._order2);
        balise.setInfo(Balise.VAR_PREFIX_BALISE_ORDER + 3, station._order3);
        balise.setInfo(Balise.VAR_PREFIX_BALISE_ORDER + 4, station._order4);
        loadObject(balise, station);
        final AbstractBalises step = (AbstractBalises) data;
        step.addBalise(balise);
        return null;
    }

    @Override
    public Object visitePenalities(final NodePenalities parent, final Object data) {
        final ObjStep step = (ObjStep) data;
        for (int i = 0;
             i < parent.getNumChild();
             i++) {
            final ObjPenalite penalite = (ObjPenalite) parent.getChild(i).accept(this, null);
            step.addPenalite(penalite);
        }
        return null;
    }

    @Override
    public Object visitePenality(final NodePenality nPenalite, final Object data) {
        final ObjPenalite penalite = new ObjPenalite(nPenalite._name);
        penalite.setActivate(nPenalite._activate);
        penalite.setBaliseObjPenalityActivate(nPenalite._penalitebalise);
        penalite.setDisqualifierConcurrent(nPenalite._disqualify);
        penalite.setDureeMaxiStr(nPenalite._timemaxi);
        penalite.setDureeMiniStr(nPenalite._timemini);
        penalite.setPenaliteStr(nPenalite._time);
        penalite.setEchellePenaliteStr(nPenalite._timescale);
        penalite.setNbBalisesMiniStr(EMPTY + nPenalite._nbbalisemini);
        penalite.setNbPointsBaliseMiniStr(EMPTY + nPenalite._nbbalisepointmini);
        penalite.setLib(nPenalite._lib);
        penalite.setUnite(nPenalite._unite);
        penalite.setTypePenalite(nPenalite._type);
        return penalite;
    }

    @Override
    public Object visiteInscribe(final NodeInscribe inscrib, final Object data) {
        final ObjDossard dossard = new ObjDossard(inscrib._number);
        dossard.setAbandon(inscrib._abandon);
        dossard.setBonification(createDate(inscrib._bonification));
        dossard.setDisqualifie(inscrib._disqualify);
        dossard.setPenality(createDate(inscrib._penality));
        dossard.setPuce(inscrib._puce);
        dossard.setIdUser(EMPTY + inscrib._userId);
        dossard.setCategory(inscrib._categorie);
        dossard.setTemps(createDate(inscrib._temps));
        loadObject(dossard, inscrib);
        final IDossards step = (IDossards) data;
        step.addDossard(dossard);
        return null;
    }

    private static void loadObject(final AbstractGetInfo<?> data, final NodeFactory node) {
        if (node.getInfos() != null) {
            final String[] keys = node.getInfos().keySet().toArray(new String[node.getInfos().keySet().size()]);
            for (final String key : keys) {
                data.setInfo(key + EMPTY, node.getInfos().get(key));
            }
        }
    }

    @Override
    public Object visiteNode(final Node parent, final Object data) {
        return null;
    }

    @Override
    public Object visiteColumn(final NodeColumn node, final Object data) {
        return createObjColTable(node._field,
                                 node._title,
                                 node._width,
                                 parseType(node._type),
                                 EMPTY,
                                 EMPTY,
                                 node._editable,
                                 false,
                                 false,
                                 node._choice,
                                 !node._show,
                                 EMPTY,
                                 node._align);
    }

    @Override
    public Object visiteWatchList(final NodeWatchlist node, final Object data) {
        final ObjConfiguration configuration = ((ObjManifestation) data).getConfiguration();
        final ObjConfig config = new ObjConfig(ConfigType.valueOf(node._name), node._title);
        config.setId(node._id);
        config.setTitre(node._title);
        for (int i = 0;
             i < node.getNumChild();
             i++) {
            final ColTable colTable = (ColTable) node.getChild(i).accept(this, null);
            config.addColTable(colTable);
        }
        configuration.addConfig(config);
        return null;
    }

}
