package fr.cm.common.widget.table;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchSite;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.common.widget.SwtUtil.proxySwt;

public class TableBuilder<T> extends CommonControlBuilder<Table, TableBuilder<T>> {
    private final TableModel<T> model;
    private final TableViewer   viewer;

    public static <T> TableBuilder<T> createTable(final MyToolkit toolkit, final Composite parent, final TableModel<T> tableModel, final int style) {
        return new TableBuilder<T>(toolkit, parent, tableModel, style);
    }

    TableBuilder(final MyToolkit toolkit, final Composite parent, final TableModel<T> tableModel, final int style) {
        super(toolkit, toolkit.createTable(parent, style));
        model = tableModel;
        viewer = new TableViewer(control);

        final TableStateListener<T> stateRenderer = proxySwt(new TableStateRenderer<T>(viewer));

        viewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                return tableModel.shouldShow((T) element);
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final Iterator<T> iter = ((IStructuredSelection) event.getSelection()).iterator();
                final Collection<T> selection = newArrayList();
                while (iter.hasNext()) {
                    selection.add((T) iter.next());
                }
                model.selectionChanged(selection);
            }
        });

        viewer.setComparator(new ViewerComparator() {
            @Override
            @SuppressWarnings("unchecked")
            public int compare(final Viewer viewer, final Object e1, final Object e2) {
                return model.getColumnModel().compare((T) e1, (T) e2);
            }

            @Override
            public boolean isSorterProperty(final Object element, final String property) {
                return super.isSorterProperty(element, property);
            }
        });
        viewer.setContentProvider(new TableModelProvider<T>(stateRenderer));
        viewer.setInput(tableModel);
    }

    @Deprecated
    public Table getTable() {
        return viewer.getTable();
    }

    public TableColumnBuilder<T> addColumn(final String property, final String label, final int style) {
        return new TableColumnBuilder<T>(viewer, model.getColumnModel(), property, label, style);
    }

    public TableBuilder<T> withHeader(final boolean visible) {
        viewer.getTable().setHeaderVisible(visible);
        return this;
    }

    @Deprecated
    public TableViewer getViewer() {
        return viewer;
    }

    public TableBuilder<T> withLine(final boolean showLine) {
        control.setLinesVisible(showLine);
        return this;
    }

    public TableBuilder<T> selectionProvider(final IWorkbenchSite site) {
        site.setSelectionProvider(viewer);
        return this;
    }

    private static class TableModelProvider<T> implements IStructuredContentProvider {
        private final TableStateListener<T> stateListener;
        private TableModel<T> model = null;

        TableModelProvider(final TableStateListener<T> stateListener) {
            this.stateListener = stateListener;
        }

        @Override
        public Object[] getElements(final Object input) {
            return ((TableModel<T>) input).getRows().toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (oldInput != null) {
                detach((TableModel<T>) oldInput);
            }
            if (newInput != null) {
                attach((TableModel<T>) newInput);
            }
        }

        private void attach(final TableModel<T> newInput) {
            newInput.addStateListener(stateListener);
            model = newInput;
        }

        @SuppressWarnings({"TypeMayBeWeakened"})
        private void detach(final TableModel<T> oldInput) {
            oldInput.removeStateListener(stateListener);
            model = null;
        }

        @Override
        public void dispose() {
            if (model != null) {
                detach(model);
            }
        }
    }

    private static class TableStateRenderer<T> implements TableStateListener<T> {
        private final TableViewer viewer;

        TableStateRenderer(final TableViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void onFilterChange() {
            viewer.refresh();
        }

        @Override
        public void onRowsAdded(final Collection<T> rows) {
            viewer.refresh();
        }

        @Override
        public void onRowsDeleted(final Collection<T> rows) {
            viewer.refresh();
        }

        @Override
        public void onRowModify(final T row) {
            viewer.refresh(row);
        }

        @Override
        public void onTableDeleted() {
            viewer.setItemCount(0);
        }

        @Override
        public void onColumnSort(final Direction newDirection, final TableColumnModel<T> columnModel) {
            final int direction = directionToSWT(newDirection);
            viewer.getTable().setSortColumn(toTableColumn(viewer, columnModel));
            viewer.getTable().setSortDirection(direction);
            viewer.refresh();
        }

        @Override
        public void onAutoResizeAllColumn() {
            final Table table = viewer.getTable();
            table.setRedraw(false);
            for (final TableColumn column : table.getColumns()) {
                final TableColumnModel<T> columnModel = (TableColumnModel<T>) column.getData();
                if (columnModel.isAutoResize() && column.getWidth() > 0) {
                    column.pack();
                }
            }
            table.setRedraw(true);
        }

        @Override
        public void onAutoRemoveEmptyColumn() {
            final Table table = viewer.getTable();
            table.setRedraw(false);
            int col = 0;
            for (final TableColumn column : table.getColumns()) {
                final TableColumnModel<T> columnModel = (TableColumnModel<T>) column.getData();
                int colSize = 0;
                for (final TableItem row : table.getItems()) {
                    final int length = row.getText(col).length();
                    colSize = length > colSize ? length : colSize;
                }
                if (colSize == 0 && columnModel.isAutoResize()) {
                    column.setWidth(0);
                    columnModel.setWidth(0);
                } else if (colSize > column.getWidth()) {
                    column.setWidth(colSize);
                }
                col++;
            }
            table.setRedraw(true);
            table.redraw();
        }

        @SuppressWarnings({"TypeMayBeWeakened"})
        private static <T> TableColumn toTableColumn(final TableViewer viewer, final TableColumnModel<T> model) {
            for (final TableColumn column : viewer.getTable().getColumns()) {
                if (column.getData() == model) {
                    return column;
                }
            }
            return null;
        }

        @SuppressWarnings({"TypeMayBeWeakened"})
        private int directionToSWT(final Direction d) {
            if (d.equals(Direction.ASCENDING)) {
                return SWT.DOWN;
            } else {
                return SWT.UP;
            }
        }

        @Override
        public void onEnable(final boolean enable) {
            viewer.getTable().setEnabled(enable);
        }
    }
}
