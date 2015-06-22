package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnBuilder<T> {
    public int minWidth = 300;
    private final TableColumnModel<T> model;
    private final TableViewerColumn   viewer;
    private final TableColumn         column;
    private final TableViewer         tableViewer;

    TableColumnBuilder(
            final TableViewer tableViewer,
            final TableColumnModels<T> columnModels,
            final String property,
            final String label,
            final int style) {
        this.tableViewer = tableViewer;
        model = columnModels.getColumn(property);
        viewer = new TableViewerColumn(tableViewer, style);
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
        column = viewer.getColumn();
        viewer.setEditingSupport(
                new EditingSupport(viewer.getViewer()) {
                    @Override
                    protected CellEditor getCellEditor(final Object element) {
                        final CellEditor cellEditor = model.getCellEditor();
                        if( cellEditor != null){
                            return cellEditor;
                        }
                        final Object value = getValue(element);
                        if (value instanceof Boolean) {
                            final CheckboxCellEditor checkboxCellEditor = new CheckboxCellEditor();
                            checkboxCellEditor.setValue(Boolean.valueOf(value.toString()));
                            return checkboxCellEditor;
                        } else {
                            final TextCellEditor textCellEditor = new TextCellEditor(tableViewer.getTable());
                            textCellEditor.setValue(value);
                            return textCellEditor;
                        }
                    }

                    @Override
                    protected boolean canEdit(final Object element) {
                        return columnModels.canModify((T) element, property);
                    }

                    @Override
                    protected Object getValue(final Object element) {
                        final Object value = columnModels.getModifiedValue((T) element, property);
                        if (value != null && ("true".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(
                                value.toString()))) {
                            return Boolean.valueOf(value.toString());
                        }
                        return value;
                    }

                    @Override
                    protected void setValue(final Object element, final Object value) {
                        columnModels.modify((T) element, property, value);
                    }
                });
        column.setData(model);
        column.setText(label);
        column.addListener(SWT.Selection, new SortSelectionListener<T>(columnModels, model));
    }

    public TableColumnBuilder<T> withWidth(final int width) {
        column.setWidth(width);
        return this;
    }

    public TableColumnBuilder<T> withMinWidth(final int width) {
        minWidth = width;
        return this;
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public TableColumnBuilder<T> withRenderer(final TableColumnRenderer<T> renderer) {
        viewer.setLabelProvider(renderer);
        if (model != null) {
            model.setComparator(renderer);
        }
        viewer.getViewer().refresh();
        if (model != null && model.isAutoResize()) {
            column.pack();
            if (column.getWidth() < minWidth) {
                column.setWidth(minWidth);
            }
        }
        return this;
    }

    public TableColumnBuilder<T> withData(final Object data) {
        column.setData(data);
        return this;
    }

    public TableColumnBuilder<T> resizable(final boolean resizable) {
        column.setResizable(resizable);
        return this;
    }

    public TableColumnBuilder<T> movable(final boolean movable) {
        column.setMoveable(movable);
        return this;
    }

    public TableColumnBuilder<T> withToolTipText(final String toolTipText) {
        column.setToolTipText(toolTipText);
        return this;
    }

    private class SortSelectionListener<B> extends SelectionAdapter implements Listener {
        private final TableColumnModels<B> columnsModel;
        private final TableColumnModel<B>  model;

        SortSelectionListener(final TableColumnModels<B> columnsModel, final TableColumnModel<B> model) {
            this.columnsModel = columnsModel;
            this.model = model;
        }

        @Override
        public void handleEvent(final Event event) {
            columnsModel.sort(model);
            tableViewer.refresh();
        }
    }

    public TableColumnBuilder<T> sortable(final Listener sortListener) {
        column.addListener(SWT.Selection, sortListener);
        return this;
    }

    public TableColumnBuilder<T> withModifier(final TableColumnModifier<T> columnModifier) {
        model.setModifier(columnModifier);
        return this;
    }
}
