package fr.cm.common.widget.composite;

import fr.cm.common.widget.MyToolkit;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings({"ClassTooDeepInInheritanceTree"})
public class CompositeBuilder extends CommonCompositeBuilder<Composite, CompositeBuilder> {

    CompositeBuilder(final MyToolkit toolkit, final Composite parent, final int style) {
        super(toolkit, toolkit.createComposite(parent, style));
    }
}
