package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.model.ManifModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;


public class ManifPage extends FormPage {

    private final ManifDetailsBlockPage block;
    private final ManifModel            manifModel;

    public ManifPage(final FormEditor editor, final ManifModel manifModel) {
        super(editor, i18n("ManifPage.MANIF"), i18n("ManifPage.MANIFESTATION"));
        this.manifModel = manifModel;
        block = new ManifDetailsBlockPage(manifModel);

    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText(manifModel.getTitle());
        form.setBackgroundImage(manifModel.getBackgroundImage());
        block.createContent(managedForm);

    }

    @Override
    public void dispose() {
        super.dispose();
        manifModel.dispose();
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        manifModel.save();
    }

    @Override
    public void doSaveAs() {
        manifModel.save();
    }

    @Override
    public boolean isDirty() {
        return manifModel.isDirty();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return manifModel.issaveAllowed();
    }
}
