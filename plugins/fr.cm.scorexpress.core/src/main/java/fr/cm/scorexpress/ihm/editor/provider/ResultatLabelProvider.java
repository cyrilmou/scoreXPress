package fr.cm.scorexpress.ihm.editor.provider;

import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjResultat;
import fr.cm.scorexpress.ihm.editor.modifier.GetInfoLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import java.util.ArrayList;

public class ResultatLabelProvider extends GetInfoLabelProvider implements IColorProvider, ITableColorProvider {
    boolean signalError = false;

    public ResultatLabelProvider(final ObjConfig config) {
        super(config);
    }

    public ResultatLabelProvider() {
        super();
    }

    public void setSignalError(final boolean signalError) {
        this.signalError = signalError;
    }

    @Override
    public Color getBackground(final Object element) {
        if (element instanceof ObjResultat) {
            final ObjResultat resultat = (ObjResultat) element;
            if (resultat.isError() && signalError) {
                resultat.showErrors();
                return new Color(Display.getCurrent(), 255, 0, 0);
            }
        }
        return null;
    }

    @Override
    public Color getForeground(final Object element) {
        if (element instanceof ObjResultat) {
            final ObjResultat resultat = (ObjResultat) element;
            if (resultat.isError() && signalError) {
                return new Color(Display.getCurrent(), 255, 255, 255);
            }
        }
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof ArrayList) {
            return "" + ((ArrayList<?>) element).get(columnIndex);
        }
        return super.getColumnText(element, columnIndex);
    }

    @Override
    public Color getBackground(final Object element, final int columnIndex) {
        final ColTable colTable = colTables.get(columnIndex);
        if (colTable.getChamp().indexOf(ObjResultat.PREFIX_RES_INTER) != -1) {
            return new Color(Display.getCurrent(), 255, 255, 160);
        }
        return null;
    }

    @Override
    public Color getForeground(final Object element, final int columnIndex) {
        return null;
    }

}
