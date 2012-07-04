/**
 * 
 */
package org.webreformatter.ebook.bom.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.webreformatter.commons.digests.Sha1Digest;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.commons.xml.XmlWrapper.XmlContext;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.bom.IBookProvider.IBookReader;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubManifest;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubManifest.EPubManifestItem;
import org.webreformatter.ebook.bom.epub.EPubBook.EPubSpine;
import org.webreformatter.ebook.io.IInput;
import org.webreformatter.ebook.io.IStore;
import org.webreformatter.ebook.io.InOutUtil;
import org.webreformatter.ebook.io.server.InputToStream;
import org.webreformatter.ebook.io.server.StreamToInput;
import org.webreformatter.ebook.io.server.StreamToOutput;
import org.webreformatter.ebook.io.server.UnzipUtil;
import org.webreformatter.ebook.io.server.UnzipUtil.IProgressListener;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author kotelnikov
 */
public class EpubReader implements IBookReader {

    private static class CachingEntityResolver implements EntityResolver {

        private Map<String, byte[]> fCache = new LinkedHashMap<String, byte[]>() {
            private static final long serialVersionUID = -4705808307813538924L;

            private long fFullSize = 0;

            @Override
            public void clear() {
                super.clear();
                fFullSize = 0;
            }

            private void decFullSize(byte[] buf) {
                fFullSize -= buf != null ? buf.length : 0;
            }

            private void incFullSize(byte[] buf) {
                fFullSize += buf != null ? buf.length : 0;
            }

            @Override
            public byte[] put(String key, byte[] value) {
                incFullSize(value);
                byte[] removed = super.put(key, value);
                decFullSize(removed);
                return removed;
            }

            @Override
            public boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
                byte[] buf = eldest.getValue();
                boolean remove = fFullSize > getMaxCacheSize();
                if (remove) {
                    decFullSize(buf);
                }
                return remove;
            }

        };

        private IStore fEntityStore;

        private Uri fPrevUri;

        public CachingEntityResolver(IStore entityStore) {
            fEntityStore = entityStore;
        }

        private String getBase64Name(Uri systemId) throws IOException {
            Sha1Digest digest = Sha1Digest
                .builder()
                .update(systemId.toString())
                .build();
            String str = digest.toString();
            return str + ".txt";
        }

        private IInput getFromCache(String publicId) {
            byte[] buf = fCache.get(publicId);
            if (buf == null) {
                return null;
            }
            return new StreamToInput(new ByteArrayInputStream(buf));
        }

        private IInput getFromStore(String publicId, String systemId)
            throws IOException {

            Uri url = new Uri(systemId);
            boolean local = "file".equals(url.getScheme());
            if (local) {
                String fileName = url.getPath().getFileName();
                url = fPrevUri.getResolved(fileName);
            }
            String cacheId = getBase64Name(url);
            IInput input = fEntityStore.getInput(cacheId);
            if (input == null) {
                input = getRemoteStream(url);
                if (input != null) {
                    try {
                        fEntityStore.getOutput(cacheId);
                    } finally {
                        input.close();
                    }
                    input = fEntityStore.getInput(cacheId);
                }
            }
            if (input != null) {
                fPrevUri = url;
            }
            return input;
        }

        protected long getMaxCacheSize() {
            return 1024 * 1024 * 3;
        }

        private IInput getRemoteStream(Uri uri) throws IOException {
            URL u = new URL(uri.toString());
            try {
                IInput stream = new StreamToInput(u.openStream());
                return stream;
            } catch (FileNotFoundException e) {
                return null;
            }
        }

        private void putToCache(String publicId, IInput input)
            throws IOException {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                StreamToOutput o = new StreamToOutput(out);
                InOutUtil.copy(input, o);
                byte[] buf = out.toByteArray();
                fCache.put(publicId, buf);
            } finally {
                input.close();
            }
        }

        public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException,
            IOException {
            IInput input = getFromCache(publicId);
            if (input == null) {
                input = getFromStore(publicId, systemId);
                putToCache(publicId, input);
                input = getFromCache(publicId);
            }
            final IInput i = input;
            InputSource source = new InputSource(new InputToStream(i));
            return source;
        }
    }

    private DocumentBuilder fBuilder;

    private EPubContainer fContainer;

    private Uri fContentDeclarationPath;

    private EntityResolver fEntityResolver;

    private EPubBook fEPubBook;

    private EPubToc fEPubToc;

    private IStore fStore;

    private Uri fTocPath;

    private XmlContext fXmlContext;

    public EpubReader(IStore bookStore, IStore xmlEntityStore) {
        fXmlContext = EPubXml.newXmlContext();
        fStore = bookStore;
        fEntityResolver = new CachingEntityResolver(xmlEntityStore);
        try {
            fBuilder = XmlWrapper.getDocumentBuilder();
        } catch (XmlException t) {
            throw EPubXml.onError("Can not create a document builder.", t);
        }
        fBuilder.setEntityResolver(fEntityResolver);
    }

    public EPubBook getBook() {
        if (fEPubBook == null) {
            try {
                EPubContainer container = getEPubContainer();
                fContentDeclarationPath = container.getContentDeclarationPath();
                fEPubBook = getXml(fContentDeclarationPath, EPubBook.class);
                fEPubBook.updatePaths(fContentDeclarationPath);
            } catch (Throwable t) {
                throw EPubXml.onError("Can not read an EPub book.", t);
            }
        }
        return fEPubBook;
    }

    public IInput getBookResource(Uri resourceRef) throws IOException {
        final IInput in = fStore.getInput(resourceRef.toString());
        if (in == null) {
            return null;
        }
        return new IInput() {

            public void close() throws IOException {
                in.close();
            }

            public int read(byte[] buf, int offset, int len) throws IOException {
                return in.read(buf, offset, len);
            }

        };
    }

    public EPubSection getBookSection(Uri sectionRef) {
        EPubSection section = getXml(sectionRef, EPubSection.class);
        return section;
    }

    public EPubToc getBookToc() {
        if (fEPubToc == null) {
            EPubBook book = getBook();
            EPubManifest manifest = book.getManifest();
            EPubSpine spine = book.getSpine();
            BookId tocId = spine.getTocId();
            EPubManifestItem tocManifestItem = manifest.getItemById(tocId);
            fTocPath = tocManifestItem.getHref();
            fEPubToc = getXml(fTocPath, EPubToc.class);
            fEPubToc.updatePaths(fTocPath);
        }
        return fEPubToc;
    }

    public Uri getContentDeclarationPath() {
        getBook();
        return fContentDeclarationPath;
    }

    protected EPubContainer getEPubContainer() {
        if (fContainer == null) {
            fContainer = getXml(
                EPubContainer.META_INF_CONTAINER_PATH,
                EPubContainer.class);
        }
        return fContainer;
    }

    public Uri getTocPath() {
        getBookToc();
        return fTocPath;
    }

    private <T extends XmlWrapper> T getXml(Uri path, Class<T> type) {
        try {
            IInput in = fStore.getInput(path.toString());
            if (in == null) {
                return null;
            }
            try {
                InputToStream input = new InputToStream(in);
                Reader reader = new InputStreamReader(input);
                InputSource source = new InputSource(reader);
                Document doc = fBuilder.parse(source);
                T wrapper = fXmlContext.wrap(doc, type);
                return wrapper;
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            throw EPubXml.onError("Can not read an XML file. Path: '"
                + path
                + "'.", t);
        }
    }

    public void unzip(IInput input, IProgressListener progress)
        throws IOException {
        UnzipUtil.unzip(input, fStore, progress);
    }

}
