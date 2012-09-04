/**
 * 
 */
package org.ubimix.ebook.bom;

import java.io.IOException;

import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.io.IInput;

/**
 * @author kotelnikov
 */
public interface IBookProvider {

    public interface IBookReader {

        IBook getBook();

        IInput getBookResource(Uri resourceRef) throws IOException;

        IBookSection getBookSection(Uri sectionHref);

        IBookToc getBookToc();

    }

    IBookReader getBookReader(Uri bookHref) throws IOException;

}
