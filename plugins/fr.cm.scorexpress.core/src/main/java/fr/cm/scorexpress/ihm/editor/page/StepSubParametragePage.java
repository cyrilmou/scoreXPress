/**
 *
 */
package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.core.model.DefaultDataVisitor;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.i18n.Messages;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import java.util.Iterator;

import static fr.cm.scorexpress.applicative.ProjectManager.importDonneeConcurrent;
import static fr.cm.scorexpress.applicative.ProjectManagerFactory.getAutoImportProcess;
import static fr.cm.scorexpress.core.model.impl.ObjStep.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static fr.cm.scorexpress.ihm.editor.i18n.Messages.*;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.jface.dialogs.MessageDialog.openConfirm;
import static org.eclipse.swt.SWT.CHECK;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;
import static org.eclipse.ui.forms.widgets.Section.DESCRIPTION;

public class StepSubParametragePage implements IDetailsPage, ModifyListener {
    private static final boolean EXPAND_CONFIG  = true;
    private static       boolean expandProperty = true;
    private static final boolean EXPAND_IMPORT  = true;

    private IManagedForm mform = null;

    private Control parent                 = null;
    private ObjStep etape                  = null;
    private Text    text                   = null;
    private Text    desc                   = null;
    private Button  checkBoxDepartGeneral  = null;
    private Button  checkBoxArriveeGeneral = null;
    private Text    baliseDebut            = null;
    private Text    baliseFin              = null;
    private Button  activate               = null;
    private Button  arretChrono            = null;
    private Button  cumulSousEtape         = null;
    private Button  classementInter        = null;
    private Button  penalitySaisie         = null;
    private Button  epreuve                = null;
    private Button  importAuto             = null;
    private Text    importFile             = null;
    private Text    categoryFilters        = null;
    private Control buttonImportGeneral    = null;
    private Control buttonImportNow        = null;
    private Control buttonClearImport      = null;
    private Control buttonImportDossard    = null;

    private boolean init  = false;
    private boolean dirty = false;
    private Text group;

    @Override
    public void createContents(final Composite parent) {
        this.parent = parent;
        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        // layout.bottomMargin = 2;
        parent.setLayout(layout);
        final FormToolkit toolkit = mform.getToolkit();
        final Section sectionConfig = toolkit.createSection(parent, TWISTIE | DESCRIPTION | Section.TITLE_BAR);
        sectionConfig.setExpanded(EXPAND_CONFIG);
        sectionConfig.addExpansionListener(new ExpandableSection());
        sectionConfig.marginWidth = 10;
        sectionConfig.setText(StepDetailsPage_Configuration_etape);
        sectionConfig.setDescription(StepDetailsPage_Info_config_balise);
        final TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        td.grabVertical = true;
        sectionConfig.setLayoutData(td);

        final Composite stepConfig = toolkit.createComposite(sectionConfig);
        stepConfig.setLayout(new GridLayout(2, false));

        final GridData gd2 = new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING);
        gd2.widthHint = 10;
        gd2.horizontalSpan = 2;
        final StepButtonAction changement = new StepButtonAction();

        toolkit.createLabel(stepConfig, StepDetailsPage_Libelle);
        text = toolkit.createText(stepConfig, EMPTY, SWT.SINGLE | SWT.BORDER);
        text.addModifyListener(this);
        text.setLayoutData(new GridData(FILL_HORIZONTAL));

        toolkit.createLabel(stepConfig, StepDetailsPage_Desc);
        desc = toolkit.createText(stepConfig, EMPTY, SWT.SINGLE | SWT.BORDER);
        desc.addModifyListener(this);
        desc.setLayoutData(new GridData(FILL_HORIZONTAL));

        checkBoxDepartGeneral = toolkit.createButton(stepConfig, StepDetailsPage_Balise_depart, CHECK);
        checkBoxDepartGeneral.addSelectionListener(changement);

        baliseDebut = toolkit.createText(stepConfig, EMPTY, SWT.SINGLE | SWT.BORDER);
        baliseDebut.addModifyListener(this);
        baliseDebut.setLayoutData(new GridData(FILL_HORIZONTAL));

