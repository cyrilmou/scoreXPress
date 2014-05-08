package fr.cm.scorexpress.ihm.editor.page;

import com.google.common.base.Predicates;
import static com.google.common.collect.Lists.newArrayList;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ColTableBuilder;
import static fr.cm.scorexpress.core.model.ConfigType.PENALITY;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjDossard;
import static fr.cm.scorexpress.core.model.ObjDossard.VAR_DOSSARD_PENALITY;
import static fr.cm.scorexpress.core.model.StepUtil.getStepWithPenaliteSaisie;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.DefaultColTableColumnModifier;
import fr.cm.scorexpress.model.StepModel;

import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;

public class TempsResultatPageModel {
    private final ObjConfig config;
    private final StepModel stepModel;
    private final FormModel titleModel = new FormModel("Gestionnaire de temps des concurrents");
    private final Collection<ColTable> columnModels;
    public static final String COLUMN_PREFIX = "TEMPSPAGE";
    private final TableModel<ObjDossard> tableModel;
    private final ButtonModel printButtonModel = new ButtonModel("Print");

    public TempsResultatPageModel(final StepModel stepModel) {
        this.stepModel = stepModel;
        config = stepModel.getStep().getManif().getConfiguration().getConfig(PENALITY);
        columnModels = createColumnModels();
        tableModel = createTableModel();
        updateData();
    }

    public String getTitle() {
        return stepModel.getName();
    }

    public FormModel getTitleModel() {
        return titleModel;
    }

    public void dispose() {
    }

    public Collection<ColTable> getColumnModels() {
        return unmodifiableCollection(columnModels);
    }

    public String getManifName() {
        return stepModel.getStep().getManif().getNom();
    }

    public TableModel<ObjDossard> getTableModel() {
        return tableModel;
    }

    public ButtonModel getPrintButtonModel() {
        return printButtonModel;
    }

    private void updateData() {
        tableModel.setRows(stepModel.getStep().getDossards());
        tableModel.autoResizeColumns();
    }

    private Collection<ColTable> createColumnModels() {
        final Collection<ColTable> columns = newArrayList(config.getColTableAll());
        int i = 1;
        for (final ObjStep step : getStepWithPenaliteSaisie(stepModel.getStep())) {
            columns.add(new ColTableBuilder(COLUMN_PREFIX + i, step.getLib()).withChampSecondaire(VAR_DOSSARD_PENALITY)
                    .modifiable()
                    .withWidth(50)
                    .withElement(step)
                    .create());
            i++;
        }
        return columns;
    }

    private TableModel<ObjDossard> createTableModel() {
        final TableModel<ObjDossard> model = new TableModel<ObjDossard>(Predicates.<ObjDossard>alwaysTrue());
        for (final ColTable colTable : getColumnModels()) {
            model.addColumn(colTable.getChamp())
                    .withAutoResize(true)
                    .withModifier(new DefaultColTableColumnModifier(colTable));
        }
        return model;
    }
}
