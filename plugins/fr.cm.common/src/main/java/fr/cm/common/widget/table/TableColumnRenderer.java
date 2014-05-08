package fr.cm.common.widget.table;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import java.util.Comparator;

public abstract class TableColumnRenderer<T> extends StyledCellLabelProvider implements ColumnComparator<T> {
    protected TableColumnRenderer() {
        super(StyledCellLabelProvider.COLORS_ON_SELECTION);
    }

    @Override
    public void update(final ViewerCell cell) {
        super.update(cell);
        final Object element = cell.getElement();
        cell.setText(getText(element));
        final Image image = getImage(element);
        cell.setImage(image);
        cell.setBackground(getBackground(element));
        cell.setForeground(getForeground(element));
        cell.setFont(getFont(element));

    }

    public Color getBackground(final Object element) {
        return null;
    }

    public Color getForeground(final Object element) {
        return null;
    }

    public Font getFont(final Object element) {
        return null;
    }

    public String getText(final Object element) {
        return getColumnText((T) element);
    }

    public Image getImage(final Object element) {
        return getColumnImage((T) element);
    }

    public Image getColumnImage(final T element) {
        return null;
    }

    public abstract String getColumnText(final T element);

    public Comparator<T> getComparator(final T element) {
        final String var = getColumnText(element);
        try {
            Integer.valueOf(var);
            return new Comparator<T>() {
                @Override
                public int compare(final T elem1, final T elem2) {
                    final Integer var1;
                    try {
                        var1 = Integer.valueOf(getColumnText(elem1));
                    } catch (Exception e) {
                        return -1;
                    }
                    final Integer var2;
                    try {
                        var2 = Integer.valueOf(getColumnText(elem2));
                    } catch (Exception e) {
                        return 1;
                    }
                    return var1.compareTo(var2);
                }
            };
        } catch (Exception e) {
        }
        return new Comparator<T>() {
            @Override
            public int compare(final T elem1, final T elem2) {
                final String var1 = getColumnText(elem1);
                final String var2 = getColumnText(elem2);
                if (var1 == null) {
                    return -1;
                }
                if (var2 == null) {
                    return 1;
                }
                return var1.compareTo(var2);
            }
        };
    }

    @Override
    public int compare(final T elem1, final T elem2) {
        return getComparator(elem1).compare(elem1, elem2);
    }
}
