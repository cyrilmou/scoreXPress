package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.table.DefaultTableColumnModifier;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjBalise;
import org.eclipse.jface.viewers.CellEditor;

/**
 * Created by Isa on 28/05/14.
 */
public class DefaultInfoTableColumnModifier extends DefaultTableColumnModifier<ObjBalise> {
    private final boolean modifiable;
    private CellEditor cellEditor = null;

    public DefaultInfoTableColumnModifier(final String property, final boolean modifiable) {
        super(property);
        this.modifiable = modifiable;
    }

    @Override
    public void modify(final ObjBalise element, final String property, final Object value) {
        element.setInfo(property, value);
    }

    @Override
    public Object getValue(final ObjBalise element, final String property) {
        return element.getInfoStr(property);
    }

    @Override
    public boolean canModify(final ObjBalise element, final String property) {
        return modifiable;
    }

    @Override
    public CellEditor getCellEditor() {
        return cellEditor;
    }

    public void setCellEditor(final CellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }
}
