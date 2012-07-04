package org.webreformatter.ebook.bom;

import java.util.List;

import org.webreformatter.ebook.BookId;

/**
 * The main interface giving access to all information about an e-book.
 * 
 * @author kotelnikov
 */
public interface IBook {

    /**
     * Returns meta-information about a book or about a specific e-book
     * resource.
     * 
     * @author kotelnikov
     */
    public interface IBookMetadata {

        /**
         * @return information about the e-book creator
         */
        String getBookCreator();

        /**
         * @return a unique e-book identifier
         */
        BookId getBookIdentifier();

        /**
         * @return the language of the book
         */
        String getBookLanguage();

        /**
         * @return an e-book title
         */
        String getBookTitle();

    }

    /**
     * This interface provides information about the reading order for an
     * e-book.
     * 
     * @author kotelnikov
     */
    public interface IBookSpine {

        /**
         * @return a list of identifiers defining the reading order of sections
         *         in the corresponding e-book
         */
        List<BookId> getSectionIds();

    }

    /**
     * @return an information about all resources contained in the covered
     *         e-book
     */
    IBookManifest getManifest();

    /**
     * @return meta-information about the e-book
     */
    IBookMetadata getMetadata();

    /**
     * @return information about the reading sequence of sections in the e-book
     */
    IBookSpine getSpine();

}
