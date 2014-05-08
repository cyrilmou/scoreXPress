/**
 *
 */
package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.applicative.ProjectManager;
import fr.cm.scorexpress.ihm.application.ImageReg;
import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.eclipse.swt.SWT.HORIZONTAL;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;
import static org.eclipse.ui.forms.widgets.Section.DESCRIPTION;
import static org.eclipse.ui.forms.widgets.TableWrapData.FILL;


public class ImportationStepPage extends FormPage {
    private static final String IMPORTATION_STEP_PAGE_ID = "fr.cm.scorexpress.ImportationStepPage";
    private       Button                   importationButton;
    private final ImportationStepPageModel model;

    public ImportationStepPage(final ImportationStepPageModel model) {
        super(IMPORTATION_STEP_PAGE_ID, i18n("StepEditor.Importation"));
        this.model = model;
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText("Importation des informations des concurrents");
        form.setBackgroundImage(ImageReg.getImg(ScoreXPressPlugin.IMG_FORM_BG));

        final Composite parent = form.getBody();

        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);

        final FormToolkit toolkit = managedForm.getToolkit();
        final Section s1 = toolkit.createSection(parent, DESCRIPTION | Section.TITLE_BAR);
        s1.marginWidth = 10;
        s1.setText(i18n("INFO_ETAPE"));
        final TableWrapData td = new TableWrapData(FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        s1.setLayoutData(td);
        final Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout(2, false));

        final GridData gd = new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING);

        toolkit.createLabel(client, i18n("LIBELLE"));
        toolkit.createLabel(client, model.getStepModel().getStep().getEpreuve().getLib());

        toolkit.createLabel(client, "Fichier d'importation");
        toolkit.createLabel(client, model.getStepModel().getStep().getEpreuve().getImportFileName());

        toolkit.createLabel(client, "Importation");
        importationButton = toolkit.createButton(client, "Importer", SWT.NONE);
        importationButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                importDossard();
            }
        });
        importationButton.setLayoutData(gd);

        toolkit.paintBordersFor(s1);
        s1.setClient(client);
    }

    private void importDossard() {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);
        fileDialog.setFilterExtensions(new String[]{"*.csv"});
        fileDialog.setFilterNames(new String[]{"Fichiers csv (*.csv)"});
        final String fileName = fileDialog.open();
        if (fileName != null) {
            ProjectManager.importDonneeConcurrent(model.getStepModel().getStep(), fileName);
        }
    }

    protected void createSpacer(final FormToolkit toolkit, final Composite parent, final int span) {
        final Label spacer = toolkit.createSeparator(parent, HORIZONTAL);
        final GridData gd = new GridData(FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.heightHint = 20;
        spacer.setLayoutData(gd);
    }
}
