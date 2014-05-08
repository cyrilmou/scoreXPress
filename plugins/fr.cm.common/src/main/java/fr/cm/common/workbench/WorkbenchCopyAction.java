package fr.cm.common.workbench;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import static org.eclipse.ui.actions.ActionFactory.COPY;

public class WorkbenchCopyAction extends Action {
    private final Clipboard clipboard;

    public WorkbenchCopyAction(final Display display) {
        this.clipboard = new Clipboard(display);
        setId(COPY.getId());
    }

    public void runWithEvent(final Event event) {
        final StringBuffer datas = new StringBuffer();
        if (event.widget instanceof Table) {
            final Table table = (Table) event.widget;
            for (int j = 0; j < table.getColumnCount(); j++) {
                if (table.getColumn(j).getWidth() > 0) {
                    datas.append(table.getColumn(j).getText()).append('\t');
                }
            }
            datas.append('\n');
            final Item[] items = table.getSelection();
            for (final Item item : items) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    if (table.getColumn(j).getWidth() > 0) {
                        datas.append(((TableItem) item).getText(j)).append('\t');
                    }
                }
                datas.append('\n');
            }
        } else if (event.widget instanceof Text) {
            datas.append(((Text) event.widget).getSelectionText());
        } else if (event.widget instanceof StyledText) {
            datas.append(((StyledText) event.widget).getSelectionText());
        } else if (event.widget instanceof Combo) {
            ((Combo) event.widget).copy();
        } else if (event.widget instanceof Tree) {
            final Tree tree = (Tree) event.widget;
            for (final TreeItem treeItem : tree.getSelection()) {
                datas.append(treeItem.getText());
                datas.append('\n');
            }
        }
        if (datas.length() > 0) {
            clipboard.setContents(new Object[]{datas.toString()}, new Transfer[]{TextTransfer.getInstance()});
        }
    }
}
