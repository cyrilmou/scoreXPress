package fr.cm.common.widget.composite;

import fr.cm.common.widget.MyToolkit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

public class SectionBuilder extends CommonCompositeBuilder<Section, SectionBuilder> {
    private SectionPart part = null;

    public SectionBuilder(final MyToolkit toolkit, final Composite parent, final int style) {
        super(toolkit, toolkit.createSection(parent, style));
    }

    public SectionBuilder withText(final String text) {
        control.setText(text);
        return this;
    }

    public SectionBuilder setLayoutData(final Object layoutdata) {
        control.setLayoutData(layoutdata);
        return this;
    }

    public SectionBuilder withPaintBorder() {
        toolkit.paintBorderFor(control);
        return this;
    }

    public CompositeBuilder addClient(final int style) {
        final CompositeBuilder builder = addComposite(style);
        control.setClient(builder.getControl());
        return builder;
    }

    public SectionBuilder withDescription(final String description) {
        control.setDescription(description);
        return this;
    }

    public SectionPart getPart() {
        return part;
    }

    public void addPartToForm(final IManagedForm form) {
        part = new SectionPart(control);
        form.addPart(part);
    }
}
