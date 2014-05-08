package fr.cm.scorexpress.ihm.editor;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.AbstractBalises;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.AbstractSteps;
import fr.cm.scorexpress.core.model.Balise;
import fr.cm.scorexpress.core.model.BaliseFactory;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.ObjBalise;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import fr.cm.scorexpress.ihm.editor.modifier.AbstractGetInfoCellModifier;
import fr.cm.scorexpress.ihm.editor.modifier.BooleanCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.GetInfoLabelProvider;
import fr.cm.scorexpress.ihm.editor.modifier.IInfoListener;
import fr.cm.scorexpress.ihm.editor.modifier.MyComboBoxCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.MyTextCellEditor;
import fr.cm.scorexpress.ihm.view.NavigateurView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.applicative.ProjectManagerFactory.getAutoImportProcess;
import static fr.cm.scorexpress.core.model.Balise.*;
import static fr.cm.scorexpress.core.model.ColTableType.BOOLEAN;
import static fr.cm.scorexpress.core.model.ConfigType.BALISES;
import static fr.cm.scorexpress.core.model.impl.StepFactory.createStep;
import static fr.cm.scorexpress.data.UserChronosLoader.createUserChrono;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.layout.GridData.BEGINNING;
import static org.eclipse.swt.layout.GridData.FILL;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TITLE_BAR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

public class EtapeConfigurationEditor extends EditorPart implements SelectionListener {
    private AutoResizeColumn autoResizeContext;

    static class BaliseCellModifier extends AbstractGetInfoCellModifier {

        BaliseCellModifier(final TableViewer viewer, final ObjConfig config) {
            super(viewer, config);
        }

        @Override
        public void modify(final ColTable colTable, final IData element, final String property, final Object value) {
            super.modify(colTable, element, property, value);
            refreshView();
        }

        @Override
        public void modify(final Object element, final String property, final Object value) {
            super.modify(element, property, value);
        }

    }

