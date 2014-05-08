package fr.cm.common.widget.tree;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;

public final class TreeNode<T> {
    private final TreeNode<T> parent;
    private volatile T content;
    private final TreeContentSource<T> source;
    private final Supplier<Collection<TreeNode<T>>> children;

    TreeNode(final TreeNode<T> parent, final T content, final TreeContentSource<T> source) {
        this.parent = parent;
        this.content = content;
        this.source = source;
        children = memoize(
                new Supplier<Collection<TreeNode<T>>>() {
                    public Collection<TreeNode<T>> get() {
                        final Collection<TreeNode<T>> kids = newArrayList();
                        addAll(kids, transform(source.getChildContents(content), intoChildNodes()));
                        return kids;
                    }

                    private Function<T, TreeNode<T>> intoChildNodes() {
                        return new Function<T, TreeNode<T>>() {
                            public TreeNode<T> apply(final T content) {
                                return new TreeNode<T>(TreeNode.this, content, source);
                            }
                        };
                    }
                });
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getContent() {
        return content;
    }

    public Collection<TreeNode<T>> getChildren() {
        return unmodifiableCollection(children.get());
    }

    public boolean equals(final Object o) {
        return o == this || o != null && o.getClass() == getClass() && o.hashCode() == hashCode();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(content.getClass())
                .append(source.getHashCodeParts(content).toArray())
                .toHashCode();
    }

    public String toString() {
        return getClass().getSimpleName() + '<' + content + '>';
    }

    TreeNode<T> addChildNode() {
        final TreeNode<T> node = new TreeNode<T>(this, source.addChildContent(content), source);
        children.get().add(node);
        return node;
    }

    void removeChildNode(final TreeNode<T> node) {
        if (children.get().remove(node)) {
            node.removeContent();
        }
    }

    void removeContent() {
        source.removeContent(content);
    }

    void setContent(final T newContent) {
        content = newContent;
        source.replaceContent(newContent);
    }
}
