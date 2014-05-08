package fr.cm.common.widget;

import fr.cm.common.widget.composite.CommonCompositeBuilder;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.common.widget.composite.SectionBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ScrolledFormBuilder extends CommonCompositeBuilder<Composite, ScrolledFormBuilder> {
    private final ScrolledForm form;

    public ScrolledFormBuilder(final MyToolkit toolkit, final IManagedForm parent, final FormModel model) {
        super(toolkit, parent.getForm().getBody());
        form = parent.getForm();
        form.setText(model.getText());
    }

    public ScrolledFormBuilder withBackgroungImage(final Image image) {
        form.setBackgroundImage(image);
        return this;
    }

    public SectionBuilder addSection() {
        return new SectionBuilder(toolkit, control, SWT.NONE);
    }
}
