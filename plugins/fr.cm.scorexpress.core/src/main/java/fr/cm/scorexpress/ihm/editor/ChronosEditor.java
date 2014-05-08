package fr.cm.scorexpress.ihm.editor;

import com.google.common.collect.Lists;
import fr.cm.common.widget.text.TextModifyAdapter;
import fr.cm.scorexpress.applicative.ProjectManagerFactory;
import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import fr.cm.scorexpress.ihm.editor.modifier.GetInfoLabelProvider;
import fr.cm.scorexpress.ihm.print.IPrintable;
import fr.cm.scorexpress.ihm.print.PrintPreview;
import fr.cm.scorexpress.model.ManifModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static fr.cm.common.workbench.WorkbenchUtils.defineCopyViewSiteAction;
import static fr.cm.scorexpress.applicative.ProjectManager.importDonneeConcurrent;
import static fr.cm.scorexpress.core.model.ConfigType.INFOSPORTIDENTS;
import static fr.cm.scorexpress.core.model.ObjChronoArrivee.TYPE;
import static fr.cm.scorexpress.core.model.ObjUserChronos.VAR_PREFIX_BALISE;
import static fr.cm.scorexpress.core.model.impl.ObjStep.VAR_FILENAME_IMPORT;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.eclipse.swt.SWT.*;

public class ChronosEditor extends EditorPart implements IAutoAjustColumnEditor, IPrintable {
    private static final String SPACE = " ";

    private EtapeEditorInput input = null;

    public static final String                      CHRONOS_EDITOR_ID = "fr.cm.chronos.ChronosEditor";
    private             boolean                     autoAjustement    = false;
    private final       AbstractList<ChronosEditor> instances         = Lists.newArrayList();
    private final       boolean                     dirty             = false;

    private Composite   top             = null;
    private TableViewer tableViewer     = null;
    private Table       table           = null;
    private Image       refreshImage    = null;
    private Image       supprimerImage  = null;
    private Image       importImage     = null;
    private Image       importDataImage = null;
    private Button buttonEffacer;
    private Button buttonImport;
    private Button buttonImporterDossard;

