package org.ubimix.ebook.bom.epub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.ubimix.commons.uri.Uri;
import org.ubimix.commons.xml.XmlException;
import org.ubimix.commons.xml.XmlWrapper;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBook;
import org.ubimix.ebook.bom.IBookManifest;

/**
 * @author kotelnikov
 */
public class EPubBook extends EPubXml implements IBook {

    public static class EPubManifest extends EPubXml implements IBookManifest {

        public static class EPubManifestItem extends EPubXml
            implements
            IBookManifestItem {

            public EPubManifestItem(Node node, XmlContext context) {
                super(node, context);
            }

            public Uri getHref() {
                String str = getString("@href");
                return str != null ? new Uri(str) : null;
            }

            public BookId getID() {
                String str = getString("@id");
                return str != null ? new BookId(str) : null;
            }

            public String getMediaType() {
                return getString("@media-type");
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

        public static EPubManifest newManifest(XmlContext xmlContext) {
            try {
                EPubManifest manifest = xmlContext.newXML(
                    "opf:manifest",
                    EPubManifest.class);
                return manifest;
            } catch (Throwable t) {
                throw onError("Can not create a new manifest object", t);
            }
        }

        public EPubManifest(Node node, XmlContext context) {
            super(node, context);
        }

        public EPubManifest.EPubManifestItem addItem(
            String href,
            String mediaType) throws XmlException {
            return addItem(new Uri(href), mediaType);
        }

        public EPubManifest.EPubManifestItem addItem(Uri href, String mediaType)
            throws XmlException {
            EPubManifestItem item = appendElement(
                "opf:item",
                EPubManifest.EPubManifestItem.class);
            return item.setHref(href).setMediaType(mediaType);
        }

        public EPubManifest.EPubManifestItem getItemByHref(Uri href) {
            EPubManifest.EPubManifestItem result = getXml("opf:item[@href='"
                + href
                + "']", EPubManifest.EPubManifestItem.class);
            return result;
        }

        public EPubManifest.EPubManifestItem getItemById(BookId id) {
            EPubManifest.EPubManifestItem result = getXml("opf:item[@id='"
                + id
                + "']", EPubManifest.EPubManifestItem.class);
            return result;
        }

        public List<IBookManifestItem> getItems() {
            List<EPubManifest.EPubManifestItem> list = getXmlList(
                "opf:item",
                EPubManifest.EPubManifestItem.class);
            List<IBookManifestItem> result = new ArrayList<IBookManifestItem>(
                list);
            return result;
        }

    }

    public static class EPubMetadata extends EPubXml implements IBookMetadata {

        public static EPubMetadata newMetadata(XmlContext xmlContext) {
            try {
                EPubMetadata toc = xmlContext.newXML(
                    "opf:metadata",
                    EPubMetadata.class);
                return toc;
            } catch (Throwable t) {
                throw onError("Can not create a new metadata object", t);
            }
        }

        public EPubMetadata(Node node, XmlContext context) {
            super(node, context);
        }

        public String getBookCreator() {
            return getString("dc:creator");
        }

        public BookId getBookIdentifier() {
            String id = getString("dc:identifier");
            return id != null ? new BookId(id) : null;
        }

        public String getBookLanguage() {
            return getString("dc:language");
        }

        public String getBookTitle() {
            return getString("dc:title");
        }

        public EPubMetadata setBookCreator(String creator) throws XmlException {
            setTextElement("dc:creator", creator);
            return cast();
        }

        public EPubMetadata setBookIdentifier(BookId id) throws XmlException {
            return setBookIdentifier(id != null ? id.toString() : "");
        }

        public EPubMetadata setBookIdentifier(String id) throws XmlException {
            XmlWrapper identifierTag = getOrCreateElement("dc:identifier");
            identifierTag.removeChildren();
            identifierTag.appendText(id);
            return cast();
        }

        public EPubMetadata setBookLanguage(String lang) throws XmlException {
            setTextElement("dc:language", lang);
            return cast();
        }

        public EPubMetadata setBookTitle(String title) throws XmlException {
            setTextElement("dc:title", title);
            return cast();
        }

    }

    public static class EPubSpine extends EPubXml implements IBookSpine {

        public static EPubSpine newSpine(XmlContext xmlContext)
            throws XmlException {
            return xmlContext.newXML("opf:spine", EPubSpine.class);
        }

        public EPubSpine(Node node, XmlContext context) {
            super(node, context);
        }

        public void addSectionId(BookId sectionId) throws XmlException {
            XmlWrapper item = appendElement("opf:itemref");
            item.setAttribute("idref", sectionId.toString());
        }

        public List<BookId> getSectionIds() {
            try {
                List<BookId> result = new ArrayList<BookId>();
                List<XmlWrapper> itemrefs = evalList("opf:itemref");
                for (XmlWrapper itemref : itemrefs) {
                    String ref = itemref.evalStr("@idref");
                    result.add(new BookId(ref));
                }
                return result;
            } catch (Throwable t) {
                throw onError("Can not load identifiers for book sections", t);
            }
        }

        public BookId getTocId() {
            String str = getString("@toc");
            return str != null ? new BookId(str) : null;
        }

        public EPubSpine setSectionIds(BookId... ids) {
            return setSectionIds(Arrays.asList(ids));
        }

        public EPubSpine setSectionIds(List<BookId> ids) {
            int size = ids != null ? ids.size() : 0;
            removeChildren();
            if (size > 0) {
                List<XmlWrapper> items = addElements("opf:itemref", size);
                for (int i = 0; i < size; i++) {
                    BookId id = ids.get(i);
                    XmlWrapper item = items.get(i);
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

    public static EPubBook newBook() throws XmlException {
        XmlContext xmlContext = newXmlContext();
        return newBook(xmlContext);
    }

    public static EPubBook newBook(XmlContext xmlContext) throws XmlException {
        EPubBook book = xmlContext.newXML("opf:package", EPubBook.class);
        book.setAttribute("version", "3.0");
        return book;
    }

    public EPubBook(Node node, XmlContext context) {
        super(node, context);
    }

    public EPubBook.EPubManifest getManifest() {
        return getManifest(true);
    }

    public EPubBook.EPubManifest getManifest(boolean create) {
        return getPackageNode(
            "opf:manifest",
            EPubBook.EPubManifest.class,
            create);
    }

    public EPubBook.EPubMetadata getMetadata() {
        return getMetadata(false);
    }

    public EPubBook.EPubMetadata getMetadata(boolean create) {
        return getPackageNode(
            "opf:metadata",
            EPubBook.EPubMetadata.class,
            create);
    }

    private <T extends EPubXml> T getPackageNode(
        String name,
        Class<T> type,
        boolean create) {
        try {
            T result;
            if (create) {
                result = getOrCreateElement(name, type);
            } else {
                result = eval("/opf:package/" + name, type);
            }
            return result;
        } catch (Throwable t) {
            throw onError("Can not get a package element", t);
        }
    }

    public EPubBook.EPubSpine getSpine() {
        return getSpine(false);
    }

    public EPubBook.EPubSpine getSpine(boolean create) {
        return getPackageNode("opf:spine", EPubBook.EPubSpine.class, create);
    }

    public void updatePaths(Uri basePath) {
        resolvePaths(basePath, "/opf:package/opf:manifest/opf:item", "href");
    }

}