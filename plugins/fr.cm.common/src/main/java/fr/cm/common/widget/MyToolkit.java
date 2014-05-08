package fr.cm.common.widget;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;

public interface MyToolkit {

    Composite createComposite(Composite parent, int style);

    Button createButton(Composite parent, int style);

    Label createLabel(Composite parent, int style);

    Table createTable(Composite parent, int style);

    StyledText createText(Composite control, int style);

    Section createSection(Composite parent, int style);

    void paintBorderFor(Composite parent);

    CCombo createComboBox(Composite parent, int style);

    Tree createTree(Composite parent, int style);
}
