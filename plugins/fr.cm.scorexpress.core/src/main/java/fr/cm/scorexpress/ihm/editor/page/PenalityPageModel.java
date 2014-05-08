package fr.cm.scorexpress.ihm.editor.page;

import com.google.common.base.Predicates;
import static com.google.common.collect.Lists.newArrayList;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.*;
import static fr.cm.scorexpress.core.model.ObjDossard.VAR_DOSSARD_PENALITY;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.DefaultColTableColumnModifier;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.StepModel;

import java.util.Collection;

public class PenalityPageModel {
    private final StepModel stepModel;
    private final FormModel titleFormMode;
    private final ButtonModel printButton;
    private final TableModel<ObjDossard> tableModel;
    private final Collection<ColTable> tableColumns;
    private final ObjConfig config;
    private static final String COLUMN_PREFIX = "PENALITYPAGE";

    public PenalityPageModel(final StepModel stepModel) {
        this.stepModel = stepModel;
        titleFormMode = new FormModel(getTitle());
        printButton = new ButtonModel(i18n("PenalityPage.Print"));
        config = stepModel.getStep().getManif().getConfiguration().getConfig(ConfigType.PENALITY);
        tableModel = createModelTable();
        tableColumns = createTableColumn();
        updateData();
    }

    private Collection<ColTable> createTableColumn() {
        final Collection<ColTable> columns = newArrayList();
        columns.addAll(config.getColTableAll());
        int i = 1;
        for (final ObjStep step : StepUtil.getStepWithPenaliteSaisie(stepModel.getStep())) {
            final String property = COLUMN_PREFIX + i;
            final ColTable colTable = new ColTableBuilder(property, step
                    .getLib()).withChampSecondaire(VAR_DOSSARD_PENALITY)
                    .modifiable()
                    .withWidth(50)
                    .withElement(step)
                    .create();
            columns.add(colTable);
            i++;
            tableModel.addColumn(property)
                    .withAutoResize(true)
                    .withModifier(new DefaultColTableColumnModifier(colTable));
        }
        return columns;
    }

    private TableModel<ObjDossard> createModelTable() {
        final TableModel model = new TableModel(Predicates.alwaysTrue());
        model.setRows(stepModel.getStep().getDossards());
        for (final ColTable colTable : getConfig().getColTableAll()) {
            model.addColumn(colTable.getChamp())
                    .withAutoResize(true)
                    .withModifier(new DefaultColTableColumnModifier(colTable));
        }
        return model;
    }

    public String getLabel() {
        return stepModel.getStep().getLib();
    }

    private String getTitle() {
        return i18n("PenalityPage.Gestionnaire_penalite");
    }

    public FormModel getTitleFormModel() {
        return titleFormMode;
    }

    public ButtonModel getPrintButton() {
        return printButton;
    }

    public TableModel<ObjDossard> getTableModel() {
        return tableModel;
    }

    public Collection<ColTable> getColumnModels() {
        return tableColumns;
    }

    public ObjConfig getConfig() {
        return config;
    }

    public String ManifName() {
        return stepModel.getStep().getManif().getNom();
    }

    private void updateData() {
        tableModel.setRows(stepModel.getStep().getDossards());
    }

}
