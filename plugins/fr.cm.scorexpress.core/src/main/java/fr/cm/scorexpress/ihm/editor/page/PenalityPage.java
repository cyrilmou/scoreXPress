package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.ScrolledFormBuilder;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.composite.AbstractCompositeBuilder;
import fr.cm.common.widget.composite.CommonCompositeBuilder;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjDossard;
import fr.cm.scorexpress.core.model.StepUtil;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.print.IPrintable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

import static fr.cm.common.widget.composite.CompositeBuilders.createScrollFormBuilder;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_FORM_BG;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.print.PrintPreview.openPrintPreview;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eclipse.swt.SWT.MULTI;
import static org.eclipse.swt.layout.GridData.*;

public class PenalityPage extends FormPage implements IPrintable {
    public static final String PENALITY_PAGE_ID = "fr.cm.scorexpress.PenalityPage";

    private final PenalityPageModel model;
    private       Table         table         = null;
    private final ButtonAdapter printListener = new ButtonAdapter() {
        @Override
        public void click() {
            print();
        }
    };

    public PenalityPage(final PenalityPageModel penalityPageModel) {
        super(PENALITY_PAGE_ID, i18n("StepEditor.Penalite"));
        model = penalityPageModel;
        model.getPrintButton().addWidgetListener(printListener);
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledFormBuilder builder =
                createScrollFormBuilder(new MyFormToolkit(managedForm.getToolkit()), managedForm,
                                        model.getTitleFormModel()).withBackgroungImage(getImg(IMG_FORM_BG))
                        .withLayout(new GridLayout(2, false)).withLayoutData(new GridData(FILL_BOTH));
        createActionUser(builder);
        createTable(builder);
    }

    @Override
    public void dispose() {
        model.getPrintButton().removeWidgetListener(printListener);
        super.dispose();
    }

    private void createActionUser(final AbstractCompositeBuilder parentBuilder) {
        final GridData gridCompData = new GridData(FILL_VERTICAL | VERTICAL_ALIGN_FILL);
        gridCompData.grabExcessHorizontalSpace = false;
        gridCompData.verticalAlignment = FILL_VERTICAL;
        final CompositeBuilder builder =
                parentBuilder.addComposite(SWT.NONE).withLayout(new GridLayout()).withLayoutData(gridCompData);
        builder.addButton(model.getPrintButton(), SWT.NONE);
    }

    public void createTable(final CommonCompositeBuilder parentBuilder) {
        final TableModel<ObjDossard> tableModel = model.getTableModel();
        final TableBuilder<ObjDossard> tableBuilder =
                parentBuilder.addTable(tableModel, MULTI | FULL_SELECTION).withHeader(true).withLine(true);
        for (final ColTable colTable : model.getColumnModels()) {
            tableBuilder.addColumn(colTable.getChamp(), colTable.getLib(), colTable.getAlign()).
                    withWidth(colTable.getWidth()).movable(true).withRenderer(new PenalityColumnRenderer(colTable));
        }
        tableBuilder.withLayoutData(new GridData(FILL_BOTH));
        tableModel.autoResizeColumns();
        table = tableBuilder.getTable();
    }

    @Override
    public void print() {
        final AbstractList<String> titles = new ArrayList<String>();
        final String title = model.ManifName();
        final String title2 = i18n("PenalityPage.Liste_penalite");
        titles.add(title);
        titles.add(title2);
        final String[] titlesStr = new String[titles.size()];
        int i = 0;
        for (Iterator<String> iter = titles.iterator(); iter.hasNext(); i++) {
            titlesStr[i] = iter.next();

        }
        openPrintPreview(table, titlesStr, i18n("PenalityPage.Penalites"));
    }

    private static class PenalityColumnRenderer extends TableColumnRenderer<ObjDossard> {
        public static final String PREFIX = "PENALITYPAGE";
        private final ColTable colTable;

        PenalityColumnRenderer(final ColTable colTable) {
            this.colTable = colTable;
        }

        @Override
        public String getColumnText(final ObjDossard element) {
            final String champs = colTable.getChamp();
            if (champs.contains(PREFIX)) {
                final ObjDossard dossard = getDossard(colTable, element);
                if (dossard == null) {
                    return EMPTY;
                }
                final String property = colTable.getChampSecondaire();
                final Object res = dossard.getInfo(property);
                if (res == null) {
                    return i18n("PenalityPage.Error");
                } else {
                    return ((Date2) res).toStringNotNull();
                }
            } else {
                return element.getInfoStr(champs);
            }
        }

        @Override
        public int compare(final ObjDossard elem1, final ObjDossard elem2) {
            return 0;
        }

        private ObjDossard getDossard(final ColTable colTable, final Object element) {
            final String numDossard = ((ObjDossard) element).getNum();
            return StepUtil.getDossard(numDossard, (ObjStep) colTable.getElement());
        }
    }
}
