package fr.cm.scorexpress.ihm.editor.page;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.composite.AbstractCompositeBuilder;
import fr.cm.common.widget.composite.CompositeBuilders;
import fr.cm.common.widget.composite.SectionBuilder;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.Balise;
import fr.cm.scorexpress.core.model.IControlerListener;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.ObjBalise;
import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.view.dnd.IDataDragListener;
import fr.cm.scorexpress.ihm.view.dnd.PenaliteTransfer;
import fr.cm.scorexpress.ihm.view.dnd.PenaliteTreeDropAdapter;
import fr.cm.scorexpress.ihm.view.dnd.StepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import java.util.ArrayList;
import java.util.Collection;

import static fr.cm.scorexpress.core.model.Balise.*;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.eclipse.swt.dnd.DND.*;
import static org.eclipse.swt.layout.GridData.*;

@SuppressWarnings({"InstanceofInterfaces"})
public class StepDetailsBlockPage extends MasterDetailsBlock {
    private final ParametrageStepPageModel model;
    private       TreeViewer viewer      = null;
    private final Image      imgRemove   = getImg(IMG_PENALITY_DESACTIVATE);
    private final Image      imgEtape    = getImg(IMG_ETAPE);
    private final Image      imgPenalite = getImg(IMG_PENALITY);
    private final Image      imgBalise   = getImg(IMG_BALISE);

    public StepDetailsBlockPage(final ParametrageStepPageModel parametrageStepPageModel) {
        model = parametrageStepPageModel;
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        final FormToolkit toolkit = managedForm.getToolkit();
        final SectionBuilder sectionBuilder =
                CompositeBuilders.createSection(new MyFormToolkit(toolkit), parent, SWT.NONE)
                                 .withText(i18n("ConfigStepPage.CONFIGURATION_ETAPE"))
                                 .withDescription(i18n("ConfigStepPage.INFOS_ETAPE"));
        final AbstractCompositeBuilder client =
                sectionBuilder.addClient(SWT.WRAP).withLayout(new GridLayout(2, false)).withPaintBorder();
        final AbstractCompositeBuilder toolbar = client.addComposite(SWT.NONE).withLayout(new GridLayout(4, false));
        toolbar.addButton(model.getAddStepButtonModel(), SWT.NONE).withImage(imgEtape)
               .withToolTip(i18n("ConfigStepPage.ADD_STEP_TIP"));
        toolbar.addButton(model.getAddPenalityButtonModel(), SWT.NONE).withImage(imgPenalite)
               .withToolTip(i18n("ConfigStepPage.ADD_PENALITY_TIP"));
        toolbar.addButton(model.getAddBaliseButtonModel(), SWT.NONE).withImage(imgBalise)
               .withToolTip(i18n("ConfigStepPage.ADD_BALISE_TIP"));
        toolbar.addButton(model.getRemoveStepButtonModel(), SWT.NONE).withImage(imgRemove)
               .withToolTip(i18n("ConfigStepPage.REMOVE_SELECTION_TIP"));
        final Tree tree = toolkit.createTree(client.getControl(), SWT.MULTI);
        final GridData treeGridData = new GridData(FILL_BOTH | GRAB_HORIZONTAL | GRAB_VERTICAL);
        treeGridData.horizontalSpan = 2;
        treeGridData.heightHint = 300;
        treeGridData.widthHint = 200;
        tree.setLayoutData(treeGridData);
        sectionBuilder.addPartToForm(managedForm);
        viewer = new TreeViewer(tree);
        viewer.addSelectionChangedListener(new SelectionTreeChanged(managedForm, sectionBuilder.getPart()));
        viewer.setContentProvider(new PenaliteTreeContentProvider());
        viewer.setLabelProvider(new StepLabelProvider());
        viewer.setInput(model);
        for (final ObjPenalite objPenalite : model.getDescendentPenalities()) {
            viewer.expandToLevel(objPenalite, AbstractTreeViewer.ALL_LEVELS);
        }
        model.setViewer(viewer);
        viewer.expandToLevel(2);
        dragAndDropTest(viewer);
    }

