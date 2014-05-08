package fr.cm.scorexpress.ihm.application;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import java.net.URL;

public class ScoreXPressPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "fr.cm.scorexpress";

    public static final String PROMPT_ON_EXIT = "PROMPT_ON_EXIT";

    public static final String IMG_SAMPLE          = "sample";
    public static final String IMG_CHECKED_IMAGE   = "checked";
    public static final String IMG_UNCHECKED_IMAGE = "unchecked";
    public static final String IMG_BECARFULL       = "attention";

    public static final String IMG_ARRETCHRONO          = "arretChrono";
    public static final String IMG_ETAPE                = "etape";
    public static final String IMG_USER                 = "participant";
    public static final String IMG_MANIFESTATION        = "manifestation";
    public static final String IMG_DESACTIVATE          = "desactivate";
    public static final String IMG_DOSSARD              = "dossard";
    public static final String IMG_USERS                = "users";
    public static final String IMG_FORM_BG              = "banner";
    public static final String IMG_HORIZONTAL           = "th_horrizontal";
    public static final String IMG_VERTICAL             = "th_vertival";
    public static final String IMG_VIEW_BG              = "view_bg";
    public static final String IMG_PENALITY_DESACTIVATE = "penality_desac";
    public static final String IMG_BALISE               = "balise";
    public static final String IMG_ACTIVATE             = "activate";
    public static final String IMG_CHECK_ACTIVATE       = "check_activate";
    public static final String IMG_CHECK_DESACTIVATE    = "check_desactivate";
    public static final String IMG_REFRESH              = "refresh";
    public static final String IMG_TOGGLE               = "toggle";
    public static final String IMG_PENALITY             = "penality";
    public static final String IMG_REMOVE               = "remove";
    public static final String IMG_IMPORT               = "import";
    public static final String IMG_DOWN                 = "down";
    public static final String IMG_NO_CHRONO            = "noUserChrono";
    public static final String IMG_RED_RESULT           = "result";
    public static final String IMG_GREEN_RESULT         = "result_green";

    private static ScoreXPressPlugin plugin;

    public ScoreXPressPlugin() {
        plugin = this;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static ScoreXPressPlugin getScoreXPressPlugin() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(final String path) {
        return getScoreXPressPlugin().getImageRegistry().getDescriptor(path);
    }

    @Override
    protected void initializeImageRegistry(final ImageRegistry registry) {
        registerImage(registry, IMG_SAMPLE, "sample.gif");
        registerImage(registry, IMG_CHECKED_IMAGE, "checked.gif");
        registerImage(registry, IMG_UNCHECKED_IMAGE, "unchecked.gif");
        registerImage(registry, IMG_BECARFULL, "attention.gif");

        registerImage(registry, IMG_ARRETCHRONO, "nav_stop.gif");
        registerImage(registry, IMG_ETAPE, "nav_go.gif");
        registerImage(registry, IMG_MANIFESTATION, "nav_home.gif");
        registerImage(registry, IMG_USER, "attention.gif");
        registerImage(registry, IMG_DESACTIVATE, "search_rem.gif");
        registerImage(registry, IMG_DOSSARD, "wn_eclcommunity48.gif");
        registerImage(registry, IMG_USERS, "tu_merge48.gif");
        registerImage(registry, IMG_FORM_BG, "form_banner.gif");
        registerImage(registry, IMG_HORIZONTAL, "th_horizontal.gif");
        registerImage(registry, IMG_VERTICAL, "th_vertical.gif");
        registerImage(registry, IMG_VIEW_BG, "firsteps_wtr.jpg");
        registerImage(registry, IMG_PENALITY_DESACTIVATE, "search_rem.gif");
        registerImage(registry, IMG_BALISE, "SportIdent_25x25.gif");
        registerImage(registry, IMG_ACTIVATE, "arrow_right.png");
        registerImage(registry, IMG_CHECK_ACTIVATE, "checked.gif");
        registerImage(registry, IMG_CHECK_DESACTIVATE, "unchecked.gif");
        registerImage(registry, IMG_REFRESH, "refresh.gif");
        registerImage(registry, IMG_TOGGLE, "synced.gif");
        registerImage(registry, IMG_PENALITY, "nav_forward.gif");
        registerImage(registry, IMG_REMOVE, "search_remall.gif");
        registerImage(registry, IMG_IMPORT, "buyarrow.gif");
        registerImage(registry, IMG_DOWN, "sellarrow.gif");
        registerImage(registry, IMG_NO_CHRONO, "nav_go2.gif");
        registerImage(registry, IMG_RED_RESULT, "logoRed25x25.gif");
        registerImage(registry, IMG_GREEN_RESULT, "logoGreen25x25.gif");
    }

    @SuppressWarnings("deprecation")
    private void registerImage(final ImageRegistry registry, final String key, final String fileName) {
        try {
            final IPath path = new Path("resources/icons/" + fileName);
            final URL url = find(path);
            if (url != null) {
                final ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
            System.err.println("Image not find " + fileName);
        }
    }
}
