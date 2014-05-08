package fr.cm.scorexpress.data.node;

public class NodeTeam extends NodeFactory {
    public Integer _userId = 0;
    public String _name;

    public String _leadername;
    public Integer _categoryId = 0;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteTeam(this, data);
    }
}
