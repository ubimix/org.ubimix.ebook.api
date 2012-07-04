/**
 * 
 */
package org.webreformatter.ebook.bom.json;

import java.util.ArrayList;
import java.util.List;

import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.IBookManifest;
import org.webreformatter.ebook.bom.IBookSection;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.bom.IBook.IBookMetadata;
import org.webreformatter.ebook.bom.IBook.IBookSpine;
import org.webreformatter.ebook.bom.IBookManifest.IBookManifestItem;
import org.webreformatter.ebook.bom.IBookToc.IBookTocItem;
import org.webreformatter.ebook.bom.json.JsonBookManifest.JsonBookManifestItem;
import org.webreformatter.ebook.bom.json.JsonBookToc.JsonBookTocItem;

/**
 * @author kotelnikov
 */
public class JsonBookUtils {

    public static JsonBook copyBook(IBook value) {
        if (value == null) {
            return null;
        }
        JsonBook copy = new JsonBook()
            .setMetadata(copyMetadata(value.getMetadata()))
            .setManifest(copyManifest(value.getManifest()))
            .setSpine(copySpine(value.getSpine()));
        return copy;
    }

    public static JsonBookSection copyBookSection(IBookSection value) {
        if (value == null) {
            return null;
        }
        JsonBookSection copy = new JsonBookSection()
            .setTitle(value.getTitle())
            .setContent(value.getContent());
        return copy;
    }

    public static JsonBookToc copyBookToc(IBookToc value) {
        if (value == null) {
            return null;
        }
        JsonBookToc copy = new JsonBookToc().setTocItems(copyBookTocItems(value
            .getTocItems()));
        return copy;
    }

    public static IBookTocItem copyBookTocItem(IBookTocItem value) {
        if (value == null) {
            return null;
        }
        JsonBookTocItem copy = new JsonBookTocItem()
            .setLabel(value.getLabel())
            .setContentHref(value.getContentHref())
            .setChildren(copyBookTocItems(value.getChildren()));
        return copy;
    }

    public static List<IBookTocItem> copyBookTocItems(List<IBookTocItem> value) {
        if (value == null) {
            return null;
        }
        List<IBookTocItem> copy = new ArrayList<IBookToc.IBookTocItem>();
        for (IBookTocItem item : value) {
            copy.add(copyBookTocItem(item));
        }
        return copy;
    }

    public static JsonBookManifest copyManifest(IBookManifest value) {
        if (value == null) {
            return null;
        }
        JsonBookManifest copy = new JsonBookManifest()
            .setItems(copyManifestItems(value.getItems()));
        return copy;
    }

    public static JsonBookManifestItem copyManifestItem(IBookManifestItem value) {
        if (value == null) {
            return null;
        }
        JsonBookManifestItem copy = new JsonBookManifestItem()
            .setHref(value.getHref())
            .setID(value.getID())
            .setMediaType(value.getMediaType());
        return copy;
    }

    public static List<IBookManifestItem> copyManifestItems(
        List<IBookManifestItem> value) {
        if (value == null) {
            return null;
        }
        List<IBookManifestItem> copy = new ArrayList<IBookManifest.IBookManifestItem>();
        for (IBookManifestItem item : value) {
            copy.add(copyManifestItem(item));
        }
        return copy;
    }

    public static JsonBookMeta copyMetadata(IBookMetadata value) {
        if (value == null) {
            return null;
        }
        JsonBookMeta copy = new JsonBookMeta()
            .setBookIdentifier(value.getBookIdentifier())
            .setBookTitle(value.getBookTitle())
            .setBookCreator(value.getBookCreator());
        // TODO: copy all other metadata fields
        return copy;
    }

    public static JsonBookSpine copySpine(IBookSpine value) {
        if (value == null) {
            return null;
        }
        JsonBookSpine copy = new JsonBookSpine().setSectionIds(value
            .getSectionIds());
        return copy;
    }

}
