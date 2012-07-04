/**
 * 
 */
package org.webreformatter.ebook.bom.epub;

/**
 * @author kotelnikov
 */
public class EPubException extends RuntimeException {
    private static final long serialVersionUID = -6858820958556342211L;

    /**
     * 
     */
    public EPubException() {
    }

    /**
     * @param message
     */
    public EPubException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public EPubException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public EPubException(Throwable cause) {
        super(cause);
    }

}
