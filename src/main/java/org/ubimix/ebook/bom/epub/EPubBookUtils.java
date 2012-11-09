/**
 * 
 */
package org.ubimix.ebook.bom.epub;

import java.io.IOException;
import java.util.List;

import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBook;
import org.ubimix.ebook.bom.IBook.IBookMetadata;
import org.ubimix.ebook.bom.IBook.IBookSpine;
import org.ubimix.ebook.bom.IBookManifest;
import org.ubimix.ebook.bom.IBookManifest.IBookManifestItem;
import org.ubimix.ebook.bom.IBookSection;
import org.ubimix.ebook.bom.IBookToc;
import org.ubimix.ebook.bom.IBookToc.IBookTocItem;
import org.ubimix.ebook.bom.epub.EPubBook.EPubManifest;
import org.ubimix.ebook.bom.epub.EPubBook.EPubManifest.EPubManifestItem;
import org.ubimix.ebook.bom.epub.EPubBook.EPubMetadata;
import org.ubimix.ebook.bom.epub.EPubBook.EPubSpine;
import org.ubimix.ebook.bom.epub.EPubToc.EPubTocItem;

/**
 * @author kotelnikov
 */
public class EPubBookUtils {

    public EPubBookUtils() {
    }

    public EPubBook copyBook(IBook value) {
        if (value == null) {
            return null;
        }
        EPubBook copy = new EPubBook();
        return copyBook(value, copy);
    }

    public EPubBook copyBook(IBook value, EPubBook copy) {
        copyMetadata(value.getMetadata(), copy.getMetadata(true));
        copyManifest(value.getManifest(), copy.getManifest(true));
        copySpine(value.getSpine(), copy.getSpine(true));
        return copy;
    }

    public EPubSection copyBookSection(IBookSection value) throws IOException {
        EPubSection copy = new EPubSection().setTitle(value.getTitle());
        return copyBookSection(value, copy);
    }

    public EPubSection copyBookSection(IBookSection value, EPubSection copy)
        throws IOException {
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

    public EPubToc copyBookToc(IBookToc value) {
        if (value == null) {
            return null;
        }
        EPubToc copy = EPubToc.newToc();
        return copyBookToc(value, copy);
    }

    public EPubToc copyBookToc(IBookToc value, EPubToc copy) {
        List<IBookTocItem> items = value.getTocItems();
        for (IBookTocItem item : items) {
            EPubTocItem copyItem = copy.addTocItem();
            copyBookTocItem(item, copyItem);
        }
        return copy;
    }

    public IBookTocItem copyBookTocItem(IBookTocItem value) {
        if (value == null) {
            return null;
        }
        EPubTocItem copy = new EPubTocItem();
        return copyBookTocItem(value, copy);
    }

    public IBookTocItem copyBookTocItem(IBookTocItem value, EPubTocItem copy) {
        copy.setLabel(value.getLabel()).setContentHref(value.getContentHref());
        for (IBookTocItem item : value.getTocItems()) {
            EPubTocItem itemCopy = copy.addChild();
            copyBookTocItem(item, itemCopy);
        }
        return copy;
    }

    public EPubManifest copyManifest(IBookManifest value) {
        if (value == null) {
            return null;
        }
        EPubManifest copy = new EPubManifest();
        return copyManifest(value, copy);
    }

    public EPubManifest copyManifest(IBookManifest value, EPubManifest copy) {
        for (IBookManifestItem item : value.getItems()) {
            copy.addItem(item.getHref(), item.getMediaType());
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

    public EPubMetadata copyMetadata(IBookMetadata value) {
        if (value == null) {
            return null;
        }
        EPubMetadata copy = new EPubMetadata();
        return copyMetadata(value, copy);
    }

    public EPubMetadata copyMetadata(IBookMetadata value, EPubMetadata copy) {
        copy
            .setBookIdentifier(value.getBookIdentifier())
            .setBookTitle(value.getBookTitle())
            .setBookCreator(value.getBookCreator())
            .setBookLanguage(value.getBookLanguage());
        return copy;
    }

    public EPubSpine copySpine(IBookSpine value) {
        if (value == null) {
            return null;
        }
        EPubSpine copy = new EPubSpine();
        copySpine(value, copy);
        return copy;
    }

    private void copySpine(IBookSpine value, EPubSpine copy) {
        List<BookId> sectionIds = value.getSectionIds();
        copy.setSectionIds(sectionIds);
    }

}
