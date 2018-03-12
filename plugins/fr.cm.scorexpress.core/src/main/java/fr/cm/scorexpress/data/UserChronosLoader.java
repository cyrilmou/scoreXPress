package fr.cm.scorexpress.data;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.DateFactory;
import fr.cm.scorexpress.core.model.impl.DateUtils;
import fr.cm.scorexpress.core.model.impl.ObjStep;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.ObjDossard.VAR_DOSSARD_CATEGORIE;

/**
 * <p>
 * Title: ChronosRAID
 * </p>
 * <p>
 * Description: Programme de chronométrage de RAID multisport
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Jurazimut
 * </p>
 *
 * @author Moutenet Cyril
 * @version 1.0
 */

public class UserChronosLoader {
    private final Collection<ObjUserChronos> userChronosList = new ArrayList<ObjUserChronos>();
    private final Collection<String> removedDossards = new ArrayList<String>();

    private static final String SEPARATEUR = ";";
    private final String fileName;
    public static final String VAR_NUM = "N° dép.";
    public static final String VAR_NOM = "Nom";
    public static final String VAR_PRENOM = "Prénom";
    public static final String VAR_SEXE = "S";
    public static final String VAR_PUCE = "Puce";
    public static final String VAR_CSV_CATEGORIE = "Long";
    public static final String VAR_VILLE = "Ville";
    static final HashMap<String, String> instances = new HashMap<String, String>();

    private UserChronosLoader(final String fileName) {
        this.fileName = fileName;
    }

    public static UserChronosLoader createUserChrono(final String fileName) {
        instances.put(fileName, fileName);
        return new UserChronosLoader(fileName);
    }

    private String getNext(final String ligne) {
        final int index = ligne.indexOf(SEPARATEUR, 0);
        if (index != -1) {
            return ligne.substring(0, index);
        }
        return ligne;
    }

    private String nextSeparator(String ligne) {
        final int index = ligne.indexOf(SEPARATEUR, 0);
        if (index != -1) {
            ligne = ligne.substring(index + 1, ligne.length());
        }
        return ligne;
    }

    private boolean hasMore(final String ligne) {
        return ligne.indexOf(SEPARATEUR, 0) != -1;
    }

    static String getLastFilename(final String numEtape) {
        return instances.get(numEtape);
    }

