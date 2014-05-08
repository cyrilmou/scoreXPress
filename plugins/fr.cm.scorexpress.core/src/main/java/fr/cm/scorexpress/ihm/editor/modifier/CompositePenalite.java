package fr.cm.scorexpress.ihm.editor.modifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class CompositePenalite extends Group {
    private static final String GESTION_DES_PENALITEES = "Gestion des pénalitées";

    public CompositePenalite(final Composite parent, final int style) {
        super(parent, style);
        initialize();
        super.setText(GESTION_DES_PENALITEES);
    }

    private void initialize() {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.horizontalAlignment = GridData.CENTER;
        final CLabel cLabelTitre = new CLabel(this, SWT.NONE);
        cLabelTitre.setText(GESTION_DES_PENALITEES);
        cLabelTitre.setLayoutData(gridData);
        setLayout(new GridLayout());
        setSize(new Point(478, 303));
    }
}
