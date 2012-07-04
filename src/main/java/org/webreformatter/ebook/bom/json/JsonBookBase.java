/**
 * 
 */
package org.webreformatter.ebook.bom.json;

import org.webreformatter.commons.json.JsonObject;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.BookId;

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
