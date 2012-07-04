/**
 * 
 */
package org.webreformatter.ebook.bom.epub;

import java.io.IOException;
import java.util.List;

import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper.XmlContext;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.IBookManifest;
import org.webreformatter.ebook.bom.IBookSection;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.bom.IBook.IBookMetadata;
import org.webreformatter.ebook.bom.IBook.IBookSpine;
import org.webreformatter.ebook.bom.IBookManifest.IBookManifestItem;
import org.webreformatter.ebook.bom.IBookToc.IBookTocItem;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubManifest;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubMetadata;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubSpine;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubManifest.EPubManifestItem;
import org.webreformatter.ebook.bom.epub.EPubToc.EPubTocItem;

/**
 * @author kotelnikov
 */
public class EPubBookUtils {

    private XmlContext fXmlContext;

    public EPubBookUtils() {
        this(EPubXml.newXmlContext());
    }

    public EPubBookUtils(XmlContext xmlContext) {
        fXmlContext = xmlContext;
    }

    public EPubBook copyBook(IBook value) throws XmlException {
        if (value == null) {
            return null;
        }
        EPubBook copy = EPubBook.newBook(fXmlContext);
        return copyBook(value, copy);
    }

    public EPubBook copyBook(IBook value, EPubBook copy) throws XmlException {
        copyMetadata(value.getMetadata(), copy.getMetadata(true));
        copyManifest(value.getManifest(), copy.getManifest(true));
        copySpine(value.getSpine(), copy.getSpine(true));
        return copy;
    }

    public EPubSection copyBookSection(IBookSection value)
        throws XmlException,
        IOException {
        EPubSection copy = EPubSection.newSection(fXmlContext).setTitle(
            value.getTitle());
        return copyBookSection(value, copy);
    }

    public EPubSection copyBookSection(IBookSection value, EPubSection copy)
        throws XmlException,
        IOException {
        if (value == null) {
            return null;
        }
        if (value instanceof EPubSection) {
            copy.setContent(((EPubSection) value).getContentElement());
        } else {
            copy.setContent(value.getContent());
        }
        return copy;
    }

    public EPubToc copyBookToc(IBookToc value) throws XmlException {
        if (value == null) {
            return null;
        }
        EPubToc copy = EPubToc.newToc();
        return copyBookToc(value, copy);
    }

    public EPubToc copyBookToc(IBookToc value, EPubToc copy)
        throws XmlException {
        List<IBookTocItem> items = value.getTocItems();
        for (IBookTocItem item : items) {
            EPubTocItem copyItem = copy.addTocItem();
            copyBookTocItem(item, copyItem);
        }
        return copy;
    }

    public IBookTocItem copyBookTocItem(IBookTocItem value) throws XmlException {
        if (value == null) {
            return null;
        }
        EPubTocItem copy = EPubTocItem.newTocItem(fXmlContext);
        return copyBookTocItem(value, copy);
    }

    public IBookTocItem copyBookTocItem(IBookTocItem value, EPubTocItem copy)
        throws XmlException {
        copy.setLabel(value.getLabel()).setContentHref(value.getContentHref());
        for (IBookTocItem item : value.getChildren()) {
            EPubTocItem itemCopy = copy.addChild();
            copyBookTocItem(item, itemCopy);
        }
        return copy;
    }

    public EPubManifest copyManifest(IBookManifest value) {
        if (value == null) {
            return null;
        }
        EPubManifest copy = EPubManifest.newManifest(fXmlContext);
        return copyManifest(value, copy);
    }

    public EPubManifest copyManifest(IBookManifest value, EPubManifest copy) {
        for (IBookManifestItem item : value.getItems()) {
            try {
                copy.addItem(item.getHref(), item.getMediaType());
            } catch (Throwable t) {
                throw EPubXml.onError("Can not copy a TOC item.", t);
            }
        }
        return copy;
    }

    public EPubManifestItem copyManifestItem(
        IBookManifestItem value,
        EPubManifestItem copy) {
        copy
            .setHref(value.getHref())
            .setID(value.getID())
            .setMediaType(value.getMediaType());
        return copy;
    }

    public EPubMetadata copyMetadata(IBookMetadata value) throws XmlException {
        if (value == null) {
            return null;
        }
        EPubMetadata copy = EPubMetadata.newMetadata(fXmlContext);
        return copyMetadata(value, copy);
    }

    public EPubMetadata copyMetadata(IBookMetadata value, EPubMetadata copy)
        throws XmlException {
        copy
            .setBookIdentifier(value.getBookIdentifier())
            .setBookTitle(value.getBookTitle())
            .setBookCreator(value.getBookCreator())
            .setBookLanguage(value.getBookLanguage());
        return copy;
    }

    public EPubSpine copySpine(IBookSpine value) throws XmlException {
        if (value == null) {
            return null;
        }
        EPubSpine copy = EPubSpine.newSpine(fXmlContext);
        copySpine(value, copy);
        return copy;
    }

    private void copySpine(IBookSpine value, EPubSpine copy) {
        List<BookId> sectionIds = value.getSectionIds();
        copy.setSectionIds(sectionIds);
    }

}
