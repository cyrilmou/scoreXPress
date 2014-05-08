/**
 *
 */
package fr.cm.scorexpress.data.node;

public class NodeCategory extends NodeFactory {
    public Integer _id;
    public String _label;
    public Integer _age;
    public String _sexe;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteCategory(this, data);
    }
}
