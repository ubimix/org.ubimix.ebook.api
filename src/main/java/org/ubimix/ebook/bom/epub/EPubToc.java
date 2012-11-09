package org.ubimix.ebook.bom.epub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBookToc;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.xml.XmlElement;

public class EPubToc extends EPubXml implements IBookToc {

    public static class EPubTocItem extends EPubXml implements IBookTocItem {

        public EPubTocItem() {
            super("ncx:navPoint");
        }

        public EPubTocItem(IHasValueMap object) {
            super(object);
        }

        public EPubTocItem(XmlElement parent, IHasValueMap map) {
            super(parent, map.getMap());
        }

        public EPubTocItem addChild() {
            EPubTocItem item = new EPubTocItem();
            addChild(item);
            return item;
        }

        @Override
        public List<IBookTocItem> getTocItems() {
            List<XmlElement> list = getChildrenByName("ncx:navPoint");
            List<IBookTocItem> result = new ArrayList<IBookToc.IBookTocItem>();
            for (XmlElement e : list) {
                result.add(new EPubTocItem(this, e));
            }
            return result;
        }

        @Override
        public Uri getContentHref() {
            String str = null;
            XmlElement e = getChildByName("ncx:content");
            if (e != null) {
                str = e.getAttribute("src");
            }
            return new Uri(str);
        }

        public String getId() {
            return getAttribute("id");
        }

        @Override
        public String getLabel() {
            XmlElement e = getChildByName("ncx:navLabel");
            String result = null;
            if (e != null) {
                e = e.getChildByName("ncx:text");
                if (e != null) {
                    result = e.toText();
                }
            }
            return result;
        }

        public int getPlayOrder() {
            String str = getAttribute("playOrder");
            int result = -1;
            try {
                str = str.trim();
                result = Integer.parseInt(str);
            } catch (Throwable t) {
            }
            return result;
        }

        public EPubTocItem setContentHref(String href) {
            XmlElement content = getOrCreateElement("ncx:content");
            content.setAttribute("src", href);
            return cast();
        }

        public EPubTocItem setContentHref(Uri href) {
            return setContentHref(href != null ? href.toString() : "");
        }

        public EPubTocItem setId(String id) {
            setAttribute("id", id);
            return cast();
        }

        public EPubTocItem setLabel(String label) {
            XmlElement navLabel = getOrCreateElement("ncx:navLabel");
            XmlElement text = navLabel.getOrCreateElement("ncx:text");
            text.setText(label);
            return cast();
        }

    }

    public static EPubToc newToc() {
        EPubToc toc = new EPubToc();
        toc.setBookId("");
        toc.setEPubCreator("");
        toc.setDepth(2);
        toc.setTotalPageCount(0);
        toc.setMaxPageNumber(0);
        toc.setTocAuthor("");
        return toc;
    }

    public EPubToc() {
        super("ncx:ncx");
    }

    public EPubToc(IHasValueMap object) {
        super(object);
    }

    public EPubToc(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    public EPubTocItem addTocItem() {
        XmlElement navMapTag = getOrCreateElement("ncx:navMap");
        EPubTocItem item = new EPubTocItem();
        navMapTag.addChild(item);
        return item;
    }

    public BookId getBookId() {
        String str = getMetaField("dtb:uid");
        return new BookId(str);
    }

    public int getDepth() {
        return getMetaFieldAsInteger("dtb:depth");
    }

    public String getEPubCreator() {
        return getMetaField("epub-creator");
    }

    public int getMaxPageNumber() {
        return getMetaFieldAsInteger("dtb:maxPageNumber");
    }

    private String getMetaField(String key) {
        String result = null;
        XmlElement head = getChildByName("ncx:head");
        if (head != null) {
            XmlElement meta = head.getChildByName("ncx:meta");
            if (meta != null) {
                String metaName = meta.getAttribute("name");
                if (key.equals(metaName)) {
                    result = meta.toText();
                }
            }
        }
        return result;
    }

    private int getMetaFieldAsInteger(String key) {
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

    public String getTocAuthor() {
        XmlElement e = getChildByPath("ncx:ncx", "ncx:docAuthor", "ncx:text");
        return e != null ? e.toText() : null;
    }

    @Override
    public List<IBookTocItem> getTocItems() {
        List<IBookTocItem> result = new ArrayList<IBookTocItem>();
        List<XmlElement> navPoints = getChildrenByPath(
            "ncx:ncx",
            "ncx:navMap",
            "ncx:navPoint");
        for (XmlElement navPoint : navPoints) {
            result.add(new EPubTocItem(navPoint));
        }
        return result;
    }

    public int getTotalPageCount() {
        return getMetaFieldAsInteger("dtb:totalPageCount");
    }

    public EPubToc setBookId(String value) {
        return setMetaField("dtb:uid", value);
    }

    public EPubToc setDepth(int value) {
        return setMetaField("dtb:depth", value + "");
    }

    public EPubToc setEPubCreator(String value) {
        return setMetaField("epub-creator", value);
    }

    public EPubToc setMaxPageNumber(int value) {
        return setMetaField("dtb:maxPageNumber", value + "");
    }

    private EPubToc setMetaField(String key, String value) {
        XmlElement headTag = getOrCreateElement("ncx:head");
        XmlElement meta = headTag.getOrCreateElement("ncx:meta");
        meta.setAttribute("name", key);
        meta.setAttribute("content", value);
        return cast();
    }

    public EPubToc setTocAuthor(String author) {
        XmlElement authorTag = getOrCreateElement("ncx:docAuthor");
        XmlElement text = authorTag.getOrCreateElement("ncx:text");
        text.setText(author);
        return cast();
    }

    public EPubToc setTotalPageCount(int value) {
        return setMetaField("dtb:totalPageCount", value + "");
    }

    public void updatePaths(Uri tocPath) {
        List<XmlElement> list = getChildrenByPath("ncx:navPoint", "ncx:content");
        for (XmlElement e : list) {
            ReferenceUtils.resolveLink(tocPath, e, "src");
        }
    }

}