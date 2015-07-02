package fr.cm.scorexpress.ihm.view;

import fr.cm.common.widget.StandardToolKit;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.scorexpress.core.model.ObjResultat;
import fr.cm.scorexpress.ihm.print.ImagePrintUtils;
import fr.cm.scorexpress.ihm.print.PrintSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import static fr.cm.common.widget.button.ButtonBuilder.createButton;
import static fr.cm.common.widget.composite.CompositeBuilders.createCompositeBuilder;
import static fr.cm.scorexpress.ihm.editor.ViewColumnViewerToolTipSupport.updateResultat;

public class ResultatView extends ViewPart {
    public static final Color WHITE        = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
    private final StandardToolKit toolkit = new StandardToolKit();
    private ScrolledComposite sc;
    private Composite         composite;
    private ButtonModel       imprimer;
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
        final CompositeBuilder builder = createCompositeBuilder(toolkit, sc, SWT.NONE);
        builder.withLayout(new GridLayout(1, false)).withBackground(WHITE);

       for (Object item : items) {
            if (item != null && item instanceof ObjResultat) {
                updateResultat(builder.getControl(), (ObjResultat) item);
            }
        }
        sc.setContent(builder.getControl());
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(builder.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
        if (composite != null) {
            composite.dispose();
        }
        composite = builder.getControl();
    }

    public void createPartControl(final Composite parent) {
        imprimer = new ButtonModel("Imprimer");
        parent.setLayout(new GridLayout(1, false));
        createButton(toolkit, parent, imprimer, SWT.NONE);

        sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        final GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        sc.setLayoutData(layoutData);

        final CompositeBuilder builder = createCompositeBuilder(toolkit, sc, SWT.NONE);
        composite = builder.getControl();

        builder.withLayout(new GridLayout(2, false)).withBackground(WHITE);

        imprimer.addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                final PrintSettings settings = new PrintSettings();
                settings.setTopString("Résultat");
                ImagePrintUtils.printControl(composite, settings);
            }
        });


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
