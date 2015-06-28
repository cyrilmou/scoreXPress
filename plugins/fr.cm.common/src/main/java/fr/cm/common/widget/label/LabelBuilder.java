package fr.cm.common.widget.label;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import static fr.cm.common.widget.SwtUtil.proxySwt;

public class LabelBuilder extends CommonControlBuilder<Label, LabelBuilder> {

    private LabelBuilder(final MyToolkit toolkit, final Composite composite, final LabelModel model, final int style) {
        super(toolkit, toolkit.createLabel(composite, style));
        control.setText(model.getLabel());
        model.addStateListener(proxySwt(new LabelStateListener() {
            @Override
            public void onChanged() {
                if (control.isDisposed()) {
                    return;
                }
                control.setText(model.getLabel());
                control.setToolTipText(model.getTooltip());
            }

            @Override
            public void onEnable(final boolean enable) {
                control.setEnabled(enable);
            }
        }));
    }

    public static LabelBuilder createLabel(final MyToolkit toolkit, final Composite composite, final LabelModel model, final int style) {
        return new LabelBuilder(toolkit, composite, model, style);
    }

    public static StaticLabelBuilder createStaticLabel(final MyToolkit toolkit, final Composite parent, final int style) {
        return new StaticLabelBuilder(toolkit, parent, style);
    }

    public LabelBuilder withFont(final Font font) {
        control.setFont(font);
        return this;
    }

    public LabelBuilder withBackground(final Color color) {
        control.setBackground(color);
        return this;
    }

    public LabelBuilder withForeground(final Color color) {
        control.setForeground(color);
        return this;
    }

    public static class StaticLabelBuilder extends CommonControlBuilder<Label, StaticLabelBuilder> {

        StaticLabelBuilder(final MyToolkit toolkit, final Composite parent, final int style) {
            super(toolkit, toolkit.createLabel(parent, style));
        }

        public StaticLabelBuilder withText(final String text) {
            control.setText(text);
            return this;
        }

        public StaticLabelBuilder withBackground(final Color color) {
            control.setBackground(color);
            return this;
        }
    }
}
