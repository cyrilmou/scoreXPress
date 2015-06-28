package fr.cm.scorexpress.ihm.editor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.StandardToolKit;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonListener;
import fr.cm.common.widget.composite.AbstractCompositeBuilder;
import fr.cm.common.widget.composite.CommonCompositeBuilder;
import fr.cm.common.widget.composite.CompositeBuilder;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.common.widget.table.TableModel;
import fr.cm.common.workbench.WorkbenchUtils;
import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjResultat;
import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.application.ImageReg;
import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import fr.cm.scorexpress.ihm.editor.input.CommonEditorInput;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import fr.cm.scorexpress.ihm.editor.input.ResultatEditorInput;
import fr.cm.scorexpress.ihm.editor.input.StepEditorInput;
import fr.cm.scorexpress.ihm.print.IPrintable;
import fr.cm.scorexpress.model.StepModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import static fr.cm.common.widget.composite.CompositeBuilders.createCompositeBuilder;
import static fr.cm.common.workbench.WorkbenchUtils.defineCopyViewSiteAction;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.ChronosEditor.CHRONOS_EDITOR_ID;
import static fr.cm.scorexpress.ihm.editor.PenaliteEditor.PENALITY_EDITOR_ID;
import static fr.cm.scorexpress.ihm.editor.ResultatEtapeEditor.LaunchEditorAction.*;
import static fr.cm.scorexpress.ihm.editor.StepEditor.STEP_EDITOR_ID;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static fr.cm.scorexpress.ihm.print.PrintPreview.openPrintPreview;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.jface.viewers.StyledCellLabelProvider.COLORS_ON_SELECTION;
import static org.eclipse.swt.layout.GridData.*;

