package fr.cm.scorexpress.ihm.editor;

import fr.cm.common.widget.table.DefaultTableColumnModifier;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjDossard;

public class DefaultColTableColumnModifier extends DefaultTableColumnModifier<ObjDossard> {
    private final ColTable colTable;

    public DefaultColTableColumnModifier(final ColTable colTable) {
        super(colTable.getChamp());
        this.colTable = colTable;
    }

    @Override
    public void modify(final ObjDossard element, final String property, final Object value) {
        element.setInfo(property, value);
    }

    @Override
    public Object getValue(final ObjDossard element, final String property) {
        return element.getInfoStr(property);
    }

    @Override
    public boolean canModify(final ObjDossard element, final String property) {
        return colTable.isModifiable();
    }
}
