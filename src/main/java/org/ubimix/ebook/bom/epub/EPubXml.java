package org.ubimix.ebook.bom.epub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.IHasValueMap;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlText;

public class EPubXml extends XmlElement {

    public static Map<String, String> checkEpubNamespaces() {
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        checkEpubNamespaces(map);
        return map;
    }

    public static void checkEpubNamespaces(Map<?, ?> map) {
        toMap(
            map,
            "odc",
            "urn:oasis:names:tc:opendocument:xmlns:container",
            "opf",
            "http://www.idpf.org/2007/opf",
            "xhtml",
            "http://www.w3.org/1999/xhtml",
            "xsi",
            "http://www.w3.org/2001/XMLSchema-instance",
            "dc",
            "http://purl.org/dc/elements/1.1/",
            // Used by TOC files
            "ncx",
            "http://www.daisy.org/z3986/2005/ncx/");
    }

    public static void checkEpubNamespaces(XmlElement e) {
        checkEpubNamespaces(e.getMap());
    }

    public static void toMap(Map<?, ?> map, String... values) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> m = (Map<Object, Object>) map;
        for (int i = 0; i < values.length;) {
            String key = values[i++];
            String value = i < values.length ? values[i++] : null;
            m.put(key, value);
        }
    }

    public static Map<String, String> toMap(String... values) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        toMap(map, values);
        return map;
    }

    public EPubXml(IHasValueMap object) {
        super(object);
    }

    public EPubXml(String name) {
        super(name);
    }

    public EPubXml(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    protected List<XmlElement> addElements(String name, int count) {
        List<XmlElement> result = new ArrayList<XmlElement>();
        for (int i = 0; i < count; i++) {
            XmlElement wrapper = new XmlElement(name);
            result.add(wrapper);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <T extends EPubXml> T cast() {
        return (T) this;
    }

    protected XmlElement setTextElement(String name, String text) {
        XmlElement node = getOrCreateElement(name);
        if (text != null) {
            node.setChildren(new XmlText(text));
        }
        return node;
    }

}