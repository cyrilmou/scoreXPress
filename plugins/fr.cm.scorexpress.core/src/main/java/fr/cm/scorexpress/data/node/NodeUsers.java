package fr.cm.scorexpress.data.node;

public class NodeUsers extends NodeFactory {

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteUsers(this, data);
    }
}
