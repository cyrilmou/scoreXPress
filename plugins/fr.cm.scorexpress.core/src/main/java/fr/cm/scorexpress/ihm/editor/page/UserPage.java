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
import fr.cm.scorexpress.core.model.ColTableUtils;
import fr.cm.scorexpress.core.model.ObjDossard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import static fr.cm.common.widget.composite.CompositeBuilders.createScrollFormBuilder;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.print.PrintPreview.openPrintPreview;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;
import static org.eclipse.swt.layout.GridData.FILL_VERTICAL;

public class UserPage extends FormPage {
    private Table table = null;
    private final UserPageModel model;
    private static final String USER_PAGE_ID = "fr.cm.scorexpress.UserPage";
    private final        Image  ACTIVATE     = getImg(IMG_CHECK_ACTIVATE);
    private final        Image  DESACTIVATE  = getImg(IMG_CHECK_DESACTIVATE);

    public UserPage(final UserPageModel userPageModel) {
        super(USER_PAGE_ID, i18n("StepEditor.Participant"));
        model = userPageModel;
        model.getPrintButtonModel().addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                super.click();
                print();
            }
        });
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledFormBuilder builder =
                createScrollFormBuilder(new MyFormToolkit(managedForm.getToolkit()), managedForm, model.getTitleModel())
                        .withBackgroungImage(getImg(IMG_FORM_BG))
                        .withLayout(new GridLayout(2, false))
                        .withLayoutData(new GridData(FILL_BOTH));
        createTable(builder);
        createActionUser(builder);
        new Clipboard(managedForm.getForm().getBody().getDisplay());
    }

    private void createTable(final CommonCompositeBuilder parentBuilder) {
        final TableModel<ObjDossard> tableModel = model.getTableModel();
        final TableBuilder<ObjDossard> tableBuilder =
                parentBuilder.addTable(tableModel, SWT.MULTI | SWT.FULL_SELECTION).withHeader(true).withLine(true);

        for (final ColTable colTable : model.getColumnModels()) {
            tableBuilder.addColumn(colTable.getChamp(), colTable.getLib(), colTable.getAlign())
                        .withWidth(colTable.getWidth())
                        .movable(true)
                        .withRenderer(new DossardColumnRenderer(colTable));
        }
        tableBuilder.withLayoutData(new GridData(GridData.FILL_BOTH));
        table = tableBuilder.getTable();
        tableModel.autoResizeColumns();
    }

    @Override
    public void dispose() {
        model.dispose();
        super.dispose();
    }

    private void createActionUser(final AbstractCompositeBuilder parentBuilder) {
        final CompositeBuilder builder = parentBuilder.addComposite(SWT.NONE)
                                                      .withLayout(new GridLayout())
                                                      .withLayoutData(new GridData(FILL_VERTICAL));
        builder.addButton(model.getAddUserButtonModel(), SWT.NONE);
        builder.addButton(model.getRemoveSelectedUserButtonModel(), SWT.NONE);
        builder.addButton(model.getPrintButtonModel(), SWT.NONE);
    }

    public void print() {
        final AbstractList<String> titles = new ArrayList<String>();
        titles.add(model.getManifName());
        titles.add(i18n("UserPage.LISTE_CONCURRENT"));
        final String[] titlesStr = new String[titles.size()];
        int i = 0;
        for (Iterator<String> iter = titles.iterator(); iter.hasNext(); i++) {
            titlesStr[i] = iter.next();

        }
        final DateFormat sdf = new SimpleDateFormat("yyMMdd");
        openPrintPreview(table, titlesStr, i18n("UserPage.CONCURRENT") + sdf.format(new Date()));
    }

    private class DossardColumnRenderer extends TableColumnRenderer<ObjDossard> {
        private final ColTable colTable;

        private DossardColumnRenderer(final ColTable colTable) {
            this.colTable = colTable;
        }

        @Override
        public String getColumnText(final ObjDossard dossard) {
            if (ColTableUtils.isBooleanType(colTable)) {
                return "";
            } else {
                return dossard.getInfoStr(colTable.getChamp());
            }
        }

        @Override
        public Image getColumnImage(final ObjDossard dossard) {
            if (ColTableUtils.isBooleanType(colTable)) {
                final Object value = dossard.getInfo(colTable.getChamp());
                return value != null && (Boolean) value ? ACTIVATE : DESACTIVATE;
            } else {
                return null;
            }
        }

        @Override
        public Comparator<ObjDossard> getComparator(final ObjDossard dossard) {
            if (ColTableUtils.isBooleanType(colTable)) {
                return new Comparator<ObjDossard>() {
                    @Override
                    public int compare(final ObjDossard elem1, final ObjDossard elem2) {
                        final Boolean var1;
                        try {
                            final Object value = elem1.getInfo(colTable.getChamp());
                            var1 = value != null ? (Boolean) value : false;
                        } catch (Exception e) {
                            return -1;
                        }
                        final Boolean var2;
                        try {
                            final Object value = elem2.getInfo(colTable.getChamp());
                            var2 = value != null ? (Boolean) value : false;
                        } catch (Exception e) {
                            return 1;
                        }
                        return var1.compareTo(var2);
                    }
                };
            }
            return super.getComparator(dossard);
        }

        @Override
        public int compare(final ObjDossard elem1, final ObjDossard elem2) {
            final int compare = super.compare(elem1, elem2);
            if (compare == 0) {
                return elem1.compareTo(elem2);
            }
            return compare;
        }
    }
}
