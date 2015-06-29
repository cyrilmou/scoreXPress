package fr.cm.common.widget;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;

public class StandardToolKit implements MyToolkit {
    public Composite createComposite(final Composite parent, final int style) {
        return new Composite(parent, style);
    }

    public Button createButton(final Composite parent, final int style) {
        return new Button(parent, style);
    }

    public Label createLabel(final Composite parent, final int style) {
        return new Label(parent, style);
    }

    public Table createTable(final Composite parent, final int style) {
        return new Table(parent, style);
    }

    public StyledText createText(final Composite control, final int style) {
        return new StyledText(control, style);
    }

    public Section createSection(final Composite parent, final int style) {
        throw new RuntimeException("Not supported");
    }

    public void paintBorderFor(final Composite parent) {
        throw new RuntimeException("Not supported");
    }

    public CCombo createComboBox(final Composite parent, final int style) {
        return new CCombo(parent, style);
    }

    public Tree createTree(final Composite parent, final int style) {
        return new Tree(parent, style);
    }
}
