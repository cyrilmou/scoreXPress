package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.StandardToolKit;
import fr.cm.common.widget.composite.CommonCompositeBuilder;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableColumnBuilder;
import fr.cm.common.widget.table.TableColumnModierListener;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjBalise;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.TableConfigurationException;
import fr.cm.scorexpress.ihm.editor.modifier.BooleanCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.GetInfoLabelProvider;
import fr.cm.scorexpress.ihm.editor.modifier.MyComboBoxCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.MyTextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.common.widget.composite.CompositeBuilders.createCompositeBuilder;
import static fr.cm.scorexpress.core.model.Balise.*;
import static fr.cm.scorexpress.core.model.ColTableType.BOOLEAN;
import static fr.cm.scorexpress.core.model.ConfigType.BALISES;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.EtapeConfigurationEditor_Pas_de_configuration_pour_la_table;
import static org.eclipse.jface.viewers.StyledCellLabelProvider.COLORS_ON_SELECTION;
import static org.eclipse.swt.layout.GridData.*;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TITLE_BAR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;
import static org.eclipse.ui.forms.widgets.Section.DESCRIPTION;

/**
 * Created by Isa on 20/05/14.
 */
public class BalisesPage extends FormPage implements IDetailsPage {
    public static final String  CONFIGURATION_EDITOR_ID = "fr.cm.chronos.etapeConfigurationEditor";
    private             Table   balisesTable            = null;
    //private             TableViewer balisesTableViewer      = null;
    private             boolean dirty                   = false;
    private       FormToolkit           toolkit;
    private       ObjStep               etape;
    private       ObjManifestation      manif;
    private final TableModel<ObjBalise> tableModel = new TableModel<ObjBalise>();
    private IStructuredSelection structuredSelection;

    public BalisesPage(final ObjManifestation manif) {
        super("", "Balises");
        this.manif = manif;
    }

