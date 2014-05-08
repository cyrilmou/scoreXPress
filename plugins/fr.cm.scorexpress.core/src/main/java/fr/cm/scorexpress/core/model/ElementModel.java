package fr.cm.scorexpress.core.model;

public interface ElementModel {

    Object accept(final ElementModelVisitor visitor, final Object data);

}
