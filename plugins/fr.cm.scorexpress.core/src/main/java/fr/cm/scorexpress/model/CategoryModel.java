package fr.cm.scorexpress.model;

import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.common.widget.table.TableAdapter;
import fr.cm.common.widget.table.TableModel;
import fr.cm.scorexpress.core.model.ObjCategorie;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

import java.util.Collection;

public abstract class CategoryModel {
    protected TableModel<ObjCategorie> categorieTableModel;
    private final FormModel titleModel;

    public CategoryModel() {
        titleModel = new FormModel(i18n("CategoryPage.GESTIONNAIRE_CATEGORIES"));
    }

    public abstract Collection<ObjCategorie> getCategories();

    public abstract ButtonModel getAddButtonModel();

    public abstract ButtonModel getRemoveButtonModel();

    public TableModel<ObjCategorie> getCategoryTableModel() {
        if (categorieTableModel == null) {
            categorieTableModel = createTableModel();
            categorieTableModel.addWidgetListener(new TableAdapter<ObjCategorie>() {
                public void modify(final ObjCategorie element, final int column) {
                }
            });
        }
        return categorieTableModel;
    }

    protected TableModel<ObjCategorie> createTableModel() {
        final TableModel<ObjCategorie> tableModel = new TableModel<ObjCategorie>();
        tableModel.addRows(getCategories());
        tableModel.addColumn("Category").withAutoResize(true);
        return tableModel;
    }

    public void categoriesUpdate() {
        categorieTableModel.refilter();
    }


    public FormModel getTitleModel() {
        return titleModel;
    }
}
