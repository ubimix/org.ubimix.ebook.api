/**
 * 
 */
package org.ubimix.ebook.bom;

import java.util.List;

import org.ubimix.commons.uri.Uri;

/**
 * Objects of this type represent a Table Of Content (TOC) for an e-book.
 * 
 * @author kotelnikov
 */
public interface IBookToc {

    /**
     * Individual entry in the Table Of Content.
     * 
     * @author kotelnikov
     */
    public interface IBookTocItem {

        /**
         * @return a list of child items
         */
        List<IBookTocItem> getChildren();

        /**
         * @return path from the root of the e-book package to the referenced
         *         resource.
         */
        Uri getContentHref();

        /**
         * @return a human-readable label to show in the Table Of Content.
         */
        String getLabel();

    }

    /**
     * @return a list of all top-level TOC items
     */
    List<IBookTocItem> getTocItems();

}
