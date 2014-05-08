package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.core.model.ObjCategorie;
import fr.cm.scorexpress.model.CategoryModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import static fr.cm.scorexpress.core.model.ObjCategorie.AGE;
import static fr.cm.scorexpress.core.model.ObjCategorie.VAR_CATEGORIE_GROUP;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

public class CategoryDetailsPage implements IDetailsPage {
    private IManagedForm mform;

    private       ObjCategorie  category;
    private       Text          text;
    private       Text          age;
    private final CategoryModel categoryModel;
    private       Text          group;

    public CategoryDetailsPage(final CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    @Override
    public void createContents(final Composite parent) {
        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);
        final TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        final FormToolkit toolkit = mform.getToolkit();
        final Section s1 = toolkit.createSection(parent, Section.TITLE_BAR);
        s1.marginWidth = 10;
        s1.setText(i18n("CategoryDetailsPage.INFO_CATEGORIE"));
        s1.setLayoutData(td);
        toolkit.paintBordersFor(s1);
        final Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout(2, false));
        s1.setClient(client);
        toolkit.createLabel(client, i18n("CategoryDetailsPage.LIBELLE"));
        text = toolkit.createText(client, EMPTY, SWT.SINGLE | SWT.BORDER);
        text.addModifyListener(new LabelChanged());
        text.setLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
        toolkit.createLabel(client, i18n("CategoryDetailsPage.AGE_MINIMUM"));
        age = toolkit.createText(client, EMPTY, SWT.SINGLE | SWT.BORDER);
        age.addModifyListener(new AgeChanged());
        age.setLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
        toolkit.createLabel(client, "Groupe");
        group = toolkit.createText(client, EMPTY, SWT.SINGLE | SWT.BORDER);
        group.addModifyListener(new GroupChanged());
        group.setLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
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
        if (category != null) {
            setTextInfo(text, category.getNom());
            setTextInfo(age, category.getInfoStr(AGE));
            setTextInfo(group, category.getInfoStr(VAR_CATEGORIE_GROUP));
        }
    }

    private static void setTextInfo(final Text text, final String info) {
        text.setText(info == null ? EMPTY : info);
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
            category = (ObjCategorie) ssel.getFirstElement();
        } else {
            category = null;
        }
        refresh();
    }

    private class LabelChanged implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (category != null) {
                category.setInfo(ObjCategorie.NOM, text.getText());
                categoryModel.categoriesUpdate();
            }
        }
    }

    private class AgeChanged implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (category != null) {
                category.setInfo(AGE, age.getText());
            }
        }
    }

    private class GroupChanged implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (category != null) {
                category.setInfo(VAR_CATEGORIE_GROUP, group.getText());
            }
        }
    }
}