        checkBoxArriveeGeneral = toolkit.createButton(stepConfig, StepDetailsPage_Balise_arrivee, CHECK);
        checkBoxArriveeGeneral.addSelectionListener(changement);

        baliseFin = toolkit.createText(stepConfig, EMPTY, SWT.SINGLE | SWT.BORDER);
        baliseFin.addModifyListener(this);
        baliseFin.setLayoutData(new GridData(FILL_HORIZONTAL));

        activate = toolkit.createButton(stepConfig, StepDetailsPage_Active, CHECK);
        activate.addSelectionListener(changement);

        arretChrono = toolkit.createButton(stepConfig, StepDetailsPage_Arret_chrono, CHECK);
        arretChrono.addSelectionListener(changement);
        createSpacer(toolkit, stepConfig, 4);

        final Section sectionProperty = toolkit.createSection(parent, TWISTIE | DESCRIPTION | Section.TITLE_BAR);
        sectionProperty.marginWidth = 10;
        sectionProperty.setText(Messages.StepDetailsPage_Propriete_etape);
        sectionProperty.setDescription(Messages.StepDetailsPage_Infos_epreuve);
        sectionProperty.setExpanded(expandProperty);
        sectionProperty.addExpansionListener(new ExpandableSection());

        final TableWrapData td2 = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td2.grabHorizontal = true;
        td2.grabVertical = true;
        sectionProperty.setLayoutData(td2);
        final Composite stepPropertiesClient = toolkit.createComposite(sectionProperty);
        stepPropertiesClient.setLayout(new GridLayout(2, false));

        epreuve = toolkit.createButton(stepPropertiesClient, StepDetailsPage_Epreuve, CHECK);
        epreuve.addSelectionListener(changement);
        epreuve.setLayoutData(gd2);

        cumulSousEtape = toolkit.createButton(stepPropertiesClient, StepDetailsPage_Cumul_sous_etape, CHECK);
        cumulSousEtape.addSelectionListener(changement);
        cumulSousEtape.setLayoutData(gd2);

        classementInter = toolkit.createButton(stepPropertiesClient, StepDetailsPage_Afficher_cls_inter, CHECK);
        classementInter.addSelectionListener(changement);
        classementInter.setLayoutData(gd2);
        classementInter.addSelectionListener(changement);

        penalitySaisie = toolkit.createButton(stepPropertiesClient, StepDetailsPage_Penalite_saisie, CHECK);
        penalitySaisie.addSelectionListener(changement);
        penalitySaisie.setLayoutData(gd2);

        toolkit.createLabel(stepConfig, "Groupe");

        group = toolkit.createText(stepConfig, EMPTY, SWT.SINGLE | SWT.BORDER);
        group.addModifyListener(this);
        group.setLayoutData(new GridData(FILL_HORIZONTAL));
        createSpacer(toolkit, stepPropertiesClient, 4);

        final Section sectionImport = toolkit.createSection(parent, TWISTIE | DESCRIPTION | Section.TITLE_BAR);
        sectionImport.marginWidth = 10;
        sectionImport.setText(StepDetailsPage_Import_des_chronos);
        sectionImport.setDescription(StepDetailsPage_Syncho_donnee_export);
        sectionImport.setExpanded(EXPAND_IMPORT);
        sectionImport.addExpansionListener(new ExpandableSection());

        final TableWrapData td3 = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td3.grabHorizontal = true;
        td3.grabVertical = true;
        sectionImport.setLayoutData(td3);
        final Composite client3 = toolkit.createComposite(sectionImport);
        client3.setLayout(new GridLayout(2, false));

        importAuto = toolkit.createButton(client3, StepDetailsPage_Import_Auto, CHECK);
        importAuto.addSelectionListener(changement);
        importAuto.setLayoutData(gd2);

        importFile = toolkit.createText(client3, EMPTY, SWT.SINGLE | SWT.BORDER);
        importFile.setTextLimit(Text.LIMIT);
        importFile.setLayoutData(gd2);
        importFile.setEditable(false);

