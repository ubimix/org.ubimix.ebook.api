package org.webreformatter.ebook.bem;

import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.bem.IBookVisitor.IBookListener;
import org.webreformatter.ebook.io.IOutput;

/**
 * @author kotelnikov
 */
public abstract class PrintBookListener implements IBookListener {

    private IBookListener fListener;

    private int fShift;

    public PrintBookListener() {
        this(null);
    }

    public PrintBookListener(IBookListener listener) {
        fListener = listener;
    }

    public void begin() {
        inc();
        if (fListener != null) {
            fListener.begin();
        }
    }

    private void dec() {
        fShift--;
    }

    public void end() {
        dec();
        if (fListener != null) {
            fListener.end();
        }
    }

    public IBookManifestListener getManifestListener() {
        final IBookManifestListener listener = fListener != null ? fListener
            .getManifestListener() : null;
        return new IBookManifestListener() {

            public void beginBookManifest() {
                if (listener != null) {
                    listener.beginBookManifest();
                }
                printShifted("Begin book manifest");
                inc();
            }

            public void endBookManifest() {
                dec();
                printShifted("End book manifest");
                if (listener != null) {
                    listener.endBookManifest();
                }
            }

            public IOutput onBookEntry(
                Uri itemPath,
                BookId itemId,
                String itemMediaType) {
                printShifted(".) "
                    + itemPath
                    + " ["
                    + itemId
                    + "] Type: <"
                    + itemMediaType
                    + ">");
                IOutput output = (listener != null) ? listener.onBookEntry(
                    itemPath,
                    itemId,
                    itemMediaType) : null;
                return output;
            }

            public IOutput onBookSection(
                Uri itemPath,
                BookId itemId,
                int readOrderIndex) {
                printShifted(readOrderIndex
                    + ") "
                    + itemPath
                    + " ["
                    + itemId
                    + "]");
                IOutput output = (listener != null) ? listener.onBookSection(
                    itemPath,
                    itemId,
                    readOrderIndex) : null;
                return output;
            }
        };
    }

    public IBookMetadataListener getMetadataListener() {
        final IBookMetadataListener listener = fListener != null ? fListener
            .getMetadataListener() : null;
        return new IBookMetadataListener() {

            public void beginBookMetadata(
                BookId bookId,
                String bookTitle,
                String bookCreator,
                String bookLanguage) {
                printShifted("Begin metadata");
                inc();
                onBookMetadata("id", bookId + "");
                onBookMetadata("title", bookTitle);
                onBookMetadata("creator", bookCreator);
                onBookMetadata("language", bookLanguage);
                if (listener != null) {
                    listener.beginBookMetadata(
                        bookId,
                        bookTitle,
                        bookCreator,
                        bookLanguage);
                }
            }

            public void endBookMetadata() {
                dec();
                printShifted("End metadata");
                if (listener != null) {
                    listener.endBookMetadata();
                }
            }

            public void onBookMetadata(String name, String value) {
                printShifted(name + "=" + value);
            }
        };
    }

    public IBookTocListener getTocListener() {
        final IBookTocListener listener = fListener != null ? fListener
            .getTocListener() : null;
        return new IBookTocListener() {

            public void beginToc() {
                printShifted("Begin TOC");
                inc();
                if (listener != null) {
                    listener.beginToc();
                }
            }

            public void beginTocItem(Uri path, String label) {
                printShifted("* " + label + " <" + path + ">");
                inc();
                if (listener != null) {
                    listener.beginTocItem(path, label);
                }
            }

            public void endToc() {
                if (listener != null) {
                    listener.endToc();
                }
                dec();
                printShifted("End TOC");

            }

            public void endTocItem() {
                dec();
                if (listener != null) {
                    listener.endTocItem();
                }

            }
        };
    }

    private void inc() {
        fShift++;
    }

    protected abstract void println(String msg);

    protected void printShifted(String msg) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < fShift; i++) {
            buf.append("  ");
        }
        buf.append(msg);
        println(buf.toString());
    }

}