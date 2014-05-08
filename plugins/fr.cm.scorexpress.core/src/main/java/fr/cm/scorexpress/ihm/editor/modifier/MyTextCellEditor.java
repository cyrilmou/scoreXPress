package fr.cm.scorexpress.ihm.editor.modifier;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class MyTextCellEditor extends TextCellEditor {
    public MyTextCellEditor() {
    }

    public MyTextCellEditor(final Composite parent) {
        super(parent);
    }

    public MyTextCellEditor(final Composite parent, final int style) {
        super(parent, style);
    }

    protected void doSetValue(Object value) {
        if (value == null)
            value = "";
        super.doSetValue(value.toString());
    }
}
