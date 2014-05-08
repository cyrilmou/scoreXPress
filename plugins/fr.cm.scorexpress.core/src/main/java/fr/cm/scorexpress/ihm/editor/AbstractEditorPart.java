package fr.cm.scorexpress.ihm.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractEditorPart<T extends IEditorInput> extends EditorPart {
    private T input = null;

    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException {
        this.input = (T) input;
        super.setSite(site);
        super.setInput(input);
    }

    protected void init(final T input) {
        doNothing();
    }

    public T getInput() {
        return (T) input;
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void doSave(final IProgressMonitor monitor) {
        doNothing();
    }

    public void doSaveAs() {
        doNothing();
    }

    public void setFocus() {
        doNothing();
    }

    @SuppressWarnings({"NoopMethodInAbstractClass"})
    private void doNothing() {
    }
}