    public ChronosEditor() {
        instances.add(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        instances.remove(this);
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        if (!(input instanceof EtapeEditorInput)) {
            throw new PartInitException(ChronosEditor_Etape_Editor_invalide);
        }
        this.input = (EtapeEditorInput) input;
        setSite(site);
        setInput(input);
        this.input.getAutoResizeContext().addAutoResizeListener(new AutoResizeColumn.AutoResizeListener() {
            @Override
            public void autoResizeChanged(final boolean autoResize) {
                autoAjustement = autoResize;
                ajustementDesColonnes();
            }
        });
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return dirty;
    }

    @Override
    public void createPartControl(final Composite parent) {
        refreshImage = getImg(IMG_REFRESH);
        supprimerImage = getImg(IMG_REMOVE);
        importImage = getImg(IMG_IMPORT);
        importDataImage = getImg(IMG_DOWN);
        final GridData gridData2 = new GridData();
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;
        gridData2.grabExcessVerticalSpace = true;
        final GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.verticalAlignment = GridData.FILL;
        gridData1.grabExcessVerticalSpace = true;
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.BEGINNING;
        gridData.horizontalAlignment = GridData.CENTER;
        parent.setLayout(new GridLayout());
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(gridData1);
        final CLabel cLabelTitre = new CLabel(top, SWT.NONE);
        cLabelTitre.setText(ChronosEditor_Gestion_des_chronometres);
        cLabelTitre.setLayoutData(gridData);
        createComposite();
        tableViewer = new TableViewer(top, SWT.SELECTED | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLayoutData(gridData2);
        table.setLinesVisible(true);
        defineCopyViewSiteAction(parent.getDisplay(), getEditorSite().getActionBars());
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Collection<Balise> gatherBalise(final ObjStep etape) {
        final Collection<Balise> res = new TreeSet<Balise>(new Comparator<Balise>() {
            @Override
            public int compare(final Balise b1, final Balise b2) {
                return b1.getNum().compareTo(b2.getNum());
            }

        });
        res.addAll(etape.getBalises());
        for (final ObjStep objStep : etape.getSteps()) {
            final Collection<Balise> tab = gatherBalise(objStep);
            res.addAll(tab);
        }
        return res;
    }

    private void update() throws TableConfigurationException {
        final ObjStep etape = input.getEtape();
        final ObjConfig config = etape.getManif().getConfiguration().getConfig(INFOSPORTIDENTS);
        if (config == null) {
            throw new TableConfigurationException(ChronosEditor_Pas_de_configuration_pour_la_table + SPACE + INFOSPORTIDENTS);
        }
        /* Initialisation des colonnes Dynamiques */
        for (final ColTable colTable : config.getColTableAll()) {
            if (colTable.isTmp()) {
                config.removeColTableDynamic(colTable);
            }
        }
        for (final Balise balise : gatherBalise(etape)) {
            final ColTable colTable =
                    new ColTableBuilder(VAR_PREFIX_BALISE + balise.getNum(), 'B' + balise.getNum()).temp().create();
            config.addColTableDynamic(colTable);
        }
        /* Balise de d'arrivée */
        final ColTable colTableArrivee =
                new ColTableBuilder(VAR_PREFIX_BALISE + TYPE, ChronosEditor_Arrivee).temp().create();
        config.addColTableDynamic(colTableArrivee);
        final Listener sortListener = new SortListener(tableViewer);
        for (final ColTable colTable : config.getColTableAll()) {
            final TableColumn tableColumn = new TableColumn(tableViewer.getTable(), SWT.CENTER);
            tableColumn.setText(colTable.getLib());
            tableColumn.setWidth(colTable.getWidth());
            tableColumn.setToolTipText(colTable.getLib2());
            tableColumn.setAlignment(colTable.getAlign());
            tableColumn.setMoveable(true);
            tableColumn.setData(colTable);
            tableColumn.addListener(SWT.Selection, sortListener);
        }
        final ManifModel manifModel = new ManifModel(etape.getManif());
        manifModel.getSearchTextModel().addModifyListener(new TextModifyAdapter<String>(){
            @Override
            public void onExit() {
                tableViewer.refresh();
            }
        });
        tableViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                final String dossard = ((ObjUserChronos) element).getDossard();
                return !manifModel.isFiltered() ||  manifModel.matchSelection(dossard) >=0;
            }
        });

        tableViewer.setLabelProvider(new ChronoLabelProvider(config.getColTableAll(), manifModel));
        tableViewer.setContentProvider(new TableUserChronosProvider());
        updateData();
    }

    @Override
    public void setFocus() {
        table.setFocus();
    }

