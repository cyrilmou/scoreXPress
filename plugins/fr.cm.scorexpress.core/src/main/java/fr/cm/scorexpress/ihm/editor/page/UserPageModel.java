package fr.cm.scorexpress.ihm.editor.page;

import com.google.common.base.Predicates;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjDossard;
import fr.cm.scorexpress.ihm.editor.DefaultColTableColumnModifier;
import fr.cm.scorexpress.model.StepModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.ConfigType.DOSSARD_PARTICIPANTS;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static java.util.Collections.unmodifiableCollection;

public class UserPageModel implements PropertyChangeListener {
    private final StepModel              stepModel;
    private final ObjConfig              config;
    private final FormModel              titleModel;
    private final TableModel<ObjDossard> tableModel;
    private final Collection<ColTable>   columnModels;
    private final ButtonModel addUserButtonModel            = new ButtonModel(i18n("UserPage.PLUS"));
    private final ButtonModel removeSelectedUserButtonModel = new ButtonModel(i18n("UserPage.MOINS"));
    private final ButtonModel printButtonModel              = new ButtonModel(i18n("UserPage.PRINT"));
    private final AutoResizeColumn autoResizeContext;

    public UserPageModel(final StepModel stepModel) {
        this.stepModel = stepModel;
        autoResizeContext = stepModel.getAutoResizeColumn();
        config = stepModel.getStep().getManif().getConfiguration().getConfig(DOSSARD_PARTICIPANTS);
        titleModel = new FormModel(
                stepModel.isTeamMode() ? i18n("UserPage.GESTION_EQUIPE") : i18n("UserPage.GESTION_UTILISATEUR"));
        columnModels = createTableColumns();
        tableModel = createTableModel();
        autoResizeContext.addAutoResizeListener(new AutoResizeColumn.AutoResizeListener() {
            @Override
            public void autoResizeChanged(final boolean autoResize) {
                if (autoResize) {
                    tableModel.autoResizeColumns();
                }
            }
        });
        updateData();
        stepModel.getStep().addPropertyChangeListener(this);
    }

    private Collection<ColTable> createTableColumns() {
        return newArrayList(config.getColTableAll());
    }

    public StepModel getStepModel() {
        return stepModel;
    }

    public String getTitle() {
        return i18n("StepEditor.Participant") + ' ' + stepModel.getName();
    }

    public FormModel getTitleModel() {
        return titleModel;
    }

    public TableModel<ObjDossard> getTableModel() {
        return tableModel;
    }

    public Collection<ColTable> getColumnModels() {
        return unmodifiableCollection(columnModels);
    }

    public void dispose() {
        stepModel.getStep().removePropertyChangeListener(this);
    }

    public String getManifName() {
        return stepModel.getStep().getManif().getNom();
    }

    public ButtonModel getAddUserButtonModel() {
        return addUserButtonModel;
    }

    public ButtonModel getRemoveSelectedUserButtonModel() {
        return removeSelectedUserButtonModel;
    }

    public ButtonModel getPrintButtonModel() {
        return printButtonModel;
    }

    private TableModel<ObjDossard> createTableModel() {
        final TableModel<ObjDossard> model = new TableModel<ObjDossard>(Predicates.<ObjDossard>alwaysTrue());

        for (final ColTable colTable : getColumnModels()) {
            model.addColumn(colTable.getChamp())
                 .withAutoResize(autoResizeContext.isAutoResize())
                 .withModifier(new DefaultColTableColumnModifier(colTable));
        }
        return model;
    }

    private void updateData() {
        tableModel.setRows(stepModel.getStep().getDossards());
        removeSelectedUserButtonModel.addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                for (final ObjDossard dossard : tableModel.getSelection()) {
                    stepModel.getStep().removeDossard(dossard);
                }
                tableModel.setRows(stepModel.getStep().getDossards());
            }
        });
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        tableModel.setRows(stepModel.getStep().getDossards());
    }

    public AutoResizeColumn getAutoResizeContext() {
        return autoResizeContext;
    }
}
