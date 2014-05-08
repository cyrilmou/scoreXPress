/**
 *
 */
package fr.cm.scorexpress.ihm.table;

import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.IData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import static org.eclipse.swt.SWT.DOWN;
import static org.eclipse.swt.SWT.UP;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableSortListener implements Listener {
    private final Viewer viewer;
    private final Table table;

    public TableSortListener(final TableViewer viewer) {
        this.viewer = viewer;
        table = viewer.getTable();
    }

    @SuppressWarnings("unchecked")
    public void handleEvent(final Event e) {
        // determine new sort column and direction
        final TableColumn sortColumn = table.getSortColumn();
        final TableColumn currentColumn = (TableColumn) e.widget;
        int dir = table.getSortDirection();
        if (sortColumn == currentColumn) {
            dir = dir == UP ? DOWN : UP;
        } else {
            table.setSortColumn(currentColumn);
            dir = UP;
        }
        // sort the data based on column and direction
        final int direction = dir;
        final ColTable colTable = (ColTable) currentColumn.getData();
        final String attribut = colTable.getChamp();
        final List<IData> data = (List<IData>) viewer.getInput();
        Collections.sort(data, new IntegerSorter(attribut, direction));
        // update data displayed in table
        table.setSortDirection(dir);
        viewer.refresh();
    }

    private static class IntegerSorter implements Comparator<IData>, Serializable {
        private static final long serialVersionUID = 2439038081585306946L;
        private final String attribut;
        private final int direction;

        public IntegerSorter(final String attribut, final int direction) {
            this.attribut = attribut;
            this.direction = direction;
        }

        public int compare(final IData arg0, final IData arg1) {
            final String str1 = arg0.getInfoStr(attribut);
            final String str2 = arg1.getInfoStr(attribut);
            try {
                final Integer value1 = new Integer(str1);
                final Integer value2 = new Integer(str2);
                if (direction == UP) {
                    return value1.compareTo(value2);
                }
                return value2.compareTo(value1);
            } catch (Exception e) {
            }
            if (direction == UP) {
                return str1.compareTo(str2);
            }
            return str2.compareTo(str1);
        }
    }
}
