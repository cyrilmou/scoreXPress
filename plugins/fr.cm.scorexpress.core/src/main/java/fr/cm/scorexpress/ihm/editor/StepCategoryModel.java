package fr.cm.scorexpress.ihm.editor;

import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.scorexpress.core.model.ObjCategorie;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.CategoryModel;
import fr.cm.scorexpress.model.StepModel;

import java.util.Collection;

public class StepCategoryModel extends CategoryModel {
    private final StepModel stepModel;
    private final ButtonModel addButtonModel;
    private final ButtonModel removeButtonModel;

    public StepCategoryModel(final StepModel stepModel) {
        this.stepModel = stepModel;
        addButtonModel = createAddButtonModel();
        removeButtonModel = createRemoveButtonModel();
    }

    public Collection<ObjCategorie> getCategories() {
        return stepModel.getStep().getFiltreCategory();
    }

    public ButtonModel getAddButtonModel() {
        return addButtonModel;
    }

    public ButtonModel getRemoveButtonModel() {
        return removeButtonModel;
    }

    private ButtonModel createAddButtonModel() {
        final ButtonModel model = new ButtonModel(i18n("CategorieDetailsBlock.AJOUTER"));
        model.addWidgetListener(new ButtonAdapter() {
            public void click() {
                stepModel.getStep().addFiltreCategory(new ObjCategorie("new"));
            }
        });
        return model;
    }

    private ButtonModel createRemoveButtonModel() {
        final ButtonModel model = new ButtonModel(i18n("CategorieDetailsBlock.SUPPRIMER"));
        model.addWidgetListener(new ButtonAdapter() {
            public void click() {
                for (final ObjCategorie categorie : categorieTableModel.getSelection()) {
                    stepModel.getStep().removeFiltreCategory(categorie);
                    categorieTableModel.removeRow(categorie);
                }
            }
        });
        return model;
    }
}
