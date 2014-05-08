package fr.cm.scorexpress.ihm.wizard;

import static org.apache.commons.lang.StringUtils.EMPTY;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import static java.lang.Integer.parseInt;

public class AjoutDossardsWizardPage extends WizardPage {

    private Text textSerieName = null;
    private int nb = 1;

    protected AjoutDossardsWizardPage(final String pageName) {
        super(pageName);
    }

    protected AjoutDossardsWizardPage(final String pageName, final String title, final ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }

    public void createControl(final Composite parent) {
        try {
            setControl(parent);
            final GridData gridData1 = new GridData();
            gridData1.grabExcessVerticalSpace = false;
            gridData1.verticalAlignment = GridData.CENTER;
            gridData1.horizontalAlignment = GridData.BEGINNING;
            gridData1.widthHint = 150;
            final GridData gridData = new GridData();
            gridData.grabExcessVerticalSpace = false;
            gridData.verticalAlignment = GridData.FILL;
            gridData.horizontalAlignment = GridData.BEGINNING;
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            final Composite top = new Composite(parent, SWT.NONE);
            top.setLayout(gridLayout);
            final CLabel cLabelLabel = new CLabel(top, SWT.NONE);
            cLabelLabel.setText("Nombre de dossard :");
            cLabelLabel.setLayoutData(gridData);
            textSerieName = new Text(top, SWT.BORDER);
            textSerieName.setLayoutData(gridData1);
            textSerieName.setText(nb + EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNb() {
        nb = parseInt(textSerieName.getText());
        return nb;
    }

    public void setNb(final int nb) {
        this.nb = nb;
        textSerieName.setText(nb + EMPTY);
    }
}
