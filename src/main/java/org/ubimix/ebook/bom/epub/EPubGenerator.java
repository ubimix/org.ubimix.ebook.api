package org.ubimix.ebook.bom.epub;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ubimix.commons.json.ext.FormattedDate;
import org.ubimix.commons.uri.Uri;
import org.ubimix.commons.xml.XmlException;
import org.ubimix.commons.xml.XmlWrapper;
import org.ubimix.commons.xml.XmlWrapper.XmlContext;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bem.IBookManifestListener;
import org.ubimix.ebook.bem.IBookMetadataListener;
import org.ubimix.ebook.bem.IBookTocListener;
import org.ubimix.ebook.bem.IBookVisitor.IBookListener;
import org.ubimix.ebook.bom.epub.EPubBook.EPubManifest;
import org.ubimix.ebook.bom.epub.EPubBook.EPubMetadata;
import org.ubimix.ebook.bom.epub.EPubBook.EPubSpine;
import org.ubimix.ebook.bom.epub.EPubToc.EPubTocItem;
import org.ubimix.ebook.io.IOutput;

/**
 * @author kotelnikov
 */
public class EPubGenerator implements IBookListener {

    private final static Uri EPUB_BOOK_FILE = new Uri("OPS/content.opf");

    private final static String EPUB_MIME_TYPE = "application/epub+zip";

    private final static String EPUB_MIME_TYPE_FILE = "mimetype";

    private final static Uri EPUB_TOC_FILE = new Uri("OPS/toc.ncx");

    private final static BookId EPUB_TOC_ID = new BookId("ncx");

    // http://idpf.org/epub/30/spec/epub30-publications.html#attrdef-package-unique-identifier
    // http://idpf.org/epub/linking/cfi/epub-cfi.html
    // http://en.wikipedia.org/wiki/EPUB

    final static String EPUB_UNIQUE_IDENTIFIER_IDREF = "bookid";

    private final static Logger log = Logger.getLogger(EPubGenerator.class
        .getName());

    private EPubBook fBook;

    private ZipOutputStream fOutput;

    private final File fOutputFile;

    private XmlContext fXmlContext;

    public EPubGenerator(File outputFile) {
        fOutputFile = outputFile;
    }

    public void begin() {
        try {
            fXmlContext = EPubXml.newXmlContext();
            fBook = EPubBook.newBook();
            fOutputFile.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(fOutputFile);
            fOutput = new ZipOutputStream(output);

            ZipEntry entry = new ZipEntry(EPUB_MIME_TYPE_FILE);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(EPUB_MIME_TYPE.length());

            byte[] bytes = EPUB_MIME_TYPE.getBytes();
            CRC32 crc = new CRC32();
            crc.update(bytes);
            entry.setCrc(crc.getValue());

            fOutput.putNextEntry(entry);
            fOutput.write(bytes);
            fOutput.closeEntry();

            EPubContainer container = EPubContainer
                .newEPubContainer(fXmlContext);
            container.setAttribute("version", "1.0");
            container.setContentDeclarationPath(EPUB_BOOK_FILE);
            writeEntry(EPubContainer.META_INF_CONTAINER_PATH, container);

        } catch (Throwable t) {
            throw onError("Can not create a new EPub book document.", t);
        }
    }

    public void end() {
        try {
            if (fOutput != null) {
                writeEntry(EPUB_BOOK_FILE, fBook);
                fOutput.close();
                fOutput = null;
            }
            fBook = null;
        } catch (Throwable t) {
            throw onError("Can not release EPub-related resources", t);
        }
    }

