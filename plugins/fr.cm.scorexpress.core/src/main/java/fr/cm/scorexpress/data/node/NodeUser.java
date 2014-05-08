package fr.cm.scorexpress.data.node;

public class NodeUser extends NodeFactory {
    public Integer _id;
    public String _firstname;
    public String _lastname;
    public Integer _categoryid;
    public String _birthday;
    public String _sexe;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteUser(this, data);
    }
}