    static class BaliseContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ObjStep) {
                final AbstractBalises etape = (AbstractBalises) inputElement;
                final Collection<Balise> balises = newArrayList();
                for (final Balise balise : etape.getBalises()) {
                    if (!balise.getType().equals(START_TYPE_BALISE) && !balise.getType().equals(END_TYPE_BALISE)) {
                        balises.add(balise);
                    }
                }
                return balises.toArray();
            }
            return null;
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    static class BaliseLabelProvider extends GetInfoLabelProvider {
        BaliseLabelProvider(final ObjConfig config) {
            super(config);
        }
    }

    class DirtyListener implements ModifyListener, SelectionListener {

        @Override
        public void modifyText(final ModifyEvent e) {
            setDirty(true);
        }

        @Override
        public void widgetDefaultSelected(final SelectionEvent e) {
        }

        @Override
        public void widgetSelected(final SelectionEvent e) {
            setDirty(true);
        }

    }

    static class EtapeContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ObjStep) {
                final AbstractSteps etape = (AbstractSteps) inputElement;
                return etape.getSteps().toArray();
            }
            return null;
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    static class EtapeLabelProvider implements ILabelProvider {
        private final Image imgArretChrono = getImg(IMG_ARRETCHRONO);
        private final Image imgBalise      = getImg(IMG_BALISE);
        private final Image imgDisable     = getImg(IMG_PENALITY_DESACTIVATE);
        private final Image imgEtape       = getImg(IMG_ETAPE);

        @Override
        public void addListener(final ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Image getImage(final Object element) {
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                if (!etape.isActif()) {
                    return imgDisable;
                }
                if (etape.isArretChrono()) {
                    return imgArretChrono;
                } else {
                    return imgEtape;
                }
            }
            if (element instanceof ObjBalise) {
                return imgBalise;
            }
            return null;
        }

        @Override
        public String getText(final Object element) {
            if (element instanceof Balise) {
                final Balise balise = (Balise) element;
                return balise.getType() + " (" + balise.getNum() + ')';
            }
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                String message = " ["; //$NON-NLS-1$
                if (etape.getBaliseDepart() == null) {
                    message += ".. "; //$NON-NLS-1$
                } else {
                    message += etape.getBaliseDepart() + ' '; //$NON-NLS-1$
                }
                if (etape.getBaliseArrivee() == null) {
                    message += "..]"; //$NON-NLS-1$
                } else {
                    message += etape.getBaliseArrivee() + ']'; //$NON-NLS-1$
                }
                return etape.getLib() + message;
            }
            return EMPTY;
        }

        @Override
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        @Override
        public void removeListener(final ILabelProviderListener listener) {
        }

    }

    static class EtapeTreeContentProvider implements ITreeContentProvider {
        @Override
        public void dispose() {
        }

        @Override
        public Object[] getChildren(final Object parentElement) {
            final Collection<Object> res = new ArrayList<Object>();
            if (parentElement instanceof ObjStep) {
                final ObjStep etape = (ObjStep) parentElement;
                res.addAll(etape.getSteps());
                for (final Balise balise : etape.getBalises()) {
                    if (!balise.getType().equals(START_TYPE_BALISE) && !balise.getType().equals(END_TYPE_BALISE)) {
                        res.add(balise);
                    }

                }
            }
            if (parentElement instanceof EtapeEditorInput) {
                res.add(((EtapeEditorInput) parentElement).getEtape());
            }
            return res.toArray();
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public Object getParent(final Object element) {
            if (element instanceof IData) {
                return ((AbstractGetInfo) element).getParent();
            }
            return null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            final Collection<Object> res = new ArrayList<Object>();
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                res.addAll(etape.getSteps());
                for (final Balise balise : etape.getBalises()) {
                    if (!balise.getType().equals(START_TYPE_BALISE) && !balise.getType().equals(END_TYPE_BALISE)) {
                        res.add(balise);
                    }

                }
            }
            return !res.isEmpty();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }

    }

    class TreeEtapeSelectionListener implements SelectionListener {
        @Override
        public void widgetDefaultSelected(final SelectionEvent element) {
        }

        @Override
        public void widgetSelected(final SelectionEvent element) {
            if (isDirty() && MessageDialog
                    .openQuestion(getSite().getShell(), EtapeConfigurationEditor_Enregistrer_les_modifications,
                                  EtapeConfigurationEditor_Voulez_vous_enregistrer_les_modifications)) {
                sav();
            }
            if (element.item.getData() instanceof ObjBalise) {
                // ObjBalise balise = (ObjBalise) element.item.getData();
                buttonBaliseAjouter.setEnabled(false);
                buttonEtapeAjouter.setEnabled(false);
                buttonEtapeSupprimer.setEnabled(true);
                balisesTableViewer.setInput(null);
                updateEtapeInfo(null);
                return;
            }
            if (element.item.getData() instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element.item.getData();
                buttonEtapeAjouter.setEnabled(true);
                buttonEtapeSupprimer.setEnabled(true);
                buttonBaliseAjouter.setEnabled(true);
                balisesTableViewer.setInput(etape);
                ajustementAutomatique(balisesTable);
                updateEtapeInfo(etape);
                return;
            }
            updateEtapeInfo(null);
        }
    }

    public static final String           CONFIGURATION_EDITOR_ID = "fr.cm.chronos.etapeConfigurationEditor";
    private             Table            balisesTable            = null;
    private             TableViewer      balisesTableViewer      = null;
    private             Button           buttonBaliseAjouter     = null;
    private             Button           buttonEtapeAjouter      = null;
    private             Button           buttonEtapeSupprimer    = null;
    private             Button           buttonImporter          = null;
    private             Button           buttonImportGeneral     = null;
    private             Button           buttonShowDatas         = null;
    private             Button           buttonValider           = null;
    private             Button           checkBoxActive          = null;
    private             Button           checkBoxArretChrono     = null;
    private             Button           checkBoxArriveeGenerale = null;
    private             Button           checkBoxDepartGeneral   = null;
    private             Composite        compositeTree           = null;
    private             Composite        compSousEtape           = null;
    private             Composite        compTreeEtape           = null;
    private             boolean          dirty                   = false;
    private             ObjStep          etapeSelected           = null;
    private             Label            importFile              = null;
    private             EtapeEditorInput input                   = null;
    private             Label            label                   = null;
    private             Text             textBaliseArrivee       = null;
    private             Text             textBaliseDepart        = null;
    private             Text             textDescription         = null;
    private             Text             textNomEtape            = null;
    private FormToolkit toolkit;

    private Tree treeEtape = null;

    private TreeViewer treeViewer = null;

    private static void addTableKeyMove(final TableViewer tv) {
        final TableCursor cursor = new TableCursor(tv.getTable(), SWT.NONE);
        cursor.setEnabled(true);
        cursor.setVisible(false);
        tv.getTable().addKeyListener(new NavigateOnTableAction(tv, cursor));
    }

    private static void ajustementAutomatique(final Table table) {
        table.setVisible(false);
        for (int j = 0; j < table.getColumnCount(); j++) {
            table.getColumn(j).pack();
        }
        table.setVisible(true);
    }

    private static CellEditor createCellEditor(final ColTable colTable, final Table table) {
        final CellEditor returnEditor;
        if (colTable.getChoix() != null) {
            final String[] datas = {TYPE_OBLIGATOIRE, TYPE_ORDONNEE, TYPE_PAS_OBLIGATOIRE, TYPE_BONUS, TYPE_PENALITY};
            returnEditor = new MyComboBoxCellEditor(table, datas);
        } else if (colTable.getType().equals(BOOLEAN)) {
            returnEditor = new BooleanCellEditor(table, SWT.CHECK);
        } else {
            returnEditor = new MyTextCellEditor(table);
        }
        return returnEditor;
    }

    private Label createImportLabel(final Composite parent) {
        final GridData gridData16 = new GridData();
        gridData16.grabExcessHorizontalSpace = false;
        gridData16.verticalAlignment = GridData.CENTER;
        gridData16.widthHint = 60;
        gridData16.horizontalAlignment = BEGINNING;
        final Label importFile = toolkit.createLabel(parent, EMPTY, SWT.NONE);
        importFile.setLayoutData(gridData16);
        return importFile;
    }

    private void createComposite(final Composite parent) {
        final GridData gridDataFill = new GridData();
        gridDataFill.grabExcessHorizontalSpace = true;
        gridDataFill.horizontalAlignment = FILL;
        gridDataFill.verticalAlignment = FILL;
        gridDataFill.grabExcessVerticalSpace = false;
        final Composite composite = toolkit.createComposite(parent);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(gridDataFill);
        createCompositeTree(parent);
        createCompSousEtape(parent);
        createCompositeBalises(parent);
        updateEtapeInfo(input.getEtape());
    }

    private void createCompositeBalises(final Composite parent) {
        final GridData gridDataCompBalise = new GridData();
        gridDataCompBalise.grabExcessHorizontalSpace = true;
        gridDataCompBalise.horizontalAlignment = FILL;
        gridDataCompBalise.verticalAlignment = FILL;
        gridDataCompBalise.grabExcessVerticalSpace = false;
        gridDataCompBalise.minimumHeight = 200;
        gridDataCompBalise.minimumWidth = 200;
        gridDataCompBalise.heightHint = 200;
        final GridData gridDataLabBalise = new GridData();
        gridDataLabBalise.grabExcessVerticalSpace = false;
        gridDataLabBalise.horizontalAlignment = FILL;
        gridDataLabBalise.verticalAlignment = FILL;
        gridDataLabBalise.horizontalSpan = 2;
        gridDataLabBalise.grabExcessHorizontalSpace = true;
        final GridData gridDataBaliseTable = new GridData();
        gridDataBaliseTable.grabExcessHorizontalSpace = true;
        gridDataBaliseTable.horizontalAlignment = FILL;
        gridDataBaliseTable.verticalAlignment = FILL;
        gridDataBaliseTable.horizontalSpan = 2;
        gridDataBaliseTable.grabExcessVerticalSpace = true;
        gridDataBaliseTable.minimumHeight = 200;
        final GridLayout gridCompBaliseLayout = new GridLayout();
        gridCompBaliseLayout.numColumns = 2;
        final GridData gridDataExp = new GridData();
        gridDataExp.grabExcessHorizontalSpace = true;
        gridDataExp.horizontalAlignment = FILL;
        gridDataExp.verticalAlignment = FILL;
        final ExpandableComposite expandableComp = toolkit.createSection(parent, TWISTIE | TITLE_BAR);
        expandableComp.setText(EtapeConfigurationEditor_BALISE_ETAPE_SELECTIONNEE);
        expandableComp.setLayout(new GridLayout());
        expandableComp.setLayoutData(gridDataExp);
        final Composite compBalises = toolkit.createComposite(expandableComp, BORDER);
        compBalises.setLayout(gridCompBaliseLayout);
        compBalises.setLayoutData(gridDataCompBalise);
        final Label cLabelBalise = toolkit.createLabel(compBalises, EtapeConfigurationEditor_Balises, SWT.NONE);
        cLabelBalise.setLayoutData(gridDataLabBalise);
        balisesTableViewer = new TableViewer(compBalises, SWT.MULTI | SWT.FULL_SELECTION);
        balisesTable = balisesTableViewer.getTable();
        balisesTable.setHeaderVisible(true);
        balisesTable.setLayoutData(gridDataBaliseTable);
        balisesTable.setLinesVisible(true);
        balisesTable.setSize(new Point(200, 100));
        expandableComp.setClient(compBalises);
        expandableComp.setExpanded(true);
    }

    private void createCompositeSousEtape() {
        final GridData gridDataCompGroupButton = new GridData();
        gridDataCompGroupButton.grabExcessVerticalSpace = false;
        gridDataCompGroupButton.verticalAlignment = BEGINNING;
        gridDataCompGroupButton.horizontalAlignment = BEGINNING;
        gridDataCompGroupButton.grabExcessVerticalSpace = true;
        final Composite compGroupButton = toolkit.createComposite(compositeTree);
        compGroupButton.setLayout(new GridLayout());
        compGroupButton.setLayoutData(gridDataCompGroupButton);
        buttonEtapeAjouter =
                toolkit.createButton(compGroupButton, EtapeConfigurationEditor_Ajouter_sous_etape, SWT.NONE);
        buttonBaliseAjouter = toolkit.createButton(compGroupButton, EtapeConfigurationEditor_Ajouter_balise, SWT.NONE);
        buttonEtapeSupprimer = toolkit.createButton(compGroupButton, EtapeConfigurationEditor_SupprimerEtape, SWT.NONE);
        buttonImportGeneral = toolkit.createButton(compGroupButton, EtapeConfigurationEditor_Importer2, SWT.NONE);
        buttonEtapeAjouter.addSelectionListener(this);
        buttonEtapeSupprimer.addSelectionListener(this);
        buttonBaliseAjouter.addSelectionListener(this);
        buttonImportGeneral.addSelectionListener(this);
    }

    private void createCompositeTree(final Composite parent) {
        final GridData gridDataTree = new GridData();
        gridDataTree.horizontalAlignment = FILL;
        gridDataTree.grabExcessHorizontalSpace = true;
        gridDataTree.heightHint = 150;
        gridDataTree.verticalAlignment = GridData.CENTER;
        final GridLayout gridLayoutCompTree = new GridLayout();
        gridLayoutCompTree.numColumns = 2;
        final GridData gridDataCompTree = new GridData();
        gridDataCompTree.horizontalAlignment = FILL;
        gridDataCompTree.grabExcessHorizontalSpace = true;
        gridDataCompTree.verticalAlignment = FILL;
        final GridData gridDataExp = new GridData();
        gridDataExp.grabExcessHorizontalSpace = true;
        gridDataExp.horizontalAlignment = FILL;
        gridDataExp.verticalAlignment = FILL;
        final ExpandableComposite expandableComp = toolkit.createSection(parent, TWISTIE | TITLE_BAR);
        expandableComp.setText(EtapeConfigurationEditor_SOUS_ETAPE);
        expandableComp.setLayout(new GridLayout());
        expandableComp.setLayoutData(gridDataExp);
        compositeTree = toolkit.createComposite(expandableComp, BORDER);
        compositeTree.setLayoutData(gridDataCompTree);
        createCompositeSousEtape();
        compositeTree.setLayout(gridLayoutCompTree);
        treeViewer = new TreeViewer(compositeTree, SWT.NONE);
        treeViewer.getTree().setLayoutData(gridDataTree);
        expandableComp.setClient(compositeTree);
        expandableComp.setExpanded(true);
        updateTree();
    }

    private void createCompositeTreeEtape() {
        final GridData gridDataButtonValider = new GridData();
        gridDataButtonValider.horizontalSpan = 5;
        final GridData gridDataCheckArrChrono = new GridData();
        gridDataCheckArrChrono.horizontalSpan = 4;
        gridDataCheckArrChrono.verticalAlignment = GridData.CENTER;
        gridDataCheckArrChrono.horizontalAlignment = FILL;
        final GridData gridDataCheckArrGen = new GridData();
        gridDataCheckArrGen.horizontalSpan = 3;
        gridDataCheckArrGen.verticalAlignment = GridData.CENTER;
        gridDataCheckArrGen.horizontalAlignment = FILL;
        final GridData gridDataCheckDepGen = new GridData();
        gridDataCheckDepGen.horizontalAlignment = FILL;
        gridDataCheckDepGen.grabExcessHorizontalSpace = false;
        gridDataCheckDepGen.horizontalSpan = 3;
        gridDataCheckDepGen.verticalAlignment = GridData.CENTER;
        final GridData gridDataButtonShowData = new GridData();
        gridDataButtonShowData.horizontalAlignment = BEGINNING;
        gridDataButtonShowData.grabExcessHorizontalSpace = true;
        gridDataButtonShowData.verticalAlignment = GridData.CENTER;
        final GridData gridDataTextBaliseArr = new GridData();
        gridDataTextBaliseArr.widthHint = 60;
        final GridData gridDataTextBaliseDep = new GridData();
        gridDataTextBaliseDep.widthHint = 60;
        final GridData gridDataDesc = new GridData();
        gridDataDesc.horizontalAlignment = BEGINNING;
        gridDataDesc.grabExcessHorizontalSpace = true;
        gridDataDesc.horizontalSpan = 4;
        gridDataDesc.widthHint = 200;
        gridDataDesc.verticalAlignment = GridData.CENTER;
        final GridData gridDataCompTreeEtape = new GridData();
        gridDataCompTreeEtape.horizontalAlignment = FILL;
        gridDataCompTreeEtape.grabExcessHorizontalSpace = true;
        gridDataCompTreeEtape.grabExcessVerticalSpace = false;
        gridDataCompTreeEtape.verticalAlignment = FILL;
        final GridData gridDataTextNomEtape = new GridData();
        gridDataTextNomEtape.horizontalAlignment = BEGINNING;
        gridDataTextNomEtape.grabExcessHorizontalSpace = true;
        gridDataTextNomEtape.verticalAlignment = GridData.CENTER;
        gridDataTextNomEtape.horizontalSpan = 4;
        gridDataTextNomEtape.minimumWidth = 200;
        gridDataTextNomEtape.widthHint = 200;
        final GridLayout gridLayoutTreeEtape = new GridLayout();
        gridLayoutTreeEtape.numColumns = 5;
        gridDataCompTreeEtape.minimumHeight = 100;
        compTreeEtape = toolkit.createComposite(compSousEtape);
        compTreeEtape.setLayout(gridLayoutTreeEtape);
        compTreeEtape.setLayoutData(gridDataCompTreeEtape);
        toolkit.createLabel(compTreeEtape, EtapeConfigurationEditor_Nom_de_l_etape__, SWT.NONE);
        textNomEtape = toolkit.createText(compTreeEtape, input.getEtape().getLib());
        textNomEtape.setLayoutData(gridDataTextNomEtape);
        toolkit.createLabel(compTreeEtape, EtapeConfigurationEditor_Liaison_SportIdent_);
        importFile = createImportLabel(compTreeEtape);
        toolkit.createLabel(compTreeEtape, EMPTY, SWT.NONE);
        buttonImporter = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Importer, SWT.NONE);
        buttonShowDatas = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Afficher, SWT.NONE);
        buttonShowDatas.setLayoutData(gridDataButtonShowData);
        toolkit.createLabel(compTreeEtape, EMPTY, SWT.NONE);
        final Button buttonClearFileName =
                toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_EFFACER, SWT.NONE);
        buttonClearFileName.setLayoutData(gridDataDesc);
        toolkit.createLabel(compTreeEtape, EtapeConfigurationEditor_Description, SWT.NONE);
        textDescription = toolkit.createText(compTreeEtape, EMPTY, BORDER);
        textDescription.setLayoutData(gridDataDesc);
        toolkit.createLabel(compTreeEtape, EtapeConfigurationEditor_Balise_de_depart, SWT.NONE);
        textBaliseDepart = toolkit.createText(compTreeEtape, EMPTY, BORDER);
        textBaliseDepart.setLayoutData(gridDataTextBaliseDep);
        checkBoxDepartGeneral = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Depart_general, SWT.CHECK);
        checkBoxDepartGeneral.setLayoutData(gridDataCheckDepGen);
        toolkit.createLabel(compTreeEtape, EtapeConfigurationEditor_Balise_d_arrivee, SWT.NONE);
        textBaliseArrivee = toolkit.createText(compTreeEtape, EMPTY, BORDER);
        textBaliseArrivee.setLayoutData(gridDataTextBaliseArr);
        checkBoxArriveeGenerale =
                toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Arrivee_generale, SWT.CHECK);
        checkBoxArriveeGenerale.setLayoutData(gridDataCheckArrGen);
        checkBoxActive = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Activee, SWT.CHECK);
        checkBoxArretChrono = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Arret_chrono, SWT.CHECK);
        checkBoxArretChrono.setLayoutData(gridDataCheckArrChrono);
        buttonValider = toolkit.createButton(compTreeEtape, EtapeConfigurationEditor_Valider, SWT.NONE);
        buttonValider.setLayoutData(gridDataButtonValider);
        checkBoxDepartGeneral.addSelectionListener(this);
        checkBoxArriveeGenerale.addSelectionListener(this);
        buttonShowDatas.addSelectionListener(new ShowDataAction(autoResizeContext));
        buttonClearFileName.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (etapeSelected != null) {
                    etapeSelected.setImportFileName(null);
                    importFile.setText(EMPTY);
                }
            }
        });
        buttonImporter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                importer(compTreeEtape);
            }

        });
        buttonValider.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                sav();
            }
        });
        textBaliseArrivee.addModifyListener(new DirtyListener());
        textBaliseDepart.addModifyListener(new DirtyListener());
        textDescription.addModifyListener(new DirtyListener());
        textNomEtape.addModifyListener(new DirtyListener());
        checkBoxActive.addSelectionListener(new DirtyListener());
        checkBoxArretChrono.addSelectionListener(new DirtyListener());
        checkBoxArriveeGenerale.addSelectionListener(new DirtyListener());
        checkBoxDepartGeneral.addSelectionListener(new DirtyListener());

    }

    private void createCompSousEtape(final Composite parent) {
        final GridData gridDataSousEtape = new GridData();
        gridDataSousEtape.grabExcessHorizontalSpace = true;
        gridDataSousEtape.verticalAlignment = FILL;
        gridDataSousEtape.grabExcessVerticalSpace = false;
        gridDataSousEtape.horizontalAlignment = FILL;
        final GridLayout gridLayoutSousEtape = new GridLayout();
        gridLayoutSousEtape.numColumns = 1;
        gridDataSousEtape.minimumHeight = 100;
        final GridData gridDataExp = new GridData();
        gridDataExp.grabExcessHorizontalSpace = true;
        gridDataExp.horizontalAlignment = FILL;
        gridDataExp.verticalAlignment = FILL;
        final ExpandableComposite expandableComp = toolkit.createSection(parent, TWISTIE | TITLE_BAR);
        expandableComp.setText(EtapeConfigurationEditor_INFO_ETAPE_SELECTIONNEE);
        expandableComp.setLayout(new GridLayout());
        expandableComp.setLayoutData(gridDataExp);
        expandableComp.setEnabled(false);
        compSousEtape = toolkit.createComposite(expandableComp, BORDER);
        compSousEtape.setLayout(gridLayoutSousEtape);
        compSousEtape.setLayoutData(gridDataSousEtape);
        expandableComp.setClient(compSousEtape);
        expandableComp.setExpanded(false);
        createCompositeTreeEtape();
    }

    @Override
    public void createPartControl(final Composite parent) {
        final GridLayout gridLayoutTop = new GridLayout();
        gridLayoutTop.numColumns = 1;
        gridLayoutTop.makeColumnsEqualWidth = false;
        final GridData gridDataLabel = new GridData();
        gridDataLabel.grabExcessHorizontalSpace = true;
        gridDataLabel.horizontalAlignment = FILL;
        gridDataLabel.verticalAlignment = GridData.CENTER;
        gridDataLabel.grabExcessVerticalSpace = false;
        final GridData gridDataFill = new GridData();
        gridDataFill.grabExcessHorizontalSpace = true;
        gridDataFill.horizontalAlignment = FILL;
        gridDataFill.verticalAlignment = FILL;
        gridDataFill.grabExcessVerticalSpace = true;
        toolkit = new FormToolkit(parent.getDisplay());
        final ScrolledForm form = toolkit.createScrolledForm(parent);
        form.setText(EtapeConfigurationEditor_Configuration_de_l_etape2);
        final Composite top = form.getBody();
        // top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        top.setLayout(gridLayoutTop);
        top.setLayoutData(gridDataFill);
        label = toolkit.createLabel(top, EtapeConfigurationEditor_Configuration_de_l_etape2);
        label.setLayoutData(gridDataLabel);
        createComposite(top);
        try {
            updateBalises();
        } catch (final TableConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        sav();
    }

    @Override
    public void doSaveAs() {
    }

    private static Collection<ObjStep> getCodeSportIdentList(final ObjStep etape) {
        final Collection<ObjStep> res = newArrayList();
        if (etape.getImportFileName() != null) {
            res.add(etape);
        }
        for (final ObjStep sousEtape : etape.getSteps()) {
            res.addAll(getCodeSportIdentList(sousEtape));
        }
        return res;
    }

    private void importer(final Control parent) {
        final FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[]{"*.csv"}); //$NON-NLS-1$
        fileDialog.setFilterNames(new String[]{EtapeConfigurationEditor_Fichiers_csv});
        final String fileName = fileDialog.open();
        createUserChrono(fileName);
        etapeSelected.setInfo(ObjStep.VAR_FILENAME_IMPORT, fileName);
        getAutoImportProcess().importSportIdent(etapeSelected);
        importFile.setText(fileName);
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        if (!(input instanceof EtapeEditorInput)) {
            throw new PartInitException(EtapeConfigurationEditor_Invalid_Input);
        }
        this.input = (EtapeEditorInput) input;
        autoResizeContext = ((IAutoAjustColumnEditor) input).getAutoResizeContext();
        setSite(site);
        setInput(input);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    private void sav() {
        if (etapeSelected != null) {
            final ObjStep etape = etapeSelected;
            etape.setLib(textNomEtape.getText());
            setPartName(input.getEtape().getLib());
            label.setText(EtapeConfigurationEditor_Configuration_de_l_etape + input.getEtape().getLib());
            etape.setBaliseDepart(textBaliseDepart.getText());
            etape.setBaliseArrivee(textBaliseArrivee.getText());
            etape.setActif(checkBoxActive.getSelection());
            etape.setArretChrono(checkBoxArretChrono.getSelection());
            etape.setInfo(ObjStep.VAR_DESCRIPTION, textDescription.getText());
            etape.setBaliseDepartGeneral(checkBoxDepartGeneral.getSelection());
            etape.setBaliseArriveeGenerale(checkBoxArriveeGenerale.getSelection());
            treeViewer.refresh(etape);
            updateTreeViewer();
        }
        setDirty(false);
    }

    public void setDirty(final boolean dirtyState) {
        dirty = dirtyState;
        firePropertyChange(PROP_DIRTY);
    }

    @Override
    public void setFocus() {
        setPartName(input.getEtape().getLib());
        treeEtape.setFocus();
    }

    private void setSelectionColor() {
    }

    private void updateBalises() throws TableConfigurationException {
        final AbstractGetInfo etape = input.getEtape();
        final ObjConfig config = etape.getManif().getConfiguration().getConfig(BALISES);
        if (config == null) {
            throw new TableConfigurationException(EtapeConfigurationEditor_Pas_de_configuration_pour_la_table);
        }
        final String[] balisesColumnName = new String[config.getColTable().size()];
        final CellEditor[] editors = new CellEditor[balisesColumnName.length];
        int i = 0;
        for (final Iterator<ColTable> iter = config.getColTable().iterator(); iter.hasNext(); i++) {
            final ColTable colTable = iter.next();
            final TableColumn tableColumn = new TableColumn(balisesTable, SWT.CENTER);
            tableColumn.setText(colTable.getLib());
            tableColumn.setWidth(colTable.getWidth());
            tableColumn.setToolTipText(colTable.getLib2());
            tableColumn.setAlignment(colTable.getAlign());
            tableColumn.setMoveable(true);
            balisesColumnName[i] = colTable.getChamp();
            // Création des éditeurs
            editors[i] = createCellEditor(colTable, balisesTable);
            tableColumn.pack();
        }
        balisesTable.setHeaderVisible(true);
        balisesTable.setLinesVisible(true);
        balisesTableViewer.setColumnProperties(balisesColumnName);
        balisesTableViewer.setCellEditors(editors);
        balisesTableViewer.setContentProvider(new BaliseContentProvider());
        balisesTableViewer.setLabelProvider(new BaliseLabelProvider(config));
        balisesTableViewer.setInput(input.getEtape());
        final AbstractGetInfoCellModifier modifier = new BaliseCellModifier(balisesTableViewer, config);
        modifier.addSetInfoListener(new IInfoListener() {
            @Override
            public void modify(final AbstractGetInfo element, final String property, final Object value) {
                treeViewer.refresh(true);
                treeViewer.refresh(element);
            }

        });
        balisesTableViewer.setCellModifier(modifier);
        setSelectionColor();
        addTableKeyMove(balisesTableViewer);
        ajustementAutomatique(balisesTable);
    }

    private void updateEtapeInfo(final ObjStep etape) {
        textNomEtape.setText(EMPTY);
        importFile.setText(EMPTY);
        textBaliseDepart.setText(EMPTY);
        textBaliseArrivee.setText(EMPTY);
        textDescription.setText(EMPTY);
        label.setText(EMPTY);
        buttonImporter.setToolTipText(EMPTY);
        etapeSelected = null;
        if (etape != null) {
            etapeSelected = etape;
            label.setText(EtapeConfigurationEditor_Configuration_de_l_etape3 + etape.getLib());
            textNomEtape.setText(etape.getInfoStr(ObjStep.VAR_LIB_STEP));
            textDescription.setText(etape.getInfoStr(ObjStep.VAR_DESCRIPTION));
            if (etape.getImportFileName() != null) {
                importFile.setText(etape.getImportFileName());
            }
            if (etape.getBaliseDepart() != null) {
                textBaliseDepart.setText(etape.getBaliseDepart());
            }
            if (etape.getBaliseArrivee() != null) {
                textBaliseArrivee.setText(etape.getBaliseArrivee());
            }
            buttonImporter.setToolTipText(etape.getInfoStr(ObjStep.VAR_FILENAME_IMPORT));
            checkBoxDepartGeneral.setSelection(etape.getBaliseDepart() == null);
            checkBoxArriveeGenerale.setSelection(etape.getBaliseArrivee() == null);
            textNomEtape.setEnabled(true);
            importFile.setEnabled(true);
            textBaliseDepart.setEnabled(etape.getBaliseDepart() != null);
            textBaliseArrivee.setEnabled(etape.getBaliseArrivee() != null);
            checkBoxActive.setSelection(etape.isActif());
            checkBoxArretChrono.setSelection(etape.isArretChrono());
            textDescription.setEnabled(true);
            buttonImporter.setEnabled(true);
            buttonShowDatas.setEnabled(true);
            buttonValider.setEnabled(true);
            checkBoxDepartGeneral.setEnabled(true);
            checkBoxArriveeGenerale.setEnabled(true);
            checkBoxActive.setEnabled(true);
            checkBoxArretChrono.setEnabled(true);
            buttonImportGeneral.setEnabled(true);
            compSousEtape.setVisible(true);
        } else {
            textNomEtape.setEnabled(false);
            importFile.setEnabled(false);
            textBaliseDepart.setEnabled(false);
            textBaliseArrivee.setEnabled(false);
            textDescription.setEnabled(false);
            buttonImporter.setEnabled(false);
            buttonShowDatas.setEnabled(false);
            checkBoxDepartGeneral.setEnabled(false);
            checkBoxArriveeGenerale.setEnabled(false);
            buttonValider.setEnabled(false);
            checkBoxActive.setEnabled(false);
            checkBoxArretChrono.setEnabled(false);
            buttonImportGeneral.setEnabled(false);
        }
        setDirty(false);
    }

    private void updateTree() {
        treeEtape = treeViewer.getTree();
        treeViewer.setLabelProvider(new EtapeLabelProvider());
        treeViewer.setContentProvider(new EtapeTreeContentProvider());
        treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        treeViewer.setInput(input);
        treeEtape.addSelectionListener(new TreeEtapeSelectionListener());
        treeEtape.addMouseListener(new DoubleClickOnTreeAction(autoResizeContext));
        treeEtape.setFocus();
        treeEtape.setSelection(treeEtape.getItem(0));
    }

    private void updateTreeViewer() {
        final NavigateurView view = (NavigateurView) getSite().getPage().findView(NavigateurView.NAVIGATOR_VIEW_ID);
        view.refresh();
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        try {
            final ObjStep etape;
            if (treeEtape.getSelectionCount() > 0) {
                if (treeEtape.getSelection()[0].getData() instanceof ObjStep) {
                    etape = (ObjStep) treeEtape.getSelection()[0].getData();
                    String baliseDepart = EMPTY;
                    if (etape.getBaliseDepart() != null) {
                        baliseDepart = etape.getBaliseDepart();
                    }
                    String baliseArrivee = EMPTY;
                    if (etape.getBaliseArrivee() != null) {
                        baliseArrivee = etape.getBaliseArrivee();
                    }
                    if (e.getSource().equals(checkBoxDepartGeneral)) {
                        if (checkBoxDepartGeneral.getSelection()) {
                            textBaliseDepart.setText(EMPTY);
                            textBaliseDepart.setEnabled(false);
                        } else {
                            textBaliseDepart.setText(baliseDepart);
                            textBaliseDepart.setEnabled(true);
                        }
                    }
                    if (e.getSource().equals(checkBoxArriveeGenerale)) {
                        if (checkBoxArriveeGenerale.getSelection()) {
                            textBaliseArrivee.setText(EMPTY);
                            textBaliseArrivee.setEnabled(false);
                        } else {
                            textBaliseArrivee.setText(baliseArrivee);
                            textBaliseArrivee.setEnabled(true);
                        }
                    }
                } else {
                    final ObjBalise balise = (ObjBalise) treeEtape.getSelection()[0].getData();
                    etape = (ObjStep) balise.getParent();
                    if (e.getSource().equals(buttonEtapeSupprimer) && balise != null) {
                        etape.removeBalise(balise);
                        balisesTableViewer.setInput(etape);
                        treeViewer.refresh(etape);
                    }
                    return;
                }
            } else {
                return;
            }
            if (e.getSource().equals(buttonEtapeAjouter)) {
                final String nr = (etape.getSteps().size() + 1) + EMPTY;
                etape.addStep(createStep(nr, EtapeConfigurationEditor_Sous_etape + nr));
                updateTreeViewer();
                treeViewer.refresh(etape);
            }
            if (e.getSource().equals(buttonEtapeSupprimer)) {
                final AbstractSteps etapeParent = (AbstractSteps) etape.getParent();
                etapeParent.removeStep(etape);
                treeViewer.refresh(etapeParent);
                updateTreeViewer();
            }
            if (e.getSource().equals(buttonBaliseAjouter)) {
                String nr = "31"; //$NON-NLS-1$
                final ObjBalise newBalise = BaliseFactory.createBalise(nr, TYPE_OBLIGATOIRE, EMPTY);
                for (final Balise objBalise : etape.getBalises()) {
                    try {
                        nr = EMPTY + (new Integer(objBalise.getNum()) + 1);
                        newBalise.setNum(nr);
                        newBalise.setPenalite(objBalise.getPenalite());
                        if (!objBalise.getType().equals(START_TYPE_BALISE) && !objBalise.getType()
                                                                                        .equals(END_TYPE_BALISE)) {
                            newBalise.setType(objBalise.getType());
                        }
                    } catch (final Exception e1) {
                    }
                }
                newBalise.setType(TYPE_OBLIGATOIRE);
                etape.addBalise(newBalise);
                balisesTableViewer.refresh();
                ajustementAutomatique(balisesTable);
                treeViewer.refresh(etape);
            }
            if (e.getSource().equals(buttonImportGeneral) && etapeSelected != null) {
                final Iterable<ObjStep> listCodeSportIdent = getCodeSportIdentList(etapeSelected);
                for (final ObjStep etapeTmp : listCodeSportIdent) {
                    getAutoImportProcess().importSportIdent(etapeTmp);
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private class ShowDataAction extends SelectionAdapter {
        private final AutoResizeColumn autoResizeContext;

        ShowDataAction(final AutoResizeColumn autoResizeContext) {
            this.autoResizeContext = autoResizeContext;
        }

        @Override
        public void widgetSelected(final SelectionEvent e) {
            try {
                final IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
                page.openEditor(new EtapeEditorInput(etapeSelected, ChronosEditor.CHRONOS_EDITOR_ID, autoResizeContext),
                                ChronosEditor.CHRONOS_EDITOR_ID);
            } catch (final Exception ignore) {
                throw new RuntimeException("Error laoding editor: " + ChronosEditor.CHRONOS_EDITOR_ID);
            }
        }
    }

    private class DoubleClickOnTreeAction extends MouseAdapter {
        private final AutoResizeColumn autoResizeContext;

        DoubleClickOnTreeAction(final AutoResizeColumn autoResizeContext) {
            this.autoResizeContext = autoResizeContext;
        }

        @Override
        public void mouseDoubleClick(final MouseEvent e) {
            try {
                if (treeEtape.getSelection().length > 0) {
                    final Widget item = treeEtape.getSelection()[0];
                    if (item.getData() instanceof ObjStep) {
                        final IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
                        final IEditorInput input =
                                new EtapeEditorInput((ObjStep) item.getData(), CONFIGURATION_EDITOR_ID,
                                                     autoResizeContext);
                        page.openEditor(input, CONFIGURATION_EDITOR_ID);
                    }
                }
            } catch (final Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private static class NavigateOnTableAction implements KeyListener {
        private final ColumnViewer tv;
        private final TableCursor  cursor;

        private NavigateOnTableAction(final ColumnViewer tv, final TableCursor cursor) {
            this.tv = tv;
            this.cursor = cursor;
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            if (e.keyCode == SWT.F2 || e.keyCode == SWT.KEY_MASK) {
                final Object element = ((IStructuredSelection) tv.getSelection()).getFirstElement();
                final int column = cursor.getColumn();
                tv.editElement(element, column);
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
        }

    }
}
