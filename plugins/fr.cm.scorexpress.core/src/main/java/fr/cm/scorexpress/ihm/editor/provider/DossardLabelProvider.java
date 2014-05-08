package fr.cm.scorexpress.ihm.editor.provider;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import static fr.cm.scorexpress.core.model.ColTableUtils.isBooleanType;
import fr.cm.scorexpress.core.model.ObjConfig;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_CHECK_ACTIVATE;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_CHECK_DESACTIVATE;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class DossardLabelProvider implements ITableLabelProvider {
    private final ObjConfig config;
    private final Image activate = getImg(IMG_CHECK_ACTIVATE);
    private final Image desactivate = getImg(IMG_CHECK_DESACTIVATE);

    public DossardLabelProvider(final ObjConfig config) {
        this.config = config;
    }

    public void addListener(final ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public Image getColumnImage(final Object element, final int columnIndex) {
        try {
            final ColTable colTable = config.getColTable().get(columnIndex);
            try {
                if (isBooleanType(colTable)) {
                    final Boolean b = (Boolean) ((AbstractGetInfo) element).getInfo(colTable.getChamp());
                    if (b != null && b) {
                        return activate;
                    } else {
                        return desactivate;
                    }
                }
            } catch (RuntimeException e) {
                return desactivate;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof AbstractGetInfo) {
            try {
                final ColTable colTable = config.getColTable().get(columnIndex);
                if (!isBooleanType(colTable)) {
                    return ((AbstractGetInfo) element).getInfoStr(colTable.getChamp());
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
    }

}
