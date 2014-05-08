package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.model.ManifModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.*;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import static fr.cm.common.widget.button.ButtonBuilder.createButton;
import static fr.cm.common.widget.label.LabelBuilder.createLabel;
import static fr.cm.common.widget.table.TableBuilder.createTable;
import static fr.cm.common.widget.text.TextBuilder.createText;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static org.eclipse.swt.SWT.H_SCROLL;
import static org.eclipse.swt.SWT.V_SCROLL;
import static org.eclipse.swt.layout.GridData.*;

public class ManifDetailsBlockPage extends MasterDetailsBlock {
    private final ManifModel manifModel;

    public ManifDetailsBlockPage(final ManifModel manifModel) {
        this.manifModel = manifModel;
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        final FormToolkit toolkit = managedForm.getToolkit();
        final MyToolkit myToolkit = new MyFormToolkit(toolkit);
        final Section section = toolkit.createSection(parent, Section.TITLE_BAR);
        section.setText(manifModel.getMasterSectionTitle());
        section.setDescription(manifModel.getMasterSectionDescription());
        section.marginWidth = 10;
        section.marginHeight = 5;
        final Composite client = toolkit.createComposite(section, SWT.WRAP);
        client.setLayout(new GridLayout(2, false));
        final Composite infoManif = toolkit.createComposite(client, SWT.NONE);
        infoManif.setLayout(new GridLayout(2, false));
        final GridData gd = new GridData(HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = 2;
        infoManif.setLayoutData(gd);
        createLabel(myToolkit, infoManif, manifModel.getNameLabelModel(), SWT.NONE);
        createText(myToolkit, infoManif, manifModel.getNameTextModel(), SWT.BORDER)
                .withLayoutData(new GridData(HORIZONTAL_ALIGN_FILL | GRAB_HORIZONTAL));
        final GridData tableGridData = new GridData(FILL_BOTH);
        tableGridData.verticalSpan = 2;
        final TableModel<ObjStep> tableModel = manifModel.getStepTableModel();
        final TableBuilder tableBuilder = createTable(myToolkit,
                                                      client,
                                                      tableModel,
                                                      SWT.NONE).withHeader(true);
        tableBuilder.addColumn("Etape", "Etape", V_SCROLL | H_SCROLL)
                    .withMinWidth(300)
                    .withRenderer(new StepColumnRenderer());
        tableBuilder.withLayoutData(tableGridData);
        toolkit.paintBordersFor(client);
        createButton(myToolkit, client, manifModel.getAddStepButtonModel(), SWT.NONE)
                .withLayoutData(new GridData(HORIZONTAL_ALIGN_FILL | VERTICAL_ALIGN_BEGINNING));
        createButton(myToolkit, client, manifModel.getRemoveStepButtonModel(), SWT.NONE)
                .withLayoutData(new GridData(HORIZONTAL_ALIGN_FILL | VERTICAL_ALIGN_BEGINNING));
        section.setClient(client);
        final SectionPart sectionPart = new SectionPart(section);
        managedForm.addPart(sectionPart);
        tableBuilder.getViewer().addSelectionChangedListener(new SelectionChanged(managedForm, sectionPart));
    }

    @Override
    protected void createToolBarActions(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        final IAction hAction = new Action("hor", Action.AS_RADIO_BUTTON) {
            @Override
            public void run() {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        hAction.setChecked(true);
        hAction.setToolTipText("Orientation horizontal");
        hAction.setImageDescriptor(getImageDescriptor(IMG_HORIZONTAL));
        final IAction vAction = new Action("ver", Action.AS_RADIO_BUTTON) {
            @Override
            public void run() {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vAction.setChecked(false);
        vAction.setToolTipText("Orientation vertical");
        vAction.setImageDescriptor(getImageDescriptor(IMG_VERTICAL));
        form.getToolBarManager().add(hAction);
        form.getToolBarManager().add(vAction);
    }

    @Override
    protected void registerPages(final DetailsPart detailsPart) {
        detailsPart.registerPage(ObjStep.class, new EtapeDetailsPage());
    }

    private static class SelectionChanged implements ISelectionChangedListener {
        private final IManagedForm managedForm;
        private final IFormPart    sectionPart;

        SelectionChanged(final IManagedForm managedForm, final IFormPart sectionPart) {
            this.managedForm = managedForm;
            this.sectionPart = sectionPart;
        }

        @Override
        public void selectionChanged(final SelectionChangedEvent event) {
            managedForm.fireSelectionChanged(sectionPart, event
                    .getSelection());
        }
    }

    private static class StepColumnRenderer extends TableColumnRenderer<ObjStep> {

        private final Image imgEtape       = getImg(IMG_ETAPE);
        private final Image imgArretChrono = getImg(IMG_ARRETCHRONO);
        private final Image imgDisable     = getImg(IMG_PENALITY_DESACTIVATE);

        @Override
        public Image getColumnImage(final ObjStep step) {
            if (!step.isActif()) {
                return imgDisable;
            }
            if (step.isArretChrono()) {
                return imgArretChrono;
            } else {
                return imgEtape;
            }
        }

        @Override
        public String getColumnText(final ObjStep step) {
            String message = " [";
            if (step.getBaliseDepart() == null) {
                message += ".. ";
            } else {
                message += step.getBaliseDepart() + ' ';
            }
            if (step.getBaliseArrivee() == null) {
                message += "..]";
            } else {
                message += step.getBaliseArrivee() + ']';
            }
            return step.getLib() + message;
        }

        @Override
        public int compare(final ObjStep elem1, final ObjStep elem2) {
            return elem1.getLib().compareTo(elem2.getLib());
        }
    }
}