    private void createComposite() {
        final GridData gridDataButtonImportDos = new GridData();
        gridDataButtonImportDos.grabExcessHorizontalSpace = true;
        gridDataButtonImportDos.verticalAlignment = GridData.CENTER;
        gridDataButtonImportDos.horizontalAlignment = GridData.END;
        final GridData gridDataButtonEffacer = new GridData();
        gridDataButtonEffacer.horizontalAlignment = GridData.END;
        gridDataButtonEffacer.grabExcessHorizontalSpace = false;
        gridDataButtonEffacer.verticalAlignment = GridData.CENTER;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        final GridData gridData4 = new GridData();
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.verticalAlignment = GridData.CENTER;
        gridData4.horizontalAlignment = GridData.FILL;
        final GridData gridDataButtonRefresh = new GridData();
        gridDataButtonRefresh.horizontalAlignment = GridData.END;
        gridDataButtonRefresh.grabExcessHorizontalSpace = false;
        gridDataButtonRefresh.verticalAlignment = GridData.CENTER;
        final Composite composite = new Composite(top, SWT.NONE);
        composite.setLayoutData(gridData4);
        composite.setLayout(gridLayout);
        buttonImporterDossard = new Button(composite, SWT.NONE);
        buttonImporterDossard.setLayoutData(gridDataButtonImportDos);
        buttonImporterDossard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                try {
                    importDossard();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttonImporterDossard.setImage(importImage);
        buttonImporterDossard.setToolTipText(ChronosEditor_Importation_des_dossards);
        buttonImport = new Button(composite, SWT.NONE);
        buttonImport.setImage(importDataImage);
        final ObjStep step = input.getEtape();
        buttonImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                ProjectManagerFactory.getAutoImportProcess().importSportIdent(step);
                updateData();
            }
        });
        buttonImport.setToolTipText("Importation de " + step.getInfoStr(VAR_FILENAME_IMPORT));
        buttonEffacer = new Button(composite, SWT.NONE);
        buttonEffacer.setImage(supprimerImage);
        buttonEffacer.setLayoutData(gridDataButtonEffacer);
        buttonEffacer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                step.clearUserChronos();
                refreshAll();
            }
        });
        buttonEffacer.setToolTipText(ChronosEditor_Suppression_des_informations_de_chronometrage);

        final Button buttonRefresh = new Button(composite, SWT.NONE);
        buttonRefresh.setLayoutData(gridDataButtonRefresh);
        buttonRefresh.setImage(refreshImage);
        buttonRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateData();
            }
        });
        buttonRefresh.setToolTipText(ChronosEditor_Actualisation_de_l_affichage);
    }

    public synchronized void updateData() {
        setPartName(input.getEtape().getLib());
        final TableItem[] itemsSelection = table.getSelection();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (tableViewer.getTable().isDisposed()) {
                    return;
                }
                top.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (table.isDisposed()) {
                            return;
                        }
                        refreshAll();
                        table.setSelection(itemsSelection);
                        ajustementDesColonnes();
                    }
                });
            }
        }, "Refresh chrono editor");
        thread.start();
    }

    private void refreshAll() {
        final ObjStep step = input.getEtape();

        buttonEffacer.setEnabled(!step.getUserChronos().isEmpty());

        final boolean importData = step.getImportFileName() != null && !step.getImportFileName().isEmpty();
        buttonImport.setEnabled(importData);

        buttonImporterDossard.setEnabled(importData);

        tableViewer.setInput(new ArrayList(step.getUserChronos()));
    }

    private void importDossard() {
        final String fileName = input.getEtape().getEpreuve().getImportFileName();
        if (fileName != null) {
            if (openQuestion(Display.getCurrent().getActiveShell(), ChronosEditor_IMPORTER_DOSSARD, fileName)) {
                importDonneeConcurrent(input.getEtape(), fileName);
            }
        }
    }

    private void ajustementDesColonnes() {
        if (autoAjustement) {
            for (final ChronosEditor editor : instances) {
                editor.table.setVisible(false);
                for (int i = 0; i < editor.table.getColumnCount(); i++) {
                    editor.table.getColumn(i).pack();
                }
                editor.table.setVisible(true);
            }
        }
    }

    @Override
    public AutoResizeColumn getAutoResizeContext() {
        return input.getAutoResizeContext();
    }

    static class TableUserChronosProvider implements IStructuredContentProvider {
        @Override
        public Object[] getElements(final Object inputElement) {
            return ((Collection<?>) inputElement).toArray();
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    class ChronoLabelProvider extends GetInfoLabelProvider implements ITableColorProvider {
        private final ManifModel model;

        ChronoLabelProvider(final List<ColTable> colTables, final ManifModel model) {
            super(colTables);
            this.model = model;
        }

        @Override
        public Color getForeground(final Object element, final int columnIndex) {
            return null;
        }

        @Override
        public Color getBackground(final Object element, final int columnIndex) {
            final ObjUserChronos userChronos = (ObjUserChronos) element;
            final ColTable colTable = (ColTable) tableViewer.getTable().getColumn(columnIndex).getData();
            final String champ = colTable.getChamp();
            final int index = champ.indexOf(VAR_PREFIX_BALISE);
            if (index != -1) {
                final String numBalise = champ.substring(VAR_PREFIX_BALISE.length());
                final ObjChrono chrono = userChronos.getChronoEnableHasPossible(numBalise);
                if (chrono != null && chrono.isCancel()) {
                    return Display.getCurrent().getSystemColor(COLOR_DARK_GREEN);
                } else {
                    if (chrono == null || chrono.isNull()) {
                        return Display.getCurrent().getSystemColor(COLOR_WIDGET_NORMAL_SHADOW);
                    } else {
                        return getBackgroundColorFromSelection(userChronos, model);
                    }
                }
            }
            return getBackgroundColorFromSelection(userChronos, model);
        }

        private Color getBackgroundColorFromSelection(final ObjUserChronos resultat, final ManifModel manifModel) {
            final int index = manifModel.matchSelection(resultat.getDossard());
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
        public String getColumnText(final ColTable colTable, final Object element) {
            final ObjUserChronos userChronos = (ObjUserChronos) element;
            final String champ = colTable.getChamp();

            final int index = champ.indexOf(VAR_PREFIX_BALISE);
            if (index != -1) {
                final String numBalise = champ.substring(VAR_PREFIX_BALISE.length());
                final ObjChrono chrono = userChronos.getChronoEnableHasPossible(numBalise);
                if (chrono != null && chrono.isCancel()) {
                    return super.getColumnText(colTable, element) + "(Suppr)";
                } else {
                    if (chrono == null || chrono.isNull()) {
                        return super.getColumnText(colTable, element) + "---";
                    }
                }
            }
            return super.getColumnText(colTable, element);
        }
    }

    @Override
    public void print() {
        try {
            final AbstractList<String> titles = new ArrayList<String>();
            final AbstractSteps etape = input.getEtape();
            final String title = etape.getManif().getNom();
            final String title2 = ChronosEditor_CHRONOS + etape.getLib();
            titles.add(title);
            titles.add(title2);
            final String[] titlesStr = new String[titles.size()];
            int i = 0;
            for (Iterator<String> iter = titles.iterator(); iter.hasNext(); i++) {
                titlesStr[i] = iter.next();

            }
            final DateFormat sdf = new SimpleDateFormat("yyMMdd");
            PrintPreview.openPrintPreview(tableViewer.getTable(), titlesStr,
                                          ChronosEditor_CHRONOS + etape.getLib() + SPACE + sdf.format(new Date()));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static class SortListener implements Listener {
        private final TableViewer viewer;

        SortListener(final TableViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void handleEvent(final Event e) {
            // determine new sort column and direction
            final TableColumn sortColumn = viewer.getTable().getSortColumn();
            final TableColumn currentColumn = (TableColumn) e.widget;
            int dir = viewer.getTable().getSortDirection();
            if (asSameInstance(sortColumn, currentColumn)) {
                dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
            } else {
                viewer.getTable().setSortColumn(currentColumn);
                dir = SWT.UP;
            }
            // sort the data based on column and direction
            final int direction = dir;
            final ColTable colTable = (ColTable) currentColumn.getData();
            final String attribut = colTable.getChamp();
            final List<IData> data = (List<IData>) viewer.getInput();
            if (attribut.contains(VAR_PREFIX_BALISE)) {
                final String numBalise = attribut.substring(VAR_PREFIX_BALISE.length());
                Collections.sort(data, new Comparator<IData>() {
                    @Override
                    public int compare(final IData data1, final IData data2) {
                        final ObjUserChronos user1 = (ObjUserChronos) data1;
                        final ObjUserChronos user2 = (ObjUserChronos) data2;
                        final ObjChrono chrono1 = user1.getChronoEnableHasPossible(numBalise);
                        final ObjChrono chrono2 = user2.getChronoEnableHasPossible(numBalise);
                        if (chrono1 == null != (chrono2 == null)) {
                            if (chrono1 == null) {
                                return direction == SWT.UP ? 1 : -1;
                            }
                            return direction == SWT.UP ? -1 : 1;
                        }
                        if (chrono1 == null) {
                            return user1.getDossard().compareTo(user2.getDossard()) * direction == SWT.UP ? 1 : -1;
                        }
                        if (chrono1.isNull() != chrono2.isNull()) {
                            if (chrono1.isNull()) {
                                return direction == SWT.UP ? 1 : -1;
                            }
                            return direction == SWT.UP ? -1 : 1;
                        }
                        return chrono1.getTemps().compareTo(chrono2.getTemps()) * direction == SWT.UP ? 1 : -1;
                    }
                });
            } else {
                Collections.sort(data, new Comparator<IData>() {
                    @Override
                    public int compare(final IData arg0, final IData arg1) {
                        final String str1 = arg0.getInfoStr(attribut);
                        final String str2 = arg1.getInfoStr(attribut);
                        try {
                            final Integer value1 = new Integer(str1);
                            final Integer value2 = new Integer(str2);
                            if (direction == SWT.UP) {
                                return value1.compareTo(value2);
                            }
                            return value2.compareTo(value1);
                        } catch (Exception ignored) {
                        }
                        if (direction == SWT.UP) {
                            return str1.compareTo(str2);
                        }
                        return str2.compareTo(str1);

                    }
                });
            }
            // update data displayed in table
            viewer.getTable().setSortDirection(dir);
            viewer.refresh();
        }
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static <T> boolean asSameInstance(final T instance1, final T instance2) {
        return instance1 == instance2;
    }
} 
