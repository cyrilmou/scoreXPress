package fr.cm.common.widget.tree;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

public class TreeBuilder<T> extends CommonControlBuilder<Tree, TreeBuilder<T>> {

    TreeBuilder(final MyToolkit toolkit, final Composite parent, final TreeModel<T> model, final int style) {
        super(toolkit, toolkit.createTree(parent, style));

        final ContentViewer viewer = new TreeViewer(control);

        viewer.setContentProvider(
                new ITreeContentProvider() {
                    public Object[] getChildren(final Object element) {
                        return ((TreeNode<?>) element).getChildren().toArray();
                    }

                    public Object getParent(final Object o) {
                        return ((TreeNode<?>) o).getParent();
                    }

                    public boolean hasChildren(final Object o) {
                        return !((TreeNode<?>) o).getChildren().isEmpty();
                    }

                    public Object[] getElements(final Object o) {
                        return model.getTopNodes().toArray();
                    }

                    public void dispose() {

                    }

                    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
                    }
                });
        viewer.setInput(model);
    }
}
