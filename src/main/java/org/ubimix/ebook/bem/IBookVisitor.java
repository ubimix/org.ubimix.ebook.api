package org.ubimix.ebook.bem;

import java.io.IOException;

/**
 * @author kotelnikov
 */
public interface IBookVisitor {

    public interface IBookListener {

        void begin();

        void end();

        IBookManifestListener getManifestListener();

        IBookMetadataListener getMetadataListener();

        IBookTocListener getTocListener();
    }

    void visitBook(IBookListener listener) throws IOException;
}