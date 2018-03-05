package fr.cm.scorexpress.ihm.print;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.text.NumberFormat;
import java.util.ArrayList;

import static fr.cm.scorexpress.core.model.ColTableUtils.isBooleanType;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.DRAW_TRANSPARENT;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;

public class PrintPreview extends Composite implements PaintListener {
    private static final NumberFormat sdfZoom = NumberFormat.getInstance();
    /**
     * Doit être toujours supérieur à 3
     */
    private static final int stringMaxLength = 27;
    private static Printer printer;
    private static PrinterData printerData;
    private static double zoom = 100.0;
    private static boolean trunc = true;
    private static PrintDialog dialog = null;
    private Button buttonImprimer = null;
    private Button buttonPrecedent = null;
    private Button buttonSuivant = null;
    private Button checkBoxTrunk = null;
    private Canvas canvas = null;
    private CCombo cComboZoom = null;
    private ArrayList<Integer> columnWidth = new ArrayList<Integer>();
    private String[][] lines = new String[0][0];
    private int fontSize = 50;
    private GridData gridCanvasData;
    private String label;
    private int lineHeight = 0;
    private PrintMargin printMargin = null;
    private Table mTable = null;
    private int nbLineByPage = 0;
    private int nrPage = 1;
    private int selectedPage = 1;
    private Rectangle printArea = null;
    private int spacing = 0;
    private String[] titles = new String[0];

    public PrintPreview(final Composite parent, final int style) {
        super(parent, style);
        label = EMPTY;
        initialize();
    }

    public PrintPreview(final Composite parent, final int style, final Table table, final String[] titles) {
        this(parent, style);
        mTable = table;
        this.titles = titles;
    }

    public PrintPreview(final Composite parent, final int style, final Table table, final String[] titles,
                        final String label) {
        this(parent, style, table, titles);
        this.label = label;
    }

    public static int openPrintPreview(final Table table, final String[] titles, final String label) {
        final Display display = Display.getCurrent();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        shell.setText(i18n("PrintPreview.PRINT_TEXT"));
        final PrintPreview print = new PrintPreview(shell, SWT.NONE, table, titles, label);
        print.createPage(2, false);
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return 0;
    }

    private static Rectangle calculPrintArea(final Printer printer) {
        // Get the printable area
        final Rectangle rect = printer.getClientArea();
        // Compute the trim
        final Rectangle trim = printer.computeTrim(0, 0, 0, 0);
        // Get the printer's DPI
        final Point dpiO = printer.getDPI();
        final Point dpi = new Point(dpiO.x / 2, dpiO.y / 2);

        // Calculate the printable area, using 1 inch margins
        int left = trim.x + dpi.x;
        if (left < rect.x) {
            left = rect.x;
        }
        int right = (rect.width + trim.x + trim.width) - dpi.x;
        if (right > rect.width) {
            right = rect.width;
        }
        int top = trim.y + dpi.y;
        if (top < rect.y) {
            top = rect.y;
        }
        int bottom = (rect.height + trim.y + trim.height) - dpi.y;
        if (bottom > rect.height) {
            bottom = rect.height;
        }
        return new Rectangle(left, top, right - left, bottom - top);
    }

    private static Font createFont(final int size) {
        return new Font(Display.getCurrent(), "Tahoma", size, SWT.NONE);
    }

    private Rectangle calculPageLayout() {
        final Rectangle printerLayout = printer.getClientArea();
        final Point canvasSize = canvas.getSize();
        final double viewScaleFactor = getScaleFactor();
        final int offsetX = (int) (canvasSize.x - (viewScaleFactor * printerLayout.width)) / 2;
        final int offsetY = (int) (canvasSize.y - (viewScaleFactor * printerLayout.height)) / 2;
        return new Rectangle(offsetX, offsetY, (int) (viewScaleFactor * printerLayout.width),
                (int) (viewScaleFactor * printerLayout.height));
    }

