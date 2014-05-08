package fr.cm.scorexpress.ihm.application;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getScoreXPressPlugin;
import org.eclipse.swt.graphics.Image;

public class ImageReg {
    private ImageReg() {
    }

    public static Image getImg(final String key) {
        return getScoreXPressPlugin().getImageRegistry().get(key);
    }
}
