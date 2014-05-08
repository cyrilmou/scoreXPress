package fr.cm.common.widget.composite;

import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.ScrolledFormBuilder;
import fr.cm.common.widget.StandardToolKit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class CompositeBuilders {
    private CompositeBuilders() {
    }

    public static CompositeBuilder createStandardCompositeBuilder(final Composite container) {
        return createCompositeBuilder(new StandardToolKit(), container, SWT.NONE);
    }

    public static ScrolledFormBuilder createScrollFormBuilder(
            final MyToolkit toolkit, final IManagedForm parent, final FormModel formModel) {
        return new ScrolledFormBuilder(toolkit, parent, formModel);
    }

    public static SectionBuilder createSection(
            final MyToolkit toolkit, final Composite parent, final int style) {
        return new SectionBuilder(toolkit, parent, style);
    }

    public static CompositeBuilder createCompositeBuilder(
            final MyToolkit toolkit, final Composite parent, final int style) {
        return new CompositeBuilder(toolkit, parent, style);
    }
}
