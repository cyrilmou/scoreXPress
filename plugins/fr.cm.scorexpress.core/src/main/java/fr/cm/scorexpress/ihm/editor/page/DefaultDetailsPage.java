package fr.cm.scorexpress.ihm.editor.page;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;

@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class DefaultDetailsPage implements IDetailsPage {
    public void setFocus() {
    }

    public void refresh() {
    }

    public boolean isStale() {
        return false;
    }

    public boolean isDirty() {
        return false;
    }

    public void dispose() {
    }

    public void commit(final boolean onSave) {
    }

    public void selectionChanged(final IFormPart part, final ISelection selection) {
    }

    public boolean setFormInput(final Object input) {
        return false;
    }
}
