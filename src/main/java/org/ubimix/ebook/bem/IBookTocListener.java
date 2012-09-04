package org.ubimix.ebook.bem;

import org.ubimix.commons.uri.Uri;

public interface IBookTocListener {

    void beginToc();

    void beginTocItem(Uri path, String label);

    void endToc();

    void endTocItem();
}