        toolkit.createLabel(client3, i18n("FILTRE_CATEGORIE"));
        categoryFilters = toolkit.createText(client3, EMPTY, SWT.SINGLE | SWT.BORDER);
        categoryFilters.setTextLimit(Text.LIMIT);
        categoryFilters.setEditable(true);
        categoryFilters.setLayoutData(new GridData(FILL_HORIZONTAL));
        categoryFilters.addModifyListener(this);
        categoryFilters.setToolTipText(i18n("FILTRE_CATEGORIE_TOOLTIP"));

        buttonImportGeneral = toolkit.createHyperlink(client3, StepDetailsPage_Choisir_fichier, SWT.NONE);
        buttonImportGeneral.addMouseListener(new TreeSelectionChanged());

        buttonClearImport = toolkit.createHyperlink(client3, StepDetailsPage_Effacer, SWT.NONE);
        buttonClearImport.addMouseListener(new TreeSelectionChanged());

        buttonImportNow = toolkit.createHyperlink(client3, StepDetailsPage_Import_maintenant, SWT.NONE);
        buttonImportNow.addMouseListener(new TreeSelectionChanged());
        buttonImportNow.setLayoutData(gd2);

        buttonImportDossard = toolkit.createHyperlink(client3, StepDetailsPage_Import_dossard, SWT.NONE);
        buttonImportDossard.addMouseListener(new TreeSelectionChanged());
        buttonImportDossard.setLayoutData(gd2);

