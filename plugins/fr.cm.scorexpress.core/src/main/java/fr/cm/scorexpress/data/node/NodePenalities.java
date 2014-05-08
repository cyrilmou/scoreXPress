package fr.cm.scorexpress.data.node;

public class NodePenalities extends NodeFactory {

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visitePenalities(this, data);
    }

}
