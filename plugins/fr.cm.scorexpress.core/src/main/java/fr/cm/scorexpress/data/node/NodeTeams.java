package fr.cm.scorexpress.data.node;

public class NodeTeams extends NodeFactory {
    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteTeams(this, data);
    }
}
