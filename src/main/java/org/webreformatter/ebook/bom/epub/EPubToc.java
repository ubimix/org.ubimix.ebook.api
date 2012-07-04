package org.webreformatter.ebook.bom.epub;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.bom.IBookToc;

public class EPubToc extends EPubXml implements IBookToc {

    public static class EPubTocItem extends EPubXml implements IBookTocItem {

        public static EPubTocItem newTocItem() throws XmlException {
            XmlContext xmlContext = newXmlContext();
            return newTocItem(xmlContext);
        }

        public static EPubTocItem newTocItem(XmlContext context)
            throws XmlException {
            EPubTocItem item = context
                .newXML("ncx:navPoint", EPubTocItem.class);
            return item;
        }

        public EPubTocItem(Node node, XmlContext context) {
            super(node, context);
        }

        public EPubTocItem addChild() {
            try {
                EPubTocItem item = appendElement(
                    "ncx:navPoint",
                    EPubTocItem.class);
                return item;
            } catch (Throwable t) {
                throw onError("Can not add a new TOC item", t);
            }
        }

        public List<IBookTocItem> getChildren() {
            List<EPubTocItem> list = getXmlList(
                "ncx:navPoint",
                EPubToc.EPubTocItem.class);
            return new ArrayList<IBookTocItem>(list);
        }

        public Uri getContentHref() {
            String str = getString("ncx:content/@src");
            return new Uri(str);
        }

        public String getId() {
            return getString("@id");
        }

        public String getLabel() {
            return getString("ncx:navLabel/ncx:text");
        }

        public int getPlayOrder() throws XmlException {
            String str = evalStr("@playOrder");
            int result = -1;
            try {
                str = str.trim();
                result = Integer.parseInt(str);
            } catch (Throwable t) {
            }
            return result;
        }

        public EPubTocItem setContentHref(String href) throws XmlException {
            XmlWrapper content = getOrCreateElement("ncx:content");
            content.setAttribute("src", href);
            return cast();
        }

        public EPubTocItem setContentHref(Uri href) throws XmlException {
            return setContentHref(href != null ? href.toString() : "");
        }

        public EPubTocItem setId(String id) {
            setAttribute("id", id);
            return cast();
        }

        public EPubTocItem setLabel(String label) throws XmlException {
            XmlWrapper navLabel = getOrCreateElement("ncx:navLabel");
            XmlWrapper text = navLabel.getOrCreateElement("ncx:text");
            text.removeChildren();
            text.appendText(label);
            return cast();
        }

    }

    public static EPubToc newToc() throws XmlException {
        XmlContext xmlContext = newXmlContext();
        return newToc(xmlContext);
    }

    public static EPubToc newToc(XmlContext xmlContext) throws XmlException {
        EPubToc toc = xmlContext.newXML("ncx:ncx", EPubToc.class);
        toc.setBookId("");
        toc.setEPubCreator("");
        toc.setDepth(2);
        toc.setTotalPageCount(0);
        toc.setMaxPageNumber(0);
        toc.setTocAuthor("");
        return toc;
    }

    public EPubToc(Node node, XmlContext context) {
        super(node, context);
    }

    public EPubTocItem addTocItem() {
        try {
            XmlWrapper navMapTag = getOrCreateElement("ncx:navMap");
            EPubTocItem item = navMapTag.appendElement(
                "ncx:navPoint",
                EPubTocItem.class);
            return item;
        } catch (Throwable t) {
            throw onError("Can not add a new TOC item", t);
        }
    }

    public BookId getBookId() throws XmlException {
        String str = getMetaField("dtb:uid");
        return new BookId(str);
    }

    public int getDepth() throws XmlException {
        return getMetaFieldAsInteger("dtb:depth");
    }

    public String getEPubCreator() throws XmlException {
        return getMetaField("epub-creator");
    }

    public int getMaxPageNumber() throws XmlException {
        return getMetaFieldAsInteger("dtb:maxPageNumber");
    }

    private String getMetaField(String key) throws XmlException {
        String str = evalStr("ncx:head/ncx:meta[@name='" + key + "']");
        return str;
    }

    private int getMetaFieldAsInteger(String key) throws XmlException {
        String str = getMetaField(key);
        int result = 0;
        if (str != null) {
            try {
                result = Integer.parseInt(str);
            } catch (Throwable t) {
            }
        }
        return result;
    }

    public String getTocAuthor() throws XmlException {
        return evalStr("/ncx:ncx/ncx:docAuthor/ncx:text");
    }

    public List<IBookTocItem> getTocItems() {
        List<EPubTocItem> list = getXmlList(
            "/ncx:ncx/ncx:navMap/ncx:navPoint",
            EPubToc.EPubTocItem.class);
        return new ArrayList<IBookTocItem>(list);
    }

    public int getTotalPageCount() throws XmlException {
        return getMetaFieldAsInteger("dtb:totalPageCount");
    }

    public EPubToc setBookId(String value) throws XmlException {
        return setMetaField("dtb:uid", value);
    }

    public EPubToc setDepth(int value) throws XmlException {
        return setMetaField("dtb:depth", value + "");
    }

    public EPubToc setEPubCreator(String value) throws XmlException {
        return setMetaField("epub-creator", value);
    }

    public EPubToc setMaxPageNumber(int value) throws XmlException {
        return setMetaField("dtb:maxPageNumber", value + "");
    }

    private EPubToc setMetaField(String key, String value) throws XmlException {
        XmlWrapper headTag = getOrCreateElement("ncx:head");
        XmlWrapper meta = headTag.eval("ncx:meta[@name='" + key + "']");
        if (meta == null) {
            meta = headTag.appendElement("ncx:meta");
            meta.setAttribute("name", key);
        }
        meta.setAttribute("content", value);
        return cast();
    }

    public EPubToc setTocAuthor(String author) throws XmlException {
        XmlWrapper authorTag = getOrCreateElement("ncx:docAuthor");
        XmlWrapper text = authorTag.getOrCreateElement("ncx:text");
        text.removeChildren();
        text.appendText(author);
        return cast();
    }

    public EPubToc setTotalPageCount(int value) throws XmlException {
        return setMetaField("dtb:totalPageCount", value + "");
    }

    public void updatePaths(Uri tocPath) {
        resolvePaths(tocPath, "//ncx:navPoint/ncx:content", "src");
    }

}