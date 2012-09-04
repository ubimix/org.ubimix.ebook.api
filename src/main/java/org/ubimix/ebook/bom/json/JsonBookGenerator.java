/**
 * 
 */
package org.ubimix.ebook.bom.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;
import org.ubimix.ebook.bem.IBookManifestListener;
import org.ubimix.ebook.bem.IBookMetadataListener;
import org.ubimix.ebook.bem.IBookTocListener;
import org.ubimix.ebook.bem.IBookVisitor.IBookListener;
import org.ubimix.ebook.bom.IBookManifest.IBookManifestItem;
import org.ubimix.ebook.bom.IBookToc.IBookTocItem;
import org.ubimix.ebook.bom.json.JsonBookManifest.JsonBookManifestItem;
import org.ubimix.ebook.bom.json.JsonBookToc.JsonBookTocItem;
import org.ubimix.ebook.io.IOutput;

/**
 * @author kotelnikov
 */
public class JsonBookGenerator implements IBookListener {

    private JsonBook fBook = new JsonBook();

    private JsonBookToc fToc = new JsonBookToc();

    /**
     * 
     */
    public JsonBookGenerator() {
    }

    /**
     * @see org.ubimix.ebook.bem.IBookVisitor.IBookListener#begin()
     */
    public void begin() {
    }

    /**
     * @see org.ubimix.ebook.bem.IBookVisitor.IBookListener#end()
     */
    public void end() {
    }

    public JsonBook getBook() {
        return fBook;
    }

    /**
     * @see org.ubimix.ebook.bem.IBookVisitor.IBookListener#getManifestListener()
     */
    public IBookManifestListener getManifestListener() {
        return new IBookManifestListener() {

            private List<BookId> fList = new ArrayList<BookId>();

            private List<IBookManifestItem> fManifestItems = new ArrayList<IBookManifestItem>();

            public void beginBookManifest() {
                // TODO Auto-generated method stub

            }

            public void endBookManifest() {
                JsonBookManifest manifest = new JsonBookManifest()
                    .setItems(fManifestItems);
                fBook.setManifest(manifest);
                JsonBookSpine spine = new JsonBookSpine().setSectionIds(fList);
                fBook.setSpine(spine);
            }

            public IOutput onBookEntry(
                Uri itemPath,
                BookId itemId,
                String itemMediaType) {
                JsonBookManifestItem item = new JsonBookManifestItem()
                    .setHref(itemPath)
                    .setID(itemId)
                    .setMediaType(itemMediaType);
                fManifestItems.add(item);
                return null;
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

    /**
     * @see org.ubimix.ebook.bem.IBookVisitor.IBookListener#getMetadataListener()
     */
    public IBookMetadataListener getMetadataListener() {
        return new IBookMetadataListener() {

            private JsonBookMeta fMetadata = new JsonBookMeta();

            public void beginBookMetadata(
                BookId bookId,
                String bookTitle,
                String bookCreator,
                String bookLanguage) {
                fMetadata
                    .setBookIdentifier(bookId)
                    .setBookTitle(bookTitle)
                    .setBookCreator(bookCreator)
                    .setBookLanguage(bookLanguage);
            }

            public void endBookMetadata() {
                fBook.setMetadata(fMetadata);
            }

            public void onBookMetadata(String name, String value) {
                fMetadata.setValue(name, value);
            }

        };
    }

    public JsonBookToc getToc() {
        return fToc;
    }

    /**
     * @see org.ubimix.ebook.bem.IBookVisitor.IBookListener#getTocListener()
     */
    public IBookTocListener getTocListener() {
        return new IBookTocListener() {

            private Stack<List<IBookTocItem>> fChildren = new Stack<List<IBookTocItem>>();

            private Stack<JsonBookTocItem> fParents = new Stack<JsonBookTocItem>();

            public void beginToc() {
                fChildren.push(new ArrayList<IBookTocItem>());
            }

            public void beginTocItem(Uri path, String label) {
                List<IBookTocItem> list = fChildren.peek();
                JsonBookTocItem item = new JsonBookTocItem()
                    .setLabel(label)
                    .setContentHref(path);
                list.add(item);
                fParents.push(item);
                fChildren.push(new ArrayList<IBookTocItem>());
            }

            public void endToc() {
                List<IBookTocItem> tocItems = fChildren.pop();
                fToc.setTocItems(tocItems);
            }

            public void endTocItem() {
                JsonBookTocItem parent = fParents.pop();
                List<IBookTocItem> children = fChildren.pop();
                if (!children.isEmpty()) {
                    parent.setChildren(children);
                }
            }
        };
    }

}
