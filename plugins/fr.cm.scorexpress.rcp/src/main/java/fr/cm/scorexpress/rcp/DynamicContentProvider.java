package fr.cm.scorexpress.rcp;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;
import org.eclipse.ui.intro.config.IIntroXHTMLContentProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.PrintWriter;
import java.util.Date;

public class DynamicContentProvider implements IIntroXHTMLContentProvider {


    public void init(final IIntroContentProviderSite site) {
    }


    public void createContent(final String id, final PrintWriter out) {
    }

    public void createContent(final String id, final Composite parent, final FormToolkit toolkit) {
    }

    private String getCurrentTimeString() {
        final StringBuffer content = new StringBuffer("Dynamic content from Intro ContentProvider: ");
        content.append("Current time is: ");
        content.append(new Date(System.currentTimeMillis()));
        return content.toString();
    }

    public void createContent(final String id, final Element parent) {
        final Document dom = parent.getOwnerDocument();
        final Element para = dom.createElement("p");
        para.setAttribute("id", "someDynamicContentId");
        para.appendChild(dom.createTextNode(getCurrentTimeString()));
        parent.appendChild(para);
    }

    public void dispose() {

    }
}
