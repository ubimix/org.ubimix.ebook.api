/**
 * 
 */
package org.ubimix.ebook.bom;

/**
 * This object provides access to individual e-book sections.
 * 
 * @author kotelnikov
 */
public interface IBookSection {

    /**
     * @return a string-serialized XHTML content of this section.
     */
    String getContent();

    /**
     * @return a human-readable title of the section
     */
    String getTitle();

}
