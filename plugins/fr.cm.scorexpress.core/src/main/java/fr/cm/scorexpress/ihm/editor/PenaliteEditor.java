package fr.cm.scorexpress.ihm.editor;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ObjChoix;
import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.model.Step;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.input.EtapeEditorInput;
import fr.cm.scorexpress.ihm.editor.modifier.MyTextCellEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

import java.util.ArrayList;
import java.util.Collection;

import static fr.cm.scorexpress.core.model.ObjPenalite.*;
import static fr.cm.scorexpress.core.util.PenalityUtils.*;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.BEGINNING;
import static org.eclipse.swt.layout.GridData.FILL;

public class PenaliteEditor extends EditorPart {

    class PenaliteCellModifier implements ICellModifier {
        private final Viewer treeViewer;

        private final StructuredViewer viewer;

        PenaliteCellModifier(final StructuredViewer viewer, final Viewer treeViewer) {
            this.viewer = viewer;
            this.treeViewer = treeViewer;
        }

        public boolean canModify(final Object element, final String property) {
            return property.equals(tableProperties[1]);
        }

        public Object getValue(final Object element, final String property) {
            if (element instanceof PenalityElement) {
                final PenalityElement edit = (PenalityElement) element;
                if (property.equals(tableProperties[1])) {
                    final String attribut = edit.property;
                    return edit.penalite.getInfo(attribut);
                }
            }
            return null;
        }

        public void modify(final Object element, final String property, final Object value) {
            if (element instanceof TableItem && ((Widget) element).getData() instanceof PenalityElement) {
                final PenalityElement<ObjPenalite> edit = (PenalityElement<ObjPenalite>) ((Widget) element).getData();
                if (property.equals(tableProperties[1])) {
                    final String attribut = edit.property;
                    edit.penalite.setInfo(attribut, value);
                    viewer.refresh(edit.penalite);
                    if (attribut.equals(VAR_PENALITY_TYPE)
                            || attribut.equals(VAR_LIB)) {
                        treeViewer.refresh();
                    }
                }
            }
        }
    }

    static class PenalityElement<T extends ObjPenalite> {
        public final String lib;
        public final T penalite;
        public final String property;

        PenalityElement(final T penalite, final String property, final String lib) {
            this.penalite = penalite;
            this.property = property;
            this.lib = lib;
        }

        public String getValue() {
            Object val = penalite.getInfo(property);
            if (val == null) {
                val = EMPTY;
            }
            return val + EMPTY;
        }
    }

    static class PenaliteLabelProvider implements ILabelProvider {
        private final Image imgEtape = getImg(IMG_ETAPE);
        private final Image imgPenalite = getImg(IMG_PENALITY);
        private final Image imgPenaliteDisable = getImg(IMG_PENALITY_DESACTIVATE);

        public void addListener(final ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public Image getImage(final Object element) {
            if (element instanceof ObjStep) {
                return imgEtape;
            }
            if (element instanceof ObjPenalite) {
                if (((ObjPenalite) element).isActivate()) {
                    return imgPenalite;
                } else {
                    return imgPenaliteDisable;
                }
            }
            return null;
        }

        public String getText(final Object element) {
            if (element instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) element;
                return penalite.getLib() + " (" + getTypePenaliteStr(penalite) + ')';
            }
            if (element instanceof Step) {
                return ((Step) element).getLib();
            }
            return EMPTY; //$NON-NLS-1$
        }

        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        public void removeListener(final ILabelProviderListener listener) {
        }

    }

    static class PenaliteStructuredProvider implements IStructuredContentProvider {
        public void dispose() {
        }

