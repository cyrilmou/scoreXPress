package fr.cm.scorexpress.ihm.view;

import fr.cm.common.widget.StandardToolKit;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.scorexpress.core.model.ObjResultat;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import static fr.cm.common.widget.composite.CompositeBuilders.createCompositeBuilder;
import static fr.cm.scorexpress.ihm.editor.ViewColumnViewerToolTipSupport.updateResultat;

public class ResultatView extends ViewPart {
    public static final Color WHITE = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
    private ScrolledComposite sc;
    private Composite         composite;
    private ISelectionListener listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
            // we ignore our own selections
            if (sourcepart != ResultatView.this) {
                showSelection(sourcepart, selection);
            }
        }
    };

    /**
     * Shows the given selection in this view.
     */
    public void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
        setContentDescription(sourcepart.getTitle());
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            showItems(ss.toArray());
        }
    }

    private void showItems(final Object[] items) {
        for (Object item : items) {
            if (item instanceof ObjResultat) {
                showItem((ObjResultat) item);
            }
        }
    }

    private void showItem(final ObjResultat resultat) {

        if (resultat != null) {
            final CompositeBuilder builder = createCompositeBuilder(new StandardToolKit(), sc, SWT.NONE);

            builder.withLayout(new GridLayout(2, false)).withBackground(WHITE);
            updateResultat(builder.getControl(), resultat);

            sc.setContent(builder.getControl());
            sc.setExpandHorizontal(true);
            sc.setExpandVertical(true);
            sc.setMinSize(builder.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
            if (composite != null) {
                composite.dispose();
            }
            composite = builder.getControl();
        }
    }

    private void showText() {
    }

    public void createPartControl(final Composite parent) {
        sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

        final CompositeBuilder builder = createCompositeBuilder(new StandardToolKit(), sc, SWT.NONE);
        composite = builder.getControl();

        builder.withLayout(new GridLayout(2, false)).withBackground(WHITE);

        sc.setContent(composite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
    }


    public void setFocus() {
        composite.setFocus();
    }

    public void dispose() {
        // important: We need do unregister our listener when the view is disposed
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(listener);
        super.dispose();
    }


}
