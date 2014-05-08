package fr.cm.scorexpress.data.node;

public class NodePenality extends NodeFactory {
    public String _name;
    public Integer _type = 0;
    public String _time;
    public String _timemaxi;
    public String _timemini;
    public String _timescale;
    public Boolean _activate = true;
    public Boolean _penalitebalise = false;
    public Boolean _disqualify = false;
    public Integer _nbbalisemini = 0;
    public Integer _nbbalisepointmini = 0;
    public String _lib;
    public String _unite;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visitePenality(this, data);
    }
}
