package fr.cm.scorexpress.data.node;

public class NodeInscribe extends NodeFactory {
    public String _number;
    public String _puce;
    public Integer _userId;
    public String _penality;
    public String _bonification;
    public Boolean _disqualify = false;
    public Boolean _abandon = false;
    public Boolean _team = false;
    public String _categorie;
    public String _temps;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteInscribe(this, data);
    }
}
