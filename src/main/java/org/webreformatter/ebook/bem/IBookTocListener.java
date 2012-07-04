package org.webreformatter.ebook.bem;

import org.webreformatter.commons.uri.Uri;

public interface IBookTocListener {

    void beginToc();

    void beginTocItem(Uri path, String label);

    void endToc();

    void endTocItem();
}