package fr.cm.scorexpress.ihm.table;

import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjConfig;
import org.eclipse.jface.viewers.TableViewer;
import static org.eclipse.swt.SWT.CENTER;
import static org.eclipse.swt.SWT.Selection;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableUtils {
    private TableUtils() {
    }

    public static void createColumns(final TableViewer tv, final ObjConfig config) {
        for (final ColTable colTable : config.getColTable()) {
            final TableColumn tableColumn = new TableColumn(tv.getTable(), CENTER);
            tableColumn.setText(colTable.getLib());
            tableColumn.setWidth(colTable.getWidth());
            tableColumn.setToolTipText(colTable.getLib2());
            tableColumn.setAlignment(colTable.getAlign());
            tableColumn.setMoveable(true);
            tableColumn.setData(colTable);
            tableColumn.addListener(Selection, new TableSortListener(tv));
            if (colTable.isMasque()) {
                tableColumn.setResizable(false);
                tableColumn.setWidth(0);
            } else {
                tableColumn.pack();
            }
        }
    }

    public static void autoResizeColumn(final Table table) {
        table.setRedraw(false);
        for (int j = 0; j < table.getColumnCount(); j++) {
            if (table.getColumn(j).getResizable() && table.getColumn(j).getWidth() != 0) {
                table.getColumn(j).pack();
            }
        }
        table.setRedraw(true);
    }
}
