package fr.cm.scorexpress.ihm.editor.page;

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
import fr.cm.scorexpress.core.util.PenalityUtils;
import fr.cm.scorexpress.ihm.view.dnd.IDataDragListener;
import fr.cm.scorexpress.ihm.view.dnd.PenaliteTransfer;
import fr.cm.scorexpress.ihm.view.dnd.PenaliteTreeDropAdapter;
import fr.cm.scorexpress.ihm.view.dnd.StepTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
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

import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;
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
        toolbar.addButton(model.getAddStepButtonModel(), SWT.NONE)
               .withImage(imgEtape)
               .withToolTip(i18n("ConfigStepPage.ADD_STEP_TIP"));
        toolbar.addButton(model.getAddPenalityButtonModel(), SWT.NONE)
               .withImage(imgPenalite)
               .withToolTip(i18n("ConfigStepPage.ADD_PENALITY_TIP"));
        toolbar.addButton(model.getAddBaliseButtonModel(), SWT.NONE)
               .withImage(imgBalise)
               .withToolTip(i18n("ConfigStepPage.ADD_BALISE_TIP"));
        toolbar.addButton(model.getRemoveStepButtonModel(), SWT.NONE)
               .withImage(imgRemove)
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
        viewer.setLabelProvider(new PenaliteLabelProvider());
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
    }

    private static void dragAndDropTest(final TreeViewer viewer) {
        /* drag */
        final int ops = DROP_COPY | DROP_MOVE | CLIPBOARD;
        final Transfer[] transfers = new Transfer[]{StepTransfer.getStepTransfer(), PenaliteTransfer.getInstance()};
        viewer.addDragSupport(ops, transfers, new IDataDragListener(viewer));
        // /* drop */
        viewer.addDropSupport(ops, transfers, new PenaliteTreeDropAdapter(viewer));
    }

    public void update() {
        if (viewer != null) {
            viewer.refresh();
        }
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
                res.addAll(((Balises) parentElement).getbalises());
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
                res.addAll(((Balises) element).getbalises());
            }
            return hasChildren || !res.isEmpty();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    static class Balises {
        private final ObjStep step;

        Balises(final ObjStep step) {
            this.step = step;
        }

        public Collection<Balise> getbalises() {
            return step.getBalises();
        }

        public String getLib() {
            return "Balises (" + step.getBalises().size() + ')';
        }

        public ObjStep getParent() {
            return step;
        }
    }

    static class PenaliteLabelProvider implements ILabelProvider {
        private final Image imgArretChrono = getImg(IMG_ARRETCHRONO);
        private final Image imgDisable     = getImg(IMG_PENALITY_DESACTIVATE);
        private final Image imgEtape       = getImg(IMG_ETAPE);
        private final Image imgPenalite    = getImg(IMG_PENALITY);
        private final Image imgBalise      = getImg(IMG_BALISE);

        @Override
        public void addListener(final ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Image getImage(final Object element) {
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                if (!etape.isActif()) {
                    return imgDisable;
                }
                if (etape.isArretChrono()) {
                    return imgArretChrono;
                } else {
                    return imgEtape;
                }
            } else if (element instanceof ObjPenalite) {
                if (((ObjPenalite) element).isActivate()) {
                    return imgPenalite;
                } else {
                    return imgDisable;
                }
            } else if (element instanceof Balises) {
                return imgBalise;
            } else if (element instanceof ObjBalise) {
                return imgBalise;
            }
            return null;
        }

        @Override
        public String getText(final Object element) {
            if (element instanceof ObjPenalite) {
                final ObjPenalite penality = (ObjPenalite) element;
                return penality.getLib() + " (" + PenalityUtils.getTypePenaliteStr(penality) + ')';
            } else if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                String message = " [";
                if (etape.getBaliseDepart() == null) {
                    message += ".. ";
                } else {
                    message += etape.getBaliseDepart() + ' ';
                }
                if (etape.getBaliseArrivee() == null) {
                    message += "..]";
                } else {
                    message += etape.getBaliseArrivee() + ']';
                }
                return etape.getLib() + message;
            } else if (element instanceof Balise) {
                return ((Balise) element).getType() + '(' + ((Balise) element).getNum() + ')';
            } else if (element instanceof Balises) {
                return ((Balises) element).getLib();
            }
            return EMPTY;
        }

        @Override
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        @Override
        public void removeListener(final ILabelProviderListener listener) {
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
