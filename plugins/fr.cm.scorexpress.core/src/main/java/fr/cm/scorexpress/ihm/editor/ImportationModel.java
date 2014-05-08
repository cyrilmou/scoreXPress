package fr.cm.scorexpress.ihm.editor;

import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.table.TableAdapter;
import fr.cm.common.widget.table.TableModel;
import fr.cm.common.widget.text.TextModel;
import fr.cm.common.widget.text.TextModifyAdapter;
import fr.cm.common.widget.text.TextModifyListener;
import fr.cm.scorexpress.core.model.ColTable;
import static fr.cm.scorexpress.core.model.ConfigType.IMPORT_PARTICIPANTS;
import fr.cm.scorexpress.core.model.ObjColTable;
import fr.cm.scorexpress.model.DirtyModel;
import fr.cm.scorexpress.model.ManifModel;
import static org.apache.commons.lang.StringUtils.EMPTY;

import static java.lang.Integer.parseInt;
import java.util.Collection;

public class ImportationModel {
    private final ManifModel manifModel;
    private final TableModel<ColTable> colTableTableModel;
    private final DirtyModel dirtyModel = new DirtyModel(false);
    private final TextModel labelTextModel;
    private final TextModel elementCsvTextModel;
    private final ButtonModel showButtonModel;
    private final TextModel witdhTextModel;
    private final TextModel fieldTextModel;

    public ImportationModel(final ManifModel manifModel) {
        this.manifModel = manifModel;
        colTableTableModel = createTableModel();
        labelTextModel = createElementCsvModel(new LabelModifier());
        elementCsvTextModel = createElementCsvModel(new ElementCsvModifier());
        showButtonModel = createShowButtonModel();
        witdhTextModel = createElementCsvModel(new WidthModifier());
        fieldTextModel = createElementCsvModel(new FieldModifier());
        initSelection();
    }

    private ButtonModel createShowButtonModel() {
        final ButtonModel model = new ButtonModel(EMPTY);
        model.addWidgetListener(new ButtonAdapter() {
            public void click() {
                for (final ColTable colTable : colTableTableModel.getSelection()) {
                    ((ObjColTable) colTable).setMasque(!model.isSelected());
                    dirtyModel.setDirty(true);
                }

            }
        });
        return model;
    }

    private void initSelection() {
        final ColTable colTable = getColTableSelected();
        if (colTable != null) {
            labelTextModel.setText(colTable.getLib());
            elementCsvTextModel.setText(colTable.getChoix());
            showButtonModel.setSelection(!colTable.isMasque());
            witdhTextModel.setText(colTable.getWidth() + EMPTY);
            fieldTextModel.setText(colTable.getChamp());
        }
    }

    public Object[] getImportationField() {
        return getImportationFields().toArray();
    }

    public TextModel getElementCsvModel() {
        return elementCsvTextModel;
    }

    public TableModel<ColTable> getColTableTableModel() {
        return colTableTableModel;
    }


    private ColTable getColTableSelected() {
        if (colTableTableModel.getSelection().isEmpty()) {
            return null;
        } else {
            return colTableTableModel.getSelection().iterator().next();
        }
    }

    private TableModel<ColTable> createTableModel() {
        final TableModel<ColTable> tableModel = new TableModel<ColTable>();
        tableModel.addRows(getImportationFields());
        tableModel.addWidgetListener(new TableAdapter() {
            public void selectionChange() {
                initSelection();
            }
        });
        return tableModel;
    }

    private Collection<ColTable> getImportationFields() {
        return manifModel.getManif().getConfiguration().getConfig(IMPORT_PARTICIPANTS).getColTableAll();
    }

    public DirtyModel getDirtyModel() {
        return dirtyModel;
    }

    public TextModel getLabelTextModel() {
        return labelTextModel;
    }

    public TextModel getFieldTextModel() {
        return fieldTextModel;
    }

    public TextModel getWitdhTextModel() {
        return witdhTextModel;
    }

    public ButtonModel getShowButtonModel() {
        return showButtonModel;
    }

    private TextModel createElementCsvModel(final TextModifyListener modifier) {
        final TextModel model = new TextModel(EMPTY);
        model.addModifyListener(modifier);
        return model;
    }

    public void commit(final boolean onSave) {
        dirtyModel.setDirty(false);
    }

    private class ElementCsvModifier extends TextModifyAdapter {
        public void onExit() {
            for (final ColTable colTable : colTableTableModel.getSelection()) {
                ((ObjColTable) colTable).setChoix(elementCsvTextModel.getText());
                dirtyModel.setDirty(true);
            }
        }
    }

    private class LabelModifier extends TextModifyAdapter {
        public void onExit() {
            for (final ColTable colTable : colTableTableModel.getSelection()) {
                ((ObjColTable) colTable).setLib(labelTextModel.getText());
                dirtyModel.setDirty(true);
            }
        }
    }

    private class FieldModifier extends TextModifyAdapter {
        public void onExit() {
            for (final ColTable colTable : colTableTableModel.getSelection()) {
                ((ObjColTable) colTable).setChamp(fieldTextModel.getText());
                dirtyModel.setDirty(true);
            }
        }
    }

    private class WidthModifier extends TextModifyAdapter {
        public void onExit() {
            try {
                for (final ColTable colTable : colTableTableModel.getSelection()) {
                    ((ObjColTable) colTable).setWidth(parseInt(witdhTextModel.getText()));
                    dirtyModel.setDirty(true);
                }
            } catch (Exception ignore) {
            }
        }
    }
}