    private Rectangle calculPageSize() {
        final Rectangle printerBound = printer.getBounds();
        final Point canvasSize = canvas.getSize();
        final double viewScaleFactor = getScaleFactor();
        final int offsetX = (canvasSize.x - (int) (viewScaleFactor * printerBound.width)) / 2;
        final int offsetY = (canvasSize.y - (int) (viewScaleFactor * printerBound.height)) / 2;
        final int marginOffsetX = offsetX + (int) (viewScaleFactor * printMargin.left);
        final int marginOffsetY = offsetY + (int) (viewScaleFactor * printMargin.top);
        return new Rectangle(marginOffsetX, marginOffsetY,
                (int) (viewScaleFactor * (printMargin.right - printMargin.left)),
                (int) (viewScaleFactor * (printMargin.bottom - printMargin.top)));
    }

    private double getScaleFactor() {
        final int canvasBorder = canvas.getBorderWidth();
        final Rectangle printerBound = printer.getBounds();
        final Rectangle canvasSize = canvas.getBounds();
        double viewScaleFactor = (canvasSize.width - canvasBorder * 2) * 1.0 / printerBound.width;
        return Math.min(viewScaleFactor, (canvasSize.height - canvasBorder * 2) * 1.0 / printerBound.height);
    }

    private void calculTableSize(final GC gc) {
        try {
            // Calcul de la taille du tableau
            final Rectangle dim = new Rectangle(0, 0, 0, 0);
            fontSize = 100;
            columnWidth.clear();
            lineHeight = 0;
            final Font font100 = createFont(fontSize);
            double ratio = 100;
            try {
                ratio = zoom;
            } catch (Exception ignored) {
            }
            ratio = ratio == 0 ? 100 : ratio;
            final int widthArea = (int) (((printArea.width) * ratio) / 100.0);
            if (lines.length == 0) {

            } else {
                while (fontSize > 1 && (dim.width <= 0 || dim.width > widthArea)) {
                    fontSize--;
                    columnWidth = new ArrayList<Integer>();
                    final Font currentFont = createFont(fontSize);
                    gc.setFont(currentFont);
                    lineHeight = gc.stringExtent("??").y;

                    dim.width = gc.stringExtent("???").x;
                    for (final String[] line : lines) {
                        dim.width = 0;
                        for (int j = 0; j < line.length; j++) {
                            final String val = line[j];
                            final int width = gc.stringExtent(val).x;
                            if (gc.stringExtent(val).y > lineHeight) {
                                lineHeight = gc.stringExtent(val).y;
                            }
                            /* On concerve la plus grande largeur de colonne */
                            if (j >= columnWidth.size()) {
                                columnWidth.add(width);
                            } else {
                                if (width > columnWidth.get(j)) {
                                    columnWidth.set(j, width);
                                }
                            }
                            dim.width += columnWidth.get(j);
                            if (dim.width > widthArea) {
                                break;
                            }
                        }
                        if (dim.width > widthArea) {
                            break;
                        }
                    }
                    currentFont.dispose();
                }
            }
            dim.height = (int) ((dim.width * 100.0 * printArea.height) / (printArea.width * ratio));
            spacing = (int) (lineHeight * 1.6 - lineHeight) / 2;
            lineHeight *= 1.7;
            font100.dispose();
            nbLineByPage = getNbLigneBypage(gc, dim);
            gridCanvasData.widthHint = printArea.width;
            gridCanvasData.heightHint = printArea.height;
            final int reste = mTable.getItems().length - (nrPage * nbLineByPage);
            buttonSuivant.setEnabled(reste > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCanvas() {
        gridCanvasData = new GridData(FILL_BOTH);
        gridCanvasData.widthHint = Display.getCurrent().getBounds().width * 2 / 3;
        gridCanvasData.heightHint = Display.getCurrent().getBounds().height * 2 / 3;
        canvas = new Canvas(this, BORDER);
        canvas.setLayoutData(gridCanvasData);
        canvas.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        canvas.addPaintListener(this);
    }

    /**
     * This method initializes compositeMenu
     */
    private void createCompositeMenu() {
        final GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 5;
        final GridData gridDataMenu = new GridData();
        gridDataMenu.grabExcessHorizontalSpace = true;
        gridDataMenu.horizontalAlignment = GridData.FILL;
        gridDataMenu.verticalAlignment = GridData.CENTER;
        gridDataMenu.grabExcessVerticalSpace = false;
        final Composite compositeMenu = new Composite(this, SWT.NONE);
        compositeMenu.setLayoutData(gridDataMenu);
        compositeMenu.setLayout(gridLayout1);
//        compositeMenu.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        buttonPrecedent = new Button(compositeMenu, SWT.NONE);
        buttonPrecedent.setText(i18n("PrintPreview.PREVIOUS"));
        buttonSuivant = new Button(compositeMenu, SWT.NONE);
        buttonSuivant.setText(i18n("PrintPreview.NEXT"));
        buttonSuivant.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                selectedPage++;
                nrPage = selectedPage;
                createPage(nrPage, false);
            }
        });
        buttonPrecedent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                selectedPage--;
                nrPage = selectedPage;
                createPage(nrPage, false);
            }
        });
        cComboZoom = new CCombo(compositeMenu, SWT.NONE);
        cComboZoom.add("200");
        cComboZoom.add("150");
        cComboZoom.add("100");
        cComboZoom.add("80");
        cComboZoom.add("50");
        cComboZoom.setText(sdfZoom.format(zoom));
        final Button buttonConfigPrint = new Button(compositeMenu, SWT.NONE);
        buttonConfigPrint.setText(i18n("PrintPreview.CONFIGURATION"));
        buttonImprimer = new Button(compositeMenu, SWT.NONE);
        buttonImprimer.setText(i18n("PrintPreview.IMPRIMER"));
        buttonImprimer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                print();
            }
        });
        buttonConfigPrint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                printerData = dialog.open();
                if (printerData != null) {
                    System.out.println("Launch print");
                    final Printer printer = new Printer(printerData);
                    setPrinter(printer, 0.0);
                    printArea = calculPrintArea(PrintPreview.printer);
                }
                nrPage = selectedPage;
                createPage(nrPage, false);
            }

        });
        cComboZoom.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                try {
                    zoom = new Double(cComboZoom.getText());
                } catch (NumberFormatException ignored) {
                }
                nrPage = selectedPage;
                createPage(nrPage, false);
            }

        });
        checkBoxTrunk = new Button(compositeMenu, SWT.CHECK);
        checkBoxTrunk.setText(i18n("PrintPreview.TRONQUER"));
        checkBoxTrunk.setSelection(trunc);
        checkBoxTrunk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                trunc = checkBoxTrunk.getSelection();
                nrPage = selectedPage;
                createPage(nrPage, false);
            }

        });
    }

    private synchronized void createPage(final int nrPage, final boolean print) {
        if (mTable == null) {
            return;
        }
        if (nrPage == 1) {
            buttonPrecedent.setEnabled(false);
        } else {
            buttonPrecedent.setEnabled(true);
        }
        final TableItem[] items = mTable.getItems();
        lines = new String[items.length + 1][mTable.getColumnCount()];
        /* Sauvegarde du contenu de la cellule */
        for (int i = -1; i < items.length; i++) {
            TableItem item = null;
            if (i >= 0) {
                item = items[i];
            }
            for (int j = 0; j < mTable.getColumnCount(); j++) {
                String val = EMPTY;
                /* Suppression d'une colonne non visible */
                if (mTable.getColumn(j).getWidth() > 0) {
                    if (i >= 0) {
                        final Object data = mTable.getColumn(j).getData();
                        assert item != null;
                        val = truncString(item.getText(j));
                        if (data != null && data instanceof ColTable) {
                            final ColTable colTable = (ColTable) mTable.getColumn(j).getData();
                            if (isBooleanType(colTable)) {
                                final Boolean v =
                                        (Boolean) ((AbstractGetInfo) item.getData()).getInfo(colTable.getChamp());
                                val = v != null && v ? i18n("PrintPreview.ENABLE") : EMPTY;
                            }
                        }
                    } else {
                        val = truncString(mTable.getColumn(j).getText());
                    }
                    val = ' ' + val + ' ';
                }
                lines[i + 1][j] = val;
            }
        }
        setPrinter(printer, 0);
        if (!print) {
            System.out.println("draw");
            canvas.redraw();
        }
    }

    int getNbLigneBypage(final GC gc, final Rectangle dim) {
        final int titleSize = gc.getFontMetrics().getHeight() * titles.length + 1;
        int nbLineByPage = (dim.height - titleSize - spacing) / lineHeight - 1;
        nbLineByPage = nbLineByPage <= 0 ? 1 : nbLineByPage;
        return nbLineByPage;
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        setLayout(new GridLayout());
        dialog = new PrintDialog(getShell(), SWT.NONE);
        if (printerData != null)
            dialog.setPrinterData(printerData);
        createCompositeMenu();
        createCanvas();
    }

    private int paint(final GC gc, final Rectangle dimPage, final Rectangle pageLayout, final int nrPage) {
        System.out.println("paint dim=" + dimPage + " pageLayout=" + pageLayout + " fontSize=" + fontSize);
        if (dimPage.width <= 0) {
            return 0;
        }
        try {
            // Calcul de la taille du tableau
            final Rectangle dim = new Rectangle(0, 0, 0, 0);
            final Font font = createFont(fontSize);
            /* Calcul de la zone d'ocupation du tableau */
            dim.width = 0;
            for (final Integer aColumnWidth : columnWidth) {
                dim.width += aColumnWidth;
            }
            dim.width += dim.x;
            dim.height = (dim.width * dimPage.height) / dimPage.width;
            dim.x = dimPage.x;
            dim.y = dimPage.y;
            int tableSize = 0;
            for (final Integer aColumnWidth : columnWidth) {
                tableSize += aColumnWidth;
            }
            if (dimPage.width - tableSize > 0) {
                dim.x += (dimPage.width - tableSize) / 2;
            }
            /* Initialisation de la zone d'affichage */
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
            gc.setBackground(mTable.getBackground());
            gc.setForeground(mTable.getForeground());
            int x = dim.x;
            int y = dim.y;
            final Font font2 = new Font(getDisplay(), "Tahoma", (int) (fontSize * 1.3), SWT.BOLD);
            gc.setFont(font2);
            int widthTable = 0;
            for (final Integer aColumnWidth : columnWidth) {
                widthTable += aColumnWidth;
            }
            for (final String title : titles) {
                final Point titlePoint = gc.stringExtent(title);
                if (widthTable > pageLayout.width) {
                    gc.drawText(title, -titlePoint.x / 2 + dim.x + pageLayout.width / 2, y, DRAW_TRANSPARENT);
                } else {
                    gc.drawText(title, -titlePoint.x / 2 + dim.x + widthTable / 2, y, DRAW_TRANSPARENT);
                }
                y += gc.getFontMetrics().getHeight();
            }
            y += gc.getFontMetrics().getHeight();
            gc.setFont(font);
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
            for (int k = 0; k < lines[0].length && k < columnWidth.size(); k++) {
                final String info = lines[0][k];
                final int width = columnWidth.get(k);
                gc.fillRectangle(x, y, width, lineHeight);
                final int offsetX = width / 2 - gc.stringExtent(info).x / 2;
                gc.drawText(info, x + offsetX, y + spacing, DRAW_TRANSPARENT);
                gc.textExtent(info);
                gc.drawRectangle(x, y, width, lineHeight);
                x += width;
            }
            gc.setBackground(mTable.getBackground());
            y += lineHeight;
            font2.dispose();
            int debut = 1 + nbLineByPage * (nrPage - 1);
            debut = debut <= 0 ? nrPage : debut;
            final Color color = new Color(getDisplay(), 247, 250, 180);
            boolean end = false;
            int nbLigneShow = 0;
            for (int i = debut; i < lines.length && !end; i++) {
                nbLigneShow++;
                x = dim.x;
                if (i % 2 == 0) {
                    gc.setBackground(color);
                }
                for (int j = 0; j < lines[i].length && j < columnWidth.size(); j++) {
                    final String info = truncString(lines[i][j]);
                    final int width = columnWidth.get(j);
                    // if (x + width <= pageLayout.width + pageLayout.x) {
                    gc.fillRectangle(x, y, width, lineHeight);
                    final int offsetX = width / 2 - gc.stringExtent(info).x / 2;
                    gc.drawText(info, x + offsetX, y + spacing, DRAW_TRANSPARENT);
                    gc.drawRectangle(x, y, width, lineHeight);
                    // }
                    x += width;
                }
                gc.setBackground(mTable.getBackground());
                y += lineHeight;
                if (nbLigneShow >= nbLineByPage) {
                    // || y + 2 * lineHeight > dim.height + dim.y) {
                    end = true;
                }
            }
            color.dispose();
            int contactFontSize = (int) (fontSize * 0.8);
            if (contactFontSize <= 0) {
                contactFontSize = fontSize;
            }
            final Font newFont = createFont(contactFontSize);
            gc.setFont(newFont);
            font.dispose();
            newFont.dispose();
            gc.dispose();
            // gc.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("and paint");
        return 0;
    }

    @Override
    public void paintControl(final PaintEvent e) {
        System.out.println("paintControl");
        if (printer == null || printer.isDisposed()) {
            return;
        }
        final Rectangle dimPage = calculPageSize();
        final Rectangle pageLayout = calculPageLayout();
        e.gc.setBackground(mTable.getBackground());
        // draws the page layout
        e.gc.fillRectangle(dimPage.x, dimPage.y, dimPage.width, dimPage.height);
        // draws the margin.
        e.gc.setLineStyle(SWT.LINE_DASH);
        e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        e.gc.drawRectangle(pageLayout.x, pageLayout.y, pageLayout.width, pageLayout.height);
        e.gc.setLineStyle(SWT.LINE_SOLID);
        final GC gc = e.gc;
        printArea = dimPage;
        calculTableSize(gc);
        paint(gc, pageLayout, dimPage, nrPage);
        gc.dispose();
    }

    private void print() {
        System.out.println("Print");
        final PrinterData printerDataTmp = dialog.open();
        System.out.println("print open " + printerData.name);
        if (printerDataTmp != null) {
            printerData = printerDataTmp;
            System.out.println("print " + printerData.name);
            printer = new Printer(printerData);
            // printArea = printer.getClientArea();
            if (label == null) {
                printer.startJob(i18n("PrintPreview.JOB_NAME_DEFAULT"));
            } else {
                printer.startJob(label);
            }

            nrPage = 1;
            final GC gc2 = new GC(printer);
            // createPage(nrPage, true);
            printArea = calculPrintArea(printer);
            calculTableSize(gc2);
            nbLineByPage = getNbLigneBypage(gc2, printArea);
            gc2.dispose();

            int nbLigne = mTable.getItems().length;

            int ligne = 1;
            while (ligne > 0) {
                if ((printerData.scope == 0 ||
                        printerData.scope == 1 && printerData.startPage >= nrPage && printerData.endPage <= nrPage ||
                        printerData.scope == 2 && nrPage == selectedPage)
                        && printer.startPage()) {
                    System.out.println("Calcul print area");
                    final GC gc = new GC(printer);
                    // createPage(nrPage, true);
                    paint(gc, printArea, printArea, nrPage);
                    printer.endPage();
                    gc.dispose();
                }
                ligne = nbLigne - (nrPage * nbLineByPage);
                nrPage++;

            }
            printer.endJob();
            getShell().dispose();
        }
    }

    void setPrinter(Printer printer, final double marginSize) {
        System.out.println("setPrint");
        if (printer == null) {
            PrintPreview.printerData = Printer.getDefaultPrinterData();
            printer = new Printer(printerData);
        }
        PrintPreview.printer = printer;
        printMargin = PrintMargin.getPrintMargin(printer, marginSize);
        canvas.redraw();
    }

    private String truncString(final String str) {
        if (!trunc) {
            return str;
        }
        if (str == null) {
            return EMPTY;
        }
        if (str.length() > stringMaxLength) {
            return str.substring(0, stringMaxLength - 3) + i18n("PrintPreview.TRUNK");
        } else {
            return str;
        }
    }

}
