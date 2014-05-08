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
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.IDossards;
import fr.cm.scorexpress.core.model.ObjDossard;
import fr.cm.scorexpress.core.model.StepUtil;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.ihm.print.IPrintable;
import fr.cm.scorexpress.ihm.print.PrintPreview;
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
import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eclipse.swt.SWT.MULTI;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;
import static org.eclipse.swt.layout.GridData.FILL_VERTICAL;

public class TempsResultatPage extends FormPage implements IPrintable {
    public static final String TEMPS_RESULTAT_PAGE_ID = "fr.cm.scorexpress.TempsResultatPage";
    private             Table  table                  = null;
    private final TempsResultatPageModel model;

    public TempsResultatPage(final TempsResultatPageModel tempsResultatPageModel) {
        super(TEMPS_RESULTAT_PAGE_ID, i18n("StepEditor.TEMPS"));
        model = tempsResultatPageModel;
        model.getTitle();
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
        createToolBar(builder);
        createTable(builder);
    }

    private void createToolBar(final AbstractCompositeBuilder parentBuilder) {
        final CompositeBuilder builder = parentBuilder.addComposite(SWT.NONE)
                                                      .withLayout(new GridLayout())
                                                      .withLayoutData(new GridData(FILL_VERTICAL));
        builder.addButton(model.getPrintButtonModel(), SWT.NONE);
    }

    public void createTable(final CommonCompositeBuilder parentBuilder) {
        final TableModel<ObjDossard> tableModel = model.getTableModel();
        final TableBuilder<ObjDossard> tableBuilder =
                parentBuilder.addTable(tableModel, MULTI | FULL_SELECTION).withHeader(true).withLine(true);
        for (final ColTable colTable : model.getColumnModels()) {
            tableBuilder.addColumn(colTable.getChamp(), colTable.getLib(), colTable.getAlign()).
                    withWidth(colTable.getWidth())
                        .movable(true)
                        .withRenderer(new ResultatDossardColumnRenderer(colTable));
        }
        tableBuilder.withLayoutData(new GridData(FILL_BOTH));
        tableModel.autoResizeColumns();
        table = tableBuilder.getTable();
    }

    @Override
    public void print() {
        final AbstractList<String> titles = new ArrayList<String>();
        titles.add(model.getManifName());
        titles.add("Subtitution des temps des concurrents");
        final String[] titlesStr = new String[titles.size()];
        int i = 0;
        for (Iterator<String> iter = titles.iterator(); iter.hasNext(); i++) {
            titlesStr[i] = iter.next();
        }
        PrintPreview.openPrintPreview(table, titlesStr, "Temps concurrents");
    }

    @Override
    public void dispose() {
        model.dispose();
        super.dispose();
    }

    private static class ResultatDossardColumnRenderer extends TableColumnRenderer<ObjDossard> {
        private final ColTable colTable;

        ResultatDossardColumnRenderer(final ColTable colTable) {
            this.colTable = colTable;
        }

        @Override
        public String getColumnText(final ObjDossard element) {
            if (colTable.getElement() != null) {
                final AbstractGetInfo dossard = getDossard(colTable, element);
                if (dossard == null) {
                    return "";
                }
                final String property = colTable.getChampSecondaire();
                final Object res = dossard.getInfo(property);
                if (res == null) {
                    return "-Error-";
                } else {
                    return ((Date2) res).toStringNotNull();
                }
            } else {
                return element.getInfoStr(colTable.getChamp());
            }
        }

        private static AbstractGetInfo getDossard(final ColTable colTable, final Object element) {
            final String numDossard = ((ObjDossard) element).getNum();
            return StepUtil.getDossard(numDossard, (IDossards) colTable.getElement());
        }
    }
}
