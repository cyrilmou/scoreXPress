package fr.cm.common.widget.text;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class TextBuilder<T> extends CommonControlBuilder<StyledText, TextBuilder<T>> {
    private final TextViewer           viewer;
    private final ITextListener        textListener;
    private final TextStateListener<T> textStateListener;
    private final TextModel<T> model;
    private boolean updateInProgress = false;

    TextBuilder(final MyToolkit toolkit, final Composite control, final TextModel<T> model, final int style) {
        super(toolkit, toolkit.createText(control, style));
        this.model = model;
        viewer = new TextViewer(control, style) {
            @Override
            protected StyledText createTextWidget(final Composite parent, final int styles) {
                return TextBuilder.this.control;
            }
        };
        viewer.setDocument(new Document(model.getText()));

        textListener = new ITextListener() {
            @Override
            public void textChanged(final TextEvent event) {
                model.textChanged(viewer.getDocument().get());
            }
        };
        viewer.addTextListener(textListener);
        viewer.getControl().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
                model.focusGained();
            }

            @Override
            public void focusLost(final FocusEvent e) {
                model.focusLost();
            }
        });

        textStateListener = new TextStateListener<T>() {
            @Override
            public void onTextChange(final String text) {
                if (!updateInProgress) {
                    updateInProgress = true;
                    viewer.getDocument().set(text);
                    updateInProgress = false;
                }
            }

            public void setVisible(boolean visible) {
                control.setVisible(visible);
            }

        };
        model.addStateListener(textStateListener);
    }

    public TextBuilder withWidth(int width) {
        control.setSize(width, control.getSize().y);
        return this;
    }

    public static Text createText(final FormToolkit toolkit, final Composite client, final String label,
                                  final ModifyListener action, final int layoutData) {
        toolkit.createLabel(client, label);
        final Text text = toolkit.createText(client, EMPTY, SWT.SINGLE | SWT.BORDER);
        text.addModifyListener(action);
        text.setLayoutData(new GridData(layoutData));
        return text;
    }

    public static <T> TextBuilder<T> createText(final MyToolkit toolkit, final Composite control,
                                                final TextModel<T> model, final int style) {
        return new TextBuilder<T>(toolkit, control, model, style);
    }

    @Override
    public TextBuilder<T> withLayoutData(final Object layoutData) {
        viewer.getControl().setLayoutData(layoutData);
        return this;
    }

    @Override
    public void dispose() {
        model.removeStateListener(textStateListener);
        viewer.removeTextListener(textListener);
    }

    public TextBuilder<T> withBackground(final Color color) {
        control.setBackground(color);
        return this;
    }
}
