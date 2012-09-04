/**
 * 
 */
package org.ubimix.ebook.bom.json;

import org.ubimix.commons.json.JsonObject;
import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;

/**
 * @author kotelnikov
 */
public class JsonBookBase extends JsonObject {

    public static IJsonValueFactory<BookId> ID_FACTORY = new IJsonValueFactory<BookId>() {
        public BookId newValue(Object object) {
            return new BookId(object + "");
        }
    };

    public static IJsonValueFactory<Uri> URI_FACTORY = new IJsonValueFactory<Uri>() {
        public Uri newValue(Object object) {
            return new Uri(object + "");
        }
    };

    @SuppressWarnings("unchecked")
    protected <T extends JsonBookBase> T cast() {
        return (T) this;
    }

}
