package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import fr.cm.scorexpress.ihm.editor.GeneralEditorModel;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class GeneraleEditorInput implements IEditorInput {
    private final GeneralEditorModel generalEditorModel;

    public GeneraleEditorInput(final GeneralEditorModel generalEditorModel) {
        this.generalEditorModel = generalEditorModel;
    }

    public boolean exists() {
        return true;
    }

    public ImageDescriptor getImageDescriptor() {
        ScoreXPressPlugin.getImageDescriptor("/icons/etape.png");
        return null;
    }

    public String getName() {
        return generalEditorModel.getManifModel().getNameTextModel().getText();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return getName();
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof GeneraleEditorInput) {
            final IEditorInput input2 = (GeneraleEditorInput) obj;
            return input2.getName().equals(getName());
        }
        return false;
    }

    public GeneralEditorModel getGeneralEditorModel() {
        return generalEditorModel;
    }
}
