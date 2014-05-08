package fr.cm.scorexpress.data.node;

public class NodeChrono extends NodeFactory {
    public String _number;
    public String _puce;
    public String _start_time;
    public String _end_time;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteChrono(this, data);
    }
}
