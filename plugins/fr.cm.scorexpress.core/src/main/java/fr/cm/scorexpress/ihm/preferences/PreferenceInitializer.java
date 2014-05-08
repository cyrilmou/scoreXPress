package fr.cm.scorexpress.ihm.preferences;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import static fr.cm.scorexpress.ihm.preferences.PreferenceConstants.*;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        final IPreferenceStore store = getScoreXPressPlugin().getPreferenceStore();
        store.setDefault(P_BOOLEAN, true);
        store.setDefault(P_CHOICE, "choice2");
        store.setDefault(P_STRING, "Default value");
    }

}
