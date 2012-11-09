/**
 * 
 */
package org.ubimix.ebook.bom.epub;

import java.util.Map;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.bom.IBookSection;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class EPubSection extends EPubXml implements IBookSection {

    public EPubSection() {
        super("html");
        getOrCreateElement("head");
        getOrCreateElement("body");
    }

    public EPubSection(IHasValueMap object) {
        super(object);
    }

    public EPubSection(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    @Override
    public String getContent() {
        EPubXml xml = getContentElement();
        return xml.toString();
    }

    public EPubXml getContentElement() {
        XmlElement body = getChildByName("body");
        XmlElement content = body.newCopy(true).setName("div");
        return new EPubXml(content);
    }

    @Override
    public String getTitle() {
        XmlElement head = getChildByName("head");
        XmlElement title = null;
        if (head != null) {
            title = head.getChildByName("title");
        }
        return title != null ? title.toText() : null;
    }

    public EPubSection setContent(String content) {
        XmlElement tag = HtmlDocument.parseFragment(content);
        return setContent(tag);
    }

    public EPubSection setContent(XmlElement content) {
        XmlElement body = getOrCreateElement("body");
        XmlElement copy = content.newCopy(true);
        body.addChildren(copy);
        return cast();
    }

    public EPubSection setTitle(String title) {
        XmlElement headTag = getOrCreateElement("head");
        XmlElement titleTag = headTag.getOrCreateElement("title");
        titleTag.setText(title);
        return cast();
    }

    public void updateReferences(Uri bookRef) {
        ReferenceUtils.resolveLinks(this, bookRef, "a", "href");
        ReferenceUtils.resolveLinks(this, bookRef, "img", "src");
    }
}
