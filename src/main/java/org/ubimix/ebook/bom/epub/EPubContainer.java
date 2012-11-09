package org.ubimix.ebook.bom.epub;

import java.util.Map;

import org.ubimix.commons.uri.Uri;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class EPubContainer extends EPubXml {

    public static Uri META_INF_CONTAINER_PATH = new Uri(
        "META-INF/container.xml");

    public EPubContainer() {
        super("odc:container");
    }

    public EPubContainer(IHasValueMap object) {
        super(object);
    }

    public EPubContainer(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    public Uri getContentDeclarationPath() {
        XmlElement e = getChildByPath(
            "odc:container",
            "odc:rootfiles",
            "odc:rootfile");
        String str = e != null ? e.getAttribute("full-path") : null;
        Uri ref = new Uri(str);
        return ref;
    }

    public EPubContainer setContentDeclarationPath(Uri path) {
        XmlElement rootfiles = getOrCreateElement("odc:rootfiles");
        XmlElement rootfile = rootfiles.getOrCreateElement("odc:rootfile");
        rootfile.setAttribute("full-path", path + "");
        rootfile.setAttribute("media-type", "application/oebps-package+xml");
        return cast();
    }

}