        public Object[] getElements(final Object inputElement) {
            final Collection<Object> res = new ArrayList<Object>();
            if (inputElement instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) inputElement;
                res.add(createPenalityElement(penalite, VAR_LIB, PenaliteEditor_Description));
                if (penalite.getTypePenalite() == TYPE_COURSE_AU_SCORE.valeur) {
                    res.add(createPenalityElement(penalite, VAR_DUREEMAXI, PenaliteEditor_Duree_de_l_etape));
                    res.add(createPenalityElement(penalite, VAR_ECHELLEPENALITE, PenaliteEditor_Echelle));
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME,
                            PenaliteEditor_DureePenalitee));
                }
                if (penalite.getTypePenalite() == TYPE_DUREE_ETAPE_MINI.valeur) {
                    res.add(createPenalityElement(penalite, VAR_DUREEMINI, PenaliteEditor_Duree_mini));
                }
                if (penalite.getTypePenalite() == TYPE_NB_BALISES_MINI.valeur) {
                    res.add(createPenalityElement(penalite, VAR_NB_BALISE_MINI, PenaliteEditor_Nb_Balises));
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME,
                            PenaliteEditor_DureePenalitee));
                }
                if (penalite.getTypePenalite() == TYPE_BALISE_ORDONNEE.valeur) {
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME, PenaliteEditor_Duree_de_la_penalitee));
                }
                if (penalite.getTypePenalite() == TYPE_PENALITE_SAISIE.valeur) {
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME,
                            PenaliteEditor_Penalite_par_unite_manquee));
                    res.add(createPenalityElement(penalite, VAR_UNITE, PenaliteEditor_Unite));
                }
                if (penalite.getTypePenalite() == TYPE_DUREE_ETAPE_MAXI.valeur) {
                    res.add(createPenalityElement(penalite, VAR_DUREEMAXI, PenaliteEditor_Duree_de_l_etape_maxi));
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME,
                            PenaliteEditor_Penalite_par_duree_depassee));
                }
                if (penalite.getTypePenalite() == TYPE_PENALITE_BALISE_OBLIGATOIRE.valeur) {
                }
                if (penalite.getTypePenalite() == TYPE_ARRET_CHRONO_SAISIE.valeur) {
                    res.add(createPenalityElement(penalite, VAR_PENALITE_TIME,
                            PenaliteEditor_Penalite_par_unite_manquee));
                    res.add(createPenalityElement(penalite, VAR_UNITE, PenaliteEditor_Unite));
                }
                if (penalite.getTypePenalite() == TYPE_ARRET_CHRONO_MAXI.valeur) {
                    res.add(createPenalityElement(penalite, VAR_DUREEMAXI, "Duree maxi de l'arret chrono"));
                    res.add(createPenalityElement(penalite, VAR_ECHELLEPENALITE, "Majoration de la limite"));
                }
                if (penalite.getTypePenalite() == TYPE_DUREE_FIXE.valeur) {
                    res.add(createPenalityElement(penalite, VAR_DUREEMAXI, "Duree maxi de l'arret chrono"));
                }
            }
            return res.toArray();
        }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }

        static PenalityElement<ObjPenalite> createPenalityElement(final ObjPenalite penalite, final String property,
                                                                  final String lib) {
            return new PenalityElement<ObjPenalite>(penalite, property, lib);
        }
    }

    static class PenaliteTableLabelProvider implements ITableLabelProvider {
        public void addListener(final ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }

        public String getColumnText(final Object element, final int columnIndex) {
            if (element instanceof PenalityElement) {
                if (columnIndex == 0) {
                    return ((PenalityElement) element).lib;
                }
                if (columnIndex == 1) {
                    return ((PenalityElement) element).getValue();
                }
            }
            return element.toString();
        }

        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        public void removeListener(final ILabelProviderListener listener) {
        }
    }

    static class PenaliteTreeContentProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public Object[] getChildren(final Object parentElement) {
            final Collection<Object> res = new ArrayList<Object>();
            if (parentElement instanceof ObjStep) {
                final ObjStep etape = (ObjStep) parentElement;
                res.addAll(etape.getSteps());
                res.addAll(etape.getPenalites());
            }
            if (parentElement instanceof EtapeEditorInput) {
                res.add(((EtapeEditorInput) parentElement).getEtape());
            }
            return res.toArray();
        }

        public Object[] getElements(final Object inputElement) {
            return getChildren(inputElement);
        }

        public Object getParent(final Object element) {
            if (element instanceof AbstractGetInfo) {
                final AbstractGetInfo obj = (AbstractGetInfo) element;
                return obj.getParent();
            }
            return null;
        }

        public boolean hasChildren(final Object element) {
            final Collection<Object> res = new ArrayList<Object>();
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                res.addAll(etape.getSteps());
                res.addAll(etape.getPenalites());
            }
            return !res.isEmpty();
        }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }

    }

    class TreeSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(final SelectionEvent element) {
        }

        public void widgetSelected(final SelectionEvent element) {
            if (element.item.getData() instanceof ObjPenalite) {
                final ObjPenalite penality = (ObjPenalite) element.item.getData();
                tableViewer.setInput(penality);
                ajustementAutomatique(table);
                buttonAjouter.setEnabled(false);
                buttonSupprimer.setEnabled(true);
                comboPenaliteType.setText(getTypePenaliteStr(penality));
                comboPenaliteType.setEnabled(true);
                checkBoxActive.setEnabled(true);
                checkBoxActive.setSelection(penality.isActivate());
                return;
            }
            if (element.item.getData() instanceof ObjStep) {
                buttonAjouter.setEnabled(true);
                buttonSupprimer.setEnabled(false);
                comboPenaliteType.setText(EMPTY);
                comboPenaliteType.setEnabled(false);
                checkBoxActive.setEnabled(false);
                checkBoxActive.setSelection(false);
            }
            tableViewer.setInput(null);
        }
    }

    public static final String PENALITY_EDITOR_ID = "fr.cm.chronos.editor.PenaliteEditor"; //$NON-NLS-1$
    private Button buttonAjouter = null;
    private Button buttonSupprimer = null;
    private Button checkBoxActive = null;
    private CCombo comboPenaliteType = null;
    private Composite compGroupButton = null;
    private EtapeEditorInput input = null;
    private Label labelEtapeName = null;
    private Table table = null;
    private String[] tableProperties = null;
    private TableViewer tableViewer = null;
    private FormToolkit toolkit = null;
    private Tree tree = null;
    private TreeViewer treeViewer = null;

    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        if (!(input instanceof EtapeEditorInput)) {
            throw new PartInitException(PenaliteEditor_Invalid_Input);
        }
        this.input = (EtapeEditorInput) input;
        setSite(site);
        setInput(input);
    }

    private static void ajustementAutomatique(final Table table) {
        for (int j = 0; j < table.getColumnCount(); j++) {
            table.getColumn(j).pack();
        }
    }

    private void createComboPenaliteType() {
        final GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = FILL;
        gridData4.widthHint = 150;
        gridData4.verticalAlignment = GridData.CENTER;
        comboPenaliteType = new CCombo(compGroupButton, BORDER);
        comboPenaliteType.setLayoutData(gridData4);
        comboPenaliteType.setEnabled(false);
        comboPenaliteType.setEditable(false);
        comboPenaliteType.setBackground(tree.getBackground());
        final ArrayList<ObjChoix> items = (ArrayList<ObjChoix>) getListPenaliteType();
        comboPenaliteType.addModifyListener(new ComboPenalityChangeListener(items));
        for (final ObjChoix element : items) {
            comboPenaliteType.add(element.lib);
        }
    }

    private void createCompGroupButton(final Composite parent) {
        final GridLayout gridLayoutGroupButton = new GridLayout();
        gridLayoutGroupButton.numColumns = 4;
        final GridData gridDataGroupButton = new GridData();
        gridDataGroupButton.grabExcessHorizontalSpace = true;
        gridDataGroupButton.verticalAlignment = FILL;
        gridDataGroupButton.horizontalSpan = 2;
        gridDataGroupButton.horizontalAlignment = FILL;
        compGroupButton = toolkit.createComposite(parent, SWT.NONE);
        toolkit.createLabel(compGroupButton, PenaliteEditor_Type_de_penalite, SWT.NONE);
        createComboPenaliteType();
        compGroupButton.setLayout(gridLayoutGroupButton);
        compGroupButton.setLayoutData(gridDataGroupButton);
        checkBoxActive = toolkit.createButton(compGroupButton, PenaliteEditor_Active, SWT.CHECK);
        checkBoxActive.addSelectionListener(new CheckBoxActiveSelection());
    }

    private void createComposite(final Composite parent) {
        final GridData gridDataComp = new GridData();
        gridDataComp.grabExcessHorizontalSpace = false;
        gridDataComp.verticalAlignment = BEGINNING;
        gridDataComp.horizontalAlignment = BEGINNING;
        final Composite composite = toolkit.createComposite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(gridDataComp);
        buttonAjouter = toolkit.createButton(composite, PenaliteEditor_Ajouter, SWT.NONE);
        buttonSupprimer = toolkit.createButton(composite, PenaliteEditor_Supprimer, SWT.NONE);
        buttonSupprimer.setEnabled(false);
        buttonAjouter.setEnabled(false);
        buttonAjouter.addSelectionListener(new AddAction());
        buttonSupprimer.addSelectionListener(new DeleteAction());

    }

    public void createPartControl(final Composite parent) {
        final GridData layoutDataTop = new GridData();
        layoutDataTop.verticalAlignment = FILL;
        layoutDataTop.horizontalAlignment = FILL;
        layoutDataTop.minimumHeight = 200;
        layoutDataTop.heightHint = 400;
        final GridData gridDataLabelEtape = new GridData();
        gridDataLabelEtape.verticalAlignment = GridData.CENTER;
        gridDataLabelEtape.horizontalAlignment = FILL;
        gridDataLabelEtape.horizontalSpan = 3;
        gridDataLabelEtape.minimumHeight = 200;
        final GridData gridDataTree = new GridData();
        gridDataTree.horizontalAlignment = FILL;
        gridDataTree.grabExcessHorizontalSpace = true;
        gridDataTree.grabExcessVerticalSpace = true;
        gridDataTree.verticalAlignment = FILL;
        gridDataTree.minimumWidth = 300;
        final GridData gridDataTable = new GridData();
        gridDataTable.horizontalAlignment = FILL;
        gridDataTable.verticalAlignment = GridData.CENTER;
        gridDataTable.grabExcessHorizontalSpace = true;
        gridDataTable.grabExcessVerticalSpace = true;
        gridDataTable.horizontalSpan = 2;
        gridDataTable.horizontalIndent = 0;
        gridDataTable.widthHint = 200;
        gridDataTable.minimumWidth = 100;
        gridDataTable.minimumHeight = 200;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        final GridData gridDataLableTitre = new GridData();
        gridDataLableTitre.grabExcessHorizontalSpace = true;
        gridDataLableTitre.verticalAlignment = GridData.CENTER;
        gridDataLableTitre.horizontalSpan = 2;
        gridDataLableTitre.horizontalAlignment = GridData.CENTER;
        toolkit = new FormToolkit(parent.getDisplay());
        final ScrolledForm form = toolkit.createScrolledForm(parent);
        form.setText(PenaliteEditor_Gestion_des_penalitees);
        final Composite top = form.getBody();
        top.setLayout(gridLayout);
        top.setLayoutData(layoutDataTop);
        final Control labelTitre = toolkit.createLabel(top, PenaliteEditor_Gestion_des_penalitees, SWT.NONE);
        labelTitre.setLayoutData(gridDataLableTitre);
        toolkit.createLabel(top, "", SWT.NONE);
        labelEtapeName = toolkit.createLabel(top, PenaliteEditor_Etape, SWT.NONE);
        labelEtapeName.setLayoutData(gridDataLabelEtape);
        createComposite(top);
        tree = toolkit.createTree(top, BORDER);
        tree.setLayoutData(gridDataTree);
        createCompGroupButton(top);
        tableViewer = new TableViewer(top, SELECTED | FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(gridDataTable);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        update();
    }

    public void doSave(final IProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void setFocus() {
    }

    private void update() {
        treeViewer = new TreeViewer(tree);
        final Step etape = input.getEtape();
        labelEtapeName.setText(etape.getLib());
        setPartName(etape.getLib());
        tableProperties = new String[2];
        final CellEditor[] editors = new CellEditor[2];
        if (table.getColumnCount() == 0) {
            final TableColumn tableColumn1 = new TableColumn(table, SELECTED);
            tableColumn1.setText(PenaliteEditor_Type);
            tableColumn1.setWidth(100);
            tableProperties[0] = "TYPE"; //$NON-NLS-1$
            editors[0] = new MyTextCellEditor(table);
            final TableColumn tableColumn2 = new TableColumn(table, SELECTED);
            tableColumn2.setText(PenaliteEditor_Valeur);
            tableColumn2.setWidth(100);
            tableProperties[1] = "VALUE"; //$NON-NLS-1$
            editors[1] = new MyTextCellEditor(table);
        }
        treeViewer.setLabelProvider(new PenaliteLabelProvider());
        treeViewer.setContentProvider(new PenaliteTreeContentProvider());
        treeViewer.setInput(input);
        treeViewer.expandAll();
        ajustementAutomatique(table);
        tree.addSelectionListener(new TreeSelectionListener());
        tableViewer.setLabelProvider(new PenaliteTableLabelProvider());
        tableViewer.setContentProvider(new PenaliteStructuredProvider());
        tableViewer.setColumnProperties(tableProperties);
        tableViewer.setCellEditors(editors);
        tableViewer.setCellModifier(new PenaliteCellModifier(tableViewer, treeViewer));
        tableViewer.setInput(null);
    }

    private class ComboPenalityChangeListener implements ModifyListener {
        private final Iterable<ObjChoix> items;

        ComboPenalityChangeListener(final Iterable<ObjChoix> items) {
            this.items = items;
        }

        public void modifyText(final ModifyEvent e) {
            if (tableViewer.getInput() instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) tableViewer
                        .getInput();
                for (final ObjChoix element : items) {
                    if (element.lib.equals(comboPenaliteType.getText())) {
                        penalite.setTypePenalite(element.valeur);
                        tableViewer.setInput(penalite);
                        tableViewer.refresh();
                        treeViewer.refresh();
                        ajustementAutomatique(table);
                        return;
                    }

                }
            }
        }
    }

    private class CheckBoxActiveSelection extends SelectionAdapter {
        public void widgetSelected(final SelectionEvent e) {
            super.widgetSelected(e);
            final ObjPenalite penalite = (ObjPenalite) tableViewer.getInput();
            penalite.setActivate(checkBoxActive.getSelection());
            treeViewer.refresh();
        }
    }

    private class DeleteAction extends SelectionAdapter {
        public void widgetSelected(final SelectionEvent e) {
            super.widgetSelected(e);
            if (tree.getSelection().length > 0 && tree.getSelection()[0].getData() instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) tree.getSelection()[0].getData();
                final ObjStep etape = penalite.getParent();
                etape.removePenalite(penalite);
                treeViewer.refresh();
                treeViewer.expandAll();
            }
        }
    }

    private class AddAction extends SelectionAdapter {
        public void widgetSelected(final SelectionEvent e) {
            super.widgetSelected(e);
            if (tree.getSelection().length > 0 && tree.getSelection()[0].getData() instanceof ObjStep) {
                final ObjStep etape = (ObjStep) tree.getSelection()[0].getData();
                etape.addPenalite(new ObjPenalite(PenaliteEditor_new_Penality + (etape.getPenalites().size() + 1)));
                treeViewer.refresh();
                treeViewer.expandAll();
            }
        }
    }
}
