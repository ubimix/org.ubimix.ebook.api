package org.webreformatter.ebook.bom.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBookToc;

/**
 * @author kotelnikov
 */
public class JsonBookToc extends JsonBookBase implements IBookToc {

    public static class JsonBookTocItem extends JsonBookBase
        implements
        IBookTocItem {

        public static IJsonValueFactory<JsonBookTocItem> FACTORY = new IJsonValueFactory<JsonBookTocItem>() {
            public JsonBookTocItem newValue(Object object) {
                return new JsonBookTocItem().setJsonObject(object);
            }
        };

        public List<IBookTocItem> getChildren() {
            List<JsonBookTocItem> list = getList("children", FACTORY);
            if (list == null) {
                list = Collections.emptyList();
            }
            return new ArrayList<IBookToc.IBookTocItem>(list);
        }

        public Uri getContentHref() {
            return getValue("href", JsonBookBase.URI_FACTORY);
        }

        public String getLabel() {
            return getString("label");
        }

        public JsonBookTocItem setChildren(JsonBookTocItem... children) {
            setValue("children", Arrays.asList(children));
            return cast();
        }

        public JsonBookTocItem setChildren(List<IBookTocItem> children) {
            if (children != null && !children.isEmpty()) {
                setValue("children", children);
            } else {
                removeValue("children");
            }
            return cast();
        }

        public JsonBookTocItem setContentHref(Uri value) {
            setValue("href", value);
            return cast();
        }

        public JsonBookTocItem setLabel(String value) {
            setValue("label", value);
            return cast();
        }
    }

    public static IJsonValueFactory<JsonBookToc> FACTORY = new IJsonValueFactory<JsonBookToc>() {
        public JsonBookToc newValue(Object object) {
            return new JsonBookToc().setJsonObject(object);
        }
    };

    public static IJsonValueFactory<IBookToc> I_FACTORY = new IJsonValueFactory<IBookToc>() {
        public IBookToc newValue(Object object) {
            JsonBookToc result = new JsonBookToc().setJsonObject(object);
            return result;
        }
    };

    public List<IBookTocItem> getTocItems() {
        List<JsonBookTocItem> list = getList(
            "navPoint",
            JsonBookTocItem.FACTORY);
        if (list == null) {
            list = Collections.emptyList();
        }
        return new ArrayList<IBookToc.IBookTocItem>(list);
    }

    public JsonBookToc setTocItems(JsonBookTocItem... children) {
        setValue("navPoint", Arrays.asList(children));
        return cast();
    }

    public JsonBookToc setTocItems(List<IBookTocItem> children) {
        if (children != null && !children.isEmpty()) {
            setValue("navPoint", children);
        } else {
            removeValue("navPoint");
        }
        return cast();
    }

}
