package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.composite.CompositeBuilder;
import static fr.cm.common.widget.composite.CompositeBuilders.createSection;
import fr.cm.common.widget.composite.SectionBuilder;
import fr.cm.scorexpress.ihm.editor.ImportationModel;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import fr.cm.scorexpress.model.DirtyListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class ImportationDetailsPage implements IDetailsPage {
    private final ImportationModel importationModel;

    private MyToolkit myToolkit = null;
    private MyDirtyListener dirtyListener = null;

    public ImportationDetailsPage(final ImportationModel importationModel) {
        this.importationModel = importationModel;
    }

    public void initialize(final IManagedForm form) {
        myToolkit = new MyFormToolkit(form.getToolkit());
        dirtyListener = new MyDirtyListener(form);
        importationModel.getDirtyModel().addDirtyListener(dirtyListener);
    }

    public void createContents(final Composite parent) {
        parent.setLayout(new TableWrapLayout());
        final TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;

        final SectionBuilder section = createSection(myToolkit, parent, Section.TITLE_BAR)
                .withText(i18n("ImportationDetailsPage.Import")).setLayoutData(td).withPaintBorder();
        final CompositeBuilder client = section.addClient(SWT.NONE);
        client.withLayout(new GridLayout(2, false));

        client.addStaticLabel(SWT.NONE).withText(i18n("ImportationDetailsPage.Libelle"));
        client.addText(importationModel.getLabelTextModel(), SWT.SINGLE | SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));

        client.addStaticLabel(SWT.NONE).withText(i18n("ImportationDetailsPage.Data"));
        client.addText(importationModel.getFieldTextModel(), SWT.SINGLE | SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));


        client.addStaticLabel(SWT.NONE).withText(i18n("ImportationDetailsPage.csv"));
        client.addText(importationModel.getElementCsvModel(), SWT.SINGLE | SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));


        client.addStaticLabel(SWT.NONE).withText(i18n("ImportationDetailsPage.largeur"));
        client.addText(importationModel.getWitdhTextModel(), SWT.SINGLE | SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));

        client.addStaticLabel(SWT.NONE).withText(i18n("ImportationDetailsPage.show"));
        client.addCheckbox(importationModel.getShowButtonModel(), SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
    }

    public void commit(final boolean onSave) {
        importationModel.commit(onSave);
    }

    public void dispose() {
        importationModel.getDirtyModel().removeDirtyListener(dirtyListener);
    }

    public boolean isDirty() {
        return importationModel.getDirtyModel().isDirty();
    }

    public boolean isStale() {
        return false;
    }

    public void refresh() {
    }

    public void setFocus() {
    }

    public boolean setFormInput(final Object input) {
        return false;
    }

    public void selectionChanged(final IFormPart part, final ISelection selection) {
    }

    private static class MyDirtyListener implements DirtyListener {
        private final IManagedForm form;

        MyDirtyListener(final IManagedForm form) {
            this.form = form;
        }

        public void onDirty() {
            form.dirtyStateChanged();
        }
    }
}
