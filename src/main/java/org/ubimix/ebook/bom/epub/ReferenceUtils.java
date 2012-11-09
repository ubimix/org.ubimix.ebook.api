package org.ubimix.ebook.bom.epub;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.uri.Uri;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class ReferenceUtils {

    public static Uri resolveLink(Uri docUrl, XmlElement tag, String attrName) {
        String attr = tag.getAttribute(attrName);
        Uri result = null;
        if (attr != null) {
            if (!attr.startsWith("//") && attr.indexOf("://") < 0) {
                result = docUrl.getResolved(attr);
                if (result != null) {
                    tag.setAttribute(attrName, result.toString());
                }
            }
        }
        return result;
    }

    public static void resolveLinks(
        List<XmlElement> result,
        XmlElement tag,
        Uri docUrl,
        String tagName,
        String attrName) {
        boolean add = tagName == null || tagName.equals(tag.getName());
        if (add) {
            Uri path = resolveLink(docUrl, tag, attrName);
            if (path != null) {
                result.add(tag);
            }
        }
        for (XmlNode node : tag) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                resolveLinks(result, e, docUrl, tagName, attrName);
            }
        }
    }

    /**
     * This method resolves all references in the specified document (transforms
     * these references to absolute ones) and returns a list of wrappers
     * corresponding to all resolved links.
     * 
     * @param doc the XML document where links should be resolved
     * @param docUrl the URL of the XML document
     * @param tagName the name of the XML tags containing references
     * @param attrName the name of the XML reference attribute
     * @return a list of all resolved XML tags
     * @throws XmlException
     */
    public static List<XmlElement> resolveLinks(
        XmlElement doc,
        Uri docUrl,
        String tagName,
        String attrName) {
        List<XmlElement> result = new ArrayList<XmlElement>();
        resolveLinks(result, doc, docUrl, tagName, attrName);
        return result;
    }

}
