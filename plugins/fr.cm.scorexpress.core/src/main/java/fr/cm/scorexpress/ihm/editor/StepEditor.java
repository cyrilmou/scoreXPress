package fr.cm.scorexpress.ihm.editor;

import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.ihm.editor.input.StepEditorInput;
import fr.cm.scorexpress.ihm.editor.page.*;
import fr.cm.scorexpress.ihm.print.IPrintable;

public class StepEditor extends FormEditor
        implements IPropertyListener, IPageChangedListener, IPrintable, IAutoAjustColumnEditor {
    public static final String STEP_EDITOR_ID = "fr.cm.scorexpress.stepEditor";

    private StepEditorModel model;

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        setPartName(input.getName());
        if (input instanceof StepEditorInput) {
            super.init(site, input);
            model = ((StepEditorInput) input).getModel();
        } else {
            throw new PartInitException(i18n("StepEditor.Error"));
        }
    }

    @Override
    protected void addPages() {
        try {
            final IFormPage parametrageStepPage =
                    new ParametrageStepPage(new ParametrageStepPageModel(model.getStepModel()));
            parametrageStepPage.initialize(this);
            parametrageStepPage.addPropertyListener(this);
            addPage(parametrageStepPage);

            if (model.getStepModel().getStep().isEpreuve()) {
                final IFormPage userPage = new UserPage(new UserPageModel(model.getStepModel()));
                userPage.initialize(this);
                userPage.addPropertyListener(this);
                addPage(userPage);
            }

            final IFormPage penalityPage = new PenalityPage(new PenalityPageModel(model.getStepModel()));
            penalityPage.initialize(this);
            addPage(penalityPage);

            // final IFormPage stepCategoryPage = new StepCategoryPage(new
            // StepCategoryModel(model.getStepModel()));
            // stepCategoryPage.initialize(this);
            // stepCategoryPage.addPropertyListener(this);
            // addPage(stepCategoryPage);

            // final IFormPage importationStepPage =
            // new ImportationStepPage(new
            // ImportationStepPageModel(model.getStepModel()));
            // importationStepPage.initialize(this);
            // importationStepPage.addPropertyListener(this);
            // addPage(importationStepPage);

            final IFormPage stepTempsPage = new TempsResultatPage(new TempsResultatPageModel(model.getStepModel()));
            stepTempsPage.initialize(this);
            stepTempsPage.addPropertyListener(this);
            addPage(stepTempsPage);
            addPageChangedListener(this);
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChanged(final Object source, final int propId) {
        firePropertyChange(propId);
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        model.doSave();
        getActivePageInstance().doSave(monitor);
    }

    @Override
    public boolean isSaveAsAllowed() {
        return getActivePageInstance().isSaveAsAllowed();
    }

    @Override
    public void dispose() {
        model.dispose();
        if (getActivePageInstance() != null) {
            getActivePageInstance().dispose();
        }
        super.dispose();
    }

    @Override
    public boolean isDirty() {
        return model.isDirty() || getActivePageInstance().isDirty();
    }

    @Override
    public void doSaveAs() {
        getActivePageInstance().doSaveAs();
    }

    @Override
    public void pageChanged(final PageChangedEvent event) {
        firePropertyChange(PROP_DIRTY);
    }

    @Override
    public void print() {
        final IFormPage page = getActivePageInstance();
        if (page != null && page instanceof IPrintable) {
            ((IPrintable) page).print();
        }
    }

    @Override
    public AutoResizeColumn getAutoResizeContext() {
        return model.getStepModel().getAutoResizeColumn();
    }
}
