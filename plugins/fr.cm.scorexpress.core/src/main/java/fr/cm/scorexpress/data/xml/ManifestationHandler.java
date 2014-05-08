package fr.cm.scorexpress.data.xml;

import fr.cm.scorexpress.data.node.NodeException;
import fr.cm.scorexpress.data.node.NodeFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collection;

public class ManifestationHandler extends DefaultHandler {
    private NodeFactory root;
    private NodeFactory current;
    private final Collection<String> warnings;

    private int level = 0;

    public ManifestationHandler() {
        warnings = new ArrayList<String>();
    }

    public NodeFactory getRoot() {
        return root;
    }

    public void startElement(final String uri, final String localName, final String qName,
                             final Attributes attributes) throws SAXException {
        level++;
        final NodeFactory comp = NodeFactory.create(qName, attributes);
        if (comp != null) {
            comp.setLevel(level);
            if (root == null) {
                root = comp;
                current = comp;
            } else {
                comp.setParent(current);
                current.addChild(comp);
                current = comp;
            }
        } else {
            if (root == null) {
                throw new NodeException("Ceci n est pas un fichier XML valide");
            }
            addWarning("ATTENTION : Balise \"" + qName + "\" est non traitée ");
        }
    }

    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException {
        if (current != null) {
            if (current.getLevel() == level && level > 1)
                current = current.getParent();
        }
        level--;
    }

    private void addWarning(final String warning) {
        warnings.add(warning);
    }
}
