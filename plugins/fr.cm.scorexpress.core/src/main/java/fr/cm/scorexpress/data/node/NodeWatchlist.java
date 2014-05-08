package fr.cm.scorexpress.data.node;

public class NodeWatchlist extends NodeFactory {
    public Integer _id;
    public String _title;
    public String _name;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteWatchList(this, data);
    }
}