        toolkit.paintBordersFor(sectionConfig);
        toolkit.paintBordersFor(sectionProperty);
        toolkit.paintBordersFor(sectionImport);
        sectionConfig.setClient(stepConfig);
        sectionProperty.setClient(stepPropertiesClient);
        sectionImport.setClient(client3);
    }

    public static void createSpacer(final FormToolkit toolkit, final Composite parent, final int span) {
        final Control spacer = toolkit.createLabel(parent, EMPTY);
        final GridData gd = new GridData();
        gd.horizontalSpan = span;
        spacer.setLayoutData(gd);
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        if (!init) {
            setDirty(true);
        }
    }

    private void setDirty(final boolean dirty) {
        this.dirty = dirty;
        mform.dirtyStateChanged();
    }

    class StepButtonAction extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent e) {
            if (etape == null || init) {
                return;
            }
            refreshDynamicsPanel();
            setDirty(true);
        }
    }

    private void importer(final Control parent) {
        final FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
        fileDialog.setFilterExtensions(new String[]{"*.csv"});
        fileDialog.setFilterNames(new String[]{EtapeConfigurationEditor_Fichiers_csv});
        final String fileName = fileDialog.open();
        if (fileName != null) {
            // Sauvegarde des fichiers
            etape.setInfo(VAR_FILENAME_IMPORT, fileName);
            importFile.setText(fileName);
            importAuto.setEnabled(true);
            importAuto.setSelection(true);
        } else {
            importAuto.setEnabled(false);
        }
        setDirty(true);
    }

    @Override
    public void commit(final boolean onSave) {
        if (onSave) {
            etape.setActif(activate.getSelection());
            etape.setBaliseDepartGeneral(checkBoxDepartGeneral.getSelection());
            baliseDebut.setEnabled(!checkBoxDepartGeneral.getSelection());
            etape.setBaliseArriveeGenerale(checkBoxArriveeGeneral.getSelection());
            baliseFin.setEnabled(!checkBoxArriveeGeneral.getSelection());
            etape.setArretChrono(arretChrono.getSelection());
            etape.setCumulerSousEtape(cumulSousEtape.getSelection());
            etape.setClassementInter(classementInter.getSelection());
            etape.setPenalitySaisie(penalitySaisie.getSelection());
            etape.setEpreuve(epreuve.getSelection());
            etape.setImportAuto(importAuto.getSelection());
            etape.setImportFileName(importFile.getText());
            etape.setCategoryFilter(categoryFilters.getText());
            etape.setInfo(VAR_DESCRIPTION, desc.getText());
            etape.setLib(text.getText());
            etape.setBaliseDepart(checkBoxDepartGeneral.getSelection() ? EMPTY : baliseDebut.getText());
            etape.setBaliseArrivee(checkBoxArriveeGeneral.getSelection() ? EMPTY : baliseFin.getText());
            etape.setInfo(VAR_STEP_GROUP, group.getText());
            setDirty(false);
        }
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
        return dirty;
    }

    @Override
    public boolean isStale() {
        return false;
    }

    @Override
    public void refresh() {
        if (etape != null) {
            init = true;
            text.setText(etape.getLib() == null ? EMPTY : etape.getLib());
            desc.setText(etape.getInfoStr(VAR_DESCRIPTION) == null ? EMPTY : etape.getInfoStr(VAR_DESCRIPTION));
            activate.setSelection(etape.isActif());

            final String baliseDepart = getBaliseDepartLabel(etape);
            final String baliseArrivee = getBaliseArriveeLabel(baliseDepart, etape);
            baliseDebut.setText(baliseDepart);
            baliseFin.setText(baliseArrivee);

            checkBoxDepartGeneral.setSelection(etape.getBaliseDepart() == null);
            checkBoxArriveeGeneral.setSelection(etape.getBaliseArrivee() == null);
            arretChrono.setSelection(etape.isArretChrono());
            cumulSousEtape.setSelection(etape.isCumulerSousEtape());
            cumulSousEtape.setToolTipText(i18n("SOUS_ETAPE_TOOLTIP"));

            classementInter.setSelection(etape.isClassementInter());

            epreuve.setSelection(etape.isEpreuve());
            penalitySaisie.setSelection(etape.isPenalitySaisie() || etape.isEpreuve());

            importFile.setText(etape.getImportFileName() == null ? EMPTY : etape.getImportFileName());
            importFile.setToolTipText(etape.getImportFileName());
            importFile.setEnabled(false);

            categoryFilters.setText(etape.getCategoryFilter() == null ? EMPTY : etape.getCategoryFilter());
            categoryFilters.setToolTipText(categoryFilters.getText());

            group.setText(etape.getInfoStr(VAR_STEP_GROUP));
            importAuto.setSelection(etape.isImportAuto());

            init = false;

            refreshDynamicsPanel();
        }
    }

    private void refreshDynamicsPanel() {
        final boolean cumulSousEtapeEnabled = cumulSousEtape.getSelection();
        if (cumulSousEtapeEnabled) {
            baliseDebut.setEnabled(false);
            baliseFin.setEnabled(false);
            checkBoxDepartGeneral.setEnabled(false);
            checkBoxArriveeGeneral.setEnabled(false);
        } else {
            baliseDebut.setEnabled(!checkBoxDepartGeneral.getSelection());
            baliseFin.setEnabled(!checkBoxArriveeGeneral.getSelection());
            checkBoxDepartGeneral.setEnabled(true);
            checkBoxArriveeGeneral.setEnabled(true);
        }

        final boolean importAvailable = epreuve.getSelection() && !cumulSousEtapeEnabled;
        final boolean importEnable = !importFile.getText().isEmpty() && importAvailable;
        importAuto.setEnabled(importEnable);
        buttonClearImport.setEnabled(importEnable && !importFile.getText().isEmpty());
        buttonImportNow.setEnabled(importEnable);
        buttonImportDossard.setEnabled(importEnable);
        categoryFilters.setEnabled(importEnable);

        final boolean hasSubSteps = !etape.getSteps().isEmpty() && epreuve.getSelection();
        cumulSousEtape.setEnabled(hasSubSteps || etape.isCumulerSousEtape());
        classementInter
                .setEnabled(etape.getEpreuve() != null && etape.getEpreuve() != etape || etape.isClassementInter());

        buttonImportGeneral.setEnabled(importAvailable);

        penalitySaisie.setEnabled(epreuve.getSelection());
    }

    private static String getBaliseDepartLabel(final ObjStep step) {
        final ObjStep previousStep = getPreviousStep(step);
        final String baliseDepart;
        if (step.getBaliseDepart() == null || step.getBaliseDepart().isEmpty()) {
            if (previousStep != null && previousStep.getBaliseArrivee() != null) {
                baliseDepart = previousStep.getBaliseArrivee();
            } else {
                baliseDepart = EMPTY;
            }
        } else {
            baliseDepart = step.getBaliseDepart();
        }
        return baliseDepart;
    }

    private static String getBaliseArriveeLabel(final String baliseDepart, final ObjStep step) {
        final ObjStep nextStep = getNextStep(step);
        final String baliseArrivee;
        if (step.getBaliseArrivee() == null || step.getBaliseArrivee().isEmpty()) {
            if (nextStep != null && nextStep.getBaliseDepart() != null && !nextStep.getBaliseDepart().isEmpty()) {
                baliseArrivee = nextStep.getBaliseDepart();
            } else {
                if (baliseDepart.isEmpty()) {
                    baliseArrivee = EMPTY;
                } else {
                    baliseArrivee = String.valueOf(Integer.valueOf(baliseDepart) + 1);
                }
            }
        } else {
            baliseArrivee = step.getBaliseArrivee();
        }
        return baliseArrivee;
    }

    private static ObjStep getNextStep(final ObjStep etape) {
        final Object result = etape.getParent().accept(new DefaultDataVisitor() {
            @Override
            public Object visitStep(final ObjStep parent, final Object data) {
                final Iterator<ObjStep> iter = parent.getSteps().iterator();
                while (iter.hasNext()) {
                    if (etape == iter.next()) {
                        return iter.hasNext() ? iter.next() : parent;
                    }
                }
                return parent;
            }

        }, null);
        if (result == null) {
            return null;
        }
        return (ObjStep) result;
    }

    private static ObjStep getPreviousStep(final ObjStep etape) {
        final Object result = etape.getParent().accept(new DefaultDataVisitor() {
            @Override
            public Object visitStep(final ObjStep parent, final Object data) {
                final Iterator<ObjStep> iter = parent.getSteps().iterator();
                ObjStep last = null;
                while (iter.hasNext()) {
                    final ObjStep next = iter.next();
                    if (etape == next) {
                        break;
                    }
                    last = next;
                }
                if (last == null) {
                    return parent;
                }
                return last;
            }

        }, null);
        if (result == null) {
            return null;
        }
        return (ObjStep) result;
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
        if (ssel.isEmpty()) {
            etape = null;
        } else {
            etape = (ObjStep) ssel.getFirstElement();
        }
        refresh();
    }

    class TreeSelectionChanged extends MouseAdapter {
        @Override
        public void mouseUp(final MouseEvent e) {
            boolean update = false;
            if (e.getSource() == buttonClearImport) {
                if (openConfirm(parent.getShell(), StepDetailsPage_Import_csv, StepDetailsPage_Annuler_import)) {
                    etape.setImportFileName(null);
                    importFile.setText(EMPTY);
                    commit(true);
                    update = true;
                }
            } else if (e.getSource() == buttonImportGeneral) {
                commit(true);
                importer(parent);
                update = true;
            } else if (e.getSource() == buttonImportNow) {
                if (openConfirm(parent.getShell(), StepDetailsPage_Importer + etape.getImportFileName(),
                                StepDetailsPage_Confirmation_import)) {
                    commit(true);
                    getAutoImportProcess().importSportIdent(etape);
                    update = true;
                }
            } else if (e.getSource() == buttonImportDossard) {
                if (openConfirm(parent.getShell(), StepDetailsPage_Import_concurrent + etape.getImportFileName(),
                                StepDetailsPage_Confirmatio_Import)) {
                    commit(true);
                    importDonneeConcurrent(etape, etape.getImportFileName());
                    update = true;
                }
            }
            if (update) {
                refresh();
            }
        }
    }

    private static class ExpandableSection extends ExpansionAdapter {
        @Override
        public void expansionStateChanged(final ExpansionEvent e) {
            expandProperty = ((ExpandableComposite) e.getSource()).isExpanded();
        }
    }
}
