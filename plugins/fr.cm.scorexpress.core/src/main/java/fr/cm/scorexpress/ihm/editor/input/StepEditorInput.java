package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import fr.cm.scorexpress.ihm.editor.StepEditorModel;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class StepEditorInput implements IEditorInput {
    private final StepEditorModel model;

    public StepEditorInput(final StepEditorModel model) {
        this.model = model;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return ScoreXPressPlugin.getImageDescriptor("/icons/etape.png");
    }

    @Override
    public String getName() {
        return model.getNameEditor();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return model.getNameEditor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof StepEditorInput) {
            final StepEditorInput input2 = (StepEditorInput) obj;
            return input2.getName().equals(getName());
        }
        return false;
    }

    public StepEditorModel getModel() {
        return model;
    }
}
