package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.ihm.application.ImageReg;
import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import fr.cm.scorexpress.ihm.editor.i18n.Message;
import fr.cm.scorexpress.model.CategoryModel;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;


public class StepCategoryPage extends FormPage {

    private final CategorieDetailsBlock block;

    public StepCategoryPage(final CategoryModel categoryModel) {
        super(Message.i18n("StepCategoryPage.CATEGORIE"),
                Message.i18n("StepCategoryPage.CATEGORIES"));
        block = new CategorieDetailsBlock(categoryModel);
    }

    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText(Message.i18n("StepCategoryPage.GESTION_CATEGORIES"));
        form.setBackgroundImage(ImageReg.getImg(ScoreXPressPlugin.IMG_FORM_BG));
        block.createContent(managedForm);

    }

    public void dispose() {
        super.dispose();
    }
}
