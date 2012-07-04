/**
 * 
 */
package org.webreformatter.ebook.bom.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.webreformatter.commons.digests.Sha1Digest;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.IBookProvider;
import org.webreformatter.ebook.bom.IBookSection;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.io.IInput;
import org.webreformatter.ebook.io.server.FileStore;
import org.webreformatter.ebook.io.server.StreamToInput;
import org.webreformatter.ebook.io.server.UnzipUtil;
import org.webreformatter.ebook.io.server.UnzipUtil.IProgressListener;

/**
 * @author kotelnikov
 */
public class EPubBookProvider implements IBookProvider {

    private File fBookDir;

    private Map<Uri, EpubReader> fReaders = new HashMap<Uri, EpubReader>();

    private File fWorkDir;

    private FileStore fXmlEntryStore;

    public EPubBookProvider(File workdir, File bookDir, File xmlEntityDir) {
        fWorkDir = workdir;
        fBookDir = bookDir;
        fXmlEntryStore = new FileStore(xmlEntityDir);

    }

    private void copy(InputStream input, OutputStream out) throws IOException {
        try {
            try {
                byte[] buf = new byte[1024 * 10];
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                input.close();
            }
        } finally {
            out.close();
        }
    }

    public IBook getBook(Uri bookHref) {
        try {
            EpubReader r = getBookReader(bookHref);
            EPubBook book = r != null ? r.getBook() : null;
            return book;
        } catch (Throwable t) {
            throw EPubXml.onError("Can not load a book. Book reference: '"
                + bookHref
                + "'.", t);
        }
    }

    public EpubReader getBookReader(Uri bookUrl) throws IOException {
        EpubReader reader = fReaders.get(bookUrl);
        if (reader == null) {
            String scheme = bookUrl.getScheme();
            File targetDir = getTargetDir(bookUrl);
            FileStore bookStore = new FileStore(targetDir);
            if (!bookStore.exists()) {
                File bookFile = new File(fBookDir, bookUrl
                    .getPath()
                    .getFileName());
                boolean local = scheme == null || "file".equals(scheme);
                if (!local) {
                    bookFile.getParentFile().mkdirs();
                    URL url = new URL(bookUrl.toString());
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (X11; Linux i686) "
                            + "AppleWebKit/535.1 (KHTML, like Gecko) "
                            + "Chrome/14.0.835.202 Safari/535.1");
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    try {
                        FileOutputStream out = new FileOutputStream(bookFile);
                        try {
                            copy(input, out);
                        } finally {
                            out.close();
                        }
                    } finally {
                        input.close();
                    }
                }
                InputStream input = new FileInputStream(bookFile);
                try {
                    StreamToInput in = new StreamToInput(input);
                    UnzipUtil.unzip(in, bookStore, new IProgressListener() {

                        public void begin() {
                            System.out.println("Start unzipping...");
                        }

                        public void end() {
                            System.out.println("Unzipped.");
                        }

                        public boolean onUpdate(
                            String name,
                            long size,
                            long time,
                            String comment) {
                            System.out
                                .println(" * " + name + " (" + size + ")");
                            return true;
                        }
                    });
                } finally {
                    input.close();
                }
            }
            reader = new EpubReader(bookStore, fXmlEntryStore);
            fReaders.put(bookUrl, reader);
        }
        return reader;
    }

    public IInput getBookResource(Uri bookHref, Uri resourceRef) {
        try {
            EpubReader r = getBookReader(bookHref);
            if (r == null) {
                throw new IllegalArgumentException("Resource not found: "
                    + bookHref);
            }
            return r.getBookResource(resourceRef);
        } catch (Throwable t) {
            throw EPubXml.onError("Can not load TOC of a book. "
                + "Book reference: '"
                + bookHref
                + "'.", t);
        }
    }

    public IBookSection getBookSection(Uri bookHref, Uri sectionHref) {
        try {
            EpubReader r = getBookReader(bookHref);
            sectionHref = sectionHref.getBuilder().setFragment(null).build();
            EPubSection bookSection = null;
            if (r != null) {
                bookSection = r.getBookSection(sectionHref);
                bookSection.updateReferences(sectionHref);
            }
            return bookSection;
        } catch (Throwable t) {
            throw EPubXml.onError("Can not load a book section. "
                + "Book reference: '"
                + bookHref
                + "'. "
                + "Section reference: '"
                + sectionHref
                + "'.", t);
        }
    }

    public IBookToc getBookToc(Uri bookHref) {
        try {
            EpubReader r = getBookReader(bookHref);
            EPubToc bookToc = r != null ? r.getBookToc() : null;
            return bookToc;
        } catch (Throwable t) {
            throw EPubXml.onError("Can not load TOC of a book. "
                + "Book reference: '"
                + bookHref
                + "'.", t);
        }
    }

    private File getTargetDir(Uri bookUrl) {
        Sha1Digest digest = Sha1Digest
            .builder()
            .update(bookUrl.toString())
            .build();
        File dir = new File(fWorkDir, digest.toString());
        return dir;
    }
}
