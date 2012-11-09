/**
 * 
 */
package org.ubimix.ebook.bom.epub;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kotelnikov
 */
public class EPubIO {

    /**
     * @author kotelnikov
     */
    public static class EPubException extends RuntimeException {
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

    private static Logger log = Logger.getLogger(EPubIO.class.getName());

    public EPubException onError(String msg, Throwable t) {
        log.log(Level.WARNING, msg, t);
        if (t instanceof EPubException) {
            return (EPubException) t;
        }
        return new EPubException(msg, t);
    }

}
