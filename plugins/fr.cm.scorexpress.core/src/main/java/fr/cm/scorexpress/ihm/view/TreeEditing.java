package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeEditing extends KeyAdapter {
    private final TreeEditor editor;
    private final Tree tree;

    public TreeEditing(final Tree tree, final TreeEditor editor) {
        this.tree = tree;
        this.editor = editor;
    }

    public void keyPressed(final KeyEvent event) {
        if (event.keyCode == SWT.F2 && tree.getSelectionCount() == 1) {
            final TreeItem item = tree.getSelection()[0];
            // Create a text field to do the editing
            final GridData gData = new GridData();
            gData.grabExcessHorizontalSpace = true;
            gData.grabExcessVerticalSpace = true;
            final Text text = new Text(tree, SWT.BORDER);
            text.setText(item.getText());
            text.selectAll();
            text.setFocus();
            text.setLayoutData(gData);
            final FocusListener focusLost = new FocusAdapter() {
                public void focusLost(final FocusEvent event) {
                    if (item.getData() instanceof ObjStep) {
                        item.setText(text.getText());
                        ((ObjStep) item.getData()).setLib(text.getText());
                    }
                    text.dispose();
                }
            };
            text.addFocusListener(focusLost);
            text.addKeyListener(new KeyAdapter() {
                public void keyPressed(final KeyEvent event) {
                    switch (event.keyCode) {
                        case SWT.CR:
                            if (item.getData() instanceof ObjStep) {
                                item.setText(text.getText());
                                ((ObjStep) item.getData()).setLib(text.getText());
                            }
                        case SWT.ESC:
                            /* End editing session */
                            text.dispose();
                            break;
                    }
                }
            });
            // Set the text field into the editor
            editor.setEditor(text, item);
            editor.layout();
        }
    }
}
