package org.ubimix.ebook.bom.epub;

import org.w3c.dom.Node;
import org.ubimix.commons.uri.Uri;
import org.ubimix.commons.xml.XmlException;
import org.ubimix.commons.xml.XmlWrapper;

/**
 * @author kotelnikov
 */
public class EPubContainer extends EPubXml {

    public static Uri META_INF_CONTAINER_PATH = new Uri(
        "META-INF/container.xml");

    public static EPubContainer newEPubContainer(XmlContext context)
        throws XmlException {
        return context.newXML("odc:container", EPubContainer.class);
    }

    public EPubContainer(Node node, XmlContext context) {
        super(node, context);
    }

    public Uri getContentDeclarationPath() throws XmlException {
        String str = evalStr("/odc:container/odc:rootfiles/odc:rootfile/@full-path");
        Uri ref = new Uri(str);
        return ref;
    }

    public EPubContainer setContentDeclarationPath(Uri path)
        throws XmlException {
        XmlWrapper rootfiles = getOrCreateElement("odc:rootfiles");
        XmlWrapper rootfile = rootfiles.getOrCreateElement("odc:rootfile");
        rootfile.setAttribute("full-path", path + "");
        rootfile.setAttribute("media-type", "application/oebps-package+xml");
        return cast();
    }

}