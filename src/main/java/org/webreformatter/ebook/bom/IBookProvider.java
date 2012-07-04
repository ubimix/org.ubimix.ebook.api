/**
 * 
 */
package org.webreformatter.ebook.bom;

import java.io.IOException;

import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.io.IInput;

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
