package org.webreformatter.ebook.bem;


import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.BookId;
import org.webreformatter.ebook.io.IOutput;

/**
 * This listener is used to notify about individual book entries.
 * 
 * @author kotelnikov
 */
public interface IBookManifestListener {

    /**
     * This method is used to notify about the beginning of the manifest.
     */
    void beginBookManifest();

    /**
     * This method is used to notify about the end of the manifest
     */
    void endBookManifest();

    /**
     * Notifies about a new entry of the book.
     * 
     * @param itemPath the path in the book; this path
     * @param itemId a unique identifier of this entry
     * @param itemMediaType the MIME type of this entry
     * @return an output stream where the content of the entry should be
     *         written; this method could return <code>null</code> if the
     *         content should not be copied.
     */
    IOutput onBookEntry(
        Uri itemPath,
        BookId itemId,
        String itemMediaType);

    /**
     * Notifies about a new text entry of the book.
     * 
     * @param itemPath the path in the book; this path
     * @param itemId a unique identifier of this entry
     * @param readOrderIndex the order in the book; if this value is less than 0
     *        then this entry is not
     * @return an output stream where the content of the notified section should
     *         be written; this method could return <code>null</code> if the
     *         content should not be copied.
     */
    IOutput onBookSection(
        Uri itemPath,
        BookId itemId,
        int readOrderIndex);

}