package fr.cm.scorexpress.ihm.wizard;

import fr.cm.scorexpress.core.model.IUsers;
import fr.cm.scorexpress.core.model.ObjUser;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.getImageDescriptor;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

public class AjoutInscribsWizard extends Wizard {
    private AjoutDossardsWizardPage page      = null;
    private IUsers                  dossards  = null;
    private ImageDescriptor         fondImage = null;

    public AjoutInscribsWizard(final IUsers dossards) {
        setWindowTitle("Ajout des dossards");
        this.dossards = dossards;
        fondImage = getImageDescriptor("/icons/boussole_wizard.jpg");
    }

    @Override
    public void addPages() {
        super.addPages();
        page = new AjoutDossardsWizardPage("Ajout de dossards", i18n("AJOUT_DOSSARD_WIDGET"), fondImage);
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        if (!page.isPageComplete()) {
            return false;
        }
        final int nb = page.getNb();
        if (nb > 0) {
            for (int i = 0; i < nb; i++) {
                dossards.addUser(new ObjUser());
            }
            return true;
        }
        return false;
    }

}