    public IBookManifestListener getManifestListener() {
        final EPubManifest manifest = fBook.getManifest(true);
        return new IBookManifestListener() {

            private final List<BookId> fList = new ArrayList<BookId>();

            public void beginBookManifest() {
                try {
                    Uri path = getRelativePath(EPUB_BOOK_FILE, EPUB_TOC_FILE);
                    manifest.addItem(path, "application/x-dtbncx+xml").setID(
                        EPUB_TOC_ID);
                } catch (Throwable t) {
                    throw onError("Can not add reference to TOC file", t);
                }
            }

            public void endBookManifest() {
                EPubSpine spine = fBook.getSpine(true);
                spine.setTocId(EPUB_TOC_ID);
                for (int i = 0; i < fList.size();) {
                    BookId id = fList.get(i);
                    if (id == null) {
                        fList.remove(i);
                    } else {
                        i++;
                    }
                }
                if (!fList.isEmpty()) {
                    spine.setSectionIds(fList);
                }
            }

            public IOutput onBookEntry(
                Uri itemPath,
                BookId itemId,
                String itemMediaType) {
                try {
                    Uri path = getRelativePath(EPUB_BOOK_FILE, itemPath);
                    manifest.addItem(path, itemMediaType).setID(itemId);
                    ZipEntry entry = new ZipEntry(itemPath.toString());
                    entry.setMethod(ZipEntry.DEFLATED);
                    fOutput.putNextEntry(entry);
                    return new IOutput() {

                        public void close() throws IOException {
                            fOutput.closeEntry();
                        }

                        public void write(byte[] buf, int offset, int len)
                            throws IOException {
                            fOutput.write(buf, offset, len);
                        }
                    };
                } catch (Exception e) {
                    throw onError("Can not add a manifest entry", e);
                }
            }

            public IOutput onBookSection(
                Uri itemPath,
                BookId itemId,
                int readOrderIndex) {
                if (readOrderIndex >= 0) {
                    while (fList.size() <= readOrderIndex) {
                        fList.add(null);
                    }
                    fList.set(readOrderIndex, itemId);
                }
                return onBookEntry(itemPath, itemId, "application/xhtml+xml");
            }
        };
    }

    public IBookMetadataListener getMetadataListener() {
        final EPubMetadata metadata = fBook.getMetadata(true);
        return new IBookMetadataListener() {

            public void beginBookMetadata(
                BookId bookId,
                String bookTitle,
                String bookCreator,
                String bookLanguage) {
                try {
                    metadata.setBookIdentifier(bookId);
                    metadata.setBookTitle(bookTitle);
                    metadata.setBookCreator(bookCreator);
                    metadata.setBookLanguage(bookLanguage);
                    // TODO: add modified date to the list of parameters
                    metadata.setModifiedDate(new FormattedDate(
                        "2011-01-01T12:00:00Z"));
                } catch (Throwable t) {
                    throw onError("Can not set book metadata", t);
                }
            }

            public void endBookMetadata() {
            }

            public void onBookMetadata(String name, String value) {
            }
        };
    }

    private Uri getRelativePath(Uri from, Uri to) {
        Uri result = from.getRelative(to);
        return result;
    }

    public IBookTocListener getTocListener() {
        try {
            final EPubToc toc = EPubToc.newToc(fXmlContext);
            return new IBookTocListener() {

                private final Stack<EPubTocItem> fStack = new Stack<EPubToc.EPubTocItem>();

                public void beginToc() {
                }

                public void beginTocItem(Uri path, String label) {
                    try {
                        EPubTocItem peek = getPeek();
                        EPubTocItem item;
                        if (peek == null) {
                            item = toc.addTocItem();
                        } else {
                            item = peek.addChild();
                        }
                        if (path != null) {
                            path = getRelativePath(EPUB_TOC_FILE, path);
                            item.setContentHref(path);
                        }
                        item.setLabel(label);
                        fStack.push(item);
                    } catch (XmlException t) {
                        throw onError("Can not create a new TOC item.", t);
                    }
                }

                public void endToc() {
                    try {
                        writeEntry(EPUB_TOC_FILE, toc);
                    } catch (Throwable t) {
                        throw onError("Can not create a TOC file.", t);
                    }
                }

                public void endTocItem() {
                    fStack.pop();
                }

                private EPubTocItem getPeek() {
                    return !fStack.isEmpty() ? fStack.peek() : null;
                }
            };
        } catch (Throwable t) {
            throw onError("Can not create a TOC file", t);
        }
    }

    private RuntimeException onError(String msg, Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        }
        log.log(Level.WARNING, msg, t);
        return new RuntimeException(msg, t);
    }

    private void writeEntry(Uri fileName, InputStream input) throws IOException {
        try {
            ZipEntry entry = new ZipEntry(fileName + "");
            entry.setMethod(ZipEntry.DEFLATED);
            fOutput.putNextEntry(entry);
            byte[] buf = new byte[1024 * 10];
            int len;
            while ((len = input.read(buf, 0, buf.length)) > 0) {
                fOutput.write(buf, 0, len);
            }
            fOutput.closeEntry();
        } finally {
            input.close();
        }
    }

    private void writeEntry(Uri fileName, XmlWrapper xml) throws IOException {
        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        buf.append(xml.toString());
        ByteArrayInputStream input = new ByteArrayInputStream(buf
            .toString()
            .getBytes("UTF-8"));
        writeEntry(fileName, input);
    }

}