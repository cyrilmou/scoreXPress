/**
 *
 */
package fr.cm.scorexpress.data.node;

import fr.cm.scorexpress.core.model.impl.Date2;
import fr.cm.scorexpress.core.model.impl.DateFactory;
import org.xml.sax.Attributes;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cyril
 * @date 22 avr. 08
 */
public class NodeFactory implements Node {
    protected    Node.DOCUMENT typeNode       = DOCUMENT.ELEMENT_NODE;
    static final char          TYPE_SEPARATOR = '.';

    protected HashMap<String, Object> infos;
    protected NodeFactory             parent;
    protected ArrayList<NodeFactory>  children;
    private int level = 0;

    protected void initializedAttributes(final Attributes attributes) {
        for (int index = 0; index < attributes.getLength(); index++) {
            final String qName = attributes.getQName(index);
            final String valeur = attributes.getValue(index);
            if (qName.indexOf(TYPE_SEPARATOR) == -1) {
                final String field = '_' + qName;
                try {
                    final Field attrib = getClass().getDeclaredField(field);
                    if (attrib.getType().getName().equals(String.class.getName())) {
                        attrib.set(this, valeur);
                    } else if (attrib.getType().getName().equals(Integer.class.getName())) {
                        attrib.set(this, new Integer(valeur));
                    } else if (attrib.getType().getName().equals(Boolean.class.getName())) {
                        attrib.set(this, Boolean.valueOf(valeur));
                    } else if (attrib.getType().getName().equals(Date2.class.getName())) {
                        attrib.set(this, DateFactory.createDate(valeur));
                    }
                } catch (IllegalArgumentException e) {
                } catch (SecurityException e) {
                } catch (IllegalAccessException e) {
                } catch (NoSuchFieldException e) {
                    System.err.println(field + " not found in " + getClass().getName());
                }
            } else {
                setInfo(qName, valeur);
            }
        }
    }

    protected void setInfo(String attribute, final String valeur) {
        try {
            if (valeur == null || valeur.equals("")) {
                return;
            }
            attribute = attribute.toUpperCase();
            final int index = attribute.indexOf(TYPE_SEPARATOR);
            if (infos == null) {
                infos = new HashMap<String, Object>();
            }
            if (index == -1) {
                infos.put(attribute, valeur);
            } else {
                final String type = attribute.substring(0, index);
                if (type.equals("DATE")) {
                    final Date2 date = DateFactory.createDate(valeur);
                    infos.put(attribute, date);
                } else if (type.equals("INT")) {
                    infos.put(attribute, new Integer(valeur));
                } else if (type.equals("B")) {
                    infos.put(attribute, new Boolean(valeur));
                } else if (type.equals("STR") || type.equals("S")) {
                    attribute = "S" + TYPE_SEPARATOR + attribute.substring(index + 1, attribute.length());
                    infos.put(attribute, valeur);
                } else if (type.equals("MAIL")) {
                    infos.put(attribute, valeur);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteNode(this, data);
    }

    @Override
    public void addChild(final Node n) {
        if (children == null) {
            children = new ArrayList<NodeFactory>();
        }
        children.add((NodeFactory) n);
    }

    @Override
    public Node getChild(final int n) {
        if (children == null || n >= children.size()) {
            return null;
        }
        return children.get(n);
    }

    @Override
    public int getNumChild() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    @Override
    public void setParent(final Node n) {
        parent = (NodeFactory) n;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    @SuppressWarnings("unchecked")
    public static NodeFactory create(final String name, final Attributes attributes) {
        final String className = NodeFactory.class.getPackage().getName() + ".Node" + name;
        try {
            final Class classe = Class.forName(className);
            final Constructor<NodeFactory> constructor = classe.getConstructor();
            final NodeFactory nodeFactory = constructor.newInstance();
            nodeFactory.initializedAttributes(attributes);
            return nodeFactory;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("Class " + className + " constructor not found");
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + className + " not found");
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        System.err.println("tag " + name + " not found");
        return null;
    }

    public NodeFactory getParent() {
        return parent;
    }

    public Map<String, Object> getInfos() {
        return infos;
    }

}
