package fr.cm.scorexpress.ihm.editor;

import fr.cm.scorexpress.model.ManifModel;

public class GeneralEditorModel {
    private final ManifModel manifModel;

    public GeneralEditorModel(final ManifModel manifModel) {
        this.manifModel = manifModel;
    }

    public ManifModel getManifModel() {
        return manifModel;
    }
}
