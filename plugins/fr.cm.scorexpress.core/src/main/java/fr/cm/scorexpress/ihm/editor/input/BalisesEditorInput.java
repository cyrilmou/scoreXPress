package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.core.model.AbstractBalises;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class BalisesEditorInput implements IEditorInput {
    private final AbstractBalises balises;

    public BalisesEditorInput(final AbstractBalises balises) {
        this.balises = balises;
    }

    public boolean exists() {
        return true;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return "Balises";
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return "Balises";
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public AbstractBalises getBalises() {
        return balises;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof BalisesEditorInput) {
            return ((BalisesEditorInput) obj).balises == balises;
        }
        return false;
    }

}
