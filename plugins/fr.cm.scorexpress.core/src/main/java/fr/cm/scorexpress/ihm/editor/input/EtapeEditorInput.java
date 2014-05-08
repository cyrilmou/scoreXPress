package fr.cm.scorexpress.ihm.editor.input;

import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.editor.IAutoAjustColumnEditor;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class EtapeEditorInput implements IEditorInput, IPersistableElement, IAutoAjustColumnEditor {
    public static final String KEY_NAME   = "ETAPE";
    public static final String KEY_EDITOR = "EDITOR";

    private final ObjStep          etape;
    private final String           idEditor;
    private final AutoResizeColumn autoResizeContext;

    public EtapeEditorInput(final ObjStep etape, final String idEditor, final AutoResizeColumn autoResizeContext) {
        this.etape = etape;
        this.idEditor = idEditor;
        this.autoResizeContext = autoResizeContext;
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
        return etape.getLib();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return etape.getLib();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return null;
    }

    public ObjStep getEtape() {
        return etape;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EtapeEditorInput) {
            final EtapeEditorInput editorInput = (EtapeEditorInput) obj;
            return editorInput.etape == etape && editorInput.idEditor.equals(idEditor);
        }
        return false;
    }

    @Override
    public String getFactoryId() {
        return EtapeEditorInputFactory.ID;
    }

    @Override
    public void saveState(final IMemento memento) {
        memento.putString(KEY_NAME, etape.getLib());
        memento.putString(KEY_EDITOR, idEditor);
    }

    @Override
    public AutoResizeColumn getAutoResizeContext() {
        return autoResizeContext;
    }
}

class EtapeEditorInputFactory implements IElementFactory {
    public static final String           ID                = "fr.cm.chronos.editor.EtapeInput";
    private final       AutoResizeColumn autoResizeContext = new AutoResizeColumn();

    @Override
    public IAdaptable createElement(final IMemento memento) {
        final String name = memento.getString(EtapeEditorInput.KEY_NAME);
        final String idEditor = memento.getString(EtapeEditorInput.KEY_EDITOR);
        System.out.println("EtapeEditorInputFactory");
        if (name != null && idEditor != null) {
            return new EtapeEditorInput(null, idEditor, autoResizeContext);
        }
        return null;
    }
}