    public synchronized void loadFile(final ObjConfig config) {
        try {
            System.out.println("Chargement de " + fileName);
            final Date d = new Date();
            final File f = new File(fileName);
            final InputStreamReader in = new InputStreamReader(new FileInputStream(f));
            final BufferedReader dis = new BufferedReader(in);
            String ligne;
            // Préparation des colonnes d'informations à récupérer dans le
            // fichier
            final List<Integer> colonneInfos = newArrayList();
            final List<String> colonneName = newArrayList();
            for (int nrLigneCSV = 0; (ligne = dis.readLine()) != null; nrLigneCSV++) {
                if (nrLigneCSV > 0) { // Titre
                    final ObjUserChronos userChronos = new ObjUserChronos("0");
                    final ArrayList<ObjChrono> chronos = new ArrayList<ObjChrono>();
                    userChronosList.add(userChronos);
                    String puce = null;
                    final ObjChrono arrivee = new ObjChronoArrivee(DateFactory.createDate(0));
                    for (int arg = 0; hasMore(ligne); arg++) {
                        String val = getNext(ligne);
                        ligne = nextSeparator(ligne);
                        // System.out.println("" + val);
                        final int indexCol = colonneInfos.indexOf(new Integer(arg));
                        if (indexCol != -1 && indexCol < colonneName.size()) {
                            if (!val.isEmpty() && val.charAt(0) == '"') {
                                if (val.length() == 1) {
                                    val = "";
                                } else {
                                    val = val.substring(1, val.length() - 1);
                                }
                            }
                            final String property = colonneName.get(indexCol);
                            userChronos.setInfo(property, val);
                        }
                        if (arg == 0) {
                            if (val.isEmpty()) {
                                System.out.println("Number exception ligne " + nrLigneCSV);
                            } else {
                                userChronos.setDossard(val);
                            }
                        } else if (arg == 1) {
                            puce = val;
                            userChronos.setPuce(puce);
                        } else if (arg == 4) {
                            if (!val.isEmpty()) {
                                userChronos.setInfoDiverse("MULTI_ID", val);
                            }
                        } else if (arg == 9) {
                            chronos.add(new ObjChronoDepart(DateFactory.createDate("0:00:00")));
                        } else if (arg == 11) {
                            arrivee.setTemps(DateFactory.createDate(val));
                            userChronos.setPuce(puce);
                            chronos.add(arrivee);
                        } else if (arg >= 46) {
                            final String tempsBaliseStr = getNext(ligne);
                            if (hasMore(ligne)) {
                                ligne = nextSeparator(ligne);
                            }
                            arg++;
                            final Date2 time = DateFactory.createDate(tempsBaliseStr);
                            if (time.getTime() < 0) {
                                System.err.println("Negative time for num" + userChronos.getDossard() + ": " + time);
                            } else if (!time.isNull()) {
                                final ObjChrono chrono = new ObjChrono(val);
                                chrono.setTemps(time);
                                chronos.add(chrono);
                            }
                        }
                    }
                    for (final ObjChrono chrono : chronos) {
                        userChronos.addChrono(chrono);
                    }
                    userChronos.sortChronos();
                } else {
                    if (config != null) {
                        for (final ColTable colTable : config.getColTableAll()) {
                            try {
                                colonneInfos.add(new Integer(colTable.getChoix()));
                                colonneName.add(colTable.getChamp());
                            } catch (Exception e) {
                            }
                        }
                    }
                    while (hasMore(ligne)) {
                        getNext(ligne);
                        ligne = nextSeparator(ligne);
                    }
                }
            }
            dis.close();
            in.close();
            System.out.println("Chargé en " + (new Date().getTime() - d.getTime()) / 1000.0 + 's');

        } catch (IOException ex) {
            System.err.println(ex);
        }
        updateChronoList();
    }

    protected static Date2 getTime(final Date2 oldTime, final Date2 time) {
        if (oldTime != null && time != null && !time.isNull()) {
            while (!oldTime.before(time)) {
                DateUtils.upTime(time, DateFactory.createDate("12:00:00"));
            }
            return time;
        }
        return oldTime;
    }

    public final synchronized void loadInfo(final ObjConfig config, final ObjStep etape) {
        if (config == null) {
            System.out.println("Echec d'importation");
            return;
        }

        loadFile(config);

        final Iterable<ObjStep> steps = StepUtil.gatherAllEpreuveFromStep(etape);
        for (final ObjStep step : steps) {
            final Iterator<ObjUserChronos> chronosIterator = userChronosList.iterator();
            while (chronosIterator.hasNext()) {
                final ObjUserChronos userChronos = chronosIterator.next();
                final String numDossard = userChronos.getDossard();
                ObjDossard dossard = StepUtil.findDossard(numDossard, step);
                final boolean found;
                if (dossard == null) {
                    found = false;
                    dossard = new ObjDossard(numDossard);
                } else {
                    found = true;
                }
                for (final ColTable colTable : config.getColTableAll()) {
                    final String attribut = colTable.getChamp();
                    final Object value = userChronos.getInfo(attribut);
                    if (attribut.equals(VAR_CSV_CATEGORIE)) {
                        if (value != null) {
                            step.getManif().addCategorie(new ObjCategorie("" + value));
                            dossard.setInfo(VAR_DOSSARD_CATEGORIE, value);
                        }
                    } else {
                        dossard.setInfo(attribut, value);
                    }
                }
                if (!removedDossards.contains(numDossard) && matchesStepCategoryFilter(etape, dossard.getCategory())) {
                    if (!found) {
                        step.addDossard(dossard);
                    }
                } else {
                    if (found) {
                        step.removeDossard(dossard);
                    }
                    chronosIterator.remove();
                }
            }
        }

    }

