/**
 * 
 */
package org.webreformatter.ebook.bom.epub;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.ebook.bom.IBookSection;

/**
 * @author kotelnikov
 */
public class EPubSection extends EPubXml implements IBookSection {

    public static EPubSection newSection(XmlContext xmlContext)
        throws XmlException {
        EPubSection page = xmlContext.newXML("xhtml:html", EPubSection.class);
        page.appendElement("xhtml:head");
        page.appendElement("xhtml:body");
        return page;
    }

    private EPubXml fContent;

    /**
     * @param node
     * @param context
     */
    public EPubSection(Node node, XmlContext context) {
        super(node, context);
    }

    public String getContent() {
        try {
            EPubXml xml = getContentElement();
            XmlWrapper div = xml.newCopy("xhtml:div");
            return div.toString();
        } catch (Throwable t) {
            throw onError("Can not create a copy of the body element", t);
        }
    }

    public EPubXml getContentElement() {
        if (fContent == null) {
            try {
                EPubXml body = eval("//xhtml:body", EPubXml.class);
                EPubXml content = body.getXmlContext().newXML(
                    "xhtml:div",
                    EPubXml.class);
                Element from = body.getRootElement();
                Element to = content.getRootElement();
                Document doc = to.getOwnerDocument();
                Node child = from.getFirstChild();
                while (child != null) {
                    Node next = child.getNextSibling();
                    child = doc.adoptNode(child);
                    to.appendChild(child);
                    child = next;
                }
                fContent = content;
            } catch (Throwable t) {
                throw onError("Can not load metadata", t);
            }
        }
        return fContent;
    }

    public String getTitle() {
        String title = getString("//xhtml:title");
        return title;
    }

    public EPubSection setContent(String content)
        throws XmlException,
        IOException {
        XmlWrapper tag = fContext.readXML(content);
        return setContent(tag);
    }

    public EPubSection setContent(XmlWrapper content) throws XmlException {
        XmlWrapper body = getOrCreateElement("xhtml:body");
        content.copyTo(body);
        return cast();
    }

    public EPubSection setTitle(String title) throws XmlException {
        XmlWrapper headTag = getOrCreateElement("xhtml:head");
        XmlWrapper titleTag = headTag.getOrCreateElement("xhtml:title");
        titleTag.removeChildren();
        titleTag.appendText(title);
        return cast();
    }

    public void updateReferences(Uri bookRef) {
        getContent();
        try {
            ReferenceUtils.resolveLinks(fContent, bookRef, "xhtml:a", "href");
            ReferenceUtils.resolveLinks(fContent, bookRef, "xhtml:img", "src");

            // Replace all "a" elements by "div"s with the same IDs
            List<XmlWrapper> refs = ReferenceUtils.getElementList(
                fContent,
                "xhtml:a");
            for (XmlWrapper ref : refs) {
                String href = ref.getAttribute("href");
                if (href == null) {
                    XmlWrapper parent = ref.getParent();
                    String id = ref.getAttribute("id");
                    if (id != null) {
                        XmlContext context = ref.getXmlContext();
                        XmlWrapper div = context.newXML("xhtml:span");
                        div.setAttribute("id", id);
                        parent.insertBefore(ref, div);
                    }
                    ref.remove();
                }
            }
        } catch (Throwable t) {
            throw onError("Can not load metadata", t);
        }
    }
}
