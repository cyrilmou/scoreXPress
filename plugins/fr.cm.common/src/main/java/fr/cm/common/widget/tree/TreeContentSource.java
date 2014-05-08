package fr.cm.common.widget.tree;

import java.util.Collection;

public interface TreeContentSource<T> {
    Iterable<? extends T> getTopContents();

    Iterable<? extends T> getChildContents(final T parentContent);

    Collection<?> getHashCodeParts(final T content);

    //optional
    T addChildContent(final T parentContent);

    //optional
    void removeContent(final T content);

    //optional
    void replaceContent(final T newContent);
}
