package fr.cm.scorexpress.data.node;

import static fr.cm.scorexpress.data.node.Node.DOCUMENT.DOCUMENT_NODE;

public class NodeManif extends NodeFactory {

    public String _name;
    public String _date;
    public String _description;

    public NodeManif() {
        typeNode = DOCUMENT_NODE;
    }

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteManif(this, data);
    }
}
