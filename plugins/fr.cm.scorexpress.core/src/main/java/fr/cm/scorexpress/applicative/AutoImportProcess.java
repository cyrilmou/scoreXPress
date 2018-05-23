package fr.cm.scorexpress.applicative;

import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.ObjChrono;
import fr.cm.scorexpress.core.model.ObjUserChronos;
import fr.cm.scorexpress.core.model.impl.DateUtils;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.data.UserChronosLoader;

import java.io.File;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.applicative.ProjectManager.importDonneeConcurrent;
import static fr.cm.scorexpress.core.model.ConfigType.IMPORT_PARTICIPANTS;
import static fr.cm.scorexpress.core.model.Step.VAR_DATE_LAST_IMPORT;
import static fr.cm.scorexpress.core.model.Step.VAR_FILENAME_IMPORT;
import static fr.cm.scorexpress.data.UserChronosLoader.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class AutoImportProcess implements Runnable {
    private final ProjectManager projectManager;
    private final Collection<IActualisationProject> listenersActualisation = newArrayList();

    private final Thread thread = new Thread(this);
    private boolean run = true;

    public AutoImportProcess(final ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        run = false;
    }

    @Override
    public void run() {
        while (run) {
            try {
                for (final AbstractSteps manif : projectManager.getProjects()) {
                    for (final ObjStep step : manif.getSteps()) {
                        importAuto(step);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            for (int i = 5; i > 0; i--) {
                fireActualisationDelayListener(i * 1000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fireActualisationDelayListener(0);
        }
    }

    private void importAuto(final ObjStep etape) {
        if (!etape.isActif()) {
            return;
        }
        if (etape.isImportAuto() && etape.getImportFileName() != null) {
            final String fileName = etape.getInfoStr(VAR_FILENAME_IMPORT);
            if (fileName != null) {
                final File f = new File(fileName);
                if (f.exists()) {
                    final String lastModif = f.lastModified() + EMPTY; //$NON-NLS-1$
                    final String lastImport = etape.getInfoStr(VAR_DATE_LAST_IMPORT);
                    if (lastImport == null || !lastImport.equals(lastModif)) {
//                        importSportIdent(etape);
                        UserChronosLoader chronosLoader = importDonneeConcurrent(etape, etape.getImportFileName());

                        final Collection<ObjUserChronos> usersChronos = chronosLoader.getInfoSportIdent();

                        final ObjStep etapeParent = etape.getEpreuve();
                        if (etapeParent == null) {
                            fireActualisationListener();
                            return;
                        }
                        // Suppression des données précédentes
                        etapeParent.clearUserChronos();

                        for (final ObjUserChronos userChronos : usersChronos) {
                            if (matchesStepCategoryFilter(etapeParent, userChronos.getInfo(VAR_CSV_CATEGORIE) + "")) {
                                etapeParent.addUserChronos(userChronos);
                            }
                        }
                        etape.setInfo(VAR_DATE_LAST_IMPORT, lastModif); //$NON-NLS-1$

                        System.out.println("SportIdent import " //$NON-NLS-1$
                                + ' ' + fileName + " (ok)"); //$NON-NLS-1$ //$NON-NLS-2$
                        fireActualisationListener();
                    }
                }
            }
        }
        for (final ObjStep sousEtape : etape.getSteps()) {
            importAuto(sousEtape);
        }
    }

    public void importSportIdent(final ObjStep etape) {
        try {
            if (etape == null) {
                return;
            }
            final String fileName = etape.getImportFileName();
            final ObjStep etapeParent = etape.getEpreuve();
            if (etapeParent == null) {
                return;
            }
            if (fileName == null || fileName.equals(EMPTY) || !new File(fileName).exists()) {
                System.out.println("SportIdent import " //$NON-NLS-1$
                        + " (Echec)"); //$NON-NLS-1$
                return;
            }
            final UserChronosLoader chronosLoader = createUserChrono(fileName);
            chronosLoader.loadFile(etape.getManif().getConfiguration().getConfig(IMPORT_PARTICIPANTS));
            final Collection<ObjUserChronos> usersChronos = chronosLoader.getInfoSportIdent();
            // Suppression des données précédentes
            etapeParent.clearUserChronos();

            for (final ObjUserChronos userChronos : usersChronos) {
                if (matchesStepCategoryFilter(etapeParent, userChronos.getInfo(VAR_CSV_CATEGORIE) + "")) {
                    etapeParent.addUserChronos(userChronos);
                }
            }
            final File f = new File(fileName);
            final String lastImport = f.lastModified() + EMPTY;
            etape.setInfo(VAR_DATE_LAST_IMPORT, lastImport); //$NON-NLS-1$
            System.out.println("SportIdent import " //$NON-NLS-1$
                    + ' ' + fileName + " (ok)"); //$NON-NLS-1$ //$NON-NLS-2$
            fireActualisationListener();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void addActualisationListener(final IActualisationProject listener) {
        listenersActualisation.add(listener);
    }

    public void fireActualisationListener() {
        for (final IActualisationProject element : listenersActualisation) {
            try {
                element.importChanged();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void fireActualisationDelayListener(final int delay) {
        for (final IActualisationProject element : listenersActualisation) {
            try {
                element.waitingDelay(delay);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void removeActualisationListener(final IActualisationProject listener) {
        listenersActualisation.remove(listener);
    }

}
