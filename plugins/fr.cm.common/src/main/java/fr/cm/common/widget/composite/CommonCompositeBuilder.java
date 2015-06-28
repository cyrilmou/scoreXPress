package fr.cm.common.widget.composite;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import fr.cm.common.widget.button.ButtonBuilder;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.combobox.ComboBuilder;
import fr.cm.common.widget.combobox.ComboModel;
import fr.cm.common.widget.label.LabelBuilder;
import fr.cm.common.widget.label.LabelBuilder.StaticLabelBuilder;
import fr.cm.common.widget.label.LabelModel;
import fr.cm.common.widget.table.TableBuilder;
import fr.cm.common.widget.table.TableModel;
import fr.cm.common.widget.text.TextBuilder;
import fr.cm.common.widget.text.TextModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import java.util.ArrayList;
import java.util.Collection;

import static fr.cm.common.widget.button.ButtonBuilder.createButton;
import static fr.cm.common.widget.button.ButtonBuilder.createCheckbox;
import static fr.cm.common.widget.combobox.ComboBuilder.createCombo;
import static fr.cm.common.widget.composite.CompositeBuilders.createCompositeBuilder;
import static fr.cm.common.widget.table.TableBuilder.createTable;

public abstract class CommonCompositeBuilder<C extends Composite, B extends AbstractCompositeBuilder<B>>
        extends CommonControlBuilder<C, B> implements AbstractCompositeBuilder<B> {

    final Collection<CommonControlBuilder> builders = new ArrayList<CommonControlBuilder>();

    protected CommonCompositeBuilder(final MyToolkit toolkit, final C control) {
        super(toolkit, control);
    }

    @Override
    @SuppressWarnings({"ClassReferencesSubclass"})
    public CompositeBuilder addComposite(final int style) {
        return addBuilder(createCompositeBuilder(toolkit, control, style));
    }

    private <T extends CommonControlBuilder>  T addBuilder(final T compositeBuilder) {
        builders.add(compositeBuilder);
        return compositeBuilder;
    }

    @Override
    public ButtonBuilder addButton(final ButtonModel model, final int style) {
        return addBuilder(createButton(toolkit, control, model, style));
    }

    public ButtonBuilder addCheckbox(final ButtonModel model, final int style) {
        return createCheckbox(toolkit, control, model, style);
    }

    public <T> ComboBuilder<T> addCombo(final ComboModel<T> model, final int style) {
        return addBuilder(createCombo(toolkit, control, model, style));
    }

    public <T> TableBuilder<T> addTable(final TableModel<T> model, final int style) {
        return addBuilder(createTable(toolkit, control, model, style));
    }

    @Override
    public C getControl() {
        return control;
    }

    public B withLayout(final Layout layout) {
        control.setLayout(layout);
        return (B) this;
    }

    public <T> TextBuilder<T> addText(final TextModel<T> model, final int style) {
        return addBuilder(TextBuilder.createText(toolkit, control, model, style));
    }

    public LabelBuilder addLabel(final LabelModel model) {
        return addBuilder(LabelBuilder.createLabel(toolkit, control, model, SWT.NONE));
    }

    public LabelBuilder addLabel(final LabelModel model, final int style) {
        return addBuilder(LabelBuilder.createLabel(toolkit, control, model, style));
    }

    public StaticLabelBuilder addStaticLabel(final int style) {
        return addBuilder(LabelBuilder.createStaticLabel(toolkit, control, style));
    }

    public B withBackground(final Color color) {
        control.setBackground(color);
        return (B) this;
    }

    public B withPaintBorder() {
        toolkit.paintBorderFor(control);
        return (B) this;
    }

    @Override
    public void dispose() {
        for (final CommonControlBuilder builder : builders) {
            builder.dispose();
        }
        builders.clear();
    }
}
