package org.webreformatter.ebook.bom2bem;

import java.io.IOException;
import java.util.List;

import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.bem.IBookManifestListener;
import org.webreformatter.ebook.bem.IBookMetadataListener;
import org.webreformatter.ebook.bem.IBookTocListener;
import org.webreformatter.ebook.bem.IBookVisitor;
import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.IBook.IBookMetadata;
import org.webreformatter.ebook.bom.IBook.IBookSpine;
import org.webreformatter.ebook.bom.IBookManifest;
import org.webreformatter.ebook.bom.IBookManifest.IBookManifestItem;
import org.webreformatter.ebook.bom.IBookProvider.IBookReader;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.bom.IBookToc.IBookTocItem;
import org.webreformatter.ebook.io.IInput;
import org.webreformatter.ebook.io.IOutput;

/**
 * @author kotelnikov
 */
public class BookVisitor implements IBookVisitor {

    public static void visitMetadata(
        IBookMetadata metadata,
        IBookMetadataListener metadataListener) {
        if (metadataListener == null) {
            return;
        }
        BookId bookId = metadata.getBookIdentifier();
        String bookTitle = metadata.getBookTitle();
        String bookCreator = metadata.getBookCreator();
        String bookLanguage = metadata.getBookLanguage();
        metadataListener.beginBookMetadata(
            bookId,
            bookTitle,
            bookCreator,
            bookLanguage);
        try {
            // TODO: add all other metadata fields
            // metadataListener.onBookMetadata(name, value);
        } finally {
            metadataListener.endBookMetadata();
        }
    }

    public static void visitToc(IBookToc toc, IBookTocListener tocListener) {
        if (tocListener == null) {
            return;
        }
        tocListener.beginToc();
        try {
            visitTocItems(tocListener, toc.getTocItems());
        } finally {
            tocListener.endToc();
        }
    }

    private static void visitTocItems(
        IBookTocListener tocListener,
        List<IBookTocItem> tocItems) {
        if (tocListener == null || tocItems == null || tocItems.isEmpty()) {
            return;
        }
        for (IBookTocItem item : tocItems) {
            Uri path = item.getContentHref();
            String label = item.getLabel();
            tocListener.beginTocItem(path, label);
            try {
                visitTocItems(tocListener, item.getChildren());
            } finally {
                tocListener.endTocItem();
            }
        }
    }

    private IBookReader fReader;

    public BookVisitor(IBookReader reader) {
        fReader = reader;
    }

    public void visitBook(IBookListener listener) throws IOException {
        listener.begin();
        try {
            visitMetadata(listener.getMetadataListener());
            visitToc(listener.getTocListener());
            visitManifest(listener.getManifestListener());
        } finally {
            listener.end();
        }
    }

    private void visitManifest(IBookManifestListener manifestListener)
        throws IOException {
        if (manifestListener == null) {
            return;
        }
        manifestListener.beginBookManifest();
        try {
            IBook book = fReader.getBook();
            IBookManifest manifest = book.getManifest();
            IBookSpine spine = book.getSpine();
            List<BookId> readingOrder = spine.getSectionIds();
            List<IBookManifestItem> manifestItems = manifest.getItems();
            for (IBookManifestItem item : manifestItems) {
                String mediaType = item.getMediaType();
                if ("application/x-dtbncx+xml".equals(mediaType)) {
                    continue;
                }
                BookId itemId = item.getID();
                Uri href = item.getHref();
                IOutput output;
                if ("application/xhtml+xml".equals(mediaType)) {
                    int order = readingOrder.indexOf(itemId);
                    output = manifestListener
                        .onBookSection(href, itemId, order);
                } else {
                    output = manifestListener.onBookEntry(
                        href,
                        itemId,
                        mediaType);
                }
                if (output != null) {
                    try {
                        IInput input = fReader.getBookResource(href);
                        if (input != null) {
                            try {
                                byte[] buf = new byte[1024 * 10];
                                int len;
                                while ((len = input.read(buf, 0, buf.length)) > 0) {
                                    output.write(buf, 0, len);
                                }
                            } finally {
                                input.close();
                            }
                        }
                    } finally {
                        output.close();
                    }
                }
            }
        } finally {
            manifestListener.endBookManifest();
        }
    }

    private void visitMetadata(IBookMetadataListener metadataListener) {
        IBook book = fReader.getBook();
        IBookMetadata metadata = book.getMetadata();
        visitMetadata(metadata, metadataListener);
    }

    private void visitToc(IBookTocListener tocListener) {
        IBookToc toc = fReader.getBookToc();
        visitToc(toc, tocListener);
    }
}