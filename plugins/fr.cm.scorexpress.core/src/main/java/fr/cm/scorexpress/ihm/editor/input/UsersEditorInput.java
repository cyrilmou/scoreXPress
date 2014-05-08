package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.IUsers;
import fr.cm.scorexpress.ihm.editor.IAutoAjustColumnEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class UsersEditorInput implements IEditorInput, IAutoAjustColumnEditor {
    private final IUsers inscribes;
    private final AutoResizeColumn autoResizeContext;

    public UsersEditorInput(final IUsers dossards, final AutoResizeColumn autoResizeContext) {
        inscribes = dossards;
        this.autoResizeContext = autoResizeContext;
    }

    public boolean exists() {
        return true;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return "Concurrents";
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return "Concurrents";
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public IUsers getUsers() {
        return inscribes;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof UsersEditorInput) {
            return ((UsersEditorInput) obj).inscribes == inscribes;
        }
        return false;
    }

    public AutoResizeColumn getAutoResizeContext() {
        return autoResizeContext;
    }
}