    @Override
    public void initialize(final IManagedForm form) {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean isDirty() {
        return super.isDirty();
    }

    @Override
    public void commit(final boolean onSave) {

    }

    @Override
    public boolean setFormInput(final Object input) {
        return false;
    }


    @Override
    public void setFocus() {
        super.setFocus();
    }

    @Override
    public boolean isStale() {
        return false;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void createContents(final Composite parent) {
        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);

        toolkit = new FormToolkit(parent.getDisplay());

        final GridLayout gridLayoutTop = new GridLayout();
        gridLayoutTop.numColumns = 1;
        gridLayoutTop.makeColumnsEqualWidth = false;
        final GridData gridDataLabel = new GridData();
        gridDataLabel.grabExcessHorizontalSpace = true;
        gridDataLabel.horizontalAlignment = FILL;
        gridDataLabel.verticalAlignment = GridData.CENTER;
        gridDataLabel.grabExcessVerticalSpace = false;
        final GridData gridDataFill = new GridData();
        gridDataFill.grabExcessHorizontalSpace = true;
        gridDataFill.horizontalAlignment = FILL;
        gridDataFill.verticalAlignment = FILL;
        gridDataFill.grabExcessVerticalSpace = true;
        final ScrolledForm form = toolkit.createScrolledForm(parent);
        form.setText(i18n("BalisePage.configuration"));
        final Composite top = form.getBody();
        //top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        top.setLayout(gridLayoutTop);
        top.setLayoutData(gridDataFill);
        createCompositeBalises(top);


    }

    @Override
    public void selectionChanged(final IFormPart part, final ISelection selection) {
        structuredSelection = (IStructuredSelection) selection;
        if (structuredSelection.size() >= 1) {
            final StepDetailsBlockPage.Balises balises =
                    (StepDetailsBlockPage.Balises) structuredSelection.getFirstElement();
            etape = balises.getParent();
            //balisesTableViewer.setInput(etape);
            //ajustementAutomatique(balisesTable);
            tableModel.setRows(balises.getBalises());
        } else {
            etape = null;
        }
    }

    private void createCompositeBalises(final Composite parent) {
        final GridData gridDataCompBalise = new GridData();
        gridDataCompBalise.grabExcessHorizontalSpace = true;
        gridDataCompBalise.horizontalAlignment = FILL;
        gridDataCompBalise.verticalAlignment = FILL;
        gridDataCompBalise.grabExcessVerticalSpace = false;
        gridDataCompBalise.minimumHeight = 200;
        gridDataCompBalise.minimumWidth = 200;
        gridDataCompBalise.heightHint = 200;
        final GridData gridDataBaliseTable = new GridData();
        gridDataBaliseTable.grabExcessHorizontalSpace = true;
        gridDataBaliseTable.horizontalAlignment = FILL;
        gridDataBaliseTable.verticalAlignment = FILL;
        gridDataBaliseTable.horizontalSpan = 2;
        gridDataBaliseTable.grabExcessVerticalSpace = true;
        gridDataBaliseTable.minimumHeight = 200;
        final GridLayout gridCompBaliseLayout = new GridLayout();
        gridCompBaliseLayout.numColumns = 2;
        final GridData gridDataExp = new GridData();
        gridDataExp.grabExcessHorizontalSpace = true;
        gridDataExp.horizontalAlignment = FILL;
        gridDataExp.verticalAlignment = FILL;
        final Section expandableComp = toolkit.createSection(parent, TWISTIE | DESCRIPTION | TITLE_BAR);
        expandableComp.setText(i18n("BalisePage.details"));
        expandableComp.setLayout(new GridLayout());
        expandableComp.setLayoutData(gridDataExp);
        expandableComp.marginWidth = 10;
        expandableComp.setDescription(i18n("BalisePage.explication"));

        //final Composite compBalises = toolkit.createComposite(expandableComp, BORDER);
        final CompositeBuilder compositeBuilder =
                createCompositeBuilder(new StandardToolKit(), expandableComp, SWT.NONE).withLayout(gridCompBaliseLayout)
                                                                                       .withLayoutData(
                                                                                               gridDataCompBalise);

        final ObjConfig config = manif.getConfiguration().getConfig(BALISES);
        if (config == null) {
            throw new TableConfigurationException(EtapeConfigurationEditor_Pas_de_configuration_pour_la_table);
        }

        balisesTable = createTable(compositeBuilder, config);

        //balisesTableViewer = new TableViewer(compBalises, SWT.MULTI | SWT.FULL_SELECTION);
        //balisesTable = balisesTableViewer.getTable();
        //balisesTable.setHeaderVisible(true);
        //balisesTable.setLayoutData(gridDataBaliseTable);
        //balisesTable.setLinesVisible(true);
        balisesTable.setSize(new Point(200, 200));
        expandableComp.setClient(compositeBuilder.getControl());
        expandableComp.setExpanded(true);

        //updateBalises();
    }

    private Table createTable(final CommonCompositeBuilder<Composite, CompositeBuilder> builder,
                              final ObjConfig config) {
        for (final ColTable colTable : config.getColTableAll()) {
            tableModel.addColumn(colTable.getChamp());
        }

        final TableBuilder<ObjBalise> tableBuilder =
                builder.addTable(tableModel, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | COLORS_ON_SELECTION)
                       .withLine(true);//.selectionProvider(getSite());
        tableBuilder.withLayoutData(new GridData(FILL_BOTH | GRAB_HORIZONTAL | GRAB_VERTICAL));

        final String[] balisesColumnName = new String[config.getColTable().size()];
        int i = 0;

        for (final ColTable colTable : config.getColTableAll()) {
            final TableColumnBuilder<ObjBalise> columnBuilder =
                    tableBuilder.addColumn(colTable.getChamp(), colTable.getLib(), colTable.getAlign());
            final DefaultInfoTableColumnModifier columnModifier =
                    new DefaultInfoTableColumnModifier(colTable.getChamp(), colTable.isModifiable());
            columnBuilder.withWidth(colTable.getWidth()).movable(true).withToolTipText(colTable.getLib()).withRenderer(

                    new TableColumnRenderer<ObjBalise>() {
                        @Override
                        public String getColumnText(final ObjBalise element) {
                            return element.getInfoStr(colTable.getChamp());
                        }
                    }
                                                                                                                      )
                         .withModifier(columnModifier);
            columnModifier.setCellEditor(createCellEditor(colTable, tableBuilder.getTable()));
            columnModifier.addModifyListener(new TableColumnModierListener<ObjBalise>() {
                @Override
                public void onModified(final ObjBalise element) {
                    etape.balisesChanged();
                }
            });
            balisesColumnName[i] = colTable.getChamp();
        }

        tableBuilder.withLayoutData(new GridData(FILL_BOTH | GRAB_HORIZONTAL | GRAB_VERTICAL)).withHeader(true);

        addTableKeyMove(tableBuilder.getViewer());

        return tableBuilder.getTable();
    }

    private static void addTableKeyMove(final TableViewer tv) {
        final TableCursor cursor = new TableCursor(tv.getTable(), SWT.NONE);
        cursor.setEnabled(true);
        cursor.setVisible(false);
        tv.getTable().addKeyListener(new NavigateOnTableAction(tv, cursor));
    }

    private static void ajustementAutomatique(final Table table) {
        table.setVisible(false);
        for (int j = 0; j < table.getColumnCount(); j++) {
            table.getColumn(j).pack();
        }
        table.setVisible(true);
    }

    private static CellEditor createCellEditor(final ColTable colTable, final Table table) {
        final CellEditor returnEditor;
        if (colTable.getChoix() != null) {
            final String[] datas = {TYPE_OBLIGATOIRE, TYPE_ORDONNEE, TYPE_PAS_OBLIGATOIRE, TYPE_BONUS, TYPE_PENALITY};
            returnEditor = new MyComboBoxCellEditor(table, datas);
        } else if (colTable.getType().equals(BOOLEAN)) {
            returnEditor = new BooleanCellEditor(table, SWT.CHECK);
        } else {
            returnEditor = new MyTextCellEditor(table);
        }
        return returnEditor;
    }

    private static class NavigateOnTableAction implements KeyListener {
        private final ColumnViewer tv;
        private final TableCursor  cursor;

        private NavigateOnTableAction(final ColumnViewer tv, final TableCursor cursor) {
            this.tv = tv;
            this.cursor = cursor;
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            if (e.keyCode == SWT.F2 || e.keyCode == SWT.KEY_MASK) {
                final Object element = ((IStructuredSelection) tv.getSelection()).getFirstElement();
                final int column = cursor.getColumn();
                tv.editElement(element, column);
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
        }

    }

    private static class BaliseLabelProvider extends GetInfoLabelProvider {
        private BaliseLabelProvider(final ObjConfig config) {
            super(config);
        }
    }

}