    @Override
    protected void createToolBarActions(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        final IAction haction = new Action("hor", Action.AS_RADIO_BUTTON) {
            @Override
            public void run() {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setToolTipText("Horizontal");
        haction.setImageDescriptor(getImageDescriptor(IMG_HORIZONTAL));
        final IAction vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
            @Override
            public void run() {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vaction.setChecked(false);
        vaction.setToolTipText("Vertical");
        vaction.setImageDescriptor(getImageDescriptor(IMG_VERTICAL));
        form.getToolBarManager().add(haction);
        form.getToolBarManager().add(vaction);
    }

    @Override
    protected void registerPages(final DetailsPart detailsPart) {
        detailsPart.registerPage(ObjPenalite.class, new PenalitySubParametragePage());
        detailsPart.registerPage(ObjStep.class, new StepSubParametragePage());
        detailsPart.registerPage(ObjBalise.class, new BalisePage());
        detailsPart.registerPage(Balises.class, new BalisesPage(model.getStepModel().getStep().getManif()));
    }

    private static void dragAndDropTest(final TreeViewer viewer) {
        /* drag */
        final int ops = DROP_COPY | DROP_MOVE | CLIPBOARD;
        final Transfer[] transfers = new Transfer[]{StepTransfer.getStepTransfer(), PenaliteTransfer.getInstance()};
        viewer.addDragSupport(ops, transfers, new IDataDragListener(viewer));
        // /* drop */
        viewer.addDropSupport(ops, transfers, new PenaliteTreeDropAdapter(viewer));
    }

    public void doSave() {
        detailsPart.commit(true);
    }

    public void update() {
        if (viewer != null) {
            viewer.refresh();
        }
    }

    public boolean isDirty() {
        return detailsPart.isDirty();
    }

    class PenaliteTreeContentProvider implements ITreeContentProvider {
        @Override
        public void dispose() {
        }

        @Override
        public Object[] getChildren(final Object parentElement) {
            final Collection<Object> res = new ArrayList<Object>();
            if (parentElement instanceof ObjStep) {
                final ObjStep etape = (ObjStep) parentElement;
                res.addAll(etape.getSteps());
                res.addAll(etape.getPenalites());
                if (!etape.getBalises().isEmpty()) {
                    res.add(new Balises(etape));
                }
            } else if (parentElement instanceof ParametrageStepPageModel) {
                res.add(model.getStepModel().getStep());
            } else if (parentElement instanceof Balises) {
                res.addAll(((Balises) parentElement).getBalises());
            }
            return res.toArray();
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public Object getParent(final Object element) {
            if (element instanceof AbstractGetInfo) {
                return ((AbstractGetInfo) element).getParent();
            } else if (element instanceof Balises) {
                return ((Balises) element).getParent();
            }
            return null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            final Collection<Object> res = new ArrayList<Object>();
            boolean hasChildren = false;
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                res.addAll(etape.getSteps());
                res.addAll(etape.getPenalites());
                hasChildren = !etape.getBalises().isEmpty();
            } else if (element instanceof Balises) {
                res.addAll(((Balises) element).getBalises());
            }
            return hasChildren || !res.isEmpty();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    public static class Balises {
        private final ObjStep step;

        Balises(final ObjStep step) {
            this.step = step;
        }

        public Collection<ObjBalise> getBalises() {
            return Collections2.filter(step.getBalises(), new Predicate<Balise>() {
                @Override
                public boolean apply(final Balise balise) {
                    return !(START_TYPE_BALISE.equals(balise.getType()) || END_TYPE_BALISE.equals(balise.getType()));
                }
            });
        }

        public String getLib() {
            final StringBuilder builder = new StringBuilder("Balises (");
            int nbObligatoires = 0;
            int nbOptionnels = 0;
            for (final Balise balise : getBalises()) {
                if (TYPE_PAS_OBLIGATOIRE.equals(balise.getType())) {
                    nbOptionnels++;
                } else if (TYPE_OBLIGATOIRE.equals(balise.getType())) {
                    nbObligatoires++;
                }
            }
            if (nbObligatoires > 0) {
                builder.append(" O=").append(nbObligatoires);
            }
            if (nbOptionnels > 0) {
                builder.append(" F=").append(nbOptionnels);
            }
            return builder.append(" T=").append(getBalises().size()).append(')').toString();
        }

        public ObjStep getParent() {
            return step;
        }
    }

    private class SelectionTreeChanged implements ISelectionChangedListener {
        private final IManagedForm managedForm;
        private final IFormPart    spart;

        SelectionTreeChanged(final IManagedForm managedForm, final IFormPart spart) {
            this.managedForm = managedForm;
            this.spart = spart;
        }

        @Override
        public void selectionChanged(final SelectionChangedEvent event) {
            if (detailsPart.getCurrentPage() != null) {
                if (detailsPart.getCurrentPage().isDirty()) {
                    detailsPart.getCurrentPage().commit(true);
                }
            }
            managedForm.fireSelectionChanged(spart, event.getSelection());
        }
    }

    static class DataControlerListener implements IControlerListener {
        private final StepDetailsBlockPage stepDetailsBlockPage;
        private final StructuredViewer     viewer;

        DataControlerListener(final StepDetailsBlockPage stepDetailsBlockPage, final StructuredViewer viewer) {
            this.stepDetailsBlockPage = stepDetailsBlockPage;
            this.viewer = viewer;
        }

        @Override
        public void dataChanged(final IData obj, final String type, final String property) {
            if (viewer != null) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        viewer.refresh(obj);
                        viewer.reveal(obj);
                    }
                });
            } else {
                stepDetailsBlockPage.update();
            }
        }
    }
}
