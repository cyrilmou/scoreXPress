package fr.cm.scorexpress.data.node;

public class NodeChronos extends NodeFactory {

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteChronos(this, data);
    }
}
