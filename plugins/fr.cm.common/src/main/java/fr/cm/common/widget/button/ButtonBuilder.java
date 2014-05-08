package fr.cm.common.widget.button;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import static fr.cm.common.widget.SwtUtil.proxySwt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ButtonBuilder extends CommonControlBuilder<Button, ButtonBuilder> {

    ButtonBuilder(final MyToolkit toolkit, final Composite parent, final ButtonModel model, final int style) {
        super(toolkit, toolkit.createButton(parent, style));

        final SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                model.setSelection(control.getSelection());
                model.click();
            }
        };

        model.addStateListener(
                proxySwt(
                        new ButtonStateListener() {
                            @Override
                            public void onLabelChanged() {
                                control.setText(model.getLabel());
                            }

                            @Override
                            public void onSelect() {
                                control.removeSelectionListener(selectionListener);
                                control.setSelection(model.isSelected());
                                control.addSelectionListener(selectionListener);
                            }

                            @Override
                            public void onEnable(final boolean enable) {
                                control.setEnabled(enable);
                            }
                        }));

        control.setEnabled(model.isEnable());
        control.setText(model.getLabel());
        control.setSelection(model.isSelected());
        control.addSelectionListener(selectionListener);
    }

    public static Button createButton(
            final FormToolkit toolkit,
            final Composite parent,
            final String label,
            final SelectionListener action,
            final int layoutData) {
        final Button addButton = toolkit.createButton(parent, label, SWT.PUSH);
        addButton.addSelectionListener(action);
        addButton.setLayoutData(new GridData(layoutData));
        return addButton;
    }

    public static ButtonBuilder createButton(
            final MyToolkit toolkit, final Composite parent, final ButtonModel model, final int style) {
        return new ButtonBuilder(toolkit, parent, model, SWT.PUSH | style);
    }

    public static ButtonBuilder createCheckbox(
            final MyToolkit toolkit, final Composite parent, final ButtonModel model, final int style) {
        return new ButtonBuilder(toolkit, parent, model, SWT.CHECK | style);
    }

    public ButtonBuilder withImage(final Image image) {
        control.setImage(image);
        return this;
    }

    public ButtonBuilder withToolTip(final String toolTip) {
        control.setToolTipText(toolTip);
        return this;
    }

    private static class MySelectionAdapter extends SelectionAdapter {
        private final ButtonModel model;

        private MySelectionAdapter(final ButtonModel model) {
            this.model = model;
        }

        @Override
        public void widgetSelected(final SelectionEvent selectionEvent) {
            model.click();
        }
    }
}
