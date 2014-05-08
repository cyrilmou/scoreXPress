package fr.cm.common.widget;

import static org.apache.commons.lang.StringUtils.EMPTY;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class MyFormToolkit implements MyToolkit {
    private final FormToolkit toolkit;

    public MyFormToolkit(final FormToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public Composite createComposite(final Composite parent, final int style) {
        return toolkit.createComposite(parent, style);
    }

    public Button createButton(final Composite parent, final int style) {
        return toolkit.createButton(parent, EMPTY, style);
    }

    public Label createLabel(final Composite parent, final int style) {
        return toolkit.createLabel(parent, EMPTY, style);
    }

    public Table createTable(final Composite parent, final int style) {
        return toolkit.createTable(parent, style);
    }

    public StyledText createText(final Composite control, final int style) {
        final StyledText text = new StyledText(control, style);
        toolkit.adapt(text);
        return text;
    }

    public Section createSection(final Composite parent, final int style) {
        return toolkit.createSection(parent, style);
    }

    public void paintBorderFor(final Composite parent) {
        toolkit.paintBordersFor(parent);
    }

    public CCombo createComboBox(final Composite parent, final int style) {
        final CCombo cCombo = new CCombo(parent, style);
        toolkit.adapt(cCombo);
        return cCombo;
    }

    public Tree createTree(final Composite parent, final int style) {
        return toolkit.createTree(parent, style);
    }
}
