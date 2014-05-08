package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.MyToolkit;
import static fr.cm.common.widget.button.ButtonBuilder.createButton;
import fr.cm.common.widget.table.TableBuilder;
import static fr.cm.common.widget.table.TableBuilder.createTable;
import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.scorexpress.core.model.ObjCategorie;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.CategoryModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import static org.eclipse.swt.layout.GridData.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.*;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class CategorieDetailsBlock extends MasterDetailsBlock {
    private final CategoryModel categoryModel;

    public CategorieDetailsBlock(final CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        final FormToolkit toolkit = managedForm.getToolkit();
        final MyToolkit myToolkit = new MyFormToolkit(toolkit);

        final Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText(i18n("CategorieDetailsBlock.CONFIGURATION"));
        section.setDescription(i18n("CategorieDetailsBlock.INFO"));
        section.marginWidth = 10;
        section.marginHeight = 5;
        final Composite client = toolkit.createComposite(section, SWT.WRAP);
        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client);

        final GridData gd = new GridData(FILL_BOTH);
        gd.verticalSpan = 2;

        createButton(myToolkit, client, categoryModel.getAddButtonModel(), SWT.NONE)
                .withLayoutData(new GridData(VERTICAL_ALIGN_BEGINNING));

        createButton(myToolkit, client, categoryModel.getRemoveButtonModel(), SWT.NONE)
                .withLayoutData(new GridData(HORIZONTAL_ALIGN_FILL | VERTICAL_ALIGN_BEGINNING));

        section.setClient(client);
        final IFormPart spart = new SectionPart(section);
        managedForm.addPart(spart);

        final TableBuilder tableBuilder = createTable(myToolkit,
                client,
                categoryModel.getCategoryTableModel(),
                SWT.NONE);

        tableBuilder.withLayoutData(gd);
        tableBuilder.addColumn("Category", "Category", SWT.NONE).withRenderer(new CategoryColumnRenderer());


        tableBuilder.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                managedForm.fireSelectionChanged(spart, event.getSelection());
            }
        });
    }

    protected void createToolBarActions(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        final IAction haction = new Action("hor", Action.AS_RADIO_BUTTON) {
            public void run() {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setToolTipText("ScrolledPropertiesBlock.horizontal");
        haction.setImageDescriptor(getImageDescriptor(IMG_HORIZONTAL));
        final IAction vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
            public void run() {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vaction.setChecked(false);
        vaction.setToolTipText("ScrolledPropertiesBlock.vertical");
        vaction.setImageDescriptor(getImageDescriptor(IMG_VERTICAL));
        form.getToolBarManager().add(haction);
        form.getToolBarManager().add(vaction);
    }

    protected void registerPages(final DetailsPart detailsPart) {
        detailsPart.registerPage(ObjCategorie.class, new CategoryDetailsPage(categoryModel));
    }

    class MasterContentProvider implements IStructuredContentProvider {
        public Object[] getElements(final Object inputElement) {
            return categoryModel.getCategories().toArray();
        }

        public void dispose() {
        }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    class MasterLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(final Object obj, final int index) {
            return ((ObjCategorie) obj).getNom();
        }

        public Image getColumnImage(final Object obj, final int index) {

            return null;
        }
    }

    private static class CategoryColumnRenderer extends TableColumnRenderer<ObjCategorie> {
        public String getColumnText(final ObjCategorie categorie) {
            return categorie.getNom();
        }

        public int compare(final ObjCategorie elem1, final ObjCategorie elem2) {
            return elem1.getNom().compareTo(elem2.getNom());
        }
    }
}
