/**
 * 
 */
package org.ubimix.ebook.bom.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.bom.IBook;
import org.ubimix.ebook.bom.IBookProvider;
import org.ubimix.ebook.bom.IBookSection;
import org.ubimix.ebook.bom.IBookToc;
import org.ubimix.ebook.io.IInput;
import org.ubimix.ebook.io.server.FileStore;
import org.ubimix.ebook.io.server.StreamToInput;
import org.ubimix.ebook.io.server.UnzipUtil;
import org.ubimix.ebook.io.server.UnzipUtil.IProgressListener;

/**
 * @author kotelnikov
 */
public class EPubBookProvider extends EPubIO implements IBookProvider {

    protected static void appendByteToBuf(StringBuilder buf, int val) {
        String str = Integer.toHexString(val & 0xFF);
        if (str.length() < 2) {
            buf.append('0');
        }
        buf.append(str);
    }

    public static String getDigest(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(string.getBytes("UTF-8"));
            byte[] digestResult = digest.digest();
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < digestResult.length; i++) {
                appendByteToBuf(buf, digestResult[i]);
            }
            String result = buf.toString();
            return result;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

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
            throw onError("Can not load a book. Book reference: '"
                + bookHref
                + "'.", t);
        }
    }

    @Override
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

                        @Override
                        public void begin() {
                            System.out.println("Start unzipping...");
                        }

                        @Override
                        public void end() {
                            System.out.println("Unzipped.");
                        }

                        @Override
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
            throw onError("Can not load TOC of a book. "
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
            throw onError("Can not load a book section. "
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
            throw onError("Can not load TOC of a book. "
                + "Book reference: '"
                + bookHref
                + "'.", t);
        }
    }

    private File getTargetDir(Uri bookUrl) {
        String str = getDigest(bookUrl.toString());
        File dir = new File(fWorkDir, str);
        return dir;
    }
}
