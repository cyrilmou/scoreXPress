/**
 *
 */
package fr.cm.scorexpress.ihm.editor.page;

import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_FORM_BG;
import fr.cm.scorexpress.ihm.editor.ImportationModel;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ImportationPage extends FormPage {

    private final MasterDetailsBlock block;

    public ImportationPage(final FormEditor editor, final ImportationModel importationModel) {
        super(editor, "Importation", "Importation");
        block = new ImportationDetailsBlockPage(importationModel);
    }

    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText("Configuration de l'importation");
        form.setBackgroundImage(getImg(IMG_FORM_BG));
        block.createContent(managedForm);
    }

    public void dispose() {
        super.dispose();
    }
}