    public static boolean matchesStepCategoryFilter(final ObjStep etape, final String category) {
        return etape.getCategoryFilter() == null || etape.getCategoryFilter().isEmpty() || Pattern
                .compile("\\b" + etape.getCategoryFilter()).matcher(category).find();
    }

    public Collection<ObjUserChronos> getInfoSportIdent() {
        return userChronosList;
    }

    private void updateChronoList() {
        removedDossards.clear();
        final Map<String, ObjUserChronos> chronosByMultiId = new HashMap<String, ObjUserChronos>();
        String MULTI_ID = ObjUserChronos.PREFIX_ + "MULTI_ID";
        for (final ObjUserChronos userChronos : userChronosList) {
            String multiId = userChronos.getInfoStr(MULTI_ID);
            if (userChronos.getInfo(MULTI_ID) != null && !multiId.equals("\"\"")) {
                if (chronosByMultiId.containsKey(multiId)) {
                    final ObjUserChronos oldUserChronos = chronosByMultiId.remove(multiId);
                    final ObjUserChronos mergeChronos = mergeChronos(oldUserChronos, userChronos);
                    mergeChronos.setDossard(multiId);

                    removedDossards.add(oldUserChronos.getDossard());
                    removedDossards.add(userChronos.getDossard());
                    removedDossards.remove(mergeChronos.getDossard());
                    chronosByMultiId.put(multiId, mergeChronos);
                } else {
                    chronosByMultiId.put(multiId, userChronos);
                }
            } else {
                chronosByMultiId.put(userChronos.getDossard(), userChronos);
            }
        }

        userChronosList.clear();
        userChronosList.addAll(chronosByMultiId.values());
    }

    private ObjUserChronos mergeChronos(final ObjUserChronos ref, final ObjUserChronos newChronos) {
        final Map<String, Integer> balises = new HashMap<String, Integer>();
        for (final ObjChrono chrono : ref.getChronos()) {
            balises.put(chrono.getNumBalise(), 0);
        }
        for (final ObjChrono chrono : newChronos.getChronos()) {
            if (balises.containsKey(chrono.getNumBalise())) {
                balises.put(chrono.getNumBalise(), 2);
            } else {
                balises.put(chrono.getNumBalise(), 1);
            }
        }
        final ObjChrono chronoArriveeRef = ref.getChronoArrivee();
        final ObjChrono chronoArriveeNew = newChronos.getChronoArrivee();
        mergeBorne(chronoArriveeRef, chronoArriveeNew);

        final ObjChrono chronoDepartRef = ref.getChronoDepart();
        final ObjChrono chronoDepartNew = newChronos.getChronoDepart();
        mergeBorne(chronoDepartRef, chronoDepartNew);

        final Collection<String> baliseToAdd = new ArrayList<>();
        final Collection<String> baliseToInvalidate = new ArrayList<>();
        for (final Map.Entry<String, Integer> balise : balises.entrySet()) {
            if (balise.getValue() == 0) {
                baliseToInvalidate.add(balise.getKey());
            } else if (balise.getValue() == 1) {
                baliseToAdd.add(balise.getKey());
                baliseToInvalidate.add(balise.getKey());
            } else if (balise.getValue() == 2) {
                baliseToAdd.add(balise.getKey());
            }
        }

        for (final ObjChrono chrono : newChronos.getChronos()) {
            if (baliseToAdd.contains(chrono.getNumBalise())) {
                ref.addChrono(chrono);
            }
        }

        for (final ObjChrono chrono : ref.getChronos()) {
            if (baliseToInvalidate.contains(chrono.getNumBalise())) {
                chrono.setCancel(true);
            }
        }

        ref.sortChronos();

        return ref;
    }

    private static void mergeBorne(ObjChrono ref, ObjChrono newChrono) {
        if (newChrono != null) {
            if (DateUtils.compare(ref, newChrono) < 0) {
                ref.setTemps(newChrono.getTemps());
            }
            if (newChrono.isNull()) {
                ref.getTemps().setNull();
            }
        }
    }
}
