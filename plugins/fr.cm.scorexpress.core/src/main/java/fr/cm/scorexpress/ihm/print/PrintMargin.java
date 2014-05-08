package fr.cm.scorexpress.ihm.print;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

class PrintMargin {
    // Margins to the bottom side, in pixels
    public final int bottom;

    // Margin to the left side, in pixels
    public final int left;

    // Margins to the right side, in pixels
    public final int right;

    // Margins to the top side, in pixels
    public final int top;

    private PrintMargin(final int left, final int right, final int top, final int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public static PrintMargin getPrintMargin(final Printer printer, final double margin) {
        return getPrintMargin(printer, margin, margin, margin, margin);
    }

    public static PrintMargin getPrintMargin(final Printer printer,
                                             final double marginLeft,
                                             final double marginRight,
                                             final double marginTop,
                                             final double marginBottom) {
        final Rectangle clientArea = printer.getClientArea();
        final Rectangle trim = printer.computeTrim(0, 0, 0, 0);
        final Point dpi = printer.getDPI();
        final int leftMargin = (int) (marginLeft * dpi.x) - trim.x;
        final int rightMargin = clientArea.width + trim.width - (int) (marginRight * dpi.x) - trim.x;
        final int topMargin = (int) (marginTop * dpi.y) - trim.y;
        final int bottomMargin = clientArea.height + trim.height - (int) (marginBottom * dpi.y) - trim.y;
        return new PrintMargin(leftMargin, rightMargin, topMargin, bottomMargin);
    }

    public String toString() {
        return "Margin { " + left + ", " + right + "; " + top + ", " + bottom + " }";
    }
}
