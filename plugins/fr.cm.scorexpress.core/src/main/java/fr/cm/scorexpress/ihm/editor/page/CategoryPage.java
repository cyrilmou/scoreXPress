package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import static fr.cm.common.widget.composite.CompositeBuilders.createScrollFormBuilder;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.CategoryModel;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class CategoryPage extends FormPage {

    private final CategorieDetailsBlock block;
    private final CategoryModel model;

    public CategoryPage(final FormEditor editor, final CategoryModel categoryModel) {
        super(editor, i18n("CategoryPage.CATEGORIE"), i18n("CategoryPage.CATEGORIES"));
        model = categoryModel;
        block = new CategorieDetailsBlock(categoryModel);
    }

    protected void createFormContent(final IManagedForm managedForm) {
        createScrollFormBuilder(new MyFormToolkit(managedForm.getToolkit()), managedForm, model.getTitleModel());
        block.createContent(managedForm);
    }

    public void dispose() {
        super.dispose();
    }
}
