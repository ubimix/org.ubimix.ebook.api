package org.ubimix.ebook.bom.json;

import org.ubimix.ebook.bom.IBook;
import org.ubimix.ebook.bom.IBookManifest;

/**
 * @author kotelnikov
 */
public class JsonBook extends JsonBookBase implements IBook {

    public static IJsonValueFactory<JsonBook> FACTORY = new IJsonValueFactory<JsonBook>() {
        public JsonBook newValue(Object object) {
            return new JsonBook().setJsonObject(object);
        }
    };;

    public static final IJsonValueFactory<IBook> I_FACTORY = new IJsonValueFactory<IBook>() {
        public IBook newValue(Object object) {
            JsonBook result = new JsonBook();
            result.setJsonObject(object);
            return result;
        }
    };

    public IBookManifest getManifest() {
        return getValue("manifest", JsonBookManifest.FACTORY);
    }

    public JsonBookMeta getMetadata() {
        return getValue("meta", JsonBookMeta.FACTORY);
    }

    public JsonBookSpine getSpine() {
        return getValue("spine", JsonBookSpine.FACTORY);
    }

    public JsonBook setManifest(JsonBookManifest manifest) {
        setValue("manifest", manifest);
        return cast();
    }

    public JsonBook setMetadata(JsonBookMeta meta) {
        setValue("meta", meta);
        return cast();
    }

    public JsonBook setSpine(JsonBookSpine value) {
        setValue("spine", value);
        return cast();
    }

}
