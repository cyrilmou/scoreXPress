package fr.cm.scorexpress.ihm.editor.modifier;

import static org.apache.commons.lang.StringUtils.EMPTY;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

import static java.util.Arrays.asList;
import java.util.List;

public class MyComboBoxCellEditor extends ComboBoxCellEditor {
    public MyComboBoxCellEditor() {
    }

    protected Object doGetValue() {
        Object res = super.doGetValue();
        final String[] items = super.getItems();
        if (res != null && res instanceof Integer) {
            final int index = (Integer) res;
            if (index == -1) {
                final List<String> listItems = asList(items);
                final int index2 = listItems.indexOf(((CCombo) getControl()).getText());
                if (index2 != -1) {
                    return listItems.get(index2);
                }
            }
            if (index >= 0 && index < items.length) {
                res = items[index];
            } else {
                res = EMPTY;
            }
        }
        return res;
    }

    protected void doSetValue(final Object value) {
        final String[] items = super.getItems();
        if (value == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(value)) {
                super.doSetValue(i);
                break;
            }
        }
    }

    public MyComboBoxCellEditor(final Composite parent, final String[] items) {
        super(parent, items);
    }

    public MyComboBoxCellEditor(final Composite parent, final String[] items, final int style) {
        super(parent, items, style);
    }
}
