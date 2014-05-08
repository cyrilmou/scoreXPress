package fr.cm.scorexpress.ihm.editor.page;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.IMG_FORM_BG;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

public class ParametrageStepPage extends FormPage {
    private static final String PARAMETRAGE_STEP_PAGE = "fr.cm.scorexpress.ParametrageStepPage";
    private final MasterDetailsBlock       block;
    private final ParametrageStepPageModel model;

    public ParametrageStepPage(final ParametrageStepPageModel parametrageStepPageModel) {
        super(PARAMETRAGE_STEP_PAGE, i18n("StepEditor.Parametrage"));
        model = parametrageStepPageModel;
        block = new StepDetailsBlockPage(parametrageStepPageModel);
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText(i18n("StepEditor.ParametrageFormText"));
        form.setBackgroundImage(getImg(IMG_FORM_BG));
        block.createContent(managedForm);
    }

    @Override
    public void dispose() {
        model.dispose();
        super.dispose();
    }

    @Override
    public boolean isDirty() {
        return model.isDirty();
    }
}
