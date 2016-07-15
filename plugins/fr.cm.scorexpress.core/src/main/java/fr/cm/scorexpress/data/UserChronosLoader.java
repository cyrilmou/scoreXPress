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

    private static final String SEPARATEUR = ";";
    private final String fileName;
    public static final String                  VAR_NUM           = "N° dép.";
    public static final String                  VAR_NOM           = "Nom";
    public static final String                  VAR_PRENOM        = "Prénom";
    public static final String                  VAR_SEXE          = "S";
    public static final String                  VAR_PUCE          = "Puce";
    public static final String                  VAR_CSV_CATEGORIE = "Long";
    public static final String                  VAR_VILLE         = "Ville";
    static final        HashMap<String, String> instances         = new HashMap<String, String>();

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

    public final synchronized void loadFile() {
        try {
            System.out.println("Chargement de " + fileName);
            final Date d = new Date();
            final File f = new File(fileName);
            final InputStreamReader in = new InputStreamReader(new FileInputStream(f));
            final BufferedReader dis = new BufferedReader(in);
            // Préparation des colonnes d'informations à récupérer dans le
            // fichier
            final int[] colonneInfosInt = {0, 1, 3, 4, 15, 11, 19};
            final List<Integer> colonneInfos = newArrayList();
            for (final int aColonneInfosInt : colonneInfosInt) {
                colonneInfos.add(new Integer(aColonneInfosInt));
            }
            final List<String> colonneName = newArrayList();
            String ligne;
            for (int nrLigneCSV = 0; (ligne = dis.readLine()) != null; nrLigneCSV++) {
                if (nrLigneCSV > 0) { // Titre
                    final ObjUserChronos userChronos = new ObjUserChronos("0");
                    userChronosList.add(userChronos);
                    String puce = null;
                    final ObjChrono arrivee = new ObjChronoArrivee(DateFactory.createDate(0));
                    for (int arg = 0; hasMore(ligne); arg++) {
                        String val = getNext(ligne);
                        ligne = nextSeparator(ligne);
                        final int indexCol = colonneInfos.indexOf(new Integer(arg));
                        if (indexCol != -1 && arg < colonneName.size()) {
                            if (!val.isEmpty() && val.charAt(0) == '"') {
                                if (val.length() == 1) {
                                    val = "";
                                } else {
                                    val = val.substring(1, val.length() - 1);
                                }
                            }
                            userChronos.setInfo("" + colonneName.get(arg), val);
                        }
                        if (arg == 0) {
                            userChronos.setDossard(val);
                        }
                        if (arg == 1) {
                            puce = val;
                            userChronos.setPuce(puce);
                        }
                        if (arg == 9) {
                            userChronos.addChrono(new ObjChronoDepart(DateFactory.createDate("0:00:00")));
                        }
                        if (arg == 11) {
                            final Date2 time = DateFactory.createDate(val);
                            arrivee.setTemps(time);
                            userChronos.setPuce(puce);
                            userChronos.addChrono(arrivee);
                        }
                        if (arg >= 46) {
                            /* Récupération du numéro de balise */
                            /*
                                    * Récuparation du temps de pointage de la balise en
                                    * heure
                                    */
                            final String tempsBaliseStr = getNext(ligne);
                            if (hasMore(ligne)) {
                                ligne = nextSeparator(ligne);
                            }
                            arg++;
                            final ObjChrono chrono = new ObjChrono(val);
                            final Date2 time = DateFactory.createDate(tempsBaliseStr);
                            chrono.setTemps(time);
                            userChronos.addChrono(chrono);
                        }
                    }
                } else {
                    while (hasMore(ligne)) {
                        final String val = getNext(ligne);
                        ligne = nextSeparator(ligne);
                        colonneName.add(val);
                    }
                }

            }
            dis.close();
            in.close();
            System.out.println("Chargé en " + (new Date().getTime() - d.getTime()) / 1000.0 + 's');
        } catch (IOException ex) {
            System.err.println(ex);
        }
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
                                System.err.println("Negative time for num" + userChronos.getDossard()+ ": " + time);
                            } else if( !time.isNull()) {
                                final ObjChrono chrono = new ObjChrono(val);
                                chrono.setTemps(time);
                                chronos.add(chrono);
                            }
                        }
                    }
                    Collections.sort(chronos, new Comparator<ObjChrono>() {
                                         @Override
                                         public int compare(final ObjChrono o1, final ObjChrono o2) {
                                             if (o2.isNull()) { return -1; }
                                             if (o1.isNull()) { return 1; }
                                             return DateUtils.compare(o1, o2);
                                         }
                                     }
                    );
                    for (final ObjChrono chrono : chronos) {
                        userChronos.addChrono(chrono);
                    }
                } else {
                    for (final ColTable colTable : config.getColTableAll()) {
                        try {
                            colonneInfos.add(new Integer(colTable.getChoix()));
                            colonneName.add(colTable.getChamp());
                        } catch (Exception e) {
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
                    if (matchesStepCategoryFilter(etape, dossard.getCategory())) {
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
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static boolean matchesStepCategoryFilter(final ObjStep etape, final String category) {
        return etape.getCategoryFilter() == null || etape.getCategoryFilter().isEmpty() || Pattern
                .compile("\\b" + etape.getCategoryFilter()).matcher(category).find();
    }

    public Collection<ObjUserChronos> getInfoSportIdent() {
        return userChronosList;
    }
}
