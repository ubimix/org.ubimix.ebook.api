/**
 * 
 */
package org.ubimix.ebook.bom.json;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBookManifest;

/**
 * @author kotelnikov
 */
public class JsonBookManifest extends JsonBookBase implements IBookManifest {

    public static class JsonBookManifestItem extends JsonBookBase
        implements
        IBookManifestItem {

        public static IJsonValueFactory<JsonBookManifestItem> FACTORY = new IJsonValueFactory<JsonBookManifestItem>() {
            public JsonBookManifestItem newValue(Object object) {
                return new JsonBookManifestItem().setJsonObject(object);
            }
        };

        public Uri getHref() {
            return getValue("href", URI_FACTORY);
        }

        public BookId getID() {
            return getValue("id", ID_FACTORY);
        }

        public String getMediaType() {
            return getString("mediaType");
        }

        public JsonBookManifestItem setHref(Uri value) {
            setValue("href", value);
            return cast();
        }

        public JsonBookManifestItem setID(BookId value) {
            setValue("id", value);
            return cast();
        }

        public JsonBookManifestItem setMediaType(String value) {
            setValue("mediaType", value);
            return cast();
        }

    }

    public static IJsonValueFactory<JsonBookManifest> FACTORY = new IJsonValueFactory<JsonBookManifest>() {
        public JsonBookManifest newValue(Object object) {
            return new JsonBookManifest().setJsonObject(object);
        }
    };

    /**
     * 
     */
    public JsonBookManifest() {
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> cast(List<? extends T> list) {
        return (List<T>) list;
    }

    /**
     * @see org.ubimix.ebook.bom.IBookManifest#getItemByHref(Uri)
     */
    public IBookManifestItem getItemByHref(Uri href) {
        // TODO: re-implement it in a more efficient way
        IBookManifestItem result = null;
        List<IBookManifestItem> list = getItems();
        for (IBookManifestItem item : list) {
            if (href.equals(item.getHref())) {
                result = item;
                break;
            }
        }
        return result;
    }

    /**
     * @see org.ubimix.ebook.bom.IBookManifest#getItemById(BookId)
     */
    public IBookManifestItem getItemById(BookId id) {
        // TODO: re-implement it in a more efficient way
        IBookManifestItem result = null;
        List<IBookManifestItem> list = getItems();
        for (IBookManifestItem item : list) {
            if (id.equals(item.getID())) {
                result = item;
                break;
            }
        }
        return result;
    }

    /**
     * @see org.ubimix.ebook.bom.IBookManifest#getItems()
     */
    public List<IBookManifestItem> getItems() {
        ArrayList<JsonBookManifestItem> list = getList(
            "items",
            JsonBookManifestItem.FACTORY);
        return new ArrayList<IBookManifestItem>(list);
    }

    public JsonBookManifest setItems(IBookManifestItem... list) {
        setValue("items", list);
        return cast();
    }

    public JsonBookManifest setItems(List<IBookManifestItem> list) {
        setValue("items", list);
        return cast();
    }

}
