package fr.cm.scorexpress.ihm.table;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class MoveOnTableAction implements KeyListener {
    private final TableViewer tv;
    private final TableCursor cursor;

    public MoveOnTableAction(final TableViewer tv) {
        this.tv = tv;
        cursor = new TableCursor(tv.getTable(), SWT.NONE);
        cursor.setEnabled(true);
        cursor.setVisible(false);
    }

    public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.F2 || e.keyCode == SWT.KEY_MASK) {
            final Object element = ((IStructuredSelection) tv.getSelection()).getFirstElement();
            final int column = cursor.getColumn();
            tv.editElement(element, column);
        }
    }

    public void keyReleased(final KeyEvent e) {
    }
}
