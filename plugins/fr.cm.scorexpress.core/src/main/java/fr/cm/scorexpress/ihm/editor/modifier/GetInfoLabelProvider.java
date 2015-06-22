package fr.cm.scorexpress.ihm.editor.modifier;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.impl.Date2;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.core.model.ColTableType.BOOLEAN;
import static fr.cm.scorexpress.core.model.ColTableType.isSame;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class GetInfoLabelProvider implements ITableLabelProvider {
    private final Image activate    = getImageDescriptor(IMG_ACTIVATE).createImage();
    private final Image desactivate = getImageDescriptor(IMG_DESACTIVATE).createImage();

    protected final List<ColTable> colTables;

    public GetInfoLabelProvider(final ObjConfig config) {
        colTables = config.getColTableAll();
    }

    public GetInfoLabelProvider() {
        colTables = newArrayList();
    }

    public GetInfoLabelProvider(final List<ColTable> colTables) {
        this.colTables = colTables;
    }

    public void setConfig(final ObjConfig config) {
        colTables.clear();
        colTables.addAll(config.getColTableAll());
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof AbstractGetInfo) {
            try {
                final ColTable colTable = colTables.get(columnIndex);
                return getColumnText(colTable, element);
            } catch (Exception ignoreAndShow) {
                System.err.println(ignoreAndShow.getMessage());
            }
        }
        return null;
    }

    public String getColumnText(final ColTable colTable, final Object element) {
        final String champs = colTable.getChamp();
        if (element == null) {
            return EMPTY;
        }
        final Object res = ((AbstractGetInfo) element).getInfo(champs);
        if (res == null) {
            return EMPTY;
        } else {
            if (res instanceof Date2) {
                return ((Date2) res).toStringNotNull();
            } else {
                return res.toString();
            }
        }
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        try {
            final ColTable coltable = colTables.get(columnIndex);
            if (isSame(BOOLEAN, coltable.getType())) {
                final Boolean b = (Boolean) ((AbstractGetInfo) element).getInfo(coltable.getChamp());
                if (b != null && b.booleanValue()) {
                    return activate;
                } else {
                    return desactivate;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void addListener(final ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
    }
}
