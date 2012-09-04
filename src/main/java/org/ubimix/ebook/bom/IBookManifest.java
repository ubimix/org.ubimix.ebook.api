package org.ubimix.ebook.bom;

import java.util.List;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.BookId;

/**
 * This interface provides references to all resources contained in a e-book.
 * 
 * @author kotelnikov
 */
public interface IBookManifest {

    /**
     * Reference to an individual e-book resource.
     * 
     * @author kotelnikov
     */
    public interface IBookManifestItem {

        /**
         * Returns a local reference (path) from the top to the corresponding
         * resource.
         * 
         * @return a local reference (path) from the top to the corresponding
         *         resource
         */
        Uri getHref();

        /**
         * Returns a unique identifier of the resource.
         * 
         * @return a unique identifier of the resource
         */
        BookId getID();

        /**
         * Returns the MIME type of the resource
         * 
         * @return the MIME type of the resource
         */
        String getMediaType();

    }

    /**
     * Returns a reference to the ea reference to the e-book resource
     * corresponding to the specified -book resource corresponding to the
     * specified path.
     * 
     * @param href the path to the requested resource
     * @return a reference to the e-book resource corresponding to the specified
     *         path
     */
    IBookManifest.IBookManifestItem getItemByHref(Uri href);

    /**
     * Returns a reference to an e-book resource corresponding to the specified
     * resource identifier.
     * 
     * @param id the identifier of a e-book resource
     * @return a reference to an e-book resource corresponding to the specified
     *         resource identifier
     */
    IBookManifest.IBookManifestItem getItemById(BookId id);

    /**
     * Returns a list of all e-book resources
     * 
     * @return a list of all e-book resources
     */
    List<IBookManifest.IBookManifestItem> getItems();

}