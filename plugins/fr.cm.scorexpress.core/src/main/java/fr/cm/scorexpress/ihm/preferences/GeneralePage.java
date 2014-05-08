package fr.cm.scorexpress.ihm.preferences;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.preferences.PreferenceConstants.*;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GeneralePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public GeneralePage() {
        super(GRID);
        setPreferenceStore(getScoreXPressPlugin().getPreferenceStore());
        setDescription("A demonstration of a preference page implementation");
    }

    public void createFieldEditors() {
        addField(new DirectoryFieldEditor(P_PATH, "&Directory preference:", getFieldEditorParent()));
        addField(new BooleanFieldEditor(P_BOOLEAN, "&An example of a boolean preference", getFieldEditorParent()));
        addField(new RadioGroupFieldEditor(P_CHOICE, "An example of a multiple-choice preference", 1,
                new String[][]{{"&Choice 1", "choice1"}, {"C&hoice 2", "choice2"}}, getFieldEditorParent()));
        addField(new StringFieldEditor(P_STRING, "A &text preference:", getFieldEditorParent()));
    }

    public void init(final IWorkbench workbench) {
    }

}
