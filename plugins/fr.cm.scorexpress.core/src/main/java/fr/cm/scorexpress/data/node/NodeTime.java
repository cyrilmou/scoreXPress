package fr.cm.scorexpress.data.node;

public class NodeTime extends NodeFactory {
    public String _id;
    public String _time;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteTime(this, data);
    }
}
