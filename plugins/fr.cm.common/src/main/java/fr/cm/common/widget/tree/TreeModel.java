package fr.cm.common.widget.tree;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import fr.cm.common.widget.CommonModel;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;

public class TreeModel<T> extends CommonModel<TreeStateListener<T>, TreeListener> {
    private final Collection<TreeNode<T>> topNodes = newArrayList();
    private final Predicate<TreeNode<T>> filter = alwaysTrue();

    //top nodes management

    public Collection<TreeNode<T>> getTopNodes() {
        return unmodifiableCollection(topNodes);
    }

    public void addTopNodes(final TreeContentSource<T> source) {
        final Collection<TreeNode<T>> nodes = copyOf(transform(source.getTopContents(), intoNodes(source)));
        topNodes.addAll(nodes);
        for (final TreeStateListener<T> listener : getStateListeners()) {
            listener.onNodesAdded(this, nodes);
        }
    }

    public void addChildNode(final TreeNode<T> parentNode) {
        final TreeNode<T> childNode = parentNode.addChildNode();
        for (final TreeStateListener<T> listener : getStateListeners()) {
            listener.onNodeAdded(parentNode, childNode);
        }
        selectNode(childNode);
    }

    private void selectNode(final TreeNode<T> node) {
        throw new NoSuchMethodError();
    }

    public void removeTopNodes() {
        if (topNodes.isEmpty()) {
            return;
        }
        final Collection<TreeNode<T>> nodes = copyOf(topNodes);
        topNodes.clear();
        for (final TreeStateListener<T> listener : getStateListeners()) {
            listener.onNodesRemoved(nodes);
        }
    }

    //filter management

    public Predicate<TreeNode<T>> getFilter() {
        return filter;
    }

    public void refilter() {
        for (final TreeStateListener<T> listener : getStateListeners()) {
            listener.onFilterChange();
        }
    }

    public void updateIf(final Predicate<T> predicate) {
        doUpdateIf(topNodes, predicate);
    }

    private void doUpdateIf(final Iterable<TreeNode<T>> nodes, final Predicate<T> predicate) {
        for (final TreeNode<T> node : nodes) {
            if (predicate.apply(node.getContent())) {
                for (final TreeStateListener<T> listener : getStateListeners()) {
                    listener.onNodeUpdated(node);
                }
            }
            doUpdateIf(node.getChildren(), predicate);
        }
    }

    private static <T> Function<T, TreeNode<T>> intoNodes(final TreeContentSource<T> source) {
        return new Function<T, TreeNode<T>>() {
            public TreeNode<T> apply(final T from) {
                return new TreeNode<T>(null, from, source);
            }
        };
    }
}