public class ResultatEtapeEditor extends EditorPart implements IPrintable,
        IAutoAjustColumnEditor {
    public static final String             RESULTAT_ETAPE_EDITOR_ID = "fr.cm.scorexpress.editor.ResultatEtapeEditor";
    private             Image              toggleImage              = null;
    private             Image              refreshImage             = null;
    private             Image              configEtapeImage         = null;
    private             Image              configPenaliteImage      = null;
    private             Image              configChronosImage       = null;
    private             ResultatEtapeModel model                    = null;
    private             Table              table                    = null;
    private CompositeBuilder builder;
    private CompositeBuilder compositeBuilder;

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        if (!(input instanceof ResultatEditorInput)) {
            throw new PartInitException(ResultatEtapeEditor_Invalid_Input_Doit_etre_une_etape);
        }
        model = ((CommonEditorInput<ResultatEtapeModel>) input).getModel();
        setSite(site);
        setInput(input);
        model.getConfigEtapeButtonModel().addWidgetListener(createStepEditorAction(this, model.getStepModel(), model));
        model.getConfigPenalityButtonModel().addWidgetListener(createPenalityEditorAction(this, model));
        model.getChronosButtonModel().addWidgetListener(createChronosEditorAction(this, model));
        setPartName(model.getLabel());
        model.updateCalcul();
        if (model.getMode() == 1) {
            setTitleImage(ImageReg.getImg(ScoreXPressPlugin.IMG_RED_RESULT));
        } else if (model.getMode() == 2) {
            setTitleImage(ImageReg.getImg(ScoreXPressPlugin.IMG_GREEN_RESULT));
        }
    }

    @Override
    public void createPartControl(final Composite parent) {
        final MyToolkit toolkit = new StandardToolKit();
        toggleImage = getImg(IMG_TOGGLE);
        refreshImage = getImg(IMG_REFRESH);
        configEtapeImage = getImg(IMG_ETAPE);
        configPenaliteImage = getImg(IMG_PENALITY);
        configChronosImage = getImg(IMG_BALISE);
        parent.setLayout(new FillLayout());
        defineCopyViewSiteAction(parent.getDisplay(), getEditorSite().getActionBars());
        compositeBuilder = createCompositeBuilder(toolkit, parent, SWT.NONE).withLayout(new GridLayout());
        createTitle(compositeBuilder);
        createMenuBar(compositeBuilder);
        table = createTable(compositeBuilder);
        createFootBar(compositeBuilder);
    }

    private void createTitle(final CommonCompositeBuilder<Composite, CompositeBuilder> builder) {
        builder.addLabel(model.getTitleLabelModel(), SWT.NONE)
                .withFont(new Font(Display.getDefault(), "Tahoma", 14, SWT.NORMAL)).withLayoutData(new GridData(VERTICAL_ALIGN_CENTER | GRAB_HORIZONTAL | HORIZONTAL_ALIGN_CENTER));
    }

    private void createMenuBar(final AbstractCompositeBuilder<CompositeBuilder> parent) {
        final CompositeBuilder builder = parent
                .addComposite(SWT.NONE)
                .withLayout(new GridLayout(8, false))
                .withLayoutData(new GridData(GRAB_HORIZONTAL | FILL_HORIZONTAL));
        final GridData gridDataCategorieCombo = new GridData();
        gridDataCategorieCombo.widthHint = 200;
        gridDataCategorieCombo.minimumWidth = 200;
        builder.addButton(model.getCategoryButtonModel(), SWT.CHECK).withImage(
                toggleImage);
        builder.addStaticLabel(SWT.NONE).withText(ResultatEtapeEditor_Tri);
        builder.addCombo(model.getCategoriesComboboxModel(), SWT.BORDER)
                .withLayoutData(gridDataCategorieCombo);
        builder.addLabel(model.getInfoLabel(), SWT.BOLD).withLayoutData(
                new GridData(GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
        builder.addButton(model.getConfigEtapeButtonModel(), SWT.NONE)
                .withImage(configEtapeImage)
                .withToolTip(
                        ResultatEtapeEditor_Afficher_la_configuration_de_l_etape)
                .withLayoutData(new GridData(HORIZONTAL_ALIGN_END));
        builder.addButton(model.getConfigPenalityButtonModel(), SWT.NONE)
                .withImage(configPenaliteImage)
                .withToolTip(
                        ResultatEtapeEditor_Afficher_la_configuration_des_penalitees);
        builder.addButton(model.getChronosButtonModel(), SWT.NONE)
                .withImage(configChronosImage)
                .withToolTip(ResultatEtapeEditor_Afficher_les_chronos);
        builder.addButton(model.getRefreshButtonModel(), SWT.NONE)
                .withImage(refreshImage)
                .withToolTip(ResultatEtapeEditor_Actualiser_les_calculs);
    }

    private Table createTable(final CommonCompositeBuilder<Composite, CompositeBuilder> builder) {
        final TableModel<ObjResultat> tableModel = model
                .getTableResultatModel();
        final TableBuilder<ObjResultat> tableBuilder = builder
                .addTable(tableModel,
                        SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | COLORS_ON_SELECTION)
                .withLine(true).selectionProvider(getSite());
        tableBuilder.withLayoutData(new GridData(FILL_BOTH | GRAB_HORIZONTAL
                | GRAB_VERTICAL));
        for (final ColTable colTable : model.getColumnConfig()) {
            tableBuilder
                    .addColumn(colTable.getChamp(), colTable.getLib(), colTable.getAlign())
                    .withWidth(colTable.getWidth())
                    .movable(true)
                    .withToolTipText(colTable.getLib())
                    .withRenderer(new ResultatEtapeColumnRenderer(colTable, model));
        }
        tableBuilder.withLayoutData(
                new GridData(FILL_BOTH | GRAB_HORIZONTAL | GRAB_VERTICAL))
                .withHeader(true);

        ViewColumnViewerToolTipSupport.enableFor(tableBuilder.getViewer());
        return tableBuilder.getTable();
    }

    private void createFootBar(final AbstractCompositeBuilder<CompositeBuilder> builder) {
        final CompositeBuilder footBar = builder
                .addComposite(SWT.NONE)
                .withLayoutData(
                        new GridData(FILL_HORIZONTAL | HORIZONTAL_ALIGN_FILL))
                .withLayout(new GridLayout(3, false));
        footBar.addStaticLabel(SWT.NONE).withText(ResultatEtapeEditor_Info);
        final GridData layoutData = new GridData(300, 20);
        layoutData.horizontalSpan = 2;
        footBar.addText(model.getPrintInfoTextModel(), SWT.BORDER)
                .withLayoutData(layoutData);

        footBar.addStaticLabel(SWT.NONE).withText(i18n("Result.search"));
        footBar.addText(model.getSearchTextModel(), SWT.BORDER | SWT.SINGLE)
                .withLayoutData(new GridData(300, 20));
        footBar.addCheckbox(model.getFilterBySearchModel(), SWT.NONE)
                .withLayoutData(new GridData(100, 20));
    }

    public ResultatEtapeModel getModel() {
        return model;
    }

    @Override
    public void dispose() {
        compositeBuilder.dispose();
        model.dispose();
        super.dispose();
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        model.getStep().setInfo(ObjStep.VAR_TITLE_PRINT,
                model.getPrintInfoTextModel().getText());
        setDirty(false);
    }

    @Override
    public boolean isDirty() {
        return model.isDirty();
    }

    public void setDirty(final boolean dirtyState) {
        model.setDirty(dirtyState);
        firePropertyChange(PROP_DIRTY);
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void print() {
        try {
            final AbstractList<String> titles = new ArrayList<String>();
            final ObjStep etape = model.getStep();
            final String title = etape.getManif().getNom();
            String title2 = ResultatEtapeEditor_Classement + ' '
                    + etape.getLib();
            if (model.getCategoriesComboboxModel().getText() != null) {
                title2 += " - " + model.getCategoriesComboboxModel().getText();
            }
            titles.add(title);
            titles.add(title2);
            if (!model.getPrintInfoTextModel().getText().equals(EMPTY)) {
                titles.add(model.getPrintInfoTextModel().getText());
            }
            final String[] titlesStr = new String[titles.size()];
            int i = 0;
            for (Iterator<String> iter = titles.iterator(); iter.hasNext(); i++) {
                titlesStr[i] = iter.next();
            }
            final String type;
            switch (model.getMode()) {
                case 1:
                    type = " (Details)";
                    break;
                case 2:
                    type = " (Inter)";
                    break;
                default:
                    type = "";
            }
            for (int col = 0; col < table.getColumns().length; col++) {
                int size = 0;
                for (final TableItem item : table.getItems()) {
                    final int textLenght = item.getText(col).length();
                    size = textLenght > size ? textLenght : size;
                }
                if (size == 0) {
                    table.getColumn(col).setWidth(0);
                }
            }

            test();

            final DateFormat sdf = new SimpleDateFormat("yyMMdd");
            openPrintPreview(table, titlesStr,
                    ResultatEtapeEditor_RESULTATS_PRINT_TEXT + etape.getLib()
                            + type + ' ' + sdf.format(new Date()));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    void test() {
        GC          gc    = new GC(Display.getCurrent());
        final Image image = new Image(Display.getCurrent(), 400, 400);
        gc.copyArea(image, 0, 0);
        gc.dispose();

        Shell popup = new Shell(Display.getCurrent());
        popup.setText("Image");
        popup.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event e) {
                image.dispose();
            }
        });

        Canvas canvas = new Canvas(popup, SWT.NONE);
        canvas.setBounds(10, 10, 400 + 10, 400 + 10);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(image, 0, 0);
            }
        });
        popup.pack();
        popup.open();

    }

    @Override
    public AutoResizeColumn getAutoResizeContext() {
        return model.getAutoResizeContext();
    }

    public static class LaunchEditorAction extends ButtonAdapter {
        private final String              id;
        private final IEditorInput        input;
        private final ResultatEtapeEditor editor;

        LaunchEditorAction(
                final ResultatEtapeEditor editor,
                final IEditorInput input,
                final String id) {
            this.editor = editor;
            this.id = id;
            this.input = input;
        }

        static LaunchEditorAction createStepEditorAction(final ResultatEtapeEditor editor,
                                                         final StepModel stepModel,
                                                         final ResultatEtapeModel model) {
            return new LaunchEditorAction(editor, new StepEditorInput(
                    new StepEditorModel(stepModel)), STEP_EDITOR_ID);
        }

        static LaunchEditorAction createChronosEditorAction(final ResultatEtapeEditor editor,
                                                            final ResultatEtapeModel model) {
            return new LaunchEditorAction(editor, new EtapeEditorInput(
                    model.getStep(), CHRONOS_EDITOR_ID,
                    model.getAutoResizeContext()), CHRONOS_EDITOR_ID);
        }

        static LaunchEditorAction createPenalityEditorAction(final ResultatEtapeEditor editor,
                                                             final ResultatEtapeModel model) {
            return new LaunchEditorAction(editor, new EtapeEditorInput(
                    model.getStep(), CHRONOS_EDITOR_ID,
                    model.getAutoResizeContext()), PENALITY_EDITOR_ID);
        }

        @Override
        public void click() {
            WorkbenchUtils.openEditor(editor, input, id);
        }
    }

    private static class ResultatEtapeColumnRenderer extends
            TableColumnRenderer<ObjResultat> {
        private final ColTable           colTable;
        private final ResultatEtapeModel model;

        ResultatEtapeColumnRenderer(
                final ColTable colTable,
                final ResultatEtapeModel model) {
            this.colTable = colTable;
            this.model = model;
        }

        private static void addInfo(final String label,
                                    final String attribute,
                                    final AbstractGetInfo resultat,
                                    final StringBuilder builder) {
            final String element = resultat.getInfoStr(attribute);
            if (!element.isEmpty()) {
                if (element.length() > 80) {
                    builder.append("\n  ").append(label).append("\n    ");
                    int length = 0;
                    for (final String split : element.split("]")) {
                        length += split.length();
                        if (length < 80) {
                            builder.append(split).append(']');
                        } else {
                            length = split.length();
                            builder.append("\n    ").append(split).append(']');
                        }
                    }
                } else {
                    builder.append("\n  ").append(label).append(" ").append(element);
                }
            }
        }

        private static void addInfoDate(final String label,
                                        final String info,
                                        final AbstractGetInfo resultat,
                                        final StringBuilder builder) {
            final Date2 date = (Date2) resultat.getInfo(info);
            if (date != null && !date.isNull()) {
                builder.append("\n  ").append(label).append(" ").append(date);
            }
        }

        @Override
        public String getToolTipText(final Object element) {
            final ObjResultat resultat = (ObjResultat) element;

            final StringBuilder builder = new StringBuilder();

            builder.append(resultat.getDossard().getNum()).append(". ")
                    .append(resultat.getDossard().getInfoStr("S.FIRSTNAME"));

            addInfo(i18n("Result.tooltip.finalTime"),
                    ObjResultat.VAR_RESULTAT_TEMPS, resultat, builder);
            builder.append("\n-----");
            addInfoDate(i18n("Result.tooltip.chronoTime"),
                    ObjResultat.VAR_TEMPS_CHRONO, resultat, builder);
            if (!resultat.getTempsArretChronoResultat().isNull()) {
                builder.append(" = ").append(resultat.getInfo(ObjResultat.VAR_TEMPSPARCOURS)).append(" ")
                        .append(resultat.getTempsArretChronoResultat());
            }
            addInfoDate(i18n("Result.tooltip.chronoMini"),
                    ObjResultat.VAR_TEMPS_CHRONO_MINI, resultat, builder);
            addInfoDate(i18n("Result.tooltip.bonusTime"),
                    ObjResultat.VAR_BONIFICATION, resultat, builder);
            addInfoDate(i18n("Result.tooltip.otherPenalityTime"),
                    ObjResultat.VAR_PENALITE_AUTRE, resultat, builder);
            addInfoDate(i18n("Result.tooltip.penalityTime"),
                    ObjResultat.VAR_PENALITE_BALISE, resultat, builder);
            builder.append("\n-----");
            addInfo(i18n("Result.tooltip.nbBalise"), ObjResultat.VAR_NB_BALISE,
                    resultat, builder);
            addInfo(i18n("Result.tooltip.nbPenaliteBalise"), ObjResultat.VAR_NB_PENALITE,
                    resultat, builder);
            addInfo(i18n("Result.tooltip.nbBaliseBonus"), ObjResultat.VAR_NB_BALISE_BONUS,
                    resultat, builder);
            addInfo(i18n("Result.tooltip.missingBalise"),
                    ObjResultat.VAR_RESULTAT_BALISESMANQUEES, resultat, builder);
            addInfo(i18n("Result.tooltip.baliseList"),
                    ObjResultat.VAR_RESULTAT_BALISES_OK, resultat, builder);
            addInfo(i18n("Result.tooltip.baliseBonus"),
                    ObjResultat.VAR_RESULTAT_BALISESBONUS, resultat, builder);
            addInfo(i18n("Result.tooltip.baliseDisordered"),
                    ObjResultat.VAR_RESULTAT_BALISE_DISORDERED, resultat, builder);


            return "";
        }

        @Override
        public boolean useNativeToolTip(Object object) {
            return false;
        }

        @Override
        public Color getToolTipBackgroundColor(final Object object) {
            return new Color(Display.getCurrent(), 138, 191, 206);
        }

        @Override
        public Font getToolTipFont(final Object object) {
            return new Font(Display.getCurrent(), new FontData("Courier", 10,
                    SWT.BOLD));
        }

        @Override
        public int getToolTipDisplayDelayTime(final Object object) {
            return 0;
        }

        @Override
        public Color getBackground(final Object element) {
            final ObjResultat resultat = (ObjResultat) element;
            if (resultat.isError() && model.isSignalError()) {
                resultat.showErrors();
                return new Color(Display.getCurrent(), 255, 0, 0);
            }
            if (colTable.isTmp()) {
                return new Color(Display.getCurrent(), 255, 255, 160);
            } else {
                return getBackgroundColorFromSelection(resultat);
            }
        }

        private Color getBackgroundColorFromSelection(final ObjResultat resultat) {
            final int index = model.matchSelection(resultat);
            if (index != -1) {
                final int red = 75 * index % 255;
                final int blue = (91 * index + (index != 0 ? 100 : 0)) % 255;
                final int green = 255 - (133 * index + (index != 0 ? 30 : 255)) % 255;
                return new Color(Display.getCurrent(), red, green, blue);
            } else {
                return null;
            }
        }

        @Override
        public Color getForeground(final Object element) {
            final ObjResultat resultat = (ObjResultat) element;
            if (resultat.isError() && model.isSignalError()) {
                return new Color(Display.getCurrent(), 255, 255, 255);
            } else {
                return null;
            }
        }

        @Override
        public String getColumnText(final ObjResultat element) {
            return element.getInfoStr(colTable.getChamp());
        }

        @Override
        public int compare(final ObjResultat elem1,
                           final ObjResultat elem2) {
            if (elem1.isAbandon() != elem2.isAbandon()) {
                if (elem1.isAbandon()) {
                    return +1;
                }
                return -1;
            }
            if (elem1.isDeclasse() != elem2.isDeclasse()) {
                if (elem1.isDeclasse()) {
                    return +1;
                }
                return -1;
            }
            if (elem1.isHorsClassement() != elem2.isHorsClassement()) {
                if (elem1.isHorsClassement()) {
                    return +1;
                }
                return -1;
            }
            return super.compare(elem1, elem2);
        }
    }
}
