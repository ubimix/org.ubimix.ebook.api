package org.ubimix.ebook.bom.epub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBook;
import org.ubimix.ebook.bom.IBookManifest;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class EPubBook extends EPubXml implements IBook {

    public static class EPubManifest extends EPubXml implements IBookManifest {

        public static class EPubManifestItem extends EPubXml
            implements
            IBookManifestItem {

            public EPubManifestItem() {
                super("opf:item");
            }

            public EPubManifestItem(XmlElement e) {
                super(e);
            }

            protected EPubManifestItem(
                XmlElement parent,
                Map<Object, Object> map) {
                super(parent, map);
            }

            @Override
            public Uri getHref() {
                String str = getAttribute("href");
                return str != null ? new Uri(str) : null;
            }

            @Override
            public BookId getID() {
                String str = getAttribute("id");
                return str != null ? new BookId(str) : null;
            }

            @Override
            public String getMediaType() {
                return getAttribute("media-type");
            }

            public EPubManifestItem setHref(Uri href) {
                setAttribute("href", href.toString());
                return cast();
            }

            public EPubManifestItem setID(BookId id) {
                setAttribute("id", id != null ? id.toString() : null);
                return cast();
            }

            public EPubManifestItem setMediaType(String mediaType) {
                setAttribute("media-type", mediaType);
                return cast();
            }
        }

        public EPubManifest() {
            super("opf:manifest");
        }

        public EPubManifest(IHasValueMap object) {
            super(object);
        }

        public EPubManifest(XmlElement parent, Map<Object, Object> map) {
            super(parent, map);
        }

        public EPubManifest.EPubManifestItem addItem(
            String href,
            String mediaType) {
            return addItem(new Uri(href), mediaType);
        }

        public EPubManifest.EPubManifestItem addItem(Uri href, String mediaType) {
            EPubManifestItem item = new EPubManifestItem();
            return item.setHref(href).setMediaType(mediaType);
        }

        @Override
        public EPubManifest.EPubManifestItem getItemByHref(Uri href) {
            EPubManifest.EPubManifestItem result = null;
            XmlElement e = searchChildByAttribute("opf:item", "href", href + "");
            if (e != null) {
                result = new EPubManifest.EPubManifestItem(e);
            } else {
                result = new EPubManifest.EPubManifestItem().setHref(href);
                addChild(result);
            }
            return result;
        }

        @Override
        public EPubManifest.EPubManifestItem getItemById(BookId id) {
            XmlElement e = searchChildByAttribute("opf:item", "id", id + "");
            return e != null ? newManifestItem(e) : null;
        }

        @Override
        public List<IBookManifestItem> getItems() {
            List<IBookManifestItem> result = new ArrayList<IBookManifestItem>();
            List<XmlElement> items = getChildrenByName("opf:item");
            for (XmlElement e : items) {
                EPubManifestItem item = newManifestItem(e);
                if (item != null) {
                    result.add(item);
                }
            }
            return result;
        }

        protected EPubManifestItem newManifestItem(XmlElement e) {
            return new EPubManifestItem(e);
        }

        private XmlElement searchChildByAttribute(
            String tagName,
            String attrName,
            String attrValue) {
            XmlElement result = null;
            List<XmlElement> children = getChildrenByName(tagName);
            for (XmlElement e : children) {
                String value = e.getAttribute(attrName);
                if (attrValue != null && attrValue.equals(value)) {
                    result = e;
                } else if (attrValue == null && value == null) {
                    result = e;
                }
                if (result != null) {
                    break;
                }
            }
            return result;
        }

    }

    public static class EPubMetadata extends EPubXml implements IBookMetadata {

        public EPubMetadata() {
            super("opf:metadata");
        }

        public EPubMetadata(IHasValueMap object) {
            super(object);
        }

        public EPubMetadata(XmlElement parent, Map<Object, Object> map) {
            super(parent, map);
        }

        @Override
        public String getBookCreator() {
            return getString("dc:creator");
        }

        @Override
        public BookId getBookIdentifier() {
            String id = getString("dc:identifier");
            return id != null ? new BookId(id) : null;
        }

        @Override
        public String getBookLanguage() {
            return getString("dc:language");
        }

        @Override
        public String getBookTitle() {
            return getString("dc:title");
        }

        protected String getString(String tagName) {
            XmlElement e = getChildByName(tagName);
            return e != null ? e.toText() : null;
        }

        public EPubMetadata setBookCreator(String creator) {
            setTextElement("dc:creator", creator);
            return cast();
        }

        public EPubMetadata setBookIdentifier(BookId id) {
            return setBookIdentifier(id != null ? id.toString() : "");
        }

        public EPubMetadata setBookIdentifier(String id) {
            XmlElement identifierTag = getOrCreateElement("dc:identifier");
            identifierTag.setText(id);
            return cast();
        }

        public EPubMetadata setBookLanguage(String lang) {
            setTextElement("dc:language", lang);
            return cast();
        }

        public EPubMetadata setBookTitle(String title) {
            setTextElement("dc:title", title);
            return cast();
        }

    }

    public static class EPubSpine extends EPubXml implements IBookSpine {

        public EPubSpine() {
            super("opf:spine");
        }

        public EPubSpine(IHasValueMap object) {
            super(object);
        }

        public EPubSpine(XmlElement parent, Map<Object, Object> map) {
            super(parent, map);
        }

        public void addSectionId(BookId sectionId) {
            XmlElement item = new XmlElement("opf:itemref");
            addChild(item);
            item.setAttribute("idref", sectionId.toString());
        }

        @Override
        public List<BookId> getSectionIds() {
            List<BookId> result = new ArrayList<BookId>();
            List<XmlElement> itemrefs = getChildrenByName("opf:itemref");
            for (XmlElement itemref : itemrefs) {
                String ref = itemref.getAttribute("idref");
                result.add(new BookId(ref));
            }
            return result;
        }

        public BookId getTocId() {
            String str = getAttribute("toc");
            return str != null ? new BookId(str) : null;
        }

        public EPubSpine setSectionIds(BookId... ids) {
            return setSectionIds(Arrays.asList(ids));
        }

        public EPubSpine setSectionIds(List<BookId> ids) {
            int size = ids != null ? ids.size() : 0;
            removeChildren();
            if (size > 0) {
                List<XmlElement> items = addElements("opf:itemref", size);
                for (int i = 0; i < size; i++) {
                    BookId id = ids.get(i);
                    XmlElement item = items.get(i);
                    item.setAttribute("idref", id.toString());
                }
            }
            return cast();
        }

        public void setSectionIds(String... sectionIds) {
            List<BookId> list = new ArrayList<BookId>();
            for (String sectionId : sectionIds) {
                BookId id = new BookId(sectionId);
                list.add(id);
            }
            setSectionIds(list);
        }

        public EPubSpine setTocId(BookId tocId) {
            setAttribute("toc", tocId + "");
            return cast();
        }

    }

    public EPubBook() {
        super("opf:package");
    }

    public EPubBook(IHasValueMap object) {
        super(object);
    }

    public EPubBook(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    @Override
    public EPubBook.EPubManifest getManifest() {
        return getManifest(true);
    }

    public EPubBook.EPubManifest getManifest(boolean create) {
        XmlElement e = getPackageNode("opf:manifest", create);
        return e != null ? new EPubManifest(e) : null;
    }

    @Override
    public EPubBook.EPubMetadata getMetadata() {
        return getMetadata(false);
    }

    public EPubBook.EPubMetadata getMetadata(boolean create) {
        XmlElement e = getPackageNode("opf:metadata", create);
        return e != null ? new EPubMetadata(e) : null;
    }

    private XmlElement getPackageNode(String name, boolean create) {
        XmlElement pckg = getOrCreateElement("opf:package");
        XmlElement result;
        if (create) {
            result = getOrCreateElement(pckg, name);
        } else {
            result = pckg.getChildByName(name);
        }
        return result;
    }

    @Override
    public EPubBook.EPubSpine getSpine() {
        return getSpine(false);
    }

    public EPubBook.EPubSpine getSpine(boolean create) {
        XmlElement e = getPackageNode("opf:spine", create);
        return e != null ? new EPubSpine(e) : null;
    }

    public void updatePaths(Uri basePath) {
        List<XmlElement> list = getChildrenByPath(
            "opf:package",
            "opf:manifest",
            "opf:item");
        for (XmlElement e : list) {
            ReferenceUtils.resolveLink(basePath, e, "href");
        }
    }

}