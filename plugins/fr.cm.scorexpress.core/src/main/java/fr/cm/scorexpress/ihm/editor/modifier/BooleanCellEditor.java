/**
 *
 */
package fr.cm.scorexpress.ihm.editor.modifier;

import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import static java.lang.Boolean.FALSE;

public class BooleanCellEditor extends CheckboxCellEditor {

    public BooleanCellEditor(final Composite parent) {
        this(parent, SWT.NONE);
    }

    public BooleanCellEditor(final Composite parent, final int style) {
        super(parent, style);
    }

    protected Object doGetValue() {
        return super.doGetValue();
    }

    protected void doSetValue(final Object value) {
        final Object b;
        if (value == null) {
            b = FALSE;
        } else {
            b = value;
        }
        super.doSetValue(b);
    }
}
