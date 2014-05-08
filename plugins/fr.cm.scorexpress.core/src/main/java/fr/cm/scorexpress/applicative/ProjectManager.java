package fr.cm.scorexpress.applicative;

import fr.cm.scorexpress.core.model.IControlerListener;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.data.UserChronosLoader;
import fr.cm.scorexpress.data.xml.XmlManifestation;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.applicative.LicenceFactory.VALID_LICENCE;
import static fr.cm.scorexpress.applicative.i18n.Messages.i18n;
import static fr.cm.scorexpress.core.model.ConfigType.IMPORT_PARTICIPANTS;
import static fr.cm.scorexpress.data.UserChronosLoader.createUserChrono;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ProjectManager implements IProjectManagerListener {
    public static final String ALLOW_PRINT           = "ALLOW_PRINT"; //$NON-NLS-1$
    public static final String ALLOW_COPY            = "ALLOW_COPY"; //$NON-NLS-1$
    public static final String VERIF_VERSION_NORMALE = "VERSION_NORMAL"; //$NON-NLS-1$
    public static final String VERIF_DATE            = "VERIF_DATE"; //$NON-NLS-1$
    public static final int    VERSION_NORMALE       = 2;

    private       Licence                     licence   = null;
    private final List<ObjManifestation>      projects  = newArrayList();
    private final Collection<IProjectManager> listeners = newArrayList();

    private DemoVersionEvent demoVersionEvent = null;

    ProjectManager() {
        initLicence();
    }

    public static ProjectManager getProjectManager() {
        return ProjectManagerFactory.getProjectManager();
    }

    ObjManifestation loadProject(final String fileName) {
        try {
            final ObjManifestation manif = XmlManifestation.loadFile(fileName);
            if (manif == null) {
                throw new RuntimeException("Manif not loaded");
            }
            for (final ObjManifestation element : projects) {
                if (element.getFileName().equals(fileName)) {
                    return element;
                }
            }
            projects.add(manif);
            getProjectManager().fireProjectManagerListener();
            manif.getControler().addView(new IControlerListener() {
                @Override
                public void dataChanged(final IData obj, final String type, final String property) {
                    fireProjectManagerListener();
                }
            });
            return manif;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProjectManagerException(
                    "Erreur de chargement du fichier", e);
        }
    }

    public static void openProject(final String fileName) {
        getProjectManager().loadProject(fileName);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static void removeProject(final String fileName) {
        final List<ObjManifestation> projects = getProjectManager().projects;
        for (final ObjManifestation manif : projects) {
            if (manif.getFileName().equals(fileName)) {
                projects.remove(manif);
                getProjectManager().fireProjectManagerListener();
                return;
            }
        }
    }

    ObjManifestation createProject(final String fileName) {
        final ObjManifestation manif = new ObjManifestation("Manifestation (double cliquez)");
        manif.setFileName(fileName);
        for (final ObjManifestation element : projects) {
            if (element.getFileName().equals(fileName)) {
                return element;
            }
        }
        projects.add(manif);
        getProjectManager().fireProjectManagerListener();
        manif.getControler().addView(new IControlerListener() {
            @Override
            public void dataChanged(final IData obj, final String type, final String property) {
                fireProjectManagerListener();
            }
        });
        return manif;
    }

    public static ObjManifestation newProject(final String fileName) {
        return getProjectManager().createProject(fileName);
    }

    void saveProjects() {
        for (final ObjManifestation element : projects) {
            save(element);
        }
    }

    public static void saveAllProjects() {
        getProjectManager().saveProjects();
    }

    public static void save(final ObjManifestation manif) {
        XmlManifestation.writeFile(manif);
    }

    public Collection<ObjManifestation> getProjects() {
        return unmodifiableList(projects);
    }

    @Override
    public void addProjectManagerListener(final IProjectManager listener) {
        listeners.add(listener);
    }

    @Override
    public void fireProjectManagerListener() {
        for (final IProjectManager element : listeners) {
            element.changed();
        }
    }

    @Override
    public void removeProjectManagerListener(final IProjectManager listener) {
        listeners.remove(listener);
    }

    public static void importDonneeConcurrent(ObjStep etape,
                                              final String fileName) {
        try {
            if (etape == null) {
                return;
            }
            etape = etape.getEpreuve();
            if (etape == null) {
                return;
            }
            if (fileName == null || fileName.equals(EMPTY)) {
                System.out.println("SportIdent import " //$NON-NLS-1$
                                           + " (Echec)"); //$NON-NLS-1$
                return;
            }
            final UserChronosLoader chronosLoader = createUserChrono(fileName);
            final ObjConfig config = etape.getManif().getConfiguration().getConfig(IMPORT_PARTICIPANTS);
            chronosLoader.loadInfo(config, etape);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void importDonneeConcurrent(final ObjStep etape) {
        if (etape == null) {
            return;
        }
        final ObjStep epreuve = etape.getEpreuve();
        if (epreuve == null) {
            return;
        }
        final String fileName = epreuve.getImportFileName();
        importDonneeConcurrent(epreuve, fileName);
    }

    /**
     * Initialise la licence
     */
    private void initLicence() {
        try {
            licence = Licence.getLicence("licence.lic"); //$NON-NLS-1$
        } catch (Exception e) {
            System.err.println(i18n("ProjectManager.11")); //$NON-NLS-1$
        }
        licence = VALID_LICENCE;
    }

    public boolean getLicenceProperty(final String property) {
        if (property == null || licence == null) {
            return true;
        } else if (property.equals(ALLOW_PRINT)) {
            return licence.getMode() > 1;
        } else if (property.equals(ALLOW_COPY)) {
            return licence.getMode() > 1;
        } else if (property.equals(VERIF_VERSION_NORMALE)) {
            return licence.getMode() == VERSION_NORMALE;
        } else if (property.equals(VERIF_DATE)) {
            return licence.isValide();
        }
        return false;
    }

    /**
     * Retourne l'état de la licence et des propriètés Déclenche un evénement en
     * cas de version de démonstration
     *
     * @param property
     * @return
     */
    public static boolean verifyLicence(final String property) {
        if (getProjectManager() != null) {
            final boolean res = getProjectManager().getLicenceProperty(property);
            if (!res) {
                getProjectManager().demoVersionEvent.usingDemoVersion(property);
            }
            return res;
        }
        return false;
    }

    /**
     * @param event
     */
    public static void setDemoVersionEvent(final DemoVersionEvent event) {
        if (getProjectManager() != null) {
            getProjectManager().demoVersionEvent = event;
        }
    }
}
