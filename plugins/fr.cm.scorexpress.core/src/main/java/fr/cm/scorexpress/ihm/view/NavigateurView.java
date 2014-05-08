package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.applicative.IProjectManager;
import fr.cm.scorexpress.applicative.ProjectManager;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.ihm.application.ImageReg;
import fr.cm.scorexpress.ihm.view.clipboard.CopyStepAction;
import fr.cm.scorexpress.ihm.view.clipboard.CutStepAction;
import fr.cm.scorexpress.ihm.view.clipboard.PasteTreeGadgetAction;
import fr.cm.scorexpress.ihm.view.dnd.StepDragListener;
import fr.cm.scorexpress.ihm.view.dnd.StepTreeDropAdapter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import static fr.cm.common.workbench.WorkbenchUtils.defineWorkbenchPopupMenu;
import static fr.cm.scorexpress.applicative.ProjectManager.*;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_VIEW_BG;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.view.NavigateurDoubleClick.applyNavigatorDoubleClick;
import static fr.cm.scorexpress.ihm.view.NavigatorContentProviderFactory.createContentProvider;
import static fr.cm.scorexpress.ihm.view.NavigatorTooltipManager.applyTooltipNavigator;
import static fr.cm.scorexpress.ihm.view.dnd.StepTransfer.getStepTransfer;
import static org.eclipse.jface.dialogs.MessageDialog.openConfirm;
import static org.eclipse.swt.dnd.DND.*;
import static org.eclipse.swt.layout.GridData.FILL;
import static org.eclipse.ui.actions.ActionFactory.*;

public class NavigateurView extends ViewPart implements IProjectManager {
    public static final String     NAVIGATOR_VIEW_ID = "fr.cm.scorexpress.view.navigateur";
    private             Tree       tree              = null;
    private             Label      labelTitre        = null;
    private             TreeViewer treeViewer        = null;

    public void addManifestation() {
        final FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[]{"*.xml;*.raid", "*.raid"});
        fileDialog.setFilterNames(new String[]{"Fichiers chronosRAID (*.xml,*.raid)", "Projet ChronosRAID (*.raid)"
        });
        final String fileName = fileDialog.open();
        if (fileName != null) {
            ProjectManager.openProject(fileName);
        }
    }

    @Override
    public void changed() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                treeViewer.refresh();
                treeViewer.expandToLevel(2);
            }
        });
    }

    @Override
    public void createPartControl(final Composite parent) {
        final GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = FILL;
        gridData1.verticalAlignment = GridData.CENTER;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        final GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = FILL;
        gridData.verticalAlignment = FILL;
        gridData.grabExcessHorizontalSpace = true;
        final Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(gridLayout);
        labelTitre = new Label(top, SWT.NONE);
        labelTitre.setText("Navigateur");
        labelTitre.setLayoutData(gridData1);
        tree = new Tree(top, SWT.MULTI);
        tree.setLayoutData(gridData);
        treeViewer = new TreeViewer(tree);
        getSite().setSelectionProvider(treeViewer);
        final Image fondImage = ImageReg.getImg(IMG_VIEW_BG);
        tree.setBackgroundImage(fondImage);
        tree.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent element) {
                labelTitre.setText(tree.getSelection()[0].getText());
                try {
                    getViewSite().getPage().showView(NAVIGATOR_VIEW_ID);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });

        applyNavigatorDoubleClick(tree, getSite().getWorkbenchWindow());

        treeViewer.setLabelProvider(new NavigatorLabelProvider());
        treeViewer.setContentProvider(createContentProvider());

        final ProjectManager manager = getProjectManager();
        manager.addProjectManagerListener(this);
        treeViewer.setInput(manager);
        defineWorkbenchPopupMenu(getSite(), treeViewer);
        initDragAndDrop();

        applyTooltipNavigator(tree);

        final TreeEditor editor = new TreeEditor(tree);
        editor.horizontalAlignment = SWT.CENTER;
        editor.grabHorizontal = true;
        editor.grabVertical = true;

        tree.addKeyListener(new TreeEditing(tree, editor));

        final Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
        final IActionBars bars = getViewSite().getActionBars();
        bars.setGlobalActionHandler(CUT.getId(), new CutStepAction(treeViewer, clipboard));
        bars.setGlobalActionHandler(COPY.getId(), new CopyStepAction(treeViewer, clipboard));
        bars.setGlobalActionHandler(PASTE.getId(), new PasteTreeGadgetAction(treeViewer, clipboard));
    }

    private void initDragAndDrop() {
        final int ops = DROP_COPY | DROP_MOVE | CLIPBOARD;
        Transfer[] transfers = new Transfer[]{getStepTransfer()};
        treeViewer.addDragSupport(ops, transfers, new StepDragListener(treeViewer));

        transfers = new Transfer[]{getStepTransfer()};
        treeViewer.addDropSupport(ops, transfers, new StepTreeDropAdapter(treeViewer));
    }

    public void refresh() {
        treeViewer.refresh();
    }

    public void removeManifestation() {
        if (tree.getSelectionCount() > 0) {
            final boolean res = openConfirm(getSite().getShell(), "Supprimer", i18n("QUESTION_RETIRER_MANIF"));
            if (res) {
                final AbstractGetInfo<?> data = (AbstractGetInfo<?>) tree.getSelection()[0].getData();
                final ObjManifestation manif = data.getManif();
                removeProject(manif.getFileName());
            }
        }
    }

    @Override
    public void setFocus() {
        tree.setFocus();
    }

    public void newManifestation() {
        final FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[]{"*.XML;*.raid", "*.raid"});
        fileDialog.setFilterNames(new String[]{"Fichiers chronosRAID (*.xml,*.raid)", "Projet ChronosRAID (*.raid)"
        });
        final String fileName = fileDialog.open();
        if (fileName != null) {
            newProject(fileName);
        }
    }
}
