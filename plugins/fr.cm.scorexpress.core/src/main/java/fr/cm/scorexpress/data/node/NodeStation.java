package fr.cm.scorexpress.data.node;

public class NodeStation extends NodeFactory {
    public String _id;
    public String _definition;
    public Integer _symbol1;
    public Integer _symbol2;
    public Integer _symbol3;
    public Integer _symbol4;
    public Integer _symbol5;
    public Integer _symbol6;
    public String _type;
    public String _time;
    public Integer _order1;
    public Integer _order2;
    public Integer _order3;
    public Integer _order4;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteStation(this, data);
    }
}
