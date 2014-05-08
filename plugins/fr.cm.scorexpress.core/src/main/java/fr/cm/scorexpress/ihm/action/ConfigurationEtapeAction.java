package fr.cm.scorexpress.ihm.action;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.action.i18n.Messages;
import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import fr.cm.scorexpress.ihm.editor.EtapeConfigurationEditor;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;

import java.util.Iterator;

public class ConfigurationEtapeAction extends Action implements
        ISelectionListener, IObjectActionDelegate {

    public static final String ID = "fr.cm.chronos.actions.ConfigurationEtapeAction"; //$NON-NLS-1$

    private final IWorkbenchWindow window;

    private IStructuredSelection selection = null;
    private final AutoResizeColumn autoResizeContext;

    public ConfigurationEtapeAction() {
        super();
        window = getScoreXPressPlugin().getWorkbench().getActiveWorkbenchWindow();
        autoResizeContext = new AutoResizeColumn();
    }

    public ConfigurationEtapeAction(final IWorkbenchWindow window) {
        this.window = window;
        window.getSelectionService().addSelectionListener(this);
        setId(ID);
        setText(Messages.i18n("ConfigurationEtapeAction.Configuration_etape___"));
        setToolTipText(Messages.i18n("ConfigurationEtapeAction.Configuration_de_etape_selectionnee"));
        setImageDescriptor(ScoreXPressPlugin.getImageDescriptor("/icons/nav_go.gif"));
        autoResizeContext = new AutoResizeColumn();
    }

    public void selectionChanged(final IWorkbenchPart part, final ISelection incoming) {
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
            setEnabled(selection.getFirstElement() instanceof ObjStep);
        } else {
            setEnabled(false);
        }
    }

    public void dispose() {
        window.getSelectionService().removeSelectionListener(this);
    }

    @Override
    public void run() {
        try {
            final Object item = selection.getFirstElement();
            final IWorkbenchPage page = window.getActivePage();
            final IEditorInput input = new EtapeEditorInput((ObjStep) item,
                    EtapeConfigurationEditor.CONFIGURATION_EDITOR_ID,
                    autoResizeContext);
            System.out.println("open" + ((AbstractSteps) item).getLib());
            page.openEditor(input, EtapeConfigurationEditor.CONFIGURATION_EDITOR_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(final IAction action) {
        final ISelection incoming = window.getActivePage().getSelection();
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
        }
        final Thread thread = new OpenEtapeEditorThread();
        thread.start();
    }

    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    }

    public void selectionChanged(final IAction action, final ISelection selection) {
    }

    private class OpenEtapeEditorThread extends Thread {
        public void run() {
            for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
                final Object item = iter.next();
                final ObjStep etape = (ObjStep) item;
                window.getShell().getDisplay().syncExec(new Runnable() {
                    public void run() {
                        try {
                            final IWorkbenchPage page = window.getActivePage();
                            final IEditorInput input = new EtapeEditorInput(
                                    etape, EtapeConfigurationEditor.CONFIGURATION_EDITOR_ID, autoResizeContext);
                            System.out.println("open" + etape.getLib());
                            page.openEditor(input,
                                    EtapeConfigurationEditor.CONFIGURATION_EDITOR_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    }
}
