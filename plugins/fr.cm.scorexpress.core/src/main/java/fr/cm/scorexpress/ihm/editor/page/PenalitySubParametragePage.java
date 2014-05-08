package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ObjChoix;
import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.util.PenalityUtils;
import fr.cm.scorexpress.ihm.editor.modifier.MyTextCellEditor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.*;

import java.util.ArrayList;
import java.util.Collection;

import static fr.cm.scorexpress.core.model.ObjPenalite.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.SINGLE;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.GRAB_HORIZONTAL;
import static org.eclipse.ui.forms.widgets.TableWrapData.FILL;
import static org.eclipse.ui.forms.widgets.TableWrapData.TOP;

public class PenalitySubParametragePage implements IDetailsPage {
    private IManagedForm mform;

    private ObjPenalite penality;
    private Text libelle;
    private Button actif;

    private Table table = null;
    private TableViewer tableViewer = null;
    private CCombo comboPenaliteType = null;

    @Override
    public void createContents(final Composite parent) {
        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);
        final FormToolkit toolkit = mform.getToolkit();
        final ExpandableComposite penalitySection = toolkit.createSection(parent, Section.TITLE_BAR);
        penalitySection.marginWidth = 10;
        penalitySection.setText(PenalityStepDetailsPage_Penalite);
        final TableWrapData wrapData = new TableWrapData(FILL, TOP);
        wrapData.grabHorizontal = true;
        penalitySection.setLayoutData(wrapData);
        final Composite client = toolkit.createComposite(penalitySection);
        client.setLayout(new GridLayout(2, false));
        toolkit.createLabel(client, PenalityStepDetailsPage_Libelle);
        libelle = toolkit.createText(client, EMPTY, SINGLE | BORDER);
        libelle.addModifyListener(new LabelChanged());
        libelle.setLayoutData(new GridData(GRAB_HORIZONTAL | FILL_HORIZONTAL));
        toolkit.createLabel(client, PenalityStepDetailsPage_Activer);
        actif = toolkit.createButton(client, EMPTY, SWT.CHECK);
        actif.addSelectionListener(new ActivateAction());
        createComboPenaliteType(client);
        createTable(client);
        toolkit.paintBordersFor(penalitySection);
        penalitySection.setClient(client);
        update();
    }

    private void createTable(final Composite top) {
        final GridData gridDataTable = new GridData();
        gridDataTable.horizontalAlignment = GridData.FILL;
        gridDataTable.verticalAlignment = GridData.CENTER;
        gridDataTable.grabExcessHorizontalSpace = true;
        gridDataTable.grabExcessVerticalSpace = true;
        gridDataTable.horizontalSpan = 2;
        gridDataTable.horizontalIndent = 0;
        gridDataTable.minimumHeight = 200;
        gridDataTable.widthHint = 80;
        tableViewer = new TableViewer(top, SWT.SELECTED | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(gridDataTable);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    private void createComboPenaliteType(final Composite parent) {
        final GridData gridDataCombo = new GridData();
        gridDataCombo.horizontalAlignment = GridData.FILL;
        gridDataCombo.widthHint = 90;
        gridDataCombo.verticalAlignment = GridData.CENTER;
        gridDataCombo.horizontalSpan = 2;
        comboPenaliteType = new CCombo(parent, BORDER);
        comboPenaliteType.setLayoutData(gridDataCombo);
        comboPenaliteType.setEditable(false);
        comboPenaliteType.setBackground(parent.getBackground());
        final Collection<ObjChoix> items = PenalityUtils.getListPenaliteType();
        comboPenaliteType.addModifyListener(new ComboPenalityTypeAction(items));
        for (final ObjChoix element : items) {
            comboPenaliteType.add(element.lib);
        }
    }

    private void update() {
        final String[] tableProperties = new String[2];
        final CellEditor[] editors = new CellEditor[2];
        if (table.getColumnCount() == 0) {
            final TableColumn typeColumn = new TableColumn(table, SWT.SELECTED);
            typeColumn.setText(PenaliteEditor_Type);
            typeColumn.setWidth(100);
            tableProperties[0] = "TYPE";
            editors[0] = new MyTextCellEditor(table);
            final TableColumn valueColumn = new TableColumn(table, SWT.SELECTED);
            valueColumn.setText(PenaliteEditor_Valeur);
            valueColumn.setWidth(100);
            tableProperties[1] = "VALUE";
            editors[1] = new MyTextCellEditor(table);
        }
        ajustementAutomatique(table);
        tableViewer.setLabelProvider(new PenaliteTableLabelProvider());
        tableViewer.setContentProvider(new PenaliteStructuredProvider());
        tableViewer.setColumnProperties(tableProperties);
        tableViewer.setCellEditors(editors);
        tableViewer.setCellModifier(new PenaliteCellModifier(tableViewer, tableProperties));
        tableViewer.setInput(null);
    }

    @Override
    public void commit(final boolean onSave) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void initialize(final IManagedForm form) {
        mform = form;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isStale() {
        return false;
    }

    @Override
    public void refresh() {
        if (penality != null) {
            libelle.setText(penality.getLib() == null ? EMPTY : penality.getLib());
            tableViewer.setInput(penality);
            ajustementAutomatique(table);
            comboPenaliteType.setText(PenalityUtils.getTypePenaliteStr(penality));
            actif.setSelection(penality.isActivate());
        }
    }

    @Override
    public void setFocus() {
    }

    @Override
    public boolean setFormInput(final Object input) {
        return false;
    }

    @Override
    public void selectionChanged(final IFormPart part, final ISelection selection) {
        final IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            penality = (ObjPenalite) ssel.getFirstElement();
        } else {
            penality = null;
        }
        refresh();
    }

    private static void ajustementAutomatique(final Table table) {
        for (int j = 0; j < table.getColumnCount(); j++) {
            table.getColumn(j).pack();
        }
    }

    public void changed() {
    }

    static class PenaliteCellModifier implements ICellModifier {
        private final String[] tableProperties;
        private final StructuredViewer viewer;

        PenaliteCellModifier(final StructuredViewer viewer, final String[] tableProperties) {
            this.viewer = viewer;
            this.tableProperties = tableProperties;
        }

        @Override
        public boolean canModify(final Object element, final String property) {
            return property.equals(tableProperties[1]);
        }

        @Override
        public Object getValue(final Object element, final String property) {
            if (element instanceof PenaliteEdit) {
                if (property.equals(tableProperties[1])) {
                    final PenaliteEdit edit = (PenaliteEdit) element;
                    final String attribut = edit.property;
                    return edit.penalite.getInfo(attribut);
                }
            }
            return null;
        }

        @Override
        public void modify(final Object element, final String property, final Object value) {
            if (element instanceof TableItem && ((Widget) element).getData() instanceof PenaliteEdit) {
                if (property.equals(tableProperties[1])) {
                    final PenaliteEdit edit = (PenaliteEdit) ((Widget) element).getData();
                    final String attribut = edit.property;
                    edit.penalite.setInfo(attribut, value);
                    viewer.refresh(edit.penalite);
                }
            }
        }
    }

    static class PenaliteEdit {
        private final String lib;
        private final AbstractGetInfo penalite;
        private final String property;

        PenaliteEdit(final AbstractGetInfo penalite, final String property, final String lib) {
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

    static class PenaliteStructuredProvider implements IStructuredContentProvider {
        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            final Collection<Object> res = new ArrayList<Object>();
            if (inputElement instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) inputElement;
                res.add(new PenaliteEdit(penalite, VAR_LIB, PenaliteEditor_Description));
                if (penalite.getTypePenalite() == PenalityUtils.TYPE_COURSE_AU_SCORE.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_DUREEMAXI, PenaliteEditor_Duree_de_l_etape));
                    res.add(new PenaliteEdit(penalite, VAR_ECHELLEPENALITE, PenaliteEditor_Echelle));
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_DureePenalitee));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_DUREE_ETAPE_MINI.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_DUREEMINI, PenaliteEditor_Duree_mini));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_NB_BALISES_MINI.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_NB_BALISE_MINI, PenaliteEditor_Nb_Balises));
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_DureePenalitee));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_BALISE_ORDONNEE.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_NB_BALISE_MINI, "Nb. ordre possible"));
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_Duree_de_la_penalitee));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_PENALITE_SAISIE.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_Penalite_par_unite_manquee));
                    res.add(new PenaliteEdit(penalite, VAR_UNITE, PenaliteEditor_Unite));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_DUREE_ETAPE_MAXI.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_DUREEMAXI, PenaliteEditor_Duree_de_l_etape_maxi));
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_Penalite_par_duree_depassee));
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_PENALITE_BALISE_OBLIGATOIRE.valeur) {
                } else if (penalite.getTypePenalite() == PenalityUtils.TYPE_ARRET_CHRONO_SAISIE.valeur) {
                    res.add(new PenaliteEdit(penalite, VAR_PENALITE_TIME, PenaliteEditor_Penalite_par_unite_manquee));
                    res.add(new PenaliteEdit(penalite, VAR_UNITE, PenaliteEditor_Unite));
                }
            }
            return res.toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    static class PenaliteTableLabelProvider implements ITableLabelProvider {
        @Override
        public void addListener(final ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(final Object element, final int columnIndex) {
            if (element instanceof PenaliteEdit) {
                if (columnIndex == 0) {
                    return ((PenaliteEdit) element).lib;
                }
                if (columnIndex == 1) {
                    return ((PenaliteEdit) element).getValue();
                }
            }
            return element.toString();
        }

        @Override
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        @Override
        public void removeListener(final ILabelProviderListener listener) {
        }
    }

    private class ComboPenalityTypeAction implements ModifyListener {
        private final Iterable<ObjChoix> items;

        ComboPenalityTypeAction(final Iterable<ObjChoix> items) {
            this.items = items;
        }

        @Override
        public void modifyText(final ModifyEvent e) {
            if (tableViewer.getInput() instanceof ObjPenalite) {
                final ObjPenalite penalite = (ObjPenalite) tableViewer
                        .getInput();
                for (final ObjChoix element : items) {
                    if (element.lib.equals(comboPenaliteType
                            .getText())) {
                        penalite.setTypePenalite(element.valeur);
                        tableViewer.setInput(penalite);
                        tableViewer.refresh();
                        ajustementAutomatique(table);
                        changed();
                        return;
                    }
                }
            }
        }
    }

    private class ActivateAction extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent e) {
            if (penality != null) {
                penality.setActivate(actif.getSelection());
                changed();
            }
        }
    }

    private class LabelChanged implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (penality != null) {
                penality.setLib(libelle.getText());
                changed();
            }
        }
    }
}
