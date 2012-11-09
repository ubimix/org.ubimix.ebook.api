/**
 * 
 */
package org.ubimix.ebook.bom.epub;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.ubimix.commons.parser.UnboundedCharStream;
import org.ubimix.commons.parser.UnboundedCharStream.ICharLoader;
import org.ubimix.commons.parser.stream.StreamCharLoader;
import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bom.IBookProvider.IBookReader;
import org.ubimix.ebook.bom.epub.EPubBook.EPubManifest;
import org.ubimix.ebook.bom.epub.EPubBook.EPubManifest.EPubManifestItem;
import org.ubimix.ebook.bom.epub.EPubBook.EPubSpine;
import org.ubimix.ebook.io.IInput;
import org.ubimix.ebook.io.IStore;
import org.ubimix.ebook.io.server.InputToStream;
import org.ubimix.ebook.io.server.UnzipUtil;
import org.ubimix.ebook.io.server.UnzipUtil.IProgressListener;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class EpubReader extends EPubIO implements IBookReader {

    private EPubContainer fContainer;

    private Uri fContentDeclarationPath;

    private EPubBook fEPubBook;

    private EPubToc fEPubToc;

    private IStore fStore;

    private Uri fTocPath;

    public EpubReader(IStore bookStore, IStore xmlEntityStore) {
        fStore = bookStore;
    }

    @Override
    public EPubBook getBook() {
        if (fEPubBook == null) {
            EPubContainer container = getEPubContainer();
            fContentDeclarationPath = container.getContentDeclarationPath();
            XmlElement e = getXml(fContentDeclarationPath);
            fEPubBook = new EPubBook(e);
            fEPubBook.updatePaths(fContentDeclarationPath);
        }
        return fEPubBook;
    }

    @Override
    public IInput getBookResource(Uri resourceRef) throws IOException {
        final IInput in = fStore.getInput(resourceRef.toString());
        if (in == null) {
            return null;
        }
        return new IInput() {

            @Override
            public void close() throws IOException {
                in.close();
            }

            @Override
            public int read(byte[] buf, int offset, int len) throws IOException {
                return in.read(buf, offset, len);
            }

        };
    }

    @Override
    public EPubSection getBookSection(Uri sectionRef) {
        XmlElement e = getXml(sectionRef);
        EPubSection section = new EPubSection(e);
        return section;
    }

    @Override
    public EPubToc getBookToc() {
        if (fEPubToc == null) {
            EPubBook book = getBook();
            EPubManifest manifest = book.getManifest();
            EPubSpine spine = book.getSpine();
            BookId tocId = spine.getTocId();
            EPubManifestItem tocManifestItem = manifest.getItemById(tocId);
            fTocPath = tocManifestItem.getHref();
            XmlElement e = getXml(fTocPath);
            fEPubToc = new EPubToc(e);
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
            XmlElement e = getXml(EPubContainer.META_INF_CONTAINER_PATH);
            fContainer = new EPubContainer(e);
        }
        return fContainer;
    }

    public Uri getTocPath() {
        getBookToc();
        return fTocPath;
    }

    private XmlElement getXml(Uri path) {
        try {
            IInput in = fStore.getInput(path.toString());
            if (in == null) {
                return null;
            }
            try {
                InputToStream input = new InputToStream(in);
                Reader reader = new InputStreamReader(input);
                ICharLoader loader = new StreamCharLoader(reader);
                UnboundedCharStream stream = new UnboundedCharStream(loader);
                XmlElement e = XmlElement.parse(stream);
                return e;
            } finally {
                in.close();
            }
            // FIXME: fix/normalize all namespaces in the imported content
        } catch (Throwable t) {
            throw onError("Can not read an XML file. Path: '" + path + "'.", t);
        }
    }

    public void unzip(IInput input, IProgressListener progress)
        throws IOException {
        UnzipUtil.unzip(input, fStore, progress);
    }

}
