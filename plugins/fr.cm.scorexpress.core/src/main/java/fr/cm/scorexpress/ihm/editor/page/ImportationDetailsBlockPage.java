package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.common.widget.composite.SectionBuilder;
import fr.cm.common.widget.table.TableAdapter;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjColTable;
import fr.cm.scorexpress.ihm.editor.ImportationModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import static fr.cm.common.widget.composite.CompositeBuilders.createSection;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.V_SCROLL;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;

public class ImportationDetailsBlockPage extends MasterDetailsBlock {
    private final ImportationModel importationModel;

    public ImportationDetailsBlockPage(final ImportationModel importationModel) {
        this.importationModel = importationModel;
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        final MyToolkit myToolkit = new MyFormToolkit(managedForm.getToolkit());

        final SectionBuilder sectionBuilder = createSection(myToolkit,
                                                            parent, Section.DESCRIPTION | Section.TITLE_BAR).withText(
                i18n("ImportationDetailsBlock.Config"))
                .withDescription(i18n("ImportationDetailsBlock.Info")).withPaintBorder();
        final CompositeBuilder client = sectionBuilder.addClient(SWT.WRAP).withLayout(new GridLayout(2, false));

        final TableModel<ColTable> tableModel = importationModel.getColTableTableModel();
        final TableBuilder<ColTable> tableBuilder = client.addTable(tableModel, V_SCROLL | BORDER);
        tableBuilder.withLayoutData(new GridData(FILL_BOTH));
        tableBuilder.addColumn("Elements", "Elements", SWT.NONE)
                    .withWidth(400)
                    .withRenderer(new ColTableColumnRenderer());

        sectionBuilder.addPartToForm(managedForm);

        importationModel.getColTableTableModel().addWidgetListener(new TableAdapter() {
            @Override
            public void selectionChange() {
                managedForm.fireSelectionChanged(sectionBuilder.getPart(),
                                                 new StructuredSelection(importationModel.getColTableTableModel()
                                                                                         .getSelection()
                                                                                         .toArray()));
            }
        });
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
        detailsPart.registerPage(ObjColTable.class, new ImportationDetailsPage(importationModel));
    }

    private static class ColTableColumnRenderer extends TableColumnRenderer<ColTable> {
        @Override
        public String getColumnText(final ColTable element) {
            return element.getChamp();
        }

        @Override
        public int compare(final ColTable elem1, final ColTable elem2) {
            return 0;
        }
    }
}
