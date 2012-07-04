package org.webreformatter.ebook.bom.epub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.commons.xml.XmlWrapper;

public class EPubXml extends XmlWrapper {

    /**
     * The logger instance used to report all errors produced by this class
     */
    private static Logger log = Logger.getLogger(EPubXml.class.getName());

    public static XmlContext newXmlContext() {
        return XmlContext.builder(
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
            "http://www.daisy.org/z3986/2005/ncx/").build();
    }

    protected static RuntimeException onError(String msg, Throwable t) {
        if (t instanceof EPubException) {
            return (EPubException) t;
        }
        log.log(Level.FINE, msg, t);
        throw new EPubException(msg, t);
    }

    public EPubXml(Node node, XmlContext context) {
        super(node, context);
    }

    protected List<XmlWrapper> addElements(String name, int count) {
        return addElements(name, count, XmlWrapper.class);
    }

    protected <T extends XmlWrapper> List<T> addElements(
        String name,
        int count,
        Class<T> type) {
        try {
            List<T> result = new ArrayList<T>();
            for (int i = 0; i < count; i++) {
                T wrapper = appendElement(name, type);
                result.add(wrapper);
            }
            return result;
        } catch (Throwable t) {
            throw onError(
                "Can not create new elements. Name: '" + name + "'.",
                t);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends EPubXml> T cast() {
        return (T) this;
    }

    protected String getString(String path) {
        try {
            return evalStr(path);
        } catch (Throwable t) {
            throw onError("Can not load metadata", t);
        }
    }

    protected <T extends EPubXml> T getXml(String path, Class<T> type) {
        try {
            return eval(path, type);
        } catch (Throwable t) {
            throw onError("Can not load metadata", t);
        }
    }

    protected <T extends EPubXml> T getXmlCopy(String path, Class<T> type) {
        try {
            T r = eval(path, type);
            return r != null ? r.createCopy(type) : null;
        } catch (Throwable t) {
            throw onError("Can not load metadata", t);
        }
    }

    protected <T extends EPubXml> List<T> getXmlList(String path, Class<T> type) {
        try {
            return evalList(path, type);
        } catch (Throwable t) {
            throw onError("Can not load metadata", t);
        }
    }

    protected void resolvePaths(Uri basePath, String expr, String attr) {
        try {
            List<XmlWrapper> references = evalList(expr);
            for (XmlWrapper ref : references) {
                String attrValue = ref.getAttribute(attr);
                if (attrValue != null) {
                    Uri path = new Uri(attrValue);
                    path = basePath.getResolved(path);
                    ref.setAttribute(attr, path.toString());
                }
            }
        } catch (Throwable t) {
            throw EPubXml.onError("Can not resolve relative references", t);
        }
    }

    protected XmlWrapper setTextElement(String name, String text) {
        return setTextElement(name, text, XmlWrapper.class);
    }

    protected <T extends XmlWrapper> T setTextElement(
        String name,
        String text,
        Class<T> type) {
        try {
            T wrapper = getOrCreateElement(name, type);
            if (text != null) {
                wrapper.appendText(text);
            }
            return wrapper;
        } catch (Throwable t) {
            throw onError("Can not set text in the element. Element name: '"
                + name
                + "'. Text: '"
                + text
                + "'.", t);
        }
    }

}