package fr.cm.common.widget.composite;

import fr.cm.common.widget.ControlBuilder;
import fr.cm.common.widget.button.ButtonBuilder;
import fr.cm.common.widget.button.ButtonModel;
import org.eclipse.swt.widgets.Composite;

public interface AbstractCompositeBuilder<B extends AbstractCompositeBuilder<B>> extends ControlBuilder<B> {

    @SuppressWarnings({"ClassReferencesSubclass"})
    CompositeBuilder addComposite(final int style);

    ButtonBuilder addButton(final ButtonModel model, int i);

    Composite getControl();
}
