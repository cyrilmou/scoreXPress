package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.ihm.editor.ResultatEtapeModel;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ResultatEditorInput implements IEditorInput, CommonEditorInput<ResultatEtapeModel> {
    private final ResultatEtapeModel model;

    public ResultatEditorInput(final ResultatEtapeModel model) {
        this.model = model;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return model.getLabel();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return model.getLabel();
    }

    @Override
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public boolean equals(final Object inputEditor) {
        if (inputEditor instanceof ResultatEditorInput) {
            final CommonEditorInput<ResultatEtapeModel> editorInput =
                    (CommonEditorInput<ResultatEtapeModel>) inputEditor;
            final ResultatEtapeModel oldModel = editorInput.getModel();
            return model.getStep() == oldModel.getStep() && model.getMode() == oldModel.getMode();
        }
        return false;
    }

    @Override
    public ResultatEtapeModel getModel() {
        return model;
    }
}
