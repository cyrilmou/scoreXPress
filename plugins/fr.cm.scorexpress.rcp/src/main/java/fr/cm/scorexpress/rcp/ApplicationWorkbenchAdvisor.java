package fr.cm.scorexpress.rcp;

import fr.cm.scorexpress.applicative.DemoVersionEvent;
import fr.cm.scorexpress.applicative.ProjectManager;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.rcp.i18n.Messages;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import java.io.PrintStream;
import java.util.Collection;

import static fr.cm.scorexpress.applicative.ProjectManager.setDemoVersionEvent;
import static fr.cm.scorexpress.rcp.DemoVerifier.createDemoVersionVerifier;
import static fr.cm.scorexpress.rcp.i18n.Messages.getString;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String NB_MANIF = "NB_MANIF";
    private static final String MANIF    = "MANIF";

    private boolean forceShutdown = false;

    private static void createConsole() {
        final MessageConsole console = new MessageConsole(Messages.getString("Console.name"), null);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
        final MessageConsoleStream stream = console.newMessageStream();
        System.setOut(new PrintStream(stream));
        System.setErr(new PrintStream(stream));
    }

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public void initialize(final IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
        createConsole();
    }

    public String getInitialWindowPerspectiveId() {
        return Perspective.PERSPECTIVE_ID;
    }

    public void postStartup() {
        super.postStartup();

        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        final int nbManif = store.getInt(NB_MANIF);
        for (int i = 1; i <= nbManif; i++) {
            try {
                final String fileName = store.getString(MANIF + i);
                if (fileName != null && !fileName.equals(StringUtils.EMPTY)) {
                    ProjectManager.openProject(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setDemoVersionEvent(new DemoVersionProjectEvent());
        createDemoVersionVerifier();
    }

    public boolean preShutdown() {
        super.preShutdown();
        final boolean res = openQuestion(Display.getCurrent().getActiveShell(),
                                         getString("ApplicationWorkbenchAdvisor.0"),
                                         getString("ApplicationWorkbenchAdvisor.1"));
        final Collection<ObjManifestation> manifs = ProjectManager.getProjectManager()
                .getProjects();
        final IPreferenceStore store = Activator.getDefault()
                .getPreferenceStore();
        if (manifs.size() >= 0) {
            store.setValue(NB_MANIF, manifs.size());
            int i = 0;
            for (final ObjManifestation manif : manifs) {
                i++;
                store.setValue(MANIF + i, manif.getFileName());
            }
        }
        if (res) {
            savProject();
            return true;
        } else {
            return forceShutdown;
        }
    }

    private boolean savProject() {
        double result = OK_ID;
        if (ProjectManager.getProjectManager().getProjects().size() > 1) {
            final Window dlg = MessageDialogWithToggle
                    .openYesNoQuestion(
                            Display.getCurrent().getActiveShell(),
                            getString("ApplicationWorkbenchAdvisor.9"),
                            getString("ApplicationWorkbenchAdvisor.10"),
                            getString("ApplicationWorkbenchAdvisor.11"), false, null, null);
            result = dlg.getReturnCode();
        }
        if (result == OK_ID) {
            System.out.println(getString("ApplicationWorkbenchAdvisor.12"));
            for (final ObjManifestation manif : ProjectManager.getProjectManager().getProjects()) {
                final boolean sav = openQuestion(Display.getCurrent()
                                                         .getActiveShell(), "Sauvegarder", manif.getFileName());
                if (sav) {
                    try {
                        ProjectManager.save(manif);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private class DemoVersionProjectEvent implements DemoVersionEvent {
        public void usingDemoVersion(final String action) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if (action.equals(ProjectManager.VERIF_VERSION_NORMALE)) {
                        MessageDialog.openWarning(Display.getDefault().getActiveShell(),
                                                  getString("ApplicationWorkbenchAdvisor.5"),
                                                  getString("ApplicationWorkbenchAdvisor.6"));
                    } else if (action.equals(ProjectManager.VERIF_DATE)) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(),
                                                getString("ApplicationWorkbenchAdvisor.13"),
                                                getString("ApplicationWorkbenchAdvisor.14"));
                        forceShutdown = true;
                        Display.getCurrent().getActiveShell().close();
                    } else {
                        MessageDialog.openWarning(Display.getDefault().getActiveShell(),
                                                  getString("ApplicationWorkbenchAdvisor.7"),
                                                  getString("ApplicationWorkbenchAdvisor.8"));
                    }
                }
            });
        }
    }
}
