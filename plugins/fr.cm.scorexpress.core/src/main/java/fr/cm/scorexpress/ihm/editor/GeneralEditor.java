package fr.cm.scorexpress.ihm.editor;

import fr.cm.scorexpress.ihm.editor.input.GeneraleEditorInput;
import fr.cm.scorexpress.ihm.editor.page.CategoryPage;
import fr.cm.scorexpress.ihm.editor.page.ImportationPage;
import fr.cm.scorexpress.ihm.editor.page.ManifPage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class GeneralEditor extends FormEditor implements IPropertyListener {
    public static final String GENERAL_EDITOR_ID = "fr.cm.scorexpress.generalEditor";

    private GeneralEditorModel generalEditorModel;

    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException {
        if (input instanceof GeneraleEditorInput) {
            super.init(site, input);
            setPartName(input.getName());
            generalEditorModel = ((GeneraleEditorInput) input).getGeneralEditorModel();
        } else {
            throw new PartInitException("Mauvais inputEditor GeneraleEditorInput attendu");
        }
    }

    protected void addPages() {
        try {
            final IFormPage manifPage = new ManifPage(this, generalEditorModel.getManifModel());
            manifPage.addPropertyListener(this);
            addPage(manifPage);
            final IFormPage categoryPage = new CategoryPage(this,
                    new ManifCategoryModel(generalEditorModel.getManifModel()));
            categoryPage.addPropertyListener(this);
            addPage(categoryPage);
            final IFormPage importationPage = new ImportationPage(this,
                    new ImportationModel(generalEditorModel.getManifModel()));
            importationPage.addPropertyListener(this);
            addPage(importationPage);
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    protected FormToolkit createToolkit(final Display display) {
        return new FormToolkit(Display.getCurrent());
    }

    public void propertyChanged(final Object source, final int propId) {
        firePropertyChange(propId);
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void doSave(final IProgressMonitor monitor) {
        if (getActivePage() != -1) {
            final ISaveablePart page = (ISaveablePart) pages.get(getActivePage());
            if (page != null) {
                page.doSave(monitor);
            }
        }
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        boolean dirty = false;
        for (final Object page1 : pages) {
            final ISaveablePart page = (ISaveablePart) page1;
            if (page != null) {
                if (page.isDirty()) {
                    dirty = true;
                }
            }
        }
        return dirty;
    }

    public void dispose() {
        super.dispose();
    }

}
