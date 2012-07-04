package org.webreformatter.ebook.bom.json;

import org.webreformatter.ebook.bom.IBookSection;

/**
 * @author kotelnikov
 */
public class JsonBookSection extends JsonBookBase implements IBookSection {

    public static IJsonValueFactory<JsonBookSection> FACTORY = new IJsonValueFactory<JsonBookSection>() {
        public JsonBookSection newValue(Object object) {
            return new JsonBookSection().setJsonObject(object);
        }
    };;

    public static final IJsonValueFactory<IBookSection> I_FACTORY = new IJsonValueFactory<IBookSection>() {
        public IBookSection newValue(Object object) {
            JsonBookSection result = new JsonBookSection();
            result.setJsonObject(object);
            return result;
        }
    };

    public String getContent() {
        return getString("content");
    }

    public String getTitle() {
        return getString("title");
    }

    public JsonBookSection setContent(String content) {
        setValue("content", content);
        return cast();
    }

    public JsonBookSection setTitle(String title) {
        setValue("title", title);
        return cast();
    }

}
