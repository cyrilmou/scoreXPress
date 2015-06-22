package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import static fr.cm.scorexpress.core.model.impl.ObjStep.VAR_DESCRIPTION;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.layout.GridData.*;

public class EtapeDetailsPage implements IDetailsPage {

    private IManagedForm mform = null;
    private ObjStep      etape = null;
    private Text         label = null;
    private Text   desc;
    private boolean init = true;

    public EtapeDetailsPage() {
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
        final Section s1 = toolkit.createSection(parent, Section.TITLE_BAR);
        s1.marginWidth = 10;
        s1.setText(i18n("EtapeDetailsPage.Info"));
        final TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        s1.setLayoutData(td);
        final Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout(2, false));
        toolkit.createLabel(client, i18n("EtapeDetailsPage.Libelle"));
        label = toolkit.createText(client, EMPTY, SWT.SINGLE | BORDER);
        label.addModifyListener(new LibelleAction());
        final GridData layoutData = new GridData(HORIZONTAL_ALIGN_BEGINNING,
                                                 VERTICAL_ALIGN_BEGINNING,
                                                 true,
                                                 false);
        label.setLayoutData(layoutData);
        toolkit.createLabel(client, i18n("EtapeDetailsPage.decs"));
        desc = toolkit.createText(client, EMPTY, SWT.SINGLE | BORDER);
        desc.addModifyListener(new DescriptionAction());
        layoutData.minimumWidth = 200;
        desc.setLayoutData(layoutData);

        toolkit.paintBordersFor(s1);
        s1.setClient(client);
    }

    @Override
    public void commit(final boolean onSave) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void initialize(final IManagedForm form) {
        mform = form;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isStale() {
        return true;
    }

    @Override
    public void refresh() {
        if (etape != null) {
            init = true;
            label.setText(etape.getLib() == null ? EMPTY : etape.getLib());
            desc.setText(etape.getInfoStr(VAR_DESCRIPTION) == null ?
                                 EMPTY :
                                 etape.getInfoStr(VAR_DESCRIPTION));
            init = false;
        }
    }

    @Override
    public void setFocus() {
    }

    @Override
    public boolean setFormInput(final Object input) {
        return false;
    }

    @Override
    public void selectionChanged(final IFormPart part, final ISelection selection) {
        final IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            etape = (ObjStep) ssel.getFirstElement();
        } else {
            etape = null;
        }
        refresh();
    }

    private class DescriptionAction implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (!init && etape != null) {
                etape.setInfo(VAR_DESCRIPTION, desc.getText());
            }
        }
    }

    private class LibelleAction implements ModifyListener {
        @Override
        public void modifyText(final ModifyEvent e) {
            if (!init && etape != null) {
                etape.setInfo(ObjStep.VAR_LIB_STEP, label.getText());
            }
        }
    }
}
