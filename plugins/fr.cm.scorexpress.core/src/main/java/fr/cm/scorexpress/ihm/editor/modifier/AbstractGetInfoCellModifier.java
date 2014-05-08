package fr.cm.scorexpress.ihm.editor.modifier;

import static com.google.common.collect.Lists.newArrayList;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import static fr.cm.scorexpress.core.model.ColTableUtils.isBooleanType;
import static fr.cm.scorexpress.core.model.ColTableUtils.isDateType;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.ObjConfig;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Widget;

import java.util.Collection;

public class AbstractGetInfoCellModifier<T extends IData> implements ICellModifier, ISetInfoListener {
    protected final TableViewer viewer;
    protected ObjConfig config;

    private final Collection<IInfoListener> listener = newArrayList();

    public AbstractGetInfoCellModifier(final TableViewer viewer) {
        this.viewer = viewer;
        addSetInfoListener(new RefreshViewerListener(viewer));
    }

    public AbstractGetInfoCellModifier(final TableViewer viewer, final ObjConfig config) {
        this.viewer = viewer;
        this.config = config;
        addSetInfoListener(new RefreshViewerListener(viewer));
    }

    public boolean canModify(final Object element, final String property) {
        return canModify(property);
    }

    public boolean canModify(final String property) {
        if (config != null) {
            final ColTable colTable = config.getColTable(property);
            return colTable == null || colTable.isModifiable();
        }
        return true;
    }

    public Object getValue(final Object element, final String property) {
        final ColTable colTable = config.getColTable(property);
        return getValue(colTable, (AbstractGetInfo) element, property);
    }

    public static Object getValue(final ColTable colTable, final AbstractGetInfo element, final String property) {
        final Object value = element.getInfo(property);
        if (value == null && isBooleanType(colTable)) {
            return Boolean.FALSE;
        } else if (isDateType(colTable)) {
            if (value == null) {
                return createDate(0);
            }
        }
        return value;
    }

    public void modify(final Object element, final String property, final Object value) {
        try {
            final T dateElement = (T) ((Widget) element).getData();
            final ColTable colTable = config.getColTable(property);
            modify(colTable, dateElement, property, value);
            viewer.refresh(dateElement);
        } catch (Exception ignore) {
            ignore.printStackTrace();
            viewer.refresh(element);
        }

    }

    public void modify(final ColTable colTable, final T element, final String property, final Object value) {
        element.setInfo(property, value);
        fireSetInfoListener(element, property, value);
    }

    public final void addSetInfoListener(final IInfoListener info) {
        listener.add(info);
    }

    public void fireSetInfoListener(final AbstractGetInfo element, final String property, final Object value) {
        if (listener == null) {
            return;
        }
        for (final IInfoListener listen : listener) {
            listen.modify(element, property, value);
        }
    }

    public void removeSetInfoListener(final IInfoListener info) {
        if (listener == null) {
            return;
        }
        listener.remove(info);
    }

    protected void refreshView() {
        viewer.refresh(true, true);
    }

    private static class RefreshViewerListener implements IInfoListener {
        private final StructuredViewer viewer;

        RefreshViewerListener(final StructuredViewer viewer) {
            this.viewer = viewer;
        }

        public void modify(final AbstractGetInfo element, final String property, final Object value) {
            viewer.refresh(element);
        }
    }
}
