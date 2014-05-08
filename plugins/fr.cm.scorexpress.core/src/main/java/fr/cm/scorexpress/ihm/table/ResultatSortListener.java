package fr.cm.scorexpress.ihm.table;

import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.IData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultatSortListener implements Listener {
    private final TableViewer viewer;

    public ResultatSortListener(final TableViewer viewer) {
        this.viewer = viewer;
    }

    @SuppressWarnings("unchecked")
    public void handleEvent(final Event e) {
        // determine new sort column and direction
        final TableColumn sortColumn = viewer.getTable().getSortColumn();
        final TableColumn currentColumn = (TableColumn) e.widget;
        int dir = viewer.getTable().getSortDirection();
        if (sortColumn == currentColumn) {
            dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
        } else {
            viewer.getTable().setSortColumn(currentColumn);
            dir = SWT.UP;
        }

        // sort the data based on column and direction
        final int direction = dir;
        final ColTable colTable = (ColTable) currentColumn.getData();
        final String attribut = colTable.getChamp();
        final List<IData> data = (ArrayList<IData>) viewer.getInput();
        Collections.sort(data, new Comparator<IData>() {
            public int compare(final IData arg0,
                               final IData arg1) {
                final String str1 = arg0.getInfoStr(attribut);
                final String str2 = arg1.getInfoStr(attribut);
                if (direction == SWT.UP) {
                    if (str1 == null) {
                        return 1;
                    }
                    if (str2 == null) {
                        return -1;
                    }
                } else {
                    if (str1 == null) {
                        return 1;
                    }
                    if (str2 == null) {
                        return -1;
                    }
                }
                Integer value1 = null;
                try {
                    value1 = new Integer(str1);
                } catch (Exception ignored) {
                }
                Integer value2 = null;
                try {
                    value2 = new Integer(str2);
                } catch (Exception ignored) {
                }
                if (value1 == null && value2 == null) {
                    if (direction == SWT.UP) {
                        return str1.compareTo(str2);
                    } else {
                        return str2.compareTo(str1);
                    }
                } else {
                    if (value2 == null) {
                        return -1;
                    }
                    if (value1 == null) {
                        return 1;
                    }
                    if (direction == SWT.UP) {
                        return value1.compareTo(value2);
                    } else {
                        return value2.compareTo(value1);
                    }
                }
            }
        });
        viewer.getTable().setSortDirection(dir);
        viewer.refresh();
    }
}
