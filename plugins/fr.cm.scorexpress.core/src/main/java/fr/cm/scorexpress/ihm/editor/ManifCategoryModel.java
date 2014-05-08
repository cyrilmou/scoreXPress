package fr.cm.scorexpress.ihm.editor;

import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.scorexpress.core.model.ObjCategorie;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.CategoryModel;
import fr.cm.scorexpress.model.ManifModel;

import java.util.Collection;

public class ManifCategoryModel extends CategoryModel {
    private final ButtonModel addButtonModel;
    private final ButtonModel removeButtonModel;
    private final ManifModel manifModel;

    public ManifCategoryModel(final ManifModel manifModel) {
        super();
        this.manifModel = manifModel;
        addButtonModel = createAddButtonModel();
        removeButtonModel = createRemoveButtonModel();
    }

    public Collection<ObjCategorie> getCategories() {
        return manifModel.getManif().getCategories();
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
                final ObjCategorie newCategorie = new ObjCategorie("new");
                manifModel.getManif().addCategorie(newCategorie);
                categorieTableModel.addRow(newCategorie);
            }
        });
        return model;
    }

    private ButtonModel createRemoveButtonModel() {
        final ButtonModel model = new ButtonModel(i18n("CategorieDetailsBlock.SUPPRIMER"));
        model.addWidgetListener(new ButtonAdapter() {
            public void click() {
                for (final ObjCategorie categorie : categorieTableModel.getSelection()) {
                    manifModel.getManif().removeCategorie(categorie);
                    categorieTableModel.removeRow(categorie);
                }
            }
        });
        return model;
    }
}
