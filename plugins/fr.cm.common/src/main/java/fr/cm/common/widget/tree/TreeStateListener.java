package fr.cm.common.widget.tree;

import fr.cm.common.widget.StateListener;
import java.util.Collection;

public interface TreeStateListener<T> extends StateListener {
    void onNodesAdded(TreeModel<T> tTreeModel, Collection<TreeNode<T>> nodes);

    void onNodeAdded(TreeNode<T> parentNode, TreeNode<T> childNode);

    void onNodesRemoved(Collection<TreeNode<T>> nodes);

    void onFilterChange();

    void onNodeUpdated(TreeNode<T> node);
}
