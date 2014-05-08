package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.MyFormToolkit;
import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.combobox.ComboModel;
import fr.cm.scorexpress.core.model.ObjBalise;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import static fr.cm.common.widget.combobox.ComboBuilder.createCombo;
import static fr.cm.scorexpress.core.model.Balise.*;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

public class BalisePage extends DefaultDetailsPage {
    private IManagedForm mform = null;

    private       ObjBalise          balise     = null;
    private       Text               labelText  = null;
    private       Text               ordre1     = null;
    private       Text               ordre2     = null;
    private       Text               ordre3     = null;
    private       Text               ordre4     = null;
    private final ComboModel<String> comboModel = new ComboModel<String>();
    private Text penality;

    public BalisePage() {
        final String[] typeBalises = {TYPE_PAS_OBLIGATOIRE, TYPE_OBLIGATOIRE, TYPE_BONUS, TYPE_ORDONNEE, TYPE_PENALITY};
        comboModel.setItems(asList(typeBalises));
    }

    @Override
    public void createContents(final Composite parent) {
        final TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);
        final FormToolkit toolkit = mform.getToolkit();
        final MyToolkit myToolkit = new MyFormToolkit(toolkit);
        final Section s1 = toolkit.createSection(parent, Section.TITLE_BAR);
        s1.marginWidth = 10;
        s1.setText(i18n("BalisePage.INFO_BALISES"));
        final TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        s1.setLayoutData(td);
        final Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout(2, false));
        labelText = createText(toolkit, client, i18n("BalisePage.NUMERO"));
        toolkit.createLabel(client, "Type balise");
        createCombo(myToolkit, client, comboModel, SWT.BORDER)
                .withLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
        penality = createText(toolkit, client, "Penalite");
        ordre1 = createText(toolkit, client, i18n("BalisePage.ORDRE1"));
        ordre2 = createText(toolkit, client, i18n("BalisePage.ORDRE2"));
        ordre3 = createText(toolkit, client, i18n("BalisePage.ORDRE3"));
        ordre4 = createText(toolkit, client, i18n("BalisePage.ORDRE4"));
        toolkit.paintBordersFor(s1);
        s1.setClient(client);
    }

    private static Text createText(final FormToolkit toolkit, final Composite client, final String label) {
        toolkit.createLabel(client, label);
        final Text labelText = toolkit.createText(client, EMPTY, SWT.SINGLE | BORDER);
        labelText.setLayoutData(new GridData(FILL_HORIZONTAL | VERTICAL_ALIGN_BEGINNING));
        return labelText;
    }

    @Override
    public void initialize(final IManagedForm form) {
        mform = form;
    }

    @Override
    public void refresh() {
        if (balise != null) {
            labelText.setText(balise.getNum() == null ? EMPTY : balise.getNum());
            String orderProperty = VAR_PREFIX_BALISE_ORDER + 1;
            ordre1.setText(balise.getInfoStr(orderProperty) == null ? EMPTY : balise.getInfoStr(orderProperty));
            orderProperty = VAR_PREFIX_BALISE_ORDER + 2;
            ordre2.setText(balise.getInfoStr(orderProperty) == null ? EMPTY : balise.getInfoStr(orderProperty));
            orderProperty = VAR_PREFIX_BALISE_ORDER + 3;
            ordre3.setText(balise.getInfoStr(orderProperty) == null ? EMPTY : balise.getInfoStr(orderProperty));
            orderProperty = VAR_PREFIX_BALISE_ORDER + 4;
            ordre4.setText(balise.getInfoStr(orderProperty) == null ? EMPTY : balise.getInfoStr(orderProperty));
            comboModel.setText(balise.getType());
            penality.setText(balise.getPenaliteStr());
        }
    }

    @Override
    public void selectionChanged(final IFormPart part, final ISelection selection) {
        commit(true);
        final IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            balise = (ObjBalise) ssel.getFirstElement();
        } else {
            balise = null;
        }
        refresh();
    }

    @Override
    public void commit(final boolean onSave) {
        if (balise != null) {
            balise.setNum(labelText.getText());
            balise.setType(comboModel.getText());
            balise.setInfo(VAR_PREFIX_BALISE_ORDER + 1, ordre1.getText());
            balise.setInfo(VAR_PREFIX_BALISE_ORDER + 2, ordre2.getText());
            balise.setInfo(VAR_PREFIX_BALISE_ORDER + 3, ordre3.getText());
            balise.setInfo(VAR_PREFIX_BALISE_ORDER + 4, ordre4.getText());
            balise.setPenaliteStr(penality.getText());
        }

    }
}
