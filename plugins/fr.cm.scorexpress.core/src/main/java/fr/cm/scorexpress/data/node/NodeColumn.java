package fr.cm.scorexpress.data.node;

public class NodeColumn extends NodeFactory {
    public String _field;
    public String _title;
    public Integer _width = new Integer(70);
    public Integer _align = new Integer(0);
    public String _choice;
    public Boolean _show = true;
    public Boolean _editable = true;
    public String _type = "String";

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteColumn(this, data);
    }
}
