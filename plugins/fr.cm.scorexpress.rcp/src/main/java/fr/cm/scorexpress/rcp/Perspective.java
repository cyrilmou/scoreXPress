package fr.cm.scorexpress.rcp;

import static fr.cm.scorexpress.ihm.view.NavigateurView.NAVIGATOR_VIEW_ID;
import org.eclipse.ui.IPageLayout;
import static org.eclipse.ui.IPageLayout.LEFT;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
    static final String PERSPECTIVE_ID = "fr.cm.scorexpress.rcp.perspective";

    public void createInitialLayout(final IPageLayout layout) {
        layout.setFixed(false);
        layout.setEditorAreaVisible(true);
        layout.addView(NAVIGATOR_VIEW_ID, LEFT, .4f, layout.getEditorArea());
    }
